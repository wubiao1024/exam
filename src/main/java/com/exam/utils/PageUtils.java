package com.exam.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.HashMap;
import java.util.List;

public class PageUtils {
    public static<PageType,ListType> HashMap<String, Object> getResMapByPage(Page<PageType> page, String listName,
                                                                             List<ListType> list){
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageTotalCount",page.getPages());
        map.put("listTotalCount",page.getTotal());
        map.put("current",page.getCurrent());
        map.put("pageSize",page.getSize());
        map.put(listName,list);
        return map;
    }
}
