package com.recruitment.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection - reads database settings from db.properties
 * (src/main/resources/db.properties) and hands out JDBC Connections.
 *
 * Edit db.properties with your MySQL username/password before running.
 * The MySQL driver jar (mysql-connector-j) must be on the classpath.
 */
public class DBConnection {

    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean loaded = false;

    /** Loads db.properties once. Throws a clear message if it is missing. */
    private static synchronized void loadProperties() throws SQLException {
        if (loaded) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new SQLException(
                        "db.properties not found on the classpath. "
                        + "Copy src/main/resources/db.properties and set your MySQL credentials.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new SQLException("Could not read db.properties", e);
        }
        url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/recruitment_db");
        username = props.getProperty("db.username", "root");
        password = props.getProperty("db.password", "");
        driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        loaded = true;
    }

    /** Returns a new JDBC connection. Caller must close it (try-with-resources). */
    public static Connection getConnection() throws SQLException {
        loadProperties();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC driver not found on the classpath. "
                    + "Add mysql-connector-j-8.0.33.jar (see README, step 2).", e);
        }
        return DriverManager.getConnection(url, username, password);
    }

    /** Quick check used by the console app to print a friendly message when MySQL is down. */
    public static boolean isDatabaseAvailable() {
        try (Connection con = getConnection()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
