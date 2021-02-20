package com.cr.gulimall.product.vo;

import com.cr.gulimall.product.entity.AttrEntity;
import com.cr.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author cr
 * @date 2021/2/20 15:25
 */
@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {

    private List<AttrEntity> attrs;

}
