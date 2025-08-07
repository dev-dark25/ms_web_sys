package ui.dao;

import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.common.NapMapUtils;
import cn.nap.utils.convert.NapCvtUtils;
import cn.nap.utils.exception.NapDbEx;
import server.cashshop.CashShop;
import tools.MysqlConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CashItemDao {
    static {
        if (!MysqlConnection.isInitialized()) {
            MysqlConnection.init();
        }
    }

    public static long selectCountByCondition(String searchText) {
        String sql = "select count(*) as count from cash_item";
        List<String> conditions = new ArrayList<>();
        if (NapComUtils.isNotEmpty(searchText)) {
            conditions.add("sn like '%" + searchText + "%' or item_id like '%" + searchText + "%'");
        }
        if (NapComUtils.isNotEmpty(conditions)) {
            sql += " where " + String.join(" and ", conditions);
        }
        Map<String, Object> selectOne = MysqlConnection.selectOne(sql);
        return NapMapUtils.getLong(selectOne, "count", 0L);
    }

    public static List<CashShop.CashItem> selectPageByCondition(int pageNo, int pageSize, String searchText) {
        String sql = "select * from cash_item";
        List<String> conditions = new ArrayList<>();
        if (NapComUtils.isNotEmpty(searchText)) {
            conditions.add("sn like '%" + searchText + "%' or item_id like '%" + searchText + "%'");
        }
        if (NapComUtils.isNotEmpty(conditions)) {
            sql += " where " + String.join(" and ", conditions);
        }
        sql += " limit " + pageSize + " offset " + pageNo * pageSize;
        List<Map<String, Object>> selectList = MysqlConnection.select(sql);
        return selectList.stream().map(map -> NapCvtUtils.mapToEntity(map, CashShop.CashItem.class)).collect(Collectors.toList());
    }

    public static void insert(List<CashShop.CashItem> cashItemList) {
        try (Connection connection = MysqlConnection.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into cash_item " +
                    "(sn, item_id, count, price, priority, period, gender, on_sale) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?)");
            for (CashShop.CashItem cashItem : cashItemList) {
                preparedStatement.setInt(1, cashItem.getSn());
                preparedStatement.setInt(2, cashItem.getItemId());
                preparedStatement.setShort(3, cashItem.getCount());
                preparedStatement.setInt(4, cashItem.getPrice());
                preparedStatement.setInt(5, cashItem.getPriority());
                preparedStatement.setLong(6, cashItem.getPeriod());
                preparedStatement.setInt(7, cashItem.getGender());
                preparedStatement.setInt(8, cashItem.isOnSale() ? 1 : 0);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
            preparedStatement.close();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
    }

    public static void deleteAll() {
        try (Connection connection = MysqlConnection.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("truncate cash_item");
            preparedStatement.execute();
            connection.commit();
            preparedStatement.close();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
    }

    public static List<CashShop.CashItem> selectAllSale() {
        String sql = "select * from cash_item where on_sale = 1";
        List<Map<String, Object>> selectList = MysqlConnection.select(sql);
        return selectList.stream().map(map -> NapCvtUtils.mapToEntity(map, CashShop.CashItem.class)).collect(Collectors.toList());
    }

    public static void updateList(List<CashShop.CashItem> itemList) {
        if (NapComUtils.isEmpty(itemList)) {
            return;
        }
        itemList.forEach(CashItemDao::update);
    }

    public static void update(CashShop.CashItem cashItem) {
        String sql = "update cash_item set count = ?, price = ?, priority = ?, period = ?, on_sale = ? where sn = ?";
        MysqlConnection.update(sql, cashItem.getCount(), String.valueOf(cashItem.getPrice()), cashItem.getPriority(),
                cashItem.getPeriod(), cashItem.getOnSale(), cashItem.getSn());
    }
}
