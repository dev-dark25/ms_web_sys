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

public class ServerAddWorldCommand extends Command {
    {
        setDescription("创建大区");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        final MapleCharacter player = c.getPlayer();
        
        ThreadManager.getInstance().newTask(new Runnable() {
            @Override
            public void run() {
                int wid = Server.getInstance().addWorld();

                if(player.isLoggedinWorld()) {
                    if(wid >= 0) {
                        player.dropMessage(5, "大区 " + wid + " 已成功创建。");
                    } else {
                        if(wid == -2) {
                            player.dropMessage(5, "加载'world.ini'时发生错误，停止创建大区。");
                        } else {
                            player.dropMessage(5, "无法创建大区，请检查端口是否被占用或大区数量已达到限制。");
                        }
                    }
                }
            }
        });
    }
}
