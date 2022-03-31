package com.cr.gulimall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author cr
 * @date 2022/3/31 23:25
 */
@Controller
public class SearchController {
    @GetMapping("/list.html")
    public String listPage() {
        return "list";
    }
}
