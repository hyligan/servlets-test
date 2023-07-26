package com.goit.dev10.servlets;

import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@WebServlet("/homework")
public class HomeWorkServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      resp.getWriter()
          .write(
              DateTimeFormatter.ISO_LOCAL_TIME
                  .format(
                      LocalDateTime.now(ZoneId.of(
                          URLEncoder.encode(
                              req.getParameter("timezone"), StandardCharsets.UTF_8)
                      ))
                  )
          );
    } catch (Exception e) {
      resp.setStatus(400);
      resp.getWriter().write("Invalid timezone");
    }
    resp.getWriter().close();
  }
}
