package client.command.commands.gm7;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;

public class ShowPlayersCommand extends Command {
    {
        setDescription("show player");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length > 1) {
            player.yellowMessage(" ‰»Î: @showplayers <map/channel/world>");
            return;
        }

        String type;
        if (params.length < 1) {
            type = "map";
        } else {
            type = params[0];
        }

        StringBuffer players = new StringBuffer();
        if ("map".equals(type)) {
            System.out.println("ShowPlayersCommand " + type);
            for (MapleCharacter chr : player.getMap().getAllPlayers()) {
                players.append("charaterId: ").append(chr.getId()).append(",").append(chr.getName()).append("\r\n");
            }
        } else if ("channel".equals(type)) {
            System.out.println("ShowPlayersCommand " + type);
            for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                players.append("charaterId: ").append(chr.getId()).append(",").append(chr.getName()).append("\r\n");
            }
        } else if ("world".equals(type)) {
            System.out.println("ShowPlayersCommand " + type);
            for (Channel channel : Server.getInstance().getAllChannels()) {
                for (MapleCharacter chr : channel.getPlayerStorage().getAllCharacters()) {
                    players.append("charaterId: ").append(chr.getId()).append(",").append(chr.getName()).append("\r\n");
                }
            }
        } else {
            return;
        }

        player.showHint(players.toString(), 200);
    }
}