/*
 * Copyright (c) 2018年01月11日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.services;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import threadmonitor.db.SqliteHelper;
import threadmonitor.entry.Command;
import threadmonitor.entry.Progress;
import threadmonitor.entry.SSHConnInfo;
import threadmonitor.util.JsonUtil;
import threadmonitor.util.Utils;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/11
 * @Version 1.0.0
 */
public class DbService {

    private SqliteHelper sqliteHelper;
    private String dbFilePath = "db/ThreadMonitor.db";

    public DbService() {
        try {
            sqliteHelper = new SqliteHelper(dbFilePath);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 创建数据库
     */
    public static void main(String[] args) {
        createDB();
    }

    public synchronized static DbService createDB() {
        return new DbService();
    }

    public synchronized void insertProsess(ObservableList<Progress> progresses ,String ip) {
        try {
            String sql = "INSERT INTO prosessTable (time, ip, value) VALUES ('" + Utils
                    .dateToString(new Date()) + "','" + ip + "','" + JsonUtil.serialize(progresses) + "')";
            sqliteHelper.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<Map<String, Progress>> getRecentRecord(String ip) {
        String sql = "SELECT * FROM prosessTable WHERE ip = '" + ip + "' ORDER BY time DESC LIMIT 8";
        try {
            List<Map<String, Progress>> b = sqliteHelper.executeQuery(sql, (resultSet, i) -> {
                HashMap<String, Progress> mapResult = new HashMap<>();
                String json = resultSet.getString(4);
                Progress[] jsonList = JsonUtil.fromJson(json, Progress[].class);
                for (Progress p : jsonList) {
                    mapResult.put(p.getPidColumn(), p);
                }
                return mapResult;
            });
            return b;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void insertCommand(String cmdStr, String cmdDesc ,String ip) {
        try {
            String sql =
                    "INSERT INTO commandTabel (ip ,command, command_desc) VALUES ('" + ip + "','" + cmdStr + "','" + cmdDesc + "');";
            sqliteHelper.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Command> queryAllCommand(String ip) {
        String sql = "SELECT * FROM commandTabel WHERE ip = '" + ip + "';";
        try {
            List<Command> commands = sqliteHelper.executeQuery(sql, (resultSet, rowNum) -> {
                Integer id = resultSet.getInt(1);
                String command = resultSet.getString(3);
                String desc = resultSet.getString(4);
                return new Command(id, command, desc);
            });
            return commands;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public List<SSHConnInfo> queryAllConnect() {
        String sql = "SELECT * FROM connectInfoTabel;";
        try {
            List<SSHConnInfo> connInfos = sqliteHelper.executeQuery(sql,(resultSet,rowNum) -> {
                Integer id = resultSet.getInt(1);
                String ip = resultSet.getString(2);
                String username = resultSet.getString(3);
                String password = resultSet.getString(4);
                String desc = resultSet.getString(5);
                return new SSHConnInfo(id,ip,username,password,desc);
            });
            return connInfos;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public void delCommand(Integer id) {
        try {
            String sql = "DELETE FROM commandTabel WHERE id = " + id;
            sqliteHelper.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insertConnect(SSHConnInfo sshConnInfo) {
        try {
            String sql = "INSERT INTO connectInfoTabel (ip, username, password, desc) VALUES ('"
                    + sshConnInfo.getConnectIp() +"','"
                    + sshConnInfo.getConnectUser() + "','"
                    + sshConnInfo.getConnectPwd() + "','"
                    + sshConnInfo.getConnectDesc() + "');";
            sqliteHelper.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
