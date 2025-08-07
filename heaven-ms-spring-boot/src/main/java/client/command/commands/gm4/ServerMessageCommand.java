package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

/**
 * 执行"ServerMessageCommand"操作的GM命令
 * Author: Arthur L
 */
public class ServerMessageCommand extends Command {
    {
        setDescription("设置服务器的公告信息。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        c.getWorldServer().setServerMessage(player.getLastCommandMessage());
    }
}