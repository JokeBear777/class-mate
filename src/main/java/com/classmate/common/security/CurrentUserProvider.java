package com.classmate.common.security;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

	public Long getCurrentUserId() {
		return getCurrentUser().getUserId();
	}

	public String getCurrentUserName() {
		return getCurrentUser().getName();
	}

	public String getCurrentUserEmail() {
		return getCurrentUser().getEmail();
	}

	public String getCurrentUserRole() {
		return getCurrentUser().getRole().name();
	}

	private CustomUserDetails getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BusinessException(ErrorCode.UNAUTHENTICATED);
		}
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof CustomUserDetails userDetails)) {
			throw new BusinessException(ErrorCode.UNAUTHENTICATED);
		}
		return userDetails;
	}
}
