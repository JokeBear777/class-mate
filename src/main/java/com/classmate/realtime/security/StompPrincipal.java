package com.classmate.realtime.security;

import java.security.Principal;

public class StompPrincipal implements Principal {

	private final Long userId;
	private final String email;
	private final String role;

	public StompPrincipal(Long userId, String email, String role) {
		this.userId = userId;
		this.email = email;
		this.role = role;
	}

	@Override
	public String getName() {
		return email;
	}

	public Long getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}
}
