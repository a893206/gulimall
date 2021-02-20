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
public class SpuSaveVo {

    private String spuName;

    private String spuDescription;

    private int catalogId;

    private int brandId;

    private double weight;

    private int publishStatus;

    private List<String> decript;

    private List<String> images;

    private Bounds bounds;

    private List<BaseAttrs> baseAttrs;

    private List<Skus> skus;

}