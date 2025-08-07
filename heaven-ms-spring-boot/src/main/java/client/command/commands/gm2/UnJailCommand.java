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
package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class UnJailCommand extends Command {
    {
        setDescription("释放监狱玩家");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!unjail <玩家名称>");
            return;
        }

        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim != null) {
            if (victim.getJailExpirationTimeLeft() <= 0) {
                player.message("该玩家已被释放...");
                return;
            }
            victim.removeJailExpirationTime();
            victim.message("由于缺乏具体的证据，你现在是清白的,享受自由吧，请自行走出监狱!");
            player.message(victim.getName() + " 玩家已被释放...");
        } else {
            player.message("玩家 '" + params[0] + "' 不存在");
        }
    }
}
