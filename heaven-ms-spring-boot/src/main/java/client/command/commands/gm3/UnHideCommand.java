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
package client.command.commands.gm3;

import client.SkillFactory;
import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import constants.skills.GM;
import constants.skills.SuperGM;
import tools.Log;

public class UnHideCommand extends Command {
    {
        setDescription("½â³ýÒþÉí");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        try{
            SkillFactory.getSkill(SuperGM.HIDE).getEffect(SkillFactory.getSkill(SuperGM.HIDE).getMaxLevel()).applyTo(player);
        }catch(Exception e){
            System.out.println("Error in UnHideCommand");
        }

    }
}
