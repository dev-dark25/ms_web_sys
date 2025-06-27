package com.example.web.controller;

import com.example.web.service.UPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class UPController {

    //    private Logger logger = LoggerFactory.getLogger("special"); // 使用定制的logger
    private final Logger logger = LoggerFactory.getLogger(UPController.class);  // 默认

    @Resource
    private UPService upService;

    // 卡池
    @RequestMapping(value = "/up/getResources", method = RequestMethod.POST)
    public Object getPermanentResource(@RequestBody Map requestParam) {
        String type = (String) requestParam.get("type");    // 0-角色；1-武器
        String limitedType = (String) requestParam.get("limitedType");  // 0-限定池1；1-限定池2
        int count = (int) requestParam.get("count");    // 抽取数量
        requestParam.put("id", 0);
        List result = upService.getResource(requestParam);
        Map<String, Object> map = new HashMap();
        map.put("code", "200");
        map.put("message", "222");
        map.put("list", result);
        return map;
    }

    // 限定池
//    @RequestMapping(value = "/up/getTimeLimitResource", method = RequestMethod.POST)
//    public Object getTimeLimitResource(@RequestBody Map requestParam) {
//        String type = (String) requestParam.get("type");    // 0-角色；1-武器
//        String limitedType = (String) requestParam.get("limitedType");  // 0-限定池1；1-限定池2
//        int count = (int) requestParam.get("count");    // 抽取数量
//        Map<String, Object> map = new HashMap();
//        map.put("code", "200");
//        map.put("message", "222");
//        return map;
//    }

    @RequestMapping(value = "/up/getResourceRecord", method = RequestMethod.POST)
    public Object getResourceRecord(@RequestBody Map requestParam) {
        String type = (String) requestParam.get("type");    // 0-角色；1-武器
        List result = upService.getResourceRecord(requestParam);
        Map<String, Object> map = new HashMap();
        map.put("code", "200");
        map.put("message", "222");
        map.put("list", result);
        return map;
    }

}
