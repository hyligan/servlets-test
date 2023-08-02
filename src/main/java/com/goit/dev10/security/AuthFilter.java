package com.goit.dev10.security;

import com.goit.dev10.entities.User;
import com.goit.dev10.repo.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@WebFilter(value = "/api/*")
public class AuthFilter extends HttpFilter {
  
  private final UserRepo userRepo = new UserRepo();

  @Override
  protected void doFilter(HttpServletRequest req,
                          HttpServletResponse resp,
                          FilterChain chain) throws IOException, ServletException {
    String token = req.getHeader("Authorization");

    Optional<User> userOptional = userRepo.findByToken(token);
    if (userOptional.isPresent()) {
      chain.doFilter(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/login");
      resp.getWriter().close();
    }
  }
}
