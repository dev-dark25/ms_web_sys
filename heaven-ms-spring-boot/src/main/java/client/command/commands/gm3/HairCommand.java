package client.command.commands.gm3;

import client.MapleStat;
import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public class HairCommand extends Command {
    {
        // 设置命令描述
        setDescription("更改玩家的发型。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            // 如果参数不足，向玩家发送消息
            player.yellowMessage("语法：!hair [<玩家名称>] <发型ID>");
            return;
        }

        try {
            if (params.length == 1) {
                int itemId = Integer.parseInt(params[0]);
                if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
                    // 如果发型ID无效，向玩家发送消息
                    player.yellowMessage("发型ID '" + params[0] + "' 不存在。");
                    return;
                }

                // 更改玩家的发型
                player.setHair(itemId);
                player.updateSingleStat(MapleStat.HAIR, itemId);
                player.equipChanged();
            } else {
                int itemId = Integer.parseInt(params[1]);
                if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
                    // 如果发型ID无效，向玩家发送消息
                    player.yellowMessage("发型ID '" + params[1] + "' 不存在。");
                    return;
                }

                // 获取目标玩家
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
                if (victim != null) {
                    // 更改目标玩家的发型
                    victim.setHair(itemId);
                    victim.updateSingleStat(MapleStat.HAIR, itemId);
                    victim.equipChanged();
                } else {
                    // 如果目标玩家不在当前频道，向玩家发送消息
                    player.yellowMessage("无法在当前频道找到玩家 '" + params[0] + "'。");
                }
            }
        } catch (Exception e) {
            // 处理异常
        }
    }
}