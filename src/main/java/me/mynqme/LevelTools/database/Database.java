package me.mynqme.LevelTools.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import me.mynqme.LevelTools.LevelTools;
public class Database {
    private static Database inst;
    private final ConnectionPoolManager pool;
    private final LevelTools instance;
    public Database(LevelTools plugin) {
        this.instance = plugin;
        inst = this;
        pool = new ConnectionPoolManager(instance);
        createTable();
    }

    public void createTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `leveltools` (`uuid` VARCHAR(36) NOT NULL, `blocks` VARCHAR(255) NOT NULL DEFAULT '0', `level` VARCHAR(255) NOT NULL DEFAULT '0', `xp` VARCHAR(255) NOT NULL DEFAULT '0', PRIMARY KEY (`uuid`));");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void fetchPlayer(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM `leveltools` WHERE `uuid` = ?;");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                instance.xpMap.put(uuid, rs.getInt("xp"));
                instance.levelMap.put(uuid, rs.getInt("level"));
                instance.blockMap.put(uuid, rs.getInt("blocks"));
            } else {
                instance.xpMap.put(uuid, 0);
                instance.levelMap.put(uuid, 0);
                instance.blockMap.put(uuid, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public void savePlayer(UUID uuid, int xp, int level, int blocks) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO `leveltools` (`uuid`, `blocks`, `level`, `xp`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `blocks` = ?, `level` = ?, `xp` = ?;");
            ps.setString(1, uuid.toString());
            ps.setInt(2, blocks);
            ps.setInt(3, level);
            ps.setInt(4, xp);
            ps.setInt(5, blocks);
            ps.setInt(6, level);
            ps.setInt(7, xp);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void closeConnections() {
        pool.closePool();
    }

    public static Database inst() {
        return inst;
    }
}
