package com.cr.gulimall.product.web;

import com.cr.gulimall.product.entity.CategoryEntity;
import com.cr.gulimall.product.service.CategoryService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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
        // 1、查处所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();

        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, Object> getCatalogJson() {
        return categoryService.getCatalogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");

        // 2、加锁
        // 阻塞式等待。默认加的锁都是30s时间。
        lock.lock();
        // 1、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间超长，锁自动过期被删掉
        // 2、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除。
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
}
