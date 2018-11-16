package at.wambo.podcaster.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by martin on 25.08.16.
 */
public class LogRequestsFilter implements Filter {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private boolean shouldLog() {
    return logger.isDebugEnabled();
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain chain) throws IOException, ServletException {
    if (shouldLog()) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      String auth = request.getAuthType() == null ? "" : " Auth: " + request.getAuthType();
      logger
          .debug("{} {} {}{}", request.getMethod(), request.getServletPath(), response.getStatus(),
              auth);
    }

    chain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {

  }
}
