package ui.dao;

import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.common.NapMapUtils;
import cn.nap.utils.convert.NapCvtUtils;
import tools.SqliteConnection;
import ui.model.GmCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GmCommandDao {
    static {
        if (!SqliteConnection.isInitialized()) {
            SqliteConnection.init();
        }
    }

    public static int selectCountByCondition(String searchText, int selectedIndex) {
        String sql = "select count(*) as count from config_gmcommand";
        List<String> conditions = new ArrayList<>();
        if (NapComUtils.isNotEmpty(searchText)) {
            conditions.add("command like '%" + searchText + "%' or description like '%" + searchText + "%'");
        }
        if (selectedIndex > -1) {
            conditions.add("gm_level = " + selectedIndex);
        }
        if (NapComUtils.isNotEmpty(conditions)) {
            sql += " where " + String.join(" and ", conditions);
        }
        Map<String, Object> selectOne = SqliteConnection.selectOne(sql);
        return NapMapUtils.getInteger(selectOne, "count", 0);
    }

    public static List<GmCommand> selectPageByCondition(int pageNo, int pageSize, String searchText, int selectedIndex) {
        String sql = "select * from config_gmcommand";
        List<String> conditions = new ArrayList<>();
        if (NapComUtils.isNotEmpty(searchText)) {
            conditions.add("(command like '%" + searchText + "%' or description like '%" + searchText + "%')");
        }
        if (selectedIndex > -1) {
            conditions.add("gm_level = " + selectedIndex);
        }
        if (NapComUtils.isNotEmpty(conditions)) {
            sql += " where " + String.join(" and ", conditions);
        }
        sql += " limit " + pageSize + " offset " + pageNo * pageSize;
        List<Map<String, Object>> selectList = SqliteConnection.select(sql);
        return selectList.stream().map(map -> NapCvtUtils.mapToEntity(map, GmCommand.class)).collect(Collectors.toList());
    }

    public static void updateList(List<GmCommand> gmCommands) {
        if (NapComUtils.isEmpty(gmCommands)) {
            return;
        }
        gmCommands.forEach(GmCommandDao::update);
    }

    public static void update(GmCommand gmCommand) {
        String sql = "update config_gmcommand set gm_level = ?, is_work = ? where id = ?";
        SqliteConnection.update(sql, gmCommand.getGmLevel(), String.valueOf(gmCommand.getIsWork()), gmCommand.getId());
    }

    public static void resetAll() {
        SqliteConnection.update("update config_gmcommand set is_work = 'true', gm_level = gm_level_primitive");
    }
}
