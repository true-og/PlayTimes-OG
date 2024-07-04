package me.codedred.playtimes.data.database.datasource.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.datasource.DataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
public class MySQL implements DataSource {

  private final String connectionUrl;
  private final String user;
  private final String password;

  public MySQL(PlayTimes plugin) {
    var config = DataManager
      .getInstance()
      .getDBConfig()
      .getConfigurationSection("database-settings");
    String host = config.getString("host");
    String port = config.getString("port");
    this.user = config.getString("user");
    this.password = config.getString("password");
    String database = config.getString("database");
    boolean SSL = config.getBoolean("useSSL");

    this.connectionUrl =
      String.format(
        "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=" + SSL,
        host,
        port,
        database
      );

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      plugin.getLogger().info("Successfully connected to database.");
    } catch (ClassNotFoundException e) {
      plugin
        .getLogger()
        .severe("MySQL JDBC Driver not found: " + e.getMessage());
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(connectionUrl, user, password);
  }

  @Override
  public void closeConnection() throws SQLException {
    // connection.close();
  }
}
