package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class NightCommand extends Command {
    {
        setDescription("在当前地图上触发夜晚效果。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        player.getMap().broadcastNightEffect(); // 向地图上的所有玩家触发夜晚效果
        player.yellowMessage("完成。"); // 向玩家发送消息，表示命令已执行
    }
}