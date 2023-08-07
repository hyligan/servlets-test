package com.goit.dev10.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

@WebServlet("/static/css/util.css")
public class UtilCssServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.getWriter().write(FileUtils.readFileToString(FileUtils.getFile(getClass().getClassLoader().getResource("static/css/util.css").getPath())));
    resp.getWriter().close();
  }
}
