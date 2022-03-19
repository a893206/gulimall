package com.cr.gulimall.ware.feign;

import com.cr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author cr
 * @date 2022/3/19 18:56
 */
@FeignClient(value = "gulimall-product", path = "/product")
public interface ProductFeignService {
    /**
     * sku信息
     *
     * @param skuId skuId
     * @return sku信息
     */
    @RequestMapping("/skuInfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
