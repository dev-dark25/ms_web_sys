/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command.commands.gm6;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import tools.MaplePacketCreator;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WarpWorldCommand extends Command {
    {
        setDescription("迁移大区"); //warp个人理解成迁移，具体没看代码实现的功能，望后面的dalao若发现翻译错误能帮我指正
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!warpworld <大区id>");
            return;
        }

        Server server = Server.getInstance();
        byte worldb = Byte.parseByte(params[0]);
        if (worldb <= (server.getWorldsSize() - 1)) {
            try {
                String[] socket = server.getInetSocket(worldb, c.getChannel());
                c.getWorldServer().removePlayer(player);
                player.getMap().removePlayer(player);//你看不到2333 ><
                player.setSessionTransitionState();
                player.setWorld(worldb);
                player.saveCharToDB();//设置新大区 :O (要这样做不然会创造两个玩家实例，并且这两个实例会分布在两个大区中)
                c.announce(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
            } catch (UnknownHostException | NumberFormatException ex) {
                ex.printStackTrace();
                player.message("迁移大区时发生错误，请确认两个大区是否有相同数量的频道。");
            }

        } else {
            player.message("非法大区id，大区id最高是: " + (server.getWorldsSize() - 1));
        }
    }
}
