package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MaplePacketCreator;

public class TimerMapCommand extends Command {
    {
        setDescription("在游戏地图中为所有玩家设置计时器或删除计时器。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!timermap <seconds>|remove");
            return;
        }

        if (params[0].equalsIgnoreCase("remove")) {
            // 如果参数为"remove"，则删除地图上所有玩家的计时器
            for (MapleCharacter victim : player.getMap().getCharacters()) {
                victim.announce(MaplePacketCreator.removeClock());
            }
        } else {
            try {
                // 设置地图上所有玩家的计时器，显示给定秒数的时间
                int seconds = Integer.parseInt(params[0]);
                for (MapleCharacter victim : player.getMap().getCharacters()) {
                    victim.announce(MaplePacketCreator.getClock(seconds));
                }
            } catch (NumberFormatException e) {
                player.yellowMessage("语法：!timermap <seconds>|remove");
            }
        }
    }
}