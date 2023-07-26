package com.goit.dev10.repo;

import com.goit.dev10.configs.JdbcPool;
import com.goit.dev10.entities.User;
import com.goit.dev10.exceptions.CantCreateTableException;
import com.goit.dev10.exceptions.CantGetConnectionFromPoolException;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static com.goit.dev10.utils.Util.calculateHMac;

public class UserRepo {

  public UserRepo() {
    try(Connection connection = JdbcPool.POOL.getConnectionFromPool();
        Statement statement = connection.createStatement()) {
      ResultSet resultSet = statement.executeQuery("SELECT table_name\n" +
          "FROM INFORMATION_SCHEMA.TABLES\n" +
          "WHERE table_type = 'BASE TABLE'");
      Set<String> tables = new LinkedHashSet<>();
      while (resultSet.next()) {
        tables.add(resultSet.getString("table_name"));
      }

      if (!tables.contains("usr")) {
        statement.execute("create table public.usr \n" +
            "(\n" +
            "    login varchar(255) PRIMARY KEY,\n" +
            "    password TEXT NOT NULL,\n" +
            "    token text NOT NULL \n"+
            ");\n");
      }
    } catch (SQLException e) {
      throw new CantCreateTableException(e.getMessage());
    } catch (InterruptedException e) {
      throw new CantGetConnectionFromPoolException(e);
    }
  }

  public Optional<User> findById(String login){
    try {
      Connection connectionFromPool = JdbcPool.POOL.getConnectionFromPool();
      PreparedStatement preparedStatement = connectionFromPool.prepareStatement("select * from usr where login = ?");
      preparedStatement.setString(1,login);
      ResultSet resultSet = preparedStatement.executeQuery();
      User user = new User();
      while (resultSet.next()){
        user.setLogin(resultSet.getString("login"));
        user.setPassword(resultSet.getString("pswd"));
        user.setToken(resultSet.getString("token"));
      }
      JdbcPool.POOL.freeConnection(connectionFromPool);
      return Optional.of(user);
    } catch (InterruptedException e) {
      return Optional.empty();
    } catch (SQLException e) {
      return Optional.empty();
    }
  }

  public Optional<User> findByToken(String token){
    try {
      Connection connectionFromPool = JdbcPool.POOL.getConnectionFromPool();
      PreparedStatement preparedStatement = connectionFromPool.prepareStatement("select * from usr where token = ?");
      preparedStatement.setString(1,token);
      ResultSet resultSet = preparedStatement.executeQuery();
      User user = new User();
      while (resultSet.next()){
        user.setLogin(resultSet.getString("login"));
        user.setPassword(resultSet.getString("pswd"));
        user.setToken(resultSet.getString("token"));
      }
      JdbcPool.POOL.freeConnection(connectionFromPool);
      return Optional.of(user);
    } catch (InterruptedException e) {
      return Optional.empty();
    } catch (SQLException e) {
      return Optional.empty();
    }
  }

  public User save(User entity) throws Exception {
    PreparedStatement preparedStatementInsert = JdbcPool.POOL.getConnectionFromPool().prepareStatement("INSERT INTO usr (login,password, token)\n" +
            "VALUES (?, ?, ?)"); 
    preparedStatementInsert.setString(1, entity.getLogin());
    preparedStatementInsert.setString(2,entity.getPassword());
    preparedStatementInsert.setString(3,calculateHMac(entity.getLogin(), entity.getPassword()));
    preparedStatementInsert.execute();
    return entity;
  }
}
