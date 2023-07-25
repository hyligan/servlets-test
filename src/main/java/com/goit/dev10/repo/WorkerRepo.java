package com.goit.dev10.repo;

import com.goit.dev10.configs.JdbcPool;
import com.goit.dev10.entities.Worker;

import java.sql.*;
import java.util.*;

import static com.goit.dev10.configs.ConnectionConfig.BATCH_SIZE;

public class WorkerRepo {


    int batchSizeUpdate = 0;
    int batchSizeInsert = 0;
    private final PreparedStatement preparedStatementInsert;
    private final PreparedStatement preparedStatementUpdate;

    public WorkerRepo() {
        try {
            preparedStatementInsert = JdbcPool.POOL.getConnectionFromPool().prepareStatement("INSERT INTO worker (worker_id,name, birthday, level, salary)\n" +
                    "VALUES (?, ?, ?, ?, ?)");
            preparedStatementUpdate = JdbcPool.POOL.getConnectionFromPool().prepareStatement("UPDATE worker " +
                    "set name = ?, birthday = ?, level = ?, salary = ? " +
                    "where worker_id = ?");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public Optional<Worker> findById(Long id){
        try {
            Connection connectionFromPool = JdbcPool.POOL.getConnectionFromPool();
            PreparedStatement preparedStatement = connectionFromPool.prepareStatement("select * from worker where worker_id = ?");
            preparedStatement.setLong(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Worker worker = new Worker();
            while (resultSet.next()){
                worker.setWorkerId(resultSet.getLong("worker_id"));
                worker.setBirthday(resultSet.getDate("birthday"));
                worker.setLevel(resultSet.getString("level"));
                worker.setName(resultSet.getString("name"));
                worker.setSalary(resultSet.getBigDecimal("salary"));
            }
            JdbcPool.POOL.freeConnection(connectionFromPool);
            return Optional.of(worker);
        } catch (InterruptedException e) {
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public List<Worker> findAll(){
        try {
            Connection connectionFromPool = JdbcPool.POOL.getConnectionFromPool();
            Statement statement = connectionFromPool.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from worker");
            List<Worker> workers = new LinkedList<>();
            while (resultSet.next()){
                Worker worker = new Worker();
                worker.setWorkerId(resultSet.getLong("worker_id"));
                worker.setBirthday(resultSet.getDate("birthday"));
                worker.setLevel(resultSet.getString("level"));
                worker.setName(resultSet.getString("name"));
                worker.setSalary(resultSet.getBigDecimal("salary"));
                workers.add(worker);
            }
            JdbcPool.POOL.freeConnection(connectionFromPool);
            return workers;
        } catch (InterruptedException e) {
            return Collections.emptyList();
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public void delete(Worker entity){
        delete(entity.getWorkerId());
    }

    public void delete(Long id){
        try {
            Connection connectionFromPool = JdbcPool.POOL.getConnectionFromPool();
            PreparedStatement preparedStatement = connectionFromPool.prepareStatement("delete from worker where worker_id = ?");
            preparedStatement.setLong(1,id);
            preparedStatement.execute();
            JdbcPool.POOL.freeConnection(connectionFromPool);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Worker save(Worker entity) throws SQLException, InterruptedException {
        if(Objects.isNull(entity.getWorkerId())){
            return saveAsNew(entity);
        }
        if(findById(entity.getWorkerId()).isPresent()){
            return updateEntity(entity);
        }
        return saveAsNew(entity);
    }

    private Worker updateEntity(Worker entity) throws SQLException {
            preparedStatementUpdate.setString(1,entity.getName());
            preparedStatementUpdate.setDate(2,entity.getBirthday());
            preparedStatementUpdate.setString(3,entity.getLevel());
            preparedStatementUpdate.setBigDecimal(4,entity.getSalary());
            preparedStatementUpdate.setLong(5,entity.getWorkerId());
            preparedStatementUpdate.addBatch();
            preparedStatementUpdate.execute();
            return entity;
    }

    private Worker saveAsNew(Worker entity) throws SQLException, InterruptedException {
        if(Objects.isNull(entity.getWorkerId())){
            Connection connectionFromPool = JdbcPool.POOL.getConnectionFromPool();
            Statement statement = connectionFromPool.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT nextval('worker_worker_id_seq');");
            resultSet.next();
            entity.setWorkerId(resultSet.getLong(1));
            JdbcPool.POOL.freeConnection(connectionFromPool);
        }
        preparedStatementInsert.setLong(1,entity.getWorkerId());
        preparedStatementInsert.setString(2,entity.getName());
        preparedStatementInsert.setDate(3,entity.getBirthday());
        preparedStatementInsert.setString(4,entity.getLevel());
        preparedStatementInsert.setBigDecimal(5,entity.getSalary());
        preparedStatementInsert.execute();
        return entity;
    }
}
