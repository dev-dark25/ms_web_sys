package com.heavenMS.controller;

import com.heavenMS.websocket.WsServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WsServerController {

    private final WsServerEndpoint wse;

    @Autowired
    public WsServerController(WsServerEndpoint wse) {
        this.wse = wse;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Object test(@RequestBody Map req) {
        System.out.println(req);
        Map<String, Object> map = new HashMap<>();
        map.put("status", 0);
        map.put("msg", "successs");
        Map<String, Object> result = new HashMap<>();
        map.put("data", result);
        try {
            wse.sendMessageTo("test send msg", "1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() {
        try {
            wse.sendMessageTo("1", "test send msg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
