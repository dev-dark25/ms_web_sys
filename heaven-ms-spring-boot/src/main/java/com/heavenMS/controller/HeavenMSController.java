package com.heavenMS.controller;

import client.MapleCharacter;
import client.MapleJob;
import cn.nap.utils.common.NapComUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import constants.inventory.ItemConstants;
import net.server.Server;
import net.server.channel.Channel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ui.dao.ConfigServerDao;
import ui.dao.GmCommandDao;
import ui.model.ConfigServer;
import ui.model.GmCommand;

import java.util.*;

@RestController
public class HeavenMSController {

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public String init(@RequestBody Map req) {
        System.out.println("init req: " + req);
        Server server = Server.getInstance();
        System.out.println(server);
        server.init();
        return "post for init success";
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
    public String shutdown() {
        System.out.println("shutdown");
        Server server = Server.getInstance();
        System.out.println(server);
        server.shutdownInternal(false);
        return "get for shutdown success";
    }

    @RequestMapping(value = "/restart", method = RequestMethod.GET)
    public String restart() {
        System.out.println("shutdown");
        Server server = Server.getInstance();
        System.out.println(server);
        server.shutdownInternal(true);
        return "get for restart success";
    }

    @RequestMapping(value = "/getConfig", method = RequestMethod.POST)
    public Map getConfig(@RequestBody Map<String, Object> req) {
        System.out.println("getConfig");
//        if (!(req.get("parameter") == null || ((String) req.get("parameter")).trim().isEmpty())) {
//
//        } else {
//            int count = ConfigServerDao.selectCount();
//            List list = ConfigServerDao.selectByPage((Integer) req.get("currentPage"), (Integer) req.get("pageSize"));
//        }
        int count = ConfigServerDao.selectUiCount((String) req.get("parameter"));
        List list = ConfigServerDao.selectUiList((String) req.get("parameter"), (Integer) req.get("currentPage"), (Integer) req.get("pageSize"));
        Map<String, Object> map = new HashMap<>();
        map.put("total", count);
        map.put("list", list);
        map.put("code", "200");
        return map;
    }

    @RequestMapping(value = "/updataConfig", method = RequestMethod.POST)
    public Map updataConfig(@RequestBody Map<String, Object> req) {
        System.out.println("updataConfig");
        ConfigServerDao.update(JSON.parseObject(JSONObject.toJSONString(req), ConfigServer.class));
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        return map;
    }

    @RequestMapping(value = "/getCommand", method = RequestMethod.POST)
    public Map getCommand(@RequestBody Map<String, Object> req) {
        System.out.println("getCommand");
        int count = GmCommandDao.selectCountByCondition((String) req.get("parameter"), (Integer) req.get("gmLevel"));
        List list = GmCommandDao.selectPageByCondition((Integer) req.get("currentPage"), (Integer) req.get("pageSize"), (String) req.get("parameter"), (Integer) req.get("gmLevel"));
        Map<String, Object> map = new HashMap<>();
        map.put("total", count);
        map.put("list", list);
        map.put("code", "200");
        return map;
    }

    @RequestMapping(value = "/updataCommand", method = RequestMethod.POST)
    public Map updataCommand(@RequestBody Map<String, Object> req) {
        System.out.println("updataCommand");
        GmCommandDao.update(JSON.parseObject(JSONObject.toJSONString(req), GmCommand.class));
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        return map;
    }

    @RequestMapping(value = "/getPlayer", method = RequestMethod.POST)
    public Map getPlayer(@RequestBody Map<String, Object> req) {
        System.out.println("getPlayer");
        Map<String, Object> map = new HashMap<>();
        if (!Server.getInstance().isOnline()) {
            map.put("code", "201");
            map.put("message", "服务未启动");
            return map;
        }
        int count = 0;
        List<Map> characters = new ArrayList();
        characters = new ArrayList();
        for (Channel channel : Server.getInstance().getAllChannels()) {
            for (MapleCharacter character : channel.getPlayerStorage().getAllCharacters()) {
                if (NapComUtils.isEmpty(req.get("parameter"))) {
                    characters.add(characterToMap(character));
                    count++;
                } else if (String.valueOf(character.getAccountID()).contains((String) req.get("parameter")) || character.getName().contains((String) req.get("parameter"))) {
                    characters.add(characterToMap(character));
                    count++;
                }
            }
        }

        map.put("total", count);
        map.put("list", characters);
        map.put("code", "200");
        return map;
    }

    @RequestMapping(value = "/updataPlayer", method = RequestMethod.POST)
    public Map updataPlayer(@RequestBody Map<String, Object> req) {
        System.out.println("updataPlayer");
        int operateValue = (int) req.get("operateValue");
        Map<String, Object> map = new HashMap<>();
        if (!Server.getInstance().isOnline()) {
            map.put("code", "201");
            map.put("message", "服务未启动");
            return map;
        }
        MapleCharacter targetCharacter = null;
        for (Channel channel : Server.getInstance().getAllChannels()) {
            for (MapleCharacter character : channel.getPlayerStorage().getAllCharacters()) {
                if (String.valueOf(character.getAccountID()).contains(req.get("parameter").toString())) {
                    targetCharacter = character;
                }
            }
        }

        if (operateValue == 0) {
            targetCharacter.getCashShop().gainNx(Integer.parseInt((String) req.get("cash")));
            targetCharacter.startMapEffect("管理员给你发送了" + req.get("cash") + "点券！", 5120015);
        } else if (operateValue == 1) {
            targetCharacter.gainExp(Integer.parseInt((String) req.get("exp")));
            targetCharacter.startMapEffect("管理员给你发送了" + req.get("exp") + "经验！", 5120015);
        } else if (operateValue == 2) {
            targetCharacter.gainMeso(Integer.parseInt((String) req.get("meso")));
            targetCharacter.startMapEffect("管理员给你发送了" + req.get("meso") + "金币！", 5120015);
        } else if (operateValue == 3) {
            if ((int) req.get("gmLevel") < 3) {
                targetCharacter.Hide(false);
                targetCharacter.setGMLevel((int) req.get("gmLevel"));
            } else {
                targetCharacter.setGMLevel((int) req.get("gmLevel"));
                targetCharacter.Hide(true);
            }
            targetCharacter.dropMessage(6, "管理员已将你的GM等级设置为[" + (int) req.get("gmLevel") + "]");
        } else if (operateValue == 4) {
            if (ItemConstants.isPet(Integer.parseInt((String) req.get("item")))) {
                long expire;
//                if (NapComUtils.isEmpty((String) req.get("expire"))) {
                expire = 90L * 24 * 60 * 60 * 1000;
//                } else {
//                    expire = Long.parseLong((String) req.get("expire")) * 24 * 60 * 60 * 1000;
//                }
                targetCharacter.getAbstractPlayerInteraction().gainItem(Integer.parseInt((String) req.get("item")), Short.parseShort((String) req.get("quantity")), false, true, expire);
            } else {
                targetCharacter.getAbstractPlayerInteraction().gainItem(Integer.parseInt((String) req.get("item")), Short.parseShort((String) req.get("quantity")));
            }
            targetCharacter.dropMessage(6, "管理员给你发送了" + req.get("quantity") + "个" + "物品");
        } else if (operateValue == 5) {
            System.out.println(req.get("equip").getClass().getName());
            String equip = (String) req.get("equip");
            System.out.println(req.get("str").getClass().getName());
            Short.parseShort(req.get("str").toString());
            targetCharacter.getAbstractPlayerInteraction().gainEquip(Integer.parseInt(equip), Short.parseShort(req.get("str").toString()), Short.parseShort(req.get("dex").toString()),
                    Short.parseShort(req.get("int").toString()), Short.parseShort(req.get("luk").toString()), Short.parseShort(req.get("hp").toString()), Short.parseShort(req.get("mp").toString()), Short.parseShort(req.get("watk").toString()),
                    Short.parseShort(req.get("matk").toString()), Short.parseShort(req.get("wdef").toString()), Short.parseShort(req.get("mdef").toString()), Short.parseShort(req.get("acc").toString()), Short.parseShort(req.get("avoid").toString()),
                    Short.parseShort(req.get("speed").toString()), Short.parseShort(req.get("jump").toString()), -1L, (short) 1);
            targetCharacter.dropMessage(6, "管理员给你发送了1件装备");
        }

        map.put("code", "200");
        return map;
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("configName", "123");
        System.out.println(JSON.parseObject(JSONObject.toJSONString(map), ConfigServer.class).getConfigName());
    }

    private Map characterToMap(MapleCharacter character) {
        Map map = new HashMap();
        map.put("accountId", character.getAccountID());
        map.put("name", character.getName());
        map.put("mapId", character.getMapId());
        map.put("job", Optional.ofNullable(MapleJob.getById(character.getJob().getId()))
                .orElse(MapleJob.BEGINNER).getName());
        map.put("level", character.getLevel());
        map.put("gmLevel", character.gmLevel());
        map.put("cash", character.getCashShop().getCash(1));
        map.put("meso", character.getMeso());
        return map;
    }
}
