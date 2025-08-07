package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;


public class PfpCommand extends Command {
    public PfpCommand() {
        setDescription("启动宠物全屏拣取:");
    }


    public void execute(MapleClient client, String[] params) {
        MapleCharacter player = client.getPlayer();
        client.announceDisableServerMessage();
        player.dropMessage(5, "宠物全屏拣取: " + (player.getPetFullPick() ? "关闭" : "开启"));
        player.setPetFullPick(player.getPetFullPick() ? 0 : 1);
    }
}
