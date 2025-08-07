package config;

import cn.nap.constant.NapPunc;
import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.convert.NapCvtUtils;
import cn.nap.utils.exception.NapEx;
import net.server.Server;
import net.server.world.World;
import tools.MysqlConnection;
import ui.constant.ConfigType;
import ui.constant.DataType;
import ui.model.ConfigServer;
import ui.util.ConfigServerResolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonConfig {
    public static CommonConfig config = loadConfig();
    public List<WorldConfig> worlds;
    public ServerConfig server;

    public static CommonConfig loadConfig() {
        CommonConfig commonConfig = new CommonConfig();
        List<ConfigServer> configList = ConfigServerResolver.getInstance().getConfigList();
        if (NapComUtils.isEmpty(configList)) {
            throw new NapEx("配置表数据为空");
        }
        Map<String, Map<String, Object>> worldConfigMap = new HashMap<>();
        Map<String, Object> serverConfigMap = new HashMap<>();
        for (ConfigServer configServer : configList) {
            if (NapPunc.ASTERISK.equals(configServer.getConfigWorld())) {
                serverConfigMap.put(configServer.getConfigName(), configServer.getConfigData());
            } else {
                if (worldConfigMap.containsKey(configServer.getConfigWorld())) {
                    worldConfigMap.get(configServer.getConfigWorld()).put(configServer.getConfigName(), configServer.getConfigData());
                } else {
                    Map<String, Object> tmp = new HashMap<>();
                    tmp.put(configServer.getConfigName(), configServer.getConfigData());
                    worldConfigMap.put(configServer.getConfigWorld(), tmp);
                }
            }
        }

        commonConfig.loadWorldConfig(worldConfigMap);
        commonConfig.loadServerConfig(serverConfigMap);
        return commonConfig;
    }

    public void loadWorldConfig(Map<String, Map<String, Object>> worldConfigMap) {
        List<WorldConfig> configServerList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : worldConfigMap.entrySet()) {
            WorldConfig worldConfig = NapCvtUtils.mapToEntity(entry.getValue(), WorldConfig.class, false);
            worldConfig.worldId = Integer.parseInt(entry.getKey());
            configServerList.add(worldConfig);
        }
        worlds = configServerList;
    }

    public void loadServerConfig(Map<String, Object> serverConfigMap) {
        server = NapCvtUtils.mapToEntity(serverConfigMap, ServerConfig.class, false);
    }

    public void reloadServerConfig(List<ConfigServer> configServers) throws Exception {
        for (ConfigServer configServer : configServers) {
            if (!NapPunc.ASTERISK.equals(configServer.getConfigWorld())) {
                int worldId = Integer.parseInt(configServer.getConfigWorld());
                WorldConfig worldConfig = worlds.get(worldId - 1);
                Field declaredField = worldConfig.getClass().getDeclaredField(configServer.getConfigName());
                declaredField.setAccessible(true);
                Object newValue = DataType.convertToDataTypeValue(configServer.getConfigDataType(), configServer.getConfigData());
                declaredField.set(worldConfig, newValue);
                if (configServer.getConfigName().contains("Rate")) {
                    reloadRates(worldId, configServer.getConfigName(), newValue);
                }
                continue;
            }
            Field declaredField = server.getClass().getDeclaredField(configServer.getConfigName());
            declaredField.setAccessible(true);
            declaredField.set(server, DataType.convertToDataTypeValue(configServer.getConfigDataType(), configServer.getConfigData()));
            if (ConfigType.DATABASE.getType().equals(configServer.getConfigType())) {
                MysqlConnection.init();
            }
        }
    }

    private void reloadRates(int worldId, String configName, Object newValue) {
        if (!Server.getInstance().isOnline()) {
            return;
        }
        World world = Server.getInstance().getWorlds().get(worldId - 1);
        switch (configName) {
            case "expRate":
                world.setExpRate((int) newValue);
                break;
            case "mesoRate":
                world.setMesoRate((int) newValue);
                break;
            case "dropRate":
                world.setDropRate((int) newValue);
                break;
            case "bossDropRate":
                world.setBossDropRate((int) newValue);
                break;
            case "questRate":
                world.setQuestRate((int) newValue);
                break;
            case "fishingRate":
                world.setFishingRate((int) newValue);
                break;
        }
    }
}
