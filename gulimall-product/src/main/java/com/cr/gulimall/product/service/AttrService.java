package com.cr.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.product.entity.AttrEntity;
import com.cr.gulimall.product.vo.AttrGroupRelationVo;
import com.cr.gulimall.product.vo.AttrRespVo;
import com.cr.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-25 23:32:51
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, String attrType, Long catalogId);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    /**
     * 根据分组id查找关联的所有基本属性
     *
     * @param attrGroupId 分组id
     * @return 商品属性对象集合
     */
    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * 获取当前分组没有关联的所有属性
     *
     * @param attrGroupId
     * @param params
     * @return
     */
    PageUtils getNoRelationAttr(Long attrGroupId, Map<String, Object> params);

    /**
     * 在指定的所有属性集合里面，挑出检索属性
     *
     * @param attrIds 属性id列表
     * @return 属性id列表
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

