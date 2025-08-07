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

import client.MapleCharacter;
import client.command.Command;
import client.MapleClient;
import net.server.Server;
import server.ThreadManager;

public class ServerRemoveWorldCommand extends Command {
    {
        setDescription("移除大区");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        final MapleCharacter player = c.getPlayer();
        
        final int rwid = Server.getInstance().getWorldsSize() - 1;
        if(rwid <= 0) {
            player.dropMessage(5, "无法移除大区 0。");
            return;
        }

        ThreadManager.getInstance().newTask(new Runnable() {
            @Override
            public void run() {
                if(Server.getInstance().removeWorld()) {
                    if(player.isLoggedinWorld()) {
                        player.dropMessage(5, "成功移除一个大区，目前大区数量: " + Server.getInstance().getWorldsSize() + "。");
                    }
                } else {
                    if(player.isLoggedinWorld()) {
                        if(rwid < 0) {
                            player.dropMessage(5, "没有上线的大区可以移除。");
                        } else {
                            player.dropMessage(5, "无法移除大区 " + rwid + "，请检查是否有玩家正在大区中。");
                        }
                    }
                }
            }
        });
    }
}
