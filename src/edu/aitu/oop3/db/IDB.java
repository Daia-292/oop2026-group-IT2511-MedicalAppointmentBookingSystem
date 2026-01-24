package edu.aitu.oop3.db;

import org.w3c.dom.ls.LSOutput;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDB {
    Connection getConnection() throws SQLException;

}
