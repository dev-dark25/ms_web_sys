package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MapleLogger;

public class IgnoredCommand extends Command {
    {
        // 设置命令描述
        setDescription("查看当前被忽略的玩家列表。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        for (Integer cid : MapleLogger.ignored) {
            // 将当前被忽略的玩家名称显示在GM的聊天中
            player.yellowMessage(MapleCharacter.getNameById(cid) + " 正在被忽略。");
        }
    }
}