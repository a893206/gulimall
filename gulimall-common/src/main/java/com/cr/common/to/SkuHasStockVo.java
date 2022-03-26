package com.cr.common.to;

import lombok.Data;

/**
 * @author cr
 * @date 2022/3/27 00:37
 */
@Data
public class SkuHasStockVo {
    private Long skuId;

    private Boolean hasStock;
}
