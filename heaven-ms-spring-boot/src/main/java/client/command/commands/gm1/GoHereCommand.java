package client.command.commands.gm1;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.maps.MapleMap;
import tools.Log;
import tools.Pair;
import tools.StringUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 玩家能可抵达的地图
 * 依据Map.wz下的map进行筛选
 *
 * @author Auler
 */
public class GoHereCommand extends Command {
    {
        setDescription("将玩家传送到指定地图");
        generateMap();
    }

    private static Map<String, List<Pair<String, String>>> MAP = new HashMap();// key：mapName关键字， value：<mapName,mapId>的列表
    private Map<String, Page> LAST_LIST_MAP = new HashMap();
    private String PLAYER_ID = "accountID";

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        PLAYER_ID = player.getAccountID() + "";
        if (params.length < 1) {
            player.yellowMessage("语法：@gohere  <地图名字/mapId/#编号/#pre/#next>");
            System.out.println("params " + Arrays.toString(params));
            return;
        }
        try {
            int mapId = -1;
            if (params[0].length() == 9 && isNumeric(params[0])) {
                mapId = Integer.parseInt(params[0]);
            } else {
                // #指令
                if('#' == params[0].charAt(0)){
                    Page page = LAST_LIST_MAP.get(PLAYER_ID);
                    if("#pre".equals(params[0])){
                        player.yellowMessage("gohere查询到多个地图：" + page.pre().getPage());
                        return;
                    }
                    if("#next".equals(params[0])){
                        player.yellowMessage("gohere查询到多个地图：" + page.next().getPage());
                        return;
                    }
                    if(isNumeric(params[0].substring(1))){
                        int index = Integer.parseInt(params[0].substring(1));
                        Pair<String, String> pair = page.getPair(index - 1);
                        if(pair != null){
                            mapId = Integer.parseInt(pair.getRight());
                        }
                    }
                }else{
                    List<Pair<String, String>> res = findMapId(params[0]);
                    if (res == null || res.isEmpty()) {
                        player.yellowMessage("gohere没有查询到相关地图信息！");
                        return;
                    }
                    if (res.size() > 1) {
                        Page page = new Page(1, 10, res);
                        LAST_LIST_MAP.put(PLAYER_ID, page);
                        player.yellowMessage("gohere查询到多个地图：" + page.getPage());
                        return;
                    }
                    mapId = Integer.parseInt(res.get(0).getRight());
                }
            }
            if(mapId == -1){
                player.yellowMessage("地图 " + params[0] + " 无效。");
                return;
            }
//            if (!player.isAlive()) {
//                player.dropMessage(1, "在死亡状态下无法使用此命令。");
//                return;
//            }
            MapleMap target = c.getChannelServer().getMapFactory().getMap(mapId);
            player.changeMap(target, target.getRandomPlayerSpawnpoint());
        } catch (Exception e) {
            System.out.println("报错" + e.getMessage());
            player.yellowMessage("地图 " + params[0] + " 无效！");
        }
    }


    private List<Pair<String, String>> findMapId(String str) {
        List<Pair<String, String>> list = MAP.get(Character.toString(str.charAt(0)));
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<Pair<String, String>> res = new ArrayList<>();
        for (Pair<String, String> pair : list) {
            if (str.equals(pair.getLeft())) {
                res = new ArrayList();
                res.add(pair);
                break;
            }
            if (pair.getLeft().contains(str)) {
                res.add(pair);
            }
        }
        return res;
    }

    private static void generateMap() {
        Set<String> mapIdSet = generateMapIdSet();
        MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz")).getData("Map.img");
        for (MapleData md : nameData) {
            for (MapleData md2 : md.getChildren()) {
                if ("".equals(md2.getName())) continue;
                String mapName = MapleDataTool.getString("mapName", nameData.getChildByPath(md.getName() + "/" + md2.getName()), "");
                String mapId = StringUtil.getLeftPaddedStr(md2.getName(), '0', 9);
                if (mapIdSet.contains(mapId)) {//包含其中，则代表有效
                    for (Character c : mapName.toCharArray()) {
                        if (Character.isWhitespace(c)) continue;
                        List<Pair<String, String>> list = MAP.getOrDefault(c.toString(), new ArrayList<>());
                        list.add(new Pair(mapName, mapId));
                        MAP.put(c.toString(), list);
                    }
                }
            }
        }
        System.out.println("gohere MAP 统计：" + MAP.size());
    }

    private static Set<String> generateMapIdSet() {
        try {
            Path directory = Paths.get(System.getProperty("wzpath") + "/Map.wz/Map");
            return Files.walk(directory, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Object::toString)
                    .map(key -> key.substring(0, 9)).collect(Collectors.toSet());
        } catch (IOException e) {
            System.out.println("浏览目录时出错: " + e.getMessage());
        }
        return new HashSet<>();
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    class Page {
        private int page;
        private int size;
        private List<Pair<String, String>> dataList;

        public Page(int page, int size, List<Pair<String, String>> list) {
            this.page = page;
            this.size = size;
            ;
            this.dataList = list;
        }

        public Page pre() {
            page = page < 1 ? 1 : page - 1;
            return this;
        }

        public Page next() {
            int totalPages = (dataList.size() + size - 1) / size;
            page = page >= totalPages ? totalPages : page + 1;
            return this;
        }

        public String getPage() {
            if (dataList == null || dataList.isEmpty()) {
                return "";
            }
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(page * size, dataList.size());

            StringBuilder sb = new StringBuilder();
            sb.append(startIndex + 1).append("~").append(endIndex)
                    .append("，共：").append(dataList.size()).append("条。");
            for (int i = startIndex; i < endIndex; i++) {
                if (i > 0) sb.append(", ");
                sb.append(i + 1).append(".").append(dataList.get(i).getLeft());
            }
//            return dataList.subList(startIndex, endIndex);
            return sb.toString();
        }

        public Pair<String , String> getPair(int index){
            return dataList.get(index);
        }
    }
}
