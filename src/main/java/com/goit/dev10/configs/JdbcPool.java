package com.goit.dev10.configs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.goit.dev10.configs.ConnectionConfig.*;

public enum JdbcPool {
    POOL;

    private int initSize = 2;
    private int maxSize = 10;
    private List<Connection> connectionsFree;
    private List<Connection> connectionsInUse;

    public Connection getConnectionFromPool() throws InterruptedException {
        if(Objects.isNull(connectionsFree)){
            connectionsFree = new LinkedList<>();
            connectionsInUse = new LinkedList<>();
            initPool();
//            logger.debug("init!");
        }
        if(connectionsFree.size()==0){

            if(connectionsInUse.size()<maxSize){
//                logger.debug("creating new!");
                connectionsFree.add(getConnection());
            }else {
//                logger.info("waiting!");
                Thread.sleep(500);
                return getConnectionFromPool();
            }
        }
        Optional<Connection> connectionOpt = connectionsFree.stream().findFirst();
        if(connectionOpt.isPresent()){
            Connection connection = connectionOpt.get();
            connectionsFree.remove(connection);
            connectionsInUse.add(connection);
//            logger.debug("adding!");
            return connection;
        }else {
//            logger.debug("wrong way!");
            return getConnectionFromPool();
        }

    }

    public void freeConnection(Connection connection){
//        logger.debug("try to free connection: {}", connection);
        if(Objects.nonNull(connection)) {
            connectionsInUse.remove(connection);
            connectionsFree.add(connection);
        }
    }

    private void initPool() {
        for(int i=0;i<=initSize;i++){
            connectionsFree.add(getConnection());
        }
    }

    public void setInitSize(int initSize){
        this.initSize = initSize;
    }

    public void setMaxSize(int maxSize){
        this.maxSize = maxSize;
    }

    private Connection getConnection(){
            try {
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(URL_JDBC, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e1) {
                throw new RuntimeException(e1);

            }
    }

}
