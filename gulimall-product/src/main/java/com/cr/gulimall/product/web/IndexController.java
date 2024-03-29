package com.cr.gulimall.product.web;

import com.cr.gulimall.product.entity.CategoryEntity;
import com.cr.gulimall.product.service.CategoryService;
import com.cr.gulimall.product.vo.Catalog2Vo;
import lombok.SneakyThrows;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author cr
 * @date 2022/3/27 17:31
 */
@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        System.out.println(Thread.currentThread().getId());
        // 1、查处所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();

        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");

        // 2、加锁
        // 阻塞式等待。默认加的锁都是30s时间。
        // lock.lock();
        // 1、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间超长，锁自动过期被删掉
        // 2、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除。

        // 10秒自动解锁，自动解锁时间一定要大于业务的执行时间。
        // 问题：lock.lock(10, TimeUnit.SECONDS);在锁时间到了以后，不会自动续期。
        // 1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        // 2、如果我们未指定锁的超时时间，就使用30 * 1000【lockWatchdogTimeout看门狗的默认时间】；
        //   只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】，每隔10s都会自动再次续期，续成30s
        //   internalLockLeaseTime【看门狗时间】 / 3，10s
        lock.lock(10, TimeUnit.SECONDS);

        // 最佳实践
        // 1、lock.lock(10, TimeUnit.SECONDS);省掉了整个续期操作。手动解锁
        try {
            System.out.println("加锁成功，执行业务……" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 3、解锁 假设解锁代码没有运行，redisson会不会出现死锁
            System.out.println("释放锁……" + Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }

    /**
     * 保证一定能读到最新数据，修改期间，写锁是一个排他锁（互斥锁、独享锁）。读锁是一个共享锁
     * 写锁没释放读就必须等待
     * 读 + 读：相当于无锁，并发读，只会在redis中记录好，所有当前的读锁。他们都会同时加锁成功
     * 写 + 读：等待写锁释放
     * 写 + 写：阻塞方式
     * 读 + 写：有读锁。写也需要等待。
     * 只要有写的存在，都必须等待
     */
    @GetMapping("/write")
    @ResponseBody
    public String writeValue() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        String s = "";

        try {
            // 1、改数据加写锁，读数据加读锁
            rLock.lock();
            System.out.println("写锁加锁成功……" + Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redissonClient.getBucket("writeValue").set(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("写锁释放" + Thread.currentThread().getId());
        }

        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        String s = "";

        try {
            // 加读锁
            rLock.lock();
            System.out.println("读锁加锁成功……" + Thread.currentThread().getId());
            Thread.sleep(30000);
            s = redissonClient.getBucket("writeValue").get().toString();
        } catch (Exception ignored) {

        } finally {
            rLock.unlock();
            System.out.println("读锁释放" + Thread.currentThread().getId());
        }

        return s;
    }

    /**
     * 车库停车
     * 3车位
     * 信号量也可以用作分布式限流；
     */
    @SneakyThrows
    @GetMapping("/park")
    @ResponseBody
    public String park() {
        RSemaphore park = redissonClient.getSemaphore("park");
        // 获取一个信号，获取一个值，占一个车位
        // park.acquire();
        boolean b = park.tryAcquire();
        if (b) {
            // 执行业务
        } else {
            return "error";
        }

        return "ok=>" + b;
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redissonClient.getSemaphore("park");
        // 释放一个车位
        park.release();

        return "ok";
    }

    /**
     * 放假，锁门
     * 1班没人了，2
     * 5个班全部走完，我们可以锁大门
     */
    @SneakyThrows
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 等待闭锁都完成
        door.trySetCount(5);
        door.await();

        return "放假了……";
    }

    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 计数减一
        door.countDown();

        return id + "班的人都走了……";
    }

}
