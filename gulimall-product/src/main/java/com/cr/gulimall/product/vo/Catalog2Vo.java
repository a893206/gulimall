package com.cr.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2级分类vo
 *
 * @author cr
 * @date 2022/3/27 17:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog2Vo {
    private String catalog1Id;

    private List<Catalog3Vo> catalog3List;

    private String id;

    private String name;

    /**
     * 3级分类vo
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catalog3Vo {
        private String catalog2Id;

        private String id;

        private String name;
    }
}
