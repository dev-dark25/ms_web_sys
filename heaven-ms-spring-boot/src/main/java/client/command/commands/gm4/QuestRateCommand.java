package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

/**
 * 执行"QuestRateCommand"操作的GM命令
 * Author: Arthur L
 */
public class QuestRateCommand extends Command {
    {
        setDescription("更改服务器的任务倍率。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!questrate <newrate>");
            return;
        }

        int questrate = Math.max(Integer.parseInt(params[0]), 1);
        c.getWorldServer().setQuestRate(questrate);
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Rate] Quest Rate has been changed to " + questrate + "x."));
    }
}