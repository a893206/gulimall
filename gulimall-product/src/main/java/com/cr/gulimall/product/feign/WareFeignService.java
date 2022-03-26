package com.cr.gulimall.product.feign;

import com.cr.common.to.SkuHasStockVo;
import com.cr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author cr
 * @date 2022/3/27 00:46
 */
@FeignClient(value = "gulimall-ware", path = "/ware")
public interface WareFeignService {
    @PostMapping("/waresku/hasStock")
    R<List<SkuHasStockVo>> getSkusHasStock(@RequestBody List<Long> skuIds);
}
