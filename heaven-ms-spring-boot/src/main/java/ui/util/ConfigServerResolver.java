package ui.util;

import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.convert.NapCvtUtils;
import constants.ui.ConfigEnum;
import constants.ui.DataTypeEnum;
import tools.SqliteConnection;
import ui.model.ConfigServer;
import ui.model.GmCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigServerResolver {
    private static ConfigServerResolver resolver;

    public static ConfigServerResolver getInstance() {
        if (null == resolver) {
            resolver = new ConfigServerResolver();
            SqliteConnection.init();
        }
        return resolver;
    }

    public List<ConfigServer> getUiConfigList() {
        List<ConfigServer> dataList = new ArrayList<>();
        List<Map<String, Object>> selectList = SqliteConnection.select("select * from config_server;");
        selectList.forEach(map -> {
            ConfigServer configServer = NapCvtUtils.mapToEntity(map, ConfigServer.class);
            configServer.setConfigWorld("*".equals(configServer.getConfigWorld()) ? "全局" : "大区" + configServer.getConfigWorld());
            configServer.setConfigType(ConfigEnum.ofType(configServer.getConfigType()));
            configServer.setConfigDataType(DataTypeEnum.of(configServer.getConfigDataType()).getDesc());
            dataList.add(configServer);
        });
        return dataList;
    }

    public List<ConfigServer> getConfigList() {
        List<ConfigServer> dataList = new ArrayList<>();
        List<Map<String, Object>> selectList = SqliteConnection.select("select * from config_server;");
        selectList.forEach(map -> {
            ConfigServer configServer = NapCvtUtils.mapToEntity(map, ConfigServer.class);
            dataList.add(configServer);
        });
        return dataList;
    }

    public void saveConfigList(List<ConfigServer> configServers) {
        if (NapComUtils.isEmpty(configServers)) {
            return;
        }
        for (ConfigServer configServer : configServers) {
            Object[] param = new Object[3];
            param[0] = configServer.getConfigData();
            param[1] = configServer.getConfigDesc();
            param[2] = configServer.getId();
            SqliteConnection.update("update config_server set config_data = ?, config_desc = ? where id = ?",
                    param);
        }
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
