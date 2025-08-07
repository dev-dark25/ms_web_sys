package client.command.commands.gm3;

import client.MapleStat;
import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public class FaceCommand extends Command {
    {
        setDescription("更改玩家的面部表情（Face）ID。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!face [<playername>] <faceid>");
            return;
        }

        try {
            if (params.length == 1) {
                int itemId = Integer.parseInt(params[0]);
                if (!ItemConstants.isFace(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
                    player.yellowMessage("面部表情ID '" + params[0] + "' 不存在。");
                    return;
                }

                player.setFace(itemId);
                player.updateSingleStat(MapleStat.FACE, itemId);
                player.equipChanged();
            } else {
                int itemId = Integer.parseInt(params[1]);
                if (!ItemConstants.isFace(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
                    player.yellowMessage("面部表情ID '" + params[1] + "' 不存在。");
                }

                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
                if (victim == null) {
                    victim.setFace(itemId);
                    victim.updateSingleStat(MapleStat.FACE, itemId);
                    victim.equipChanged();
                } else {
                    player.yellowMessage("在此频道中找不到玩家 '" + params[0] + "'。");
                }
            }
        } catch (Exception e) {
        }
    }
}