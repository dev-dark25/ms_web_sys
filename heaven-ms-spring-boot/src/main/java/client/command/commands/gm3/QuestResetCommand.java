package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.quest.MapleQuest;

public class QuestResetCommand extends Command {
    {
        setDescription("重置指定任务的进度。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length < 1){
            player.yellowMessage("语法：!resetquest <questid>");
            return;
        }

        int questid_ = Integer.parseInt(params[0]);

        if (player.getQuestStatus(questid_) != 0) {
            MapleQuest quest = MapleQuest.getInstance(questid_);
            if (quest != null) {
                // 重置指定任务的进度
                quest.reset(player);
                player.dropMessage(5, "任务 " + questid_ + " 已重置。");
            } else {    // 不应该出现的情况
                player.dropMessage(5, "任务ID " + questid_ + " 无效。");
            }
        }
    }
}