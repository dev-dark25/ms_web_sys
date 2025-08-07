package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MaplePacketCreator;

public class TimerCommand extends Command {
    {
        setDescription("为指定玩家设置计时器或删除计时器。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("语法：!timer <playername> <seconds>|remove");
            return;
        }

        // 获取玩家名称
        String playerName = params[0];

        // 根据名称查找玩家对象
        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(playerName);

        if (victim != null) {
            if (params[1].equalsIgnoreCase("remove")) {
                // 如果参数为"remove"，则删除玩家的计时器
                victim.announce(MaplePacketCreator.removeClock());
            } else {
                try {
                    // 设置玩家的计时器，显示给定秒数的时间
                    int seconds = Integer.parseInt(params[1]);
                    victim.announce(MaplePacketCreator.getClock(seconds));
                } catch (NumberFormatException e) {
                    player.yellowMessage("语法：!timer <playername> <seconds>|remove");
                }
            }
        } else {
            player.message("找不到玩家'" + playerName + "'。");
        }
    }
}