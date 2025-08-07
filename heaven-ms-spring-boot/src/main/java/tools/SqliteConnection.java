package tools;

import cn.nap.constant.NapDriverEnum;
import cn.nap.datasource.NapDbProp;
import cn.nap.datasource.NapDbSource;
import cn.nap.utils.common.NapComUtils;
import constants.net.ServerConstants;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SqliteConnection {
    public static final String SOURCE_SQLITE = "napMs_sqlite";
    private static AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * 初始化数据源
     */
    public static void init() {
        NapDbProp dbProp = new NapDbProp();
        dbProp.setUrl(NapDriverEnum.SQLITE.getPreJdbc() + ServerConstants.RESOURCE_DIR + "db/Server.db");
        dbProp.setDriver(NapDriverEnum.SQLITE);
        // 这里单位为秒
        dbProp.setConnectTimeout(10);
        dbProp.setExecuteTimeout(10);
        // 引入这个，为后续多数据源（sqlite）做准备
        NapDbSource.createInstance(SOURCE_SQLITE, dbProp);
        initialized.set(true);
    }

    /**
     * 查一条数据
     *
     * @param sql    sql
     * @param params 可选参数
     * @return 一行结果对应的map
     */
    public static Map<String, Object> selectOne(String sql, Object... params) {
        List<Map<String, Object>> selectList = NapDbSource.getInstance().select(SOURCE_SQLITE, sql, params);
        return NapComUtils.isEmpty(selectList) ? new HashMap<>() : selectList.get(0);
    }

    /**
     * 查多条数据
     *
     * @param sql    sql
     * @param params 可选参数
     * @return 多行结果对应的map
     */
    public static List<Map<String, Object>> select(String sql, Object... params) {
        List<Map<String, Object>> selectList = NapDbSource.getInstance().select(SOURCE_SQLITE, sql, params);
        return NapComUtils.isEmpty(selectList) ? new ArrayList<>() : selectList;
    }

    /**
     * 新增一条
     *
     * @param sql    sql
     * @param params 可选参数
     */
    public static void insert(String sql, Object... params) {
        NapDbSource.getInstance().update(SOURCE_SQLITE, sql, params);
    }

    /**
     * 新增多条
     *
     * @param sql    sql
     * @param params 可选参数
     */
    public static void batchInsert(String sql, Object[]... params) {
        NapDbSource.getInstance().batch(SOURCE_SQLITE, sql, params);
    }

    /**
     * 更新一条
     *
     * @param sql    sql
     * @param params 可选参数
     */
    public static void update(String sql, Object... params) {
        NapDbSource.getInstance().update(SOURCE_SQLITE, sql, params);
    }

    /**
     * 删除一条
     *
     * @param sql    sql
     * @param params 可选参数
     */
    public static void delete(String sql, Object... params) {
        NapDbSource.getInstance().update(SOURCE_SQLITE, sql, params);
    }

    /**
     * 删除多条
     *
     * @param sql    sql
     * @param params 可选参数
     */
    public static void batchDelete(String sql, Object[]... params) {
        NapDbSource.getInstance().batch(SOURCE_SQLITE, sql, params);
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public static Connection getConnection() {
        return NapDbSource.getInstance().getConnection(SOURCE_SQLITE);
    }

    /**
     * 自动获取连接并释放
     *
     * @param consumer 获取连接后执行的语句
     */
    public static void getConnectionAndFree(Consumer<Connection> consumer) {
        Connection conn = getConnection();
        consumer.accept(conn);
        freeConnection(conn);
    }

    /**
     * 自动获取连接并关闭
     *
     * @param consumer 获取连接后执行的语句
     */
    public static void getConnectionAndClose(Consumer<Connection> consumer) {
        Connection conn = getConnection();
        consumer.accept(conn);
        closeConnection(conn);
    }

    /**
     * 释放连接
     *
     * @param connections 可选释放多个连接
     */
    public static void freeConnection(Connection... connections) {
        if (null == connections) {
            return;
        }
        for (Connection connection : connections) {
            NapDbSource.getInstance().freeConnection(connection);
        }
    }

    /**
     * 关闭连接
     *
     * @param connections 关闭多个或者关闭所有
     */
    public static void closeConnection(Connection... connections) {
        if (null == connections || connections.length == 0) {
            NapDbSource.getInstance().close(SOURCE_SQLITE);
            return;
        }
        for (Connection connection : connections) {
            NapDbSource.getInstance().close(connection);
        }
    }

    public static boolean isInitialized() {
        return initialized.get();
    }
}
