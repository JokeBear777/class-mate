package com.classmate.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		long startedAt = System.currentTimeMillis();
		try {
			filterChain.doFilter(request, response);
		} finally {
			long elapsedMs = System.currentTimeMillis() - startedAt;
			int status = response.getStatus();
			String message = "method={} uri={} queryString={} remoteAddr={} userAgent={} status={} elapsedMs={}";
			Object[] args = {
					request.getMethod(),
					request.getRequestURI(),
					request.getQueryString(),
					request.getRemoteAddr(),
					request.getHeader("User-Agent"),
					status,
					elapsedMs
			};

			if (status >= 500) {
				log.warn(message, args);
			} else if (status >= 400) {
				log.info(message, args);
			} else {
				log.info(message, args);
			}
		}
	}
}
