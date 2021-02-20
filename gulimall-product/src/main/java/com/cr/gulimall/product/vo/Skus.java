/**
  * Copyright 2021 json.cn 
  */
package com.cr.gulimall.product.vo;
import lombok.Data;

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

    private String price;

    private String skuTitle;

    private String skuSubtitle;

    private List<Images> images;

    private List<String> descar;

    private int fullCount;

    private double discount;

    private int countStatus;

    private int fullPrice;

    private int reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;

}