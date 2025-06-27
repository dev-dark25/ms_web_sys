package com.example.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class WebController {

    //    private Logger logger = LoggerFactory.getLogger("special"); // 使用定制的logger
    private final Logger logger = LoggerFactory.getLogger(WebController.class);  // 默认

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestBody String body) {
        logger.info("login: {}", body);
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("token", "333");
        map.put("message", "222");
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", "111");
        userInfo.put("age", 20);
        map.put("userInfo", userInfo);
        return map;
    }

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public Object getUserInfo() {
        logger.info("getUserInfo");
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("token", "333");
        map.put("message", "222");
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", "111");
        userInfo.put("age", 20);
        map.put("userInfo", userInfo);
        return map;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Object logout() {
        logger.info("logout");
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("token", "333");
        map.put("message", "222");
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", "111");
        userInfo.put("age", 20);
        map.put("userInfo", userInfo);
        return map;
    }

    @RequestMapping(value = "/getMenuInfo", method = RequestMethod.POST)
    public Object getMenuInfo() {
        logger.info("getMenuInfo");
        Map<String, Object> map = new HashMap<>();
        List<Object> menuList = new ArrayList<>();

        Map<String, Object> menu2 = new HashMap<>();
        menu2.put("path", "/vue3Dev");
        menu2.put("name", "vue3Dev");
        menu2.put("icon", "promotion");
        menu2.put("title", "Vue3Dev");
        menu2.put("isMenu", false);
        menu2.put("vuePage", "Vue3Dev");
        menu2.put("directory", "Pages");
        menuList.add(menu2);

        Map<String, Object> menu3 = new HashMap<>();
        menu3.put("path", "/menu1");
        menu3.put("icon", "location");
        menu3.put("title", "Menu");
        menu3.put("isMenu", true);
        menu3.put("menuNum", "1");
        menuList.add(menu3);
        Map<String, Object> menu3_1 = new HashMap<>();
        menu3_1.put("path", "/list");
        menu3_1.put("name", "list");
        menu3_1.put("icon", "place");
        menu3_1.put("title", "List");
        menu3_1.put("menuNum", "1");
        menu3_1.put("isMenu", false);
        menu3_1.put("vuePage", "List");
        menu3_1.put("directory", "Pages");
        menuList.add(menu3_1);
        Map<String, Object> menu3_2 = new HashMap<>();
        menu3_2.put("path", "/echarts");
        menu3_2.put("name", "echarts");
        menu3_2.put("icon", "money");
        menu3_2.put("title", "Echarts");
        menu3_2.put("menuNum", "1");
        menu3_2.put("vuePage", "Echarts");
        menu3_2.put("directory", "Pages");
        menuList.add(menu3_2);

        Map<String, Object> menu6 = new HashMap<>();
        menu6.put("path", "/menu3");
        menu6.put("icon", "lollipop");
        menu6.put("title", "MapleStory");
        menu6.put("isMenu", true);
        menu6.put("menuNum", "3");
        menuList.add(menu6);
        Map<String, Object> menu6_1 = new HashMap<>();
        menu6_1.put("path", "/mapleStory/index");
        menu6_1.put("name", "index");
        menu6_1.put("icon", "lollipop");
        menu6_1.put("title", "Index");
        menu6_1.put("menuNum", "3");
        menu6_1.put("vuePage", "Index");
        menu6_1.put("directory", "MapleStory");
        menuList.add(menu6_1);
        Map<String, Object> menu6_7 = new HashMap<>();
        menu6_7.put("path", "/mapleStory/config");
        menu6_7.put("name", "config");
        menu6_7.put("icon", "lollipop");
        menu6_7.put("title", "Config");
        menu6_7.put("menuNum", "3");
        menu6_7.put("vuePage", "Config");
        menu6_7.put("directory", "MapleStory");
        menuList.add(menu6_7);
        Map<String, Object> menu6_8 = new HashMap<>();
        menu6_8.put("path", "/mapleStory/command");
        menu6_8.put("name", "command");
        menu6_8.put("icon", "lollipop");
        menu6_8.put("title", "Command");
        menu6_8.put("menuNum", "3");
        menu6_8.put("vuePage", "Command");
        menu6_8.put("directory", "MapleStory");
        menuList.add(menu6_8);
        Map<String, Object> menu6_9 = new HashMap<>();
        menu6_9.put("path", "/mapleStory/player");
        menu6_9.put("name", "player");
        menu6_9.put("icon", "lollipop");
        menu6_9.put("title", "Player");
        menu6_9.put("menuNum", "3");
        menu6_9.put("vuePage", "Player");
        menu6_9.put("directory", "MapleStory");
        menuList.add(menu6_9);
        Map<String, Object> menu6_2 = new HashMap<>();
        menu6_2.put("path", "/mapleStory/monitor");
        menu6_2.put("name", "monitor");
        menu6_2.put("icon", "lollipop");
        menu6_2.put("title", "Monitor");
        menu6_2.put("menuNum", "3");
        menu6_2.put("vuePage", "Monitor");
        menu6_2.put("directory", "MapleStory");
        menuList.add(menu6_2);
        Map<String, Object> menu6_4 = new HashMap<>();
        menu6_4.put("path", "/mapleStory/accounts");
        menu6_4.put("name", "accounts");
        menu6_4.put("icon", "lollipop");
        menu6_4.put("title", "Accounts");
        menu6_4.put("menuNum", "3");
        menu6_4.put("vuePage", "Accounts");
        menu6_4.put("directory", "MapleStory");
        menuList.add(menu6_4);
        Map<String, Object> menu6_5 = new HashMap<>();
        menu6_5.put("path", "/mapleStory/characters");
        menu6_5.put("name", "characters");
        menu6_5.put("icon", "lollipop");
        menu6_5.put("title", "Characters");
        menu6_5.put("menuNum", "3");
        menu6_5.put("vuePage", "Characters");
        menu6_5.put("directory", "MapleStory");
        menuList.add(menu6_5);
        Map<String, Object> menu6_6 = new HashMap<>();
        menu6_6.put("path", "/mapleStory/skills");
        menu6_6.put("name", "skills");
        menu6_6.put("icon", "lollipop");
        menu6_6.put("title", "Skills");
        menu6_6.put("menuNum", "3");
        menu6_6.put("vuePage", "Skills");
        menu6_6.put("directory", "MapleStory");
        menuList.add(menu6_6);

        map.put("code", "200");
        map.put("message", "222");
        map.put("menuList", menuList);
        return map;
    }
}
