package com.air_ops_system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

  private static final long WINDOW_MS    = 60_000;
  private static final int  GLOBAL_LIMIT = 100;
  private static final int  LOGIN_LIMIT  = 10;

  private final ConcurrentHashMap<String, long[]> globalWindows = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, long[]> loginWindows  = new ConcurrentHashMap<>();

  private boolean isAllowed(ConcurrentHashMap<String, long[]> map, String key, int limit) {
    long now = System.currentTimeMillis();
    long[] window = map.compute(key, (k, w) -> {
      if (w == null || now - w[0] >= WINDOW_MS) {
        return new long[]{now, 1};
      }
      w[1]++;
      return w;
    });
    return window[1] <= limit;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    String ip = getClientIp(request);
    boolean isLogin = "/auth/login".equals(request.getServletPath());

    boolean allowed = isLogin
        ? isAllowed(loginWindows, ip, LOGIN_LIMIT)
        : isAllowed(globalWindows, ip, GLOBAL_LIMIT);

    if (allowed) {
      chain.doFilter(request, response);
    } else {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(
          "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Limite de requisições atingido. Tente novamente em instantes.\"}"
      );
    }
  }

  private String getClientIp(HttpServletRequest request) {
    String xff = request.getHeader("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) {
      return xff.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
