package com.classmate.common.config;

import com.classmate.common.exception.ErrorCode;
import com.classmate.common.filter.RedisRateLimitFilter;
import com.classmate.common.ratelimit.RateLimitProperties;
import com.classmate.common.response.ApiResponse;
import com.classmate.common.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class SecurityConfig {

	private static final String[] SWAGGER_PATHS = {
			"/swagger-ui/**",
			"/swagger-ui.html",
			"/v3/api-docs/**"
	};

	private static final String[] AUTH_PUBLIC_PATHS = {
			"/api/v1/auth/signup",
			"/api/v1/auth/login"
	};

	private static final String[] WEBSOCKET_PATHS = {
			"/ws",
			"/ws/**"
	};

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final RedisRateLimitFilter redisRateLimitFilter;
	private final ObjectMapper objectMapper;
	private final CorsConfigurationSource corsConfigurationSource;

	public SecurityConfig(
			JwtAuthenticationFilter jwtAuthenticationFilter,
			RedisRateLimitFilter redisRateLimitFilter,
			ObjectMapper objectMapper,
			CorsConfigurationSource corsConfigurationSource
	) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.redisRateLimitFilter = redisRateLimitFilter;
		this.objectMapper = objectMapper;
		this.corsConfigurationSource = corsConfigurationSource;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(ErrorCode.UNAUTHENTICATED.getHttpStatus().value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setCharacterEncoding("UTF-8");
					objectMapper.writeValue(response.getWriter(), ApiResponse.fail(ErrorCode.UNAUTHENTICATED));
				}))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(SWAGGER_PATHS).permitAll()
						.requestMatchers(AUTH_PUBLIC_PATHS).permitAll()
						.requestMatchers(WEBSOCKET_PATHS).permitAll()
						.requestMatchers("/api/v1/**").authenticated()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				// TODO: Move rate limit and request logging to Spring Cloud Gateway when services are separated.
				.addFilterAfter(redisRateLimitFilter, JwtAuthenticationFilter.class)
				.build();
	}

	@Bean
	public FilterRegistrationBean<RedisRateLimitFilter> redisRateLimitFilterRegistration(
			RedisRateLimitFilter filter
	) {
		FilterRegistrationBean<RedisRateLimitFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
