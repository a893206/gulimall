package com.cr.gulimall.product.service.impl;

import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr.common.utils.PageUtils;
import com.cr.common.utils.Query;
import com.cr.gulimall.product.dao.AttrGroupDao;
import com.cr.gulimall.product.entity.AttrEntity;
import com.cr.gulimall.product.entity.AttrGroupEntity;
import com.cr.gulimall.product.service.AttrGroupService;
import com.cr.gulimall.product.service.AttrService;
import com.cr.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author cr
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catalogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (catalogId != 0) {
            wrapper.eq("catalog_id", catalogId);
        }
        String key = (String) params.get("key");
        //select * from pms_attr_group where catalog_id = ? and (attr_group_id = key or attr_group_name like %key%)
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     *
     * @param catalogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatalogId(Long catalogId) {
        // 1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = list(new QueryWrapper<AttrGroupEntity>().eq("catalog_id", catalogId));

        // 2、查询所有属性
        return attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());
    }

}