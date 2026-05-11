package com.restaurantdeliverysystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A utility class for connecting to the MySql database.
 * Hardcoded connection for simplicity.
 */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/food_delivery?useSSL=false&serverTimezone=UTC"; // MySql URL with database name and timezone settings
    private static final String USER = "root"; // MySql username
    private static final String PASS = "yournewpassword"; // MySql password

    private static Connection connection;

    /** Makes a connection to the MySql database using hardcoded credentials stored in the class. */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }
}
