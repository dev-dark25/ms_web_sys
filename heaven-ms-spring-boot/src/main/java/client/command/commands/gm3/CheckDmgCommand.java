package client.command.commands.gm3;

import client.MapleBuffStat;
import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class CheckDmgCommand extends Command {
    {
        setDescription("检查玩家的最大基础伤害（在技能之前）。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim != null) {
            int maxBase = victim.calculateMaxBaseDamage(victim.getTotalWatk());
            Integer watkBuff = victim.getBuffedValue(MapleBuffStat.WATK);
            Integer matkBuff = victim.getBuffedValue(MapleBuffStat.MATK);
            Integer blessing = victim.getSkillLevel(10000000 * player.getJobType() + 12);
            if (watkBuff == null) watkBuff = 0;
            if (matkBuff == null) matkBuff = 0;

            player.dropMessage(5, "当前力量： " + victim.getTotalStr() + " 当前敏捷： " + victim.getTotalDex() + " 当前智力： " + victim.getTotalInt() + " 当前运气： " + victim.getTotalLuk());
            player.dropMessage(5, "当前物理攻击： " + victim.getTotalWatk() + " 当前魔法攻击： " + victim.getTotalMagic());
            player.dropMessage(5, "当前物理攻击增益： " + watkBuff + " 当前魔法攻击增益： " + matkBuff + " 当前祝福等级： " + blessing);
            player.dropMessage(5, victim.getName() + " 的最大基础伤害（在技能之前）是 " + maxBase);
        } else {
            player.message("在这个世界中找不到玩家 '" + params[0] + "'。");
        }
    }
}