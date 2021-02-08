package com.cr.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr.common.utils.PageUtils;
import com.cr.common.utils.Query;
import com.cr.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.cr.gulimall.product.dao.AttrDao;
import com.cr.gulimall.product.dao.AttrGroupDao;
import com.cr.gulimall.product.dao.CategoryDao;
import com.cr.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.cr.gulimall.product.entity.AttrEntity;
import com.cr.gulimall.product.entity.AttrGroupEntity;
import com.cr.gulimall.product.entity.CategoryEntity;
import com.cr.gulimall.product.service.AttrService;
import com.cr.gulimall.product.vo.AttrRespVo;
import com.cr.gulimall.product.vo.AttrVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author cr
 */
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        // 1、保存基本数据
        save(attrEntity);

        // 2、保存关联关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationDao.insert(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catalogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        if (catalogId != 0) {
            queryWrapper.eq("catalog_id", catalogId);
        }

        String key = (String) params.get("key");
        if (Strings.isNotBlank(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            // 1、设置分类和分组的名字
            AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (attrId != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatalogId());
            if (categoryEntity != null) {
                attrRespVo.setCatalogName(categoryEntity.getName());
            }

            return attrRespVo;
        })).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }

}