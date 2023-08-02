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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/login")
public class UserServlet extends HttpServlet {
  private final   UserRepo userRepo = new UserRepo();

  private TemplateEngine engine;

  @Override
  public void init() throws ServletException {
    engine = new TemplateEngine();

    FileTemplateResolver resolver = new FileTemplateResolver();
    resolver.setPrefix(getClass().getClassLoader().getResource("templates").getPath());
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML5");
    resolver.setOrder(engine.getTemplateResolvers().size());
    resolver.setCacheable(false);
    engine.addTemplateResolver(resolver);
  }

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

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html");

    Map<String, Object> respMap = new LinkedHashMap<>();
    respMap.put("loginTop", "Сторінка авторизації");
    respMap.put("forgot", "Ви напевно щось забули");
    respMap.put("createAccount", "Не потрібно сперичатись, просто створіть новий аккаунт!");
    respMap.put("loginButton", "Мацяти тута!");

    Context simpleContext = new Context(
        req.getLocale(),
        respMap
    );

    engine.process("login", simpleContext, resp.getWriter());
    resp.getWriter().close();
  }
}
