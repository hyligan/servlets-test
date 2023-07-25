package com.goit.dev10.servlets;

import com.goit.dev10.exceptions.CantCreateTableException;
import com.goit.dev10.exceptions.CantGetConnectionFromPoolException;
import com.goit.dev10.configs.JdbcPool;
import com.goit.dev10.entities.Worker;
import com.goit.dev10.exceptions.CantSaveWorkerException;
import com.goit.dev10.repo.WorkerRepo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.goit.dev10.configs.ConnectionConfig.CREATE_TABLE;
import static com.goit.dev10.configs.ConnectionConfig.TABLE_NAME;

public class FirstServlet extends HttpServlet {
    private WorkerRepo workerRepo = new WorkerRepo();
    @Override
    public void init(ServletConfig config) throws ServletException {
        try(Connection connection = JdbcPool.POOL.getConnectionFromPool();
            Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT table_name\n" +
                    "FROM INFORMATION_SCHEMA.TABLES\n" +
                    "WHERE table_type = 'BASE TABLE'");
            Set<String> tables = new LinkedHashSet<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString("table_name"));
            }

            if (!tables.contains(TABLE_NAME)) {
                statement.execute(CREATE_TABLE);
            }
        } catch (SQLException e) {
            throw new CantCreateTableException(e.getMessage());
        } catch (InterruptedException e) {
            throw new CantGetConnectionFromPoolException(e);
        }
        super.init(config);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("req from get type: "+req.getMethod());
        List<Worker> all = workerRepo.findAll();
        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().write(new Gson().toJson(all));
        resp.getWriter().close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Worker worker =
                gson.fromJson(
                                req.getReader()
                                        .lines().collect(Collectors.joining(System.lineSeparator()
                                        ))
                                , Worker.class);
        try {
            Worker save = workerRepo.save(worker);
            resp.getWriter().write(new Gson().toJson(save));
            resp.getWriter().close();
        } catch (SQLException e) {
            throw new CantSaveWorkerException(e);
        } catch (InterruptedException e) {
            throw new CantGetConnectionFromPoolException(e);
        }

    }

    @Override
    public void destroy() {
        log("destroy");
        super.destroy();
    }
}
