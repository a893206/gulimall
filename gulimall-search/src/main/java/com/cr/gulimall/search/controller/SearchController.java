package com.cr.gulimall.search.controller;

import com.cr.gulimall.search.service.MallSearchService;
import com.cr.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author cr
 * @date 2022/3/31 23:25
 */
@Controller
public class SearchController {
    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(@RequestBody SearchParam param) {
        Object result = mallSearchService.search(param);

        return "list";
    }
}
