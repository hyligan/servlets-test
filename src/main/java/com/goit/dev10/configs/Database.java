package com.goit.dev10.configs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import static com.goit.dev10.configs.ConnectionConfig.*;

public enum Database {
    INSTANCE;

    private Connection connection;

    public Connection getConnection(){
        if(Objects.isNull(connection)) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL_JDBC, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e1) {
                throw new RuntimeException(e1);
            }
        }
        return connection;
    }

}
