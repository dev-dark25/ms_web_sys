package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.Pair;

import java.io.File;
import java.util.List;

public class SearchCommand extends Command {
    private static MapleData npcStringData;
    private static MapleData mobStringData;
    private static MapleData skillStringData;
    private static MapleData mapStringData;

    {
        setDescription("在游戏中进行搜索。");

        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("wz/String.wz"));
        npcStringData = dataProvider.getData("Npc.img");
        mobStringData = dataProvider.getData("Mob.img");
        skillStringData = dataProvider.getData("Skill.img");
        mapStringData = dataProvider.getData("Map.img");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("语法：!search <类型> <名称>");
            return;
        }
        StringBuilder sb = new StringBuilder();

        String search = joinStringFrom(params,1);
        long start = System.currentTimeMillis();//for the lulz
        MapleData data = null;
        if (!params[0].equalsIgnoreCase("ITEM")) {
            int searchType = 0;

            if (params[0].equalsIgnoreCase("NPC")) {
                data = npcStringData;
            } else if (params[0].equalsIgnoreCase("怪物") || params[0].equalsIgnoreCase("MONSTER")) {
                data = mobStringData;
            } else if (params[0].equalsIgnoreCase("技能")) {
                data = skillStringData;
            } else if (params[0].equalsIgnoreCase("地图")) {
                data = mapStringData;
                searchType = 1;
            } else if (params[0].equalsIgnoreCase("任务")) {
                data = mapStringData;
                searchType = 2;
            } else {
                sb.append("#b无效的搜索。\r\n语法: '!search [类型] [名称]', 其中[类型]可以是 地图、任务、NPC、ITEM、怪物 或 技能。");
            }
            if (data != null) {
                String name;

                if (searchType == 0) {
                    for (MapleData searchData : data.getChildren()) {
                        name = MapleDataTool.getString(searchData.getChildByPath("name"), "NO-NAME");
                        if (name.toLowerCase().contains(search.toLowerCase())) {
                            sb.append("#b").append(Integer.parseInt(searchData.getName())).append("#k - #r").append(name).append("\r\n");
                        }
                    }
                } else if (searchType == 1) {
                    String mapName, streetName;

                    for (MapleData searchDataDir : data.getChildren()) {
                        for (MapleData searchData : searchDataDir.getChildren()) {
                            mapName = MapleDataTool.getString(searchData.getChildByPath("mapName"), "NO-NAME");
                            streetName = MapleDataTool.getString(searchData.getChildByPath("streetName"), "NO-NAME");

                            if (mapName.toLowerCase().contains(search.toLowerCase()) || streetName.toLowerCase().contains(search.toLowerCase())) {
                                sb.append("#b").append(Integer.parseInt(searchData.getName())).append("#k - #r").append(streetName).append(" - ").append(mapName).append("\r\n");
                            }
                        }
                    }
                } else {
                    for (MapleQuest mq : MapleQuest.getMatchedQuests(search)) {
                        sb.append("#b").append(mq.getId()).append("#k - #r");

                        String parentName = mq.getParentName();
                        if (!parentName.isEmpty()) {
                            sb.append(parentName).append(" - ");
                        }
                        sb.append(mq.getName()).append("\r\n");
                    }
                }
            }
        } else {
            for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                if (sb.length() < 32654) {//ohlol
                    if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                        sb.append("#b").append(itemPair.getLeft()).append("#k - #r").append(itemPair.getRight()).append("\r\n");
                    }
                } else {
                    sb.append("#b无法加载所有物品，结果太多。\r\n");
                    break;
                }
            }
        }
        if (sb.length() == 0) {
            sb.append("#b未找到").append(params[0].toLowerCase()).append("。\r\n");
        }
        sb.append("\r\n#k加载时间: ").append((double) (System.currentTimeMillis() - start) / 1000).append(" 秒。");//just for fun

        c.getAbstractPlayerInteraction().npcTalk(9010000, sb.toString());
    }
}