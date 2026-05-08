package com.classmate.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "Lecture not found."),
	LECTURE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this lecture."),
	LECTURE_ALREADY_JOINED(HttpStatus.CONFLICT, "You have already joined this lecture."),
	INVALID_JOIN_CODE(HttpStatus.BAD_REQUEST, "Invalid lecture join code."),
	LECTURE_SESSION_ALREADY_ACTIVE(HttpStatus.CONFLICT, "An active lecture session already exists."),
	SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Lecture session not found."),
	SESSION_NOT_ACTIVE(HttpStatus.CONFLICT, "Lecture session is not active."),
	SESSION_ALREADY_ENDED(HttpStatus.CONFLICT, "Lecture session has already ended."),
	QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Question not found."),
	FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "Feedback event not found."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found."),
	POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this post."),
	TEAM_RECRUIT_NOT_FOUND(HttpStatus.NOT_FOUND, "Team recruit post not found."),
	TEAM_RECRUIT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this team recruit post."),
	INVALID_TEAM_RECRUIT_MEMBER_COUNT(HttpStatus.BAD_REQUEST, "Current members cannot exceed required members."),
	RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please try again later."),
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email is already in use."),
	INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email or password is invalid."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found."),
	UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Authentication is required."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired token."),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Request validation failed."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getMessage() {
		return message;
	}
}
