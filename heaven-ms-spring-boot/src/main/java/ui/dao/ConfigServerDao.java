package ui.dao;

import cn.nap.constant.NapExConst;
import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.common.NapMapUtils;
import cn.nap.utils.convert.NapCvtUtils;
import cn.nap.utils.exception.NapDbEx;
import tools.SqliteConnection;
import ui.model.ConfigServer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigServerDao {
    static {
        if (!SqliteConnection.isInitialized()) {
            SqliteConnection.init();
        }
    }

    public static List<ConfigServer> select() {
        List<Map<String, Object>> mapList = SqliteConnection.select("select * from config_server");
        return mapList.stream().map(map -> NapCvtUtils.mapToEntity(map, ConfigServer.class)).collect(Collectors.toList());
    }

    public static List<ConfigServer> selectPopular() {
        List<Map<String, Object>> mapList = SqliteConnection.select("select * from config_server where popular = 'true'");
        return mapList.stream().map(map -> NapCvtUtils.mapToEntity(map, ConfigServer.class)).collect(Collectors.toList());
    }

    public static void update(ConfigServer configServer) {
        if (configServer == null || configServer.getId() == null) {
            throw new NapDbEx(NapExConst.DbExType.DEFAULT.getCode(), "主键不能为空");
        }
        SqliteConnection.update("update config_server set config_data = ?, popular = ?, simple_name = ? where id = ?",
                configServer.getConfigData(), configServer.getPopular(), configServer.getSimpleName(), configServer.getId());
    }

    public static void updateList(List<ConfigServer> configServerList) {
        configServerList.forEach(ConfigServerDao::update);
    }

    public static int selectCount() {
        Map<String, Object> resultMap = SqliteConnection.selectOne("select count(1) as count from config_server");
        return NapMapUtils.getInteger(resultMap, "count", 0);
    }

    public static int selectUiCount(String text) {
        if (NapComUtils.isEmpty(text)) {
            return selectCount();
        }
        Map<String, Object> resultMap = SqliteConnection.selectOne("select count(1) as count from config_server where config_name like '%" + text + "%' or config_desc like '%" + text + "%'");
        return NapMapUtils.getInteger(resultMap, "count", 0);
    }

    public static List<ConfigServer> selectByPage(int pageNum, int pageSize) {
        List<Map<String, Object>> resultList = SqliteConnection.select("select * from config_server limit " + pageSize + " offset " + pageNum * pageSize);
        return resultList.stream().map(map -> NapCvtUtils.mapToEntity(map, ConfigServer.class)).collect(Collectors.toList());
    }

    public static List<ConfigServer> selectUiList(String text, int pageNum, int pageSize) {
        if (NapComUtils.isEmpty(text)) {
            return selectByPage(pageNum, pageSize);
        }
        List<Map<String, Object>> resultList = SqliteConnection.select("select * from config_server where config_name like '%" + text + "%' or config_desc like '%" + text + "%' limit " + pageSize + " offset " + pageNum * pageSize);
        return resultList.stream().map(map -> NapCvtUtils.mapToEntity(map, ConfigServer.class)).collect(Collectors.toList());
    }
}
