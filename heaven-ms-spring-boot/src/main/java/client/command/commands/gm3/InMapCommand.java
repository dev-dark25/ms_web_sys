package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class InMapCommand extends Command {
    {
        // 设置命令描述
        setDescription("查看当前地图上的所有玩家。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        String st = "";
        for (MapleCharacter chr : player.getMap().getCharacters()) {
            // 将当前地图上的每个玩家名称添加到字符串中
            st += chr.getName() + " ";
        }
        // 将包含所有玩家名称的字符串发送给GM
        player.message(st);
    }
}