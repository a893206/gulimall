package com.cr.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author cr
 * @date 2022/3/26 23:32
 */
@Data
public class SkuEsModel {
    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private String brandName;

    private String brandImg;

    private Long catalogId;

    private String catalogName;

    private List<Attr> attrs;

    @Data
    public static class Attr {
        private Long attrId;

        private String attrName;

        private String attrValue;
    }
}
