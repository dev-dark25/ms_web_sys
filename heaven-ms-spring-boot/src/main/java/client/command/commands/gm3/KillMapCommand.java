package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class KillMapCommand extends Command {
    {
        // 设置命令描述
        setDescription("杀死当前地图上的所有玩家。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        for (MapleCharacter mch : player.getMap().getCharacters()) {
            // 将当前地图上的每个玩家的HP设置为0，即杀死他们
            mch.updateHp(0);
        }
    }
}