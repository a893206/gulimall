package com.cr.gulimall.product.feign;

import com.cr.common.to.es.SkuEsModel;
import com.cr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author cr
 * @date 2022/3/27 01:42
 */
@FeignClient(value = "gulimall-search", path = "/search")
public interface SearchFeignService {
    /**
     * 上架商品
     */
    @PostMapping("/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
