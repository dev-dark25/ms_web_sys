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

public class SetGmLevelCommand extends Command {
    {
        setDescription("设置GM等级");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("语法：!setgmlevel <玩家昵称> <GM等级>");
            return;
        }

        int newLevel = Integer.parseInt(params[1]);
        MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
        if (target != null) {
            target.setGMLevel(newLevel);
            target.getClient().setGMLevel(newLevel);

            target.dropMessage("您现在是" + newLevel + "级GM，输入 @commands 命令查询可使用的命令。");
            player.dropMessage(target + " 现在是" + newLevel + "级GM。");
        } else {
            player.dropMessage("玩家 '" + params[0] + "' 不在这个频道。");
        }
    }
}
