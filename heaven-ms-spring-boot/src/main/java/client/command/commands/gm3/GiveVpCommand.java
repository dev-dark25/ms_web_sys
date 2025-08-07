package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class GiveVpCommand extends Command {
    {
        setDescription("向玩家授予投票点（Vote Points）。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("语法：!givevp <playername> <gainvotepoint>");
            return;
        }

        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim != null) {
            victim.getClient().addVotePoints(Integer.parseInt(params[1]));
            player.message("VP given.");
        } else {
            player.message("Player '" + params[0] + "' could not be found.");
        }
    }
}