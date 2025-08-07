package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import server.maps.MapleMiniDungeonInfo;

public class WarpCommand extends Command {
    {
        setDescription("将玩家传送到指定地图。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!warp <mapid>");
            return;
        }

        try {
            MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
            if (target == null) {
                player.yellowMessage("地图 ID " + params[0] + " 无效。");
                return;
            }

            if (!player.isAlive()) {
                player.dropMessage(1, "在死亡状态下无法使用此命令。");
                return;
            }

            if (!player.isGM()) {
                if (player.getEventInstance() != null || MapleMiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
                    player.dropMessage(1, "无法在此地图中使用此命令。");
                    return;
                }
            }

            // 检查是否在远征活动中的问题
            player.saveLocationOnWarp();
            // 切换地图
            player.changeMap(target, target.getRandomPlayerSpawnpoint());
        } catch (Exception ex) {
            player.yellowMessage("地图 ID " + params[0] + " 无效！");
        }
    }
}