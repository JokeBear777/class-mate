package com.classmate.auth.api;

import com.classmate.auth.application.AuthService;
import com.classmate.auth.dto.request.LoginRequest;
import com.classmate.auth.dto.request.SignupRequest;
import com.classmate.auth.dto.response.AuthResponse;
import com.classmate.auth.dto.response.UserResponse;
import com.classmate.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Signup, login, and current user APIs")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = "Signup", description = "Creates a user account and returns an access token.")
	@PostMapping("/signup")
	public ApiResponse<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
		return ApiResponse.success("Signup completed.", authService.signup(request));
	}

	@Operation(summary = "Login", description = "Authenticates by email and password and returns an access token.")
	@PostMapping("/login")
	public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.success("Login completed.", authService.login(request));
	}

	@Operation(summary = "Get current user", description = "Returns the current authenticated user.")
	@GetMapping("/me")
	public ApiResponse<UserResponse> getMe() {
		return ApiResponse.success(authService.getMe());
	}
}
