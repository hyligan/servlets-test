package com.goit.dev10.servlets;

import com.goit.dev10.entities.User;
import com.goit.dev10.entities.Worker;
import com.goit.dev10.repo.UserRepo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
  private final   UserRepo userRepo = new UserRepo();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json; charset=utf-8");
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    User user =
        gson.fromJson(
            req.getReader()
                .lines().collect(Collectors.joining(System.lineSeparator()
                ))
            , User.class);
    try {
      resp.getWriter().write(new Gson().toJson( userRepo.save(user)));
    } catch (Exception e) {
      resp.setStatus(400);
      resp.getWriter().write("can't create user");
    }
    resp.getWriter().close();
  }
}
