package com.ch.filters;

import static com.ch.service.LoggingService.LoggingLevel.DEBUG;
import static com.ch.service.LoggingService.tag;

import com.ch.service.LoggingService;
import com.google.common.util.concurrent.RateLimiter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by Aaron.Witter on 10/04/2016.
 */
@SuppressWarnings("PMD")
public class RateLimitFilter implements Filter {

  private int rateLimit;

  public RateLimitFilter(int rateLimit) {
    this.rateLimit = rateLimit;
    LoggingService.log(tag, DEBUG, String.format("Rate Limit set to %d requests per second", rateLimit), RateLimitFilter.class);
  }

  public void init(FilterConfig filterConfig) throws ServletException {
  }

  /**
   * Limits the rate of concurrent requests to the apps resources.
   *
   * @param servletRequest  HttpServletRequest
   * @param servletResponse HttpServletResponse
   * @param chain           FilterChain
   * @throws IOException      class of exceptions produced by failed or interrupted I/O operations
   * @throws ServletException general exception a servlet can throw when it encounters difficulty
   */
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
    throws IOException, ServletException {
    // limiting the submission of requests to x per second
    RateLimiter limiter = RateLimiter.create(rateLimit);

    //acquires the limiter after the block has expired
    limiter.acquire();

    //causes the resource at the end of the chain to be invoked.
    chain.doFilter(servletRequest, servletResponse);
  }

  public void destroy() {

  }
}
