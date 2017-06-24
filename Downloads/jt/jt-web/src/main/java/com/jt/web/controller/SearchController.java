package com.jt.web.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jt.common.vo.SysResult;
import com.jt.web.service.SearchService;


@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping("search")
    public String search(@RequestParam("q") String keyWords,
            @RequestParam(value = "page", defaultValue = "1") Integer page, Model model) {
        
        SysResult result = this.searchService.search(keyWords, page);
        model.addAttribute("itemList", result.getData());
        
        //后台将记录总数放在msg信息中
        Integer total = Integer.valueOf(result.getMsg());
        Integer SEARCH_COUNT = 20;
        Integer pages = (total - 1 + SEARCH_COUNT) / SEARCH_COUNT;
        model.addAttribute("pages", pages);
        model.addAttribute("page", page);
        
        try {
        	//web会以get请求传参，参数中有中文
            keyWords = new String(keyWords.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        model.addAttribute("query", keyWords);
        return "search";
    }

}
