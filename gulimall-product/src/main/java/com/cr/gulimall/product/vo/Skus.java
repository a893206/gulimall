/**
  * Copyright 2021 json.cn 
  */
package com.cr.gulimall.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-02-20 17:13:7
 *
 * @author cr
 */
@Data
public class Skus {

    private List<Attr> attr;

    private String skuName;

    private BigDecimal price;

    private String skuTitle;

    private String skuSubtitle;

    private List<Images> images;

    private List<String> descartes;

    private int fullCount;

    private BigDecimal discount;

    private int countStatus;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;

}