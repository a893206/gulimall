package com.cr.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cr.gulimall.product.entity.BrandEntity;
import com.cr.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    void contextLoads() {
    }

    @Test
    void testBrandService() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("苹果");
//        brandService.save(brandEntity);

//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("苹果");
//        brandService.updateById(brandEntity);

        List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        for (BrandEntity brandEntity : brandEntityList) {
            System.out.println("brandEntity = " + brandEntity);
        }
    }

}
