package me.mynqme.LevelTools.database;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.ResultSet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.mynqme.LevelTools.LevelTools;

public class ConnectionPoolManager {

    private final LevelTools instance;
    private HikariDataSource dataSource;

    public ConnectionPoolManager(LevelTools plugin) {
        this.instance = plugin;
        setupPool();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + instance.config.getString("database.host") + "/" + instance.config.getString("database.database"));
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(instance.config.getString("database.user"));
        config.setPassword(instance.config.getString("database.pass"));
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(30);
        config.setConnectionTimeout(300);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close(Connection conn, java.sql.PreparedStatement ps, ResultSet res) {
        if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        if (res != null) try { res.close(); } catch (SQLException ignored) {}
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}