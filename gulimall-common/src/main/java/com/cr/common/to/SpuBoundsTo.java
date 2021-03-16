package com.cr.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author cr
 * @date 2021/3/16 22:58
 */
@Data
public class SpuBoundsTo {

    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;

}
