package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.maps.MapleMap;
import net.server.Server;
import net.server.channel.Channel;

public class SummonCommand extends Command {
    {
        setDescription("将另一个玩家传送到您所在的地图。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!warphere <playername>");
            return;
        }

        MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim == null) {
            // 如果被传送的玩家不在当前频道，循环检查当前世界的所有频道。

            for (Channel ch : Server.getInstance().getChannelsFromWorld(c.getWorld())) {
                victim = ch.getPlayerStorage().getCharacterByName(params[0]);
                if (victim != null) {
                    break; // 我们找到这个玩家，无需继续循环。
                }
            }
        }
        if (victim != null) {
            if (!victim.isLoggedinWorld()) {
                player.dropMessage(6, "玩家当前未登录或不可到达。");
                return;
            }

            if (player.getClient().getChannel() != victim.getClient().getChannel()) {// 如果需要，切换频道。
                victim.dropMessage("正在切换频道，请稍等片刻。");
                victim.getClient().changeChannel(player.getClient().getChannel());
            }

            try {
                for (int i = 0; i < 7; i++) {   // 等待一段时间，直到玩家重新连接
                    if (victim.isLoggedinWorld()) break;
                    Thread.sleep(1777);
                }
            } catch (InterruptedException e) {}

            MapleMap map = player.getMap();
            victim.saveLocationOnWarp();
            victim.forceChangeMap(map, map.findClosestPortal(player.getPosition()));
        } else {
            player.dropMessage(6, "未知玩家。");
        }
    }
}