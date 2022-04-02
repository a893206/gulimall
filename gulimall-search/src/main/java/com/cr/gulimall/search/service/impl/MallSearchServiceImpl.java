package com.cr.gulimall.search.service.impl;

import com.cr.gulimall.search.service.MallSearchService;
import com.cr.gulimall.search.vo.SearchParam;
import com.cr.gulimall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

/**
 * @author cr
 * @date 2022/4/1 23:08
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    /**
     * 检索
     *
     * @param param 检索的所有参数
     * @return 返回检索的结果
     */
    @Override
    public SearchResult search(SearchParam param) {
        return null;
    }
}
