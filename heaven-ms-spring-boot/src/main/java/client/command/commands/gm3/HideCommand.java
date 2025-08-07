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

import client.Skill;
import client.SkillFactory;
import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import constants.skills.SuperGM;
import server.MapleStatEffect;
import tools.Log;

/**
 * @author Auler
 */
public class HideCommand extends Command {
    {
        setDescription("隐身");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        //gmLevel > 2 才能使用隐身
        //TODO：不会收到真实的伤害，但是客户端还存在碰撞检测效果
        try {
            Skill skill = SkillFactory.getSkill(SuperGM.HIDE);
            if (skill == null) {
                System.out.println("HideCommand: 技能不存在！");
                return;
            }
            MapleStatEffect mapleStatEffect = skill.getEffect(skill.getMaxLevel());
            mapleStatEffect.applyTo(player);
        } catch (Exception e) {
            System.out.println("Error in HideCommand");
        }

    }
}
