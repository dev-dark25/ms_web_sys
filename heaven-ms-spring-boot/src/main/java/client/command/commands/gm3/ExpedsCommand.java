package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import net.server.channel.Channel;
import server.expeditions.MapleExpedition;

import java.util.List;
import java.util.Map.Entry;

public class ExpedsCommand extends Command {
    {
        setDescription("列出服务器中各频道的远征队伍信息。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        for (Channel ch : Server.getInstance().getChannelsFromWorld(c.getWorld())) {
            List<MapleExpedition> expeds = ch.getExpeditions();
            if (expeds.isEmpty()) {
                player.yellowMessage("在频道 " + ch.getId() + " 中没有远征队伍。");
                continue;
            }
            player.yellowMessage("频道 " + ch.getId() + " 中的远征队伍列表：");
            int id = 0;
            for (MapleExpedition exped : expeds) {
                id++;
                player.yellowMessage("> 远征队伍 " + id);
                player.yellowMessage(">> 类型: " + exped.getType().toString());
                player.yellowMessage(">> 状态: " + (exped.isRegistering() ? "招募中" : "进行中"));
                player.yellowMessage(">> 人数: " + exped.getMembers().size());
                player.yellowMessage(">> 队长: " + exped.getLeader().getName());
                int memId = 2;
                for (Entry<Integer, String> e : exped.getMembers().entrySet()) {
                    if (exped.isLeader(e.getKey())) {
                        continue;
                    }
                    player.yellowMessage(">>> 成员 " + memId + ": " + e.getValue());
                    memId++;
                }
            }
        }
    }
}