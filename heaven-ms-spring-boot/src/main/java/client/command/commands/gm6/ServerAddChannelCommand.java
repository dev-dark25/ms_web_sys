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

public class ServerAddChannelCommand extends Command {
    {
        setDescription("添加频道");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        final MapleCharacter player = c.getPlayer();
        
        if (params.length < 1) {
            player.dropMessage(5, "语法：@addchannel <大区id>");
            return;
        }

        final int worldid = Integer.parseInt(params[0]);

        ThreadManager.getInstance().newTask(new Runnable() {
            @Override
            public void run() {
                int chid = Server.getInstance().addChannel(worldid);
                if(player.isLoggedinWorld()) {
                    if(chid >= 0) {
                        player.dropMessage(5, "新频道 " + chid + " 成功添加到大区 " + worldid);
                    } else {
                        if(chid == -3) {
                            player.dropMessage(5, "无效的大区id，频道创建已被禁止");
                        } else if(chid == -2) {
                            player.dropMessage(5, "频道已达大区 " + worldid + " 的上限，禁止继续创建频道");
                        } else if(chid == -1) {
                            player.dropMessage(5, "加载大区文件 'world.ini' 失败，频道创建已被禁止");
                        } else {
                            player.dropMessage(5, "新频道部署失败，请检查端口是否被占用");
                        }
                    }
                }
            }
        });
    }
}
