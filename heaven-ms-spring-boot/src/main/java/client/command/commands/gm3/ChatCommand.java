package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class ChatCommand extends Command {
    {
        setDescription("切换聊天模式。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        player.toggleWhiteChat();
        player.message("你的聊天模式现在是 " + (player.getWhiteChat() ? " 白色聊天" : "普通聊天") + " 模式。");
    }
}