package com.cr.gulimall.product;

import com.cr.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testFindPath() {
        Long[] catalogPath = categoryService.findCatalogPath(225L);
        log.info("完整路径：{}", Arrays.toString(catalogPath));
    }

}
