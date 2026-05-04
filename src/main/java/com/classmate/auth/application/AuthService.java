package com.classmate.auth.application;

import com.classmate.auth.domain.User;
import com.classmate.auth.dto.request.LoginRequest;
import com.classmate.auth.dto.request.SignupRequest;
import com.classmate.auth.dto.response.AuthResponse;
import com.classmate.auth.dto.response.UserResponse;
import com.classmate.auth.infra.UserRepository;
import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.common.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final CurrentUserProvider currentUserProvider;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider,
			CurrentUserProvider currentUserProvider
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.currentUserProvider = currentUserProvider;
	}

	@Transactional
	public AuthResponse signup(SignupRequest request) {
		String email = normalizeEmail(request.email());
		if (userRepository.existsByEmail(email)) {
			throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		String encodedPassword = passwordEncoder.encode(request.password());
		User user = userRepository.save(User.create(email, encodedPassword, request.name(), request.role()));
		return AuthResponse.from(jwtTokenProvider.createAccessToken(user), user);
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(normalizeEmail(request.email()))
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
		}

		return AuthResponse.from(jwtTokenProvider.createAccessToken(user), user);
	}

	public UserResponse getMe() {
		Long currentUserId = currentUserProvider.getCurrentUserId();
		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		return UserResponse.from(user);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase();
	}
}
