package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class HealMapCommand extends Command {
    {
        // 设置命令描述
        setDescription("恢复当前地图上所有角色的健康（HP和MP）。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        // 循环遍历当前地图上的所有角色
        for (MapleCharacter mch : player.getMap().getCharacters()) {
            if (mch != null) {
                // 恢复角色的HP和MP
                mch.healHpMp();
            }
        }
    }
}