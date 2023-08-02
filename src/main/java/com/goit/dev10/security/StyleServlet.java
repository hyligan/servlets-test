package com.goit.dev10.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter("/static/*")
public class StyleServlet extends HttpFilter {

  @Override
  protected void doFilter(HttpServletRequest req,
                          HttpServletResponse resp,
                          FilterChain chain) throws IOException, ServletException {
    Logger.getAnonymousLogger().log(Level.INFO, req.getPathInfo());
    resp.getWriter().write(FileUtils.readFileToString(FileUtils.getFile(getClass().getClassLoader().getResource("static/css/style.css").getPath())));
    resp.getWriter().close();
    chain.doFilter(req, resp);
  }
}
