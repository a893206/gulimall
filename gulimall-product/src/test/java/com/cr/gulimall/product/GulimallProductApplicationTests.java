package com.cr.gulimall.product;

import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
public class GulimallProductApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {
        // 上传文件流。
        InputStream inputStream = new FileInputStream("/Users/cr/Desktop/20200131.png");
        ossClient.putObject("gulimall-cr0898", "20200131.png", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传完成");
    }

}
