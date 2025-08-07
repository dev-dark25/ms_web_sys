package client.command.commands.gm7;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;

/**
 * 执行"APCommand"操作的GM命令
 */
public class UpdateAPCommand extends Command {
    {
        setDescription("修改指定角色AP。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
//        c.getWorldServer();     //当前角色世界
//        c.getChannelServer();   //当前角色频道
        MapleCharacter player = c.getPlayer();

        int characterId, count;
        try {
            characterId = Integer.parseInt(params[0]);
            count = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            player.dropMessage("请输入正确的数字");
            return;
        }

        MapleCharacter targetCharacter = null;
//        for (Channel channel : Server.getInstance().getAllChannels()) {
//            for (MapleCharacter character : channel.getPlayerStorage().getAllCharacters()) {
//                if (character.getId() == characterId) {
//                    targetCharacter = character;
//                }
//            }
//        }
        targetCharacter = c.getWorldServer().getPlayerStorage().getCharacterById(characterId);
        if (targetCharacter != null) {
//            targetCharacter.gainAp(count, true);
            targetCharacter.changeRemainingAp(count, false);
            targetCharacter.yellowMessage("管理员修改了你的AP：" + count);
        } else {
            player.message("玩家 '" + params[0] + "' 不存在");
        }
    }
}