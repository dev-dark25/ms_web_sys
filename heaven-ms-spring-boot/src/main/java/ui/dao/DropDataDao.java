package ui.dao;

import cn.nap.utils.common.NapMapUtils;
import cn.nap.utils.convert.NapCvtUtils;
import tools.MysqlConnection;
import ui.model.DropData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DropDataDao {
    static {
        if (!MysqlConnection.isInitialized()) {
            MysqlConnection.init();
        }
    }

    public static int selectCountByItemId(int itemId) {
        Map<String, Object> selectOne = MysqlConnection.selectOne("select count(*) as count from drop_data where itemid = ?", itemId);
        return NapMapUtils.getLong(selectOne, "count", 1L).intValue();
    }

    public static List<DropData> selectByItemId(int itemId, int pageNo, int pageSize) {
        List<Map<String, Object>> list = MysqlConnection.select("select * from drop_data where itemid = ? order by dropperid limit ? offset ?",
                itemId, pageSize, pageNo * pageSize);
        return list.stream().map(map -> NapCvtUtils.mapToEntity(map, DropData.class)).collect(Collectors.toList());
    }

    public static int selectCountByMobId(int mobId) {
        Map<String, Object> selectOne = MysqlConnection.selectOne("select count(*) as count from drop_data where dropperid = ?", mobId);
        return NapMapUtils.getLong(selectOne, "count", 1L).intValue();
    }

    public static List<DropData> selectByMobId(int mobId, int pageNo, int pageSize) {
        List<Map<String, Object>> list = MysqlConnection.select("select * from drop_data where dropperid = ? order by itemid  limit ? offset ?",
                mobId, pageSize, pageNo * pageSize);
        return list.stream().map(map -> NapCvtUtils.mapToEntity(map, DropData.class)).collect(Collectors.toList());
    }
}
