package tools.sql;

import cn.nap.constant.NapPunc;
import cn.nap.datasource.NapDbSource;
import cn.nap.utils.common.NapComUtils;
import constants.net.ServerConstants;
import tools.Log;
import tools.MysqlConnection;
import tools.SqliteConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

public class SqlOperator {

    public static boolean execute(String datasource, String sqlFile) {
        File file = new File(sqlFile);
        // 如果是放在resources里面可以省略全路径，不是则要指定全路径
        if (!file.exists()) {
            file = new File(ServerConstants.RESOURCE_DIR + sqlFile);
        }
        if (!file.exists()) {
            System.out.println("文件不存在");
            return false;
        }
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader bis = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(bis)) {
            String line;
            boolean startComment = false;
            StringBuilder sqlBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // 去除空字符
                line = line.trim();
                // 空行跳过
                if (NapComUtils.isEmpty(line)) {
                    continue;
                }
                // 是否注释行
                if (line.startsWith("--") || line.startsWith("//") || line.startsWith("#")) {
                    continue;
                }
                if (!startComment && line.startsWith("/*")) {
                    if (line.endsWith("*/")) {
                        continue;
                    }
                    startComment = true;
                    continue;
                }
                if (startComment) {
                    if (line.endsWith("*/")) {
                        startComment = false;
                    }
                    continue;
                }
                if (line.endsWith(";")) {
                    sqlBuilder.append(line);
                    // 执行sql
                    if (!executeSql(datasource, sqlBuilder.toString())) {
                        return false;
                    }
                    // 清空sql
                    sqlBuilder.delete(0, sqlBuilder.length());
                    continue;
                }
                sqlBuilder.append(line).append(NapPunc.SPACE);
            }
            return true;
        } catch (Exception e) {
            System.out.println("解析sql文件失败");
        }
        return false;
    }

    public static boolean executeSql(String datasource, String sql) {
        try {
            Connection connection;
            // 默认都是mysql数据源
            if (SqliteConnection.SOURCE_SQLITE.equals(datasource)) {
                connection = SqliteConnection.getConnection();
            } else {
                connection = MysqlConnection.getConnection();
            }
            if (sql.startsWith("select") || sql.startsWith("SELECT")) {
                NapDbSource.getInstance().select(datasource, connection, sql);
            } else {
                NapDbSource.getInstance().update(datasource, connection, sql);
            }
            connection.close();
            return true;
        } catch (Exception e) {
            System.out.println("脚本 " + sql + " 执行失败");
        }
        return false;
    }
}
