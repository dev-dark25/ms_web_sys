package ui.dao;

import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.common.NapMapUtils;
import cn.nap.utils.convert.NapCvtUtils;
import cn.nap.utils.exception.NapDbEx;
import tools.MysqlConnection;
import ui.model.KookMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KookMessageDao {
    static {
        if (!MysqlConnection.isInitialized()) {
            MysqlConnection.init();
        }
    }

    public static List<KookMessage> selectByCondition(KookMessage kookMessage) {
        return selectPageByCondition(kookMessage, null, null);
    }

    public static List<KookMessage> selectPageByCondition(KookMessage kookMessage, Integer pageNo, Integer pageSize) {
        try (Connection connection = MysqlConnection.getConnection()) {
            String sql = "select * from kook_message";
            List<String> conditions = new ArrayList<>();
            if (kookMessage != null) {
                if (kookMessage.getId() != null) {
                    conditions.add("id = " + kookMessage.getId());
                }
                if (kookMessage.getChannelId() != null) {
                    conditions.add("channel_id = '" + kookMessage.getChannelId() + "'");
                }
                if (kookMessage.getAuthorId() != null) {
                    conditions.add("author_id = '" + kookMessage.getAuthorId() + "'");
                }
                if (kookMessage.getReceiveMessageId() != null) {
                    conditions.add("receive_message_id = '" + kookMessage.getReceiveMessageId() + "'");
                }
                if (kookMessage.getReceiveContent() != null) {
                    conditions.add("receive_content like '%" + kookMessage.getReceiveContent() + "%'");
                }
                if (kookMessage.getReceiveTimestamp() != null) {
                    conditions.add("receive_timestamp <= " + kookMessage.getReceiveTimestamp());
                }
                if (kookMessage.getReplyMessageId() != null) {
                    conditions.add("reply_message_id = '" + kookMessage.getReplyMessageId() + "'");
                }
                if (kookMessage.getReplyContent() != null) {
                    conditions.add("reply_content like '%" + kookMessage.getReplyContent() + "%'");
                }
                if (kookMessage.getReplyTimestamp() != null) {
                    conditions.add("reply_timestamp <= " + kookMessage.getReplyTimestamp());
                }
                if (kookMessage.getBindCode() != null) {
                    conditions.add("bind_code = '" + kookMessage.getBindCode() + "'");
                }
                if (kookMessage.getAccountId() != null) {
                    conditions.add("account_id = " + kookMessage.getAccountId());
                }
            }
            if (NapComUtils.isNotEmpty(conditions)) {
                sql += " where " + String.join(" and ", conditions);
            }
            if (pageNo != null && pageSize != null) {
                sql += " limit " + pageSize + " offset " + (pageNo * pageSize);
            }
            return doSelect(connection, sql);
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
    }

    public static List<KookMessage> selectBindCode(String authorId, String bindCode) {
        try (Connection connection = MysqlConnection.getConnection()) {
            String sql = "select * from kook_message";
            List<String> conditions = new ArrayList<>();
            if (NapComUtils.isNotEmpty(authorId)) {
                conditions.add("author_id = '" + authorId + "'");
            }
            if (bindCode == null) {
                conditions.add("bind_code is not null");
            } else {
                conditions.add("bind_code = '" + bindCode + "'");
            }
            if (NapComUtils.isNotEmpty(conditions)) {
                sql += " where " + String.join(" and ", conditions);
            }
            return doSelect(connection, sql);
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
    }

    public static int selectCountByUiCondition(String input, Boolean isBind) {
        try (Connection connection = MysqlConnection.getConnection()) {
            String sql = "select count(*) as count from kook_message";
            List<String> conditions = new ArrayList<>();
            if (NapComUtils.isNotEmpty(input)) {
                conditions.add("account_id like '%" + input + "%'");
            }
            if (isBind != null) {
                conditions.add(isBind ? "account_id is not null" : "account_id is null");
            }
            if (NapComUtils.isNotEmpty(conditions)) {
                sql += " where " + String.join(" and ", conditions);
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("count");
            }
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
        return 0;
    }

    public static List<KookMessage> selectByUiCondition(String input, Boolean isBind, int pageNo, int pageSize) {
        try (Connection connection = MysqlConnection.getConnection()) {
            String sql = "select * from kook_message";
            List<String> conditions = new ArrayList<>();
            if (NapComUtils.isNotEmpty(input)) {
                conditions.add("account_id like '%" + input + "%'");
            }
            if (isBind != null) {
                conditions.add(isBind ? "account_id is not null" : "account_id is null");
            }
            if (NapComUtils.isNotEmpty(conditions)) {
                sql += " where " + String.join(" and ", conditions);
            }
            sql += " limit " + pageSize + " offset " + (pageNo * pageSize);
            return doSelect(connection, sql);
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
    }

    private static List<KookMessage> doSelect(Connection connection, String sql) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<KookMessage> resultList = new ArrayList<>();
        while (resultSet.next()) {
            KookMessage result = new KookMessage();
            result.setId(resultSet.getLong("id"));
            result.setChannelId(resultSet.getString("channel_id"));
            result.setAuthorId(resultSet.getString("author_id"));
            result.setReceiveMessageId(resultSet.getString("receive_message_id"));
            result.setReceiveContent(resultSet.getString("receive_content"));
            result.setReceiveTimestamp(resultSet.getLong("receive_timestamp"));
            result.setReplyMessageId(resultSet.getString("reply_message_id"));
            result.setReplyContent(resultSet.getString("reply_content"));
            result.setReplyTimestamp(resultSet.getLong("reply_timestamp"));
            result.setBindCode(resultSet.getString("bind_code"));
            result.setAccountId(resultSet.getInt("account_id"));
            resultList.add(result);
        }
        resultSet.close();
        preparedStatement.close();
        return resultList;
    }

    public static void insert(KookMessage kookMessage) {
        insertList(Collections.singletonList(kookMessage));
    }

    public static void insertList(List<KookMessage> kookMessageList) {
        try (Connection connection = MysqlConnection.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into kook_message " +
                    "(channel_id, author_id, receive_message_id, receive_content, receive_timestamp, reply_message_id, reply_content, reply_timestamp, bind_code) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (KookMessage kookMessage : kookMessageList) {
                preparedStatement.setString(1, kookMessage.getChannelId());
                preparedStatement.setString(2, kookMessage.getAuthorId());
                preparedStatement.setString(3, kookMessage.getReceiveMessageId());
                preparedStatement.setString(4, kookMessage.getReceiveContent());
                preparedStatement.setLong(5, kookMessage.getReceiveTimestamp());
                preparedStatement.setString(6, kookMessage.getReplyMessageId());
                preparedStatement.setString(7, kookMessage.getReplyContent());
                preparedStatement.setLong(8, kookMessage.getReplyTimestamp());
                preparedStatement.setString(9, kookMessage.getBindCode());
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

    public static List<KookMessage> selectBindResult(Integer accountId) {
        try (Connection connection = MysqlConnection.getConnection()) {
            String sql = "select * from kook_message where bind_code is not null and account_id = " + accountId;
            return doSelect(connection, sql);
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
    }

    public static void update(KookMessage kookMessage) {
        try (Connection connection = MysqlConnection.getConnection()) {
            List<String> conditions = new ArrayList<>();
            if (kookMessage.getChannelId() != null) {
                conditions.add("channel_id = '" + kookMessage.getChannelId() + "'");
            }
            if (kookMessage.getAuthorId() != null) {
                conditions.add("author_id = '" + kookMessage.getAuthorId() + "'");
            }
            if (kookMessage.getReceiveMessageId() != null) {
                conditions.add("receive_message_id = '" + kookMessage.getReceiveMessageId() + "'");
            }
            if (kookMessage.getReceiveContent() != null) {
                conditions.add("receive_content = '" + kookMessage.getReceiveContent() + "'");
            }
            if (kookMessage.getReceiveTimestamp() != null) {
                conditions.add("receive_timestamp = " + kookMessage.getReceiveTimestamp());
            }
            if (kookMessage.getReplyMessageId() != null) {
                conditions.add("reply_message_id = '" + kookMessage.getReplyMessageId() + "'");
            }
            if (kookMessage.getReplyContent() != null) {
                conditions.add("reply_content = '" + kookMessage.getReplyContent() + "'");
            }
            if (kookMessage.getReplyTimestamp() != null) {
                conditions.add("reply_timestamp = " + kookMessage.getReplyTimestamp());
            }
            if (kookMessage.getBindCode() != null) {
                conditions.add("bind_code = '" + kookMessage.getBindCode() + "'");
            }
            if (kookMessage.getAccountId() != null) {
                conditions.add("account_id = " + kookMessage.getAccountId());
            }
            if (NapComUtils.isEmpty(conditions)) {
                return;
            }
            String sql = "update kook_message set " + String.join(", ", conditions) + " where id = " + kookMessage.getId();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            throw new NapDbEx(e.getMessage());
        }
    }

    public static long selectCountByCondition(int selectedIndex) {
        String sql = "select count(*) as count from kook_message";
        if (selectedIndex == 0) {
            sql += " where account_id is null";
        } else if (selectedIndex == 1) {
            sql += " where account_id is not null";
        }
        Map<String, Object> selectOne = MysqlConnection.selectOne(sql);
        return NapMapUtils.getLong(selectOne, "count", 0L);
    }

    public static List<KookMessage> selectPageByCondition(int pageNo, int pageSize, int selectedIndex) {
        String sql = "select * from kook_message";
        if (selectedIndex == 0) {
            sql += " where account_id is null";
        } else if (selectedIndex == 1) {
            sql += " where account_id is not null";
        }
        sql += " limit " + pageSize + " offset " + pageNo * pageSize;
        List<Map<String, Object>> selectList = MysqlConnection.select(sql);
        return selectList.stream().map(map -> NapCvtUtils.mapToEntity(map, KookMessage.class)).collect(Collectors.toList());
    }
}
