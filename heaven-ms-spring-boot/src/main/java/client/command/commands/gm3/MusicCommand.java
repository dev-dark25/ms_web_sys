package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import constants.game.GameConstants;
import tools.MaplePacketCreator;

public class MusicCommand extends Command {
    {
        setDescription("播放指定的游戏音乐。");
    }

    private static String getSongList() {
        String songList = "音乐列表:\r\n";
        for (String s : GameConstants.GAME_SONGS) {
            songList += ("  " + s + "\r\n");
        }

        return songList;
    }

    @Override
    public void execute(MapleClient c, String[] params) {

        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            String sendMsg = "";

            sendMsg += "语法：#r!music <歌曲名称>#k\r\n\r\n";
            sendMsg += getSongList();

            c.announce(MaplePacketCreator.getNPCTalk(1052015, (byte) 0, sendMsg, "00 00", (byte) 0));
            return;
        }

        String song = player.getLastCommandMessage();
        for (String s : GameConstants.GAME_SONGS) {
            if (s.equalsIgnoreCase(song)) {
                player.getMap().broadcastMessage(MaplePacketCreator.musicChange(s));
                player.yellowMessage("现在播放歌曲 " + s + ".");
                return;
            }
        }

        String sendMsg = "";
        sendMsg += "未找到该歌曲，请在下面输入歌曲名称。\r\n\r\n";
        sendMsg += getSongList();

        c.announce(MaplePacketCreator.getNPCTalk(1052015, (byte) 0, sendMsg, "00 00", (byte) 0));
    }
}