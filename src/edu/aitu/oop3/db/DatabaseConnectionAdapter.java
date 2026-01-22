package edu.aitu.oop3.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionAdapter implements IDB {

    @Override
    public Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
}
