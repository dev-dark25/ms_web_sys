package client.command;

import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.convert.NapCvtUtils;
import tools.SqliteConnection;
import ui.model.GmCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandResolver {
    private static CommandResolver resolver;

    public static CommandResolver getInstance() {
        if (null == resolver) {
            resolver = new CommandResolver();
            SqliteConnection.init();
        }
        return resolver;
    }

    public List<GmCommand> getgmCommandList() {
        List<GmCommand> dataList = new ArrayList<>();
        List<Map<String, Object>> selectList = SqliteConnection.select("select * from config_gmcommand;");
        selectList.forEach(map -> {
            GmCommand gmCommand = NapCvtUtils.mapToEntity(map, GmCommand.class);
            dataList.add(gmCommand);
        });
        return dataList;
    }

    public void savegmCommandList(List<GmCommand> gmCommands) {
        if (NapComUtils.isEmpty(gmCommands)) {
            return;
        }
        for (GmCommand gmCommand : gmCommands) {
            Object[] param = new Object[4];
            param[0] = gmCommand.getCommand();
            param[1] = gmCommand.getGmLevel();
            param[2] = gmCommand.getDescription();
            param[3] = gmCommand.getId();
            SqliteConnection.update("update config_gmcommand set command = ?, gm_level = ?, description = ? where id = ?",
                    param);
        }
    }
}
