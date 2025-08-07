package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.quest.MapleQuest;

public class QuestStartCommand extends Command {
    {
        setDescription("启动指定任务。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length < 1){
            player.yellowMessage("语法：!startquest <questid>");
            return;
        }

        int questid = Integer.parseInt(params[0]);

        if (player.getQuestStatus(questid) == 0) {
            MapleQuest quest = MapleQuest.getInstance(questid);
            if (quest != null && quest.getNpcRequirement(false) != -1) {
                // 启动指定任务
                c.getAbstractPlayerInteraction().forceStartQuest(questid, quest.getNpcRequirement(false));
            } else {
                c.getAbstractPlayerInteraction().forceStartQuest(questid);
            }

            player.dropMessage(5, "任务 " + questid + " 已启动。");
        } else {
            player.dropMessage(5, "任务ID " + questid + " 已经启动/完成。");
        }
    }
}