package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.maps.MapleMap;

import java.awt.*;

/**
 * 生成暗黑龙王（Horntail）的GM命令
 * Author: Arthur L
 */
public class HorntailCommand extends Command {
    {
        setDescription("在玩家当前位置生成暗黑龙王。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        final Point targetPoint = player.getPosition();
        final MapleMap targetMap = player.getMap();

        // 在玩家当前位置生成暗黑龙王
        targetMap.spawnHorntailOnGroundBelow(targetPoint);
    }
}