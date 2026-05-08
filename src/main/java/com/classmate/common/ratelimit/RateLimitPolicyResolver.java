package com.classmate.common.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class RateLimitPolicyResolver {

	private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit";

	private final RateLimitProperties properties;

	public RateLimitPolicyResolver(RateLimitProperties properties) {
		this.properties = properties;
	}

	public RateLimitPolicy resolve(HttpServletRequest request) {
		String method = request.getMethod();
		String uri = normalize(request.getRequestURI());

		if (isExcluded(method, uri)) {
			return null;
		}
		if (isPost(method) && matches(uri, "api", "v1", "sessions", "*", "questions")) {
			return policy("question", properties.getQuestion());
		}
		if (isPost(method) && matches(uri, "api", "v1", "sessions", "*", "feedback")) {
			return policy("feedback", properties.getFeedback());
		}
		if (isPost(method) && matches(uri, "api", "v1", "lectures", "*", "posts")) {
			return policy("post", properties.getPost());
		}
		if (isPost(method) && matches(uri, "api", "v1", "lectures", "*", "team-recruits")) {
			return policy("team_recruit", properties.getTeamRecruit());
		}
		if (isGet(method) && matches(uri, "api", "v1", "sessions", "*", "dashboard")) {
			return policy("dashboard", properties.getDashboard());
		}
		if (uri.equals("/api/v1") || uri.startsWith("/api/v1/")) {
			return new RateLimitPolicy(
					"default",
					properties.getDefaultLimit(),
					properties.getDefaultWindowSeconds(),
					RATE_LIMIT_KEY_PREFIX + ":default"
			);
		}
		return null;
	}

	private RateLimitPolicy policy(String name, RateLimitProperties.Policy policy) {
		return new RateLimitPolicy(name, policy.limit(), policy.windowSeconds(), RATE_LIMIT_KEY_PREFIX + ":" + name);
	}

	private boolean isExcluded(String method, String uri) {
		return HttpMethod.OPTIONS.matches(method)
				|| uri.startsWith("/swagger-ui/")
				|| uri.equals("/swagger-ui.html")
				|| uri.startsWith("/v3/api-docs")
				|| uri.equals("/api/v1/auth/signup")
				|| uri.equals("/api/v1/auth/login")
				|| uri.equals("/ws")
				|| uri.startsWith("/ws/");
	}

	private boolean isPost(String method) {
		return HttpMethod.POST.matches(method);
	}

	private boolean isGet(String method) {
		return HttpMethod.GET.matches(method);
	}

	private boolean matches(String uri, String... pattern) {
		String[] segments = segments(uri);
		if (segments.length != pattern.length) {
			return false;
		}
		for (int i = 0; i < pattern.length; i++) {
			if (!"*".equals(pattern[i]) && !pattern[i].equals(segments[i])) {
				return false;
			}
		}
		return true;
	}

	private String[] segments(String uri) {
		String normalized = normalize(uri);
		if ("/".equals(normalized)) {
			return new String[0];
		}
		return normalized.substring(1).split("/");
	}

	private String normalize(String uri) {
		if (uri == null || uri.isBlank()) {
			return "/";
		}
		return uri.endsWith("/") && uri.length() > 1 ? uri.substring(0, uri.length() - 1) : uri;
	}
}
