package client.command.commands.gm3;

import client.MapleStat;
import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class FameCommand extends Command {
    {
        setDescription("更改玩家的声望（Fame）值。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("语法：!fame <playername> <gainfame>");
            return;
        }

        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim != null) {
            victim.setFame(Integer.parseInt(params[1]));
            victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            player.message("已赋予声望（FAME）。");
        } else {
            player.message("无法找到玩家 '" + params[0] + "'。");
        }
    }
}