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
	CHAT_SEQUENCE_ISSUE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "Failed to issue chat message sequence."),
	INVALID_CHAT_SEQ_RANGE(HttpStatus.BAD_REQUEST, "Invalid chat sequence range."),
	CHAT_CATCH_UP_RANGE_TOO_LARGE(HttpStatus.BAD_REQUEST, "Chat catch-up range is too large."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found."),
	POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this post."),
	TEAM_RECRUIT_NOT_FOUND(HttpStatus.NOT_FOUND, "Team recruit post not found."),
	TEAM_RECRUIT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this team recruit post."),
	INVALID_TEAM_RECRUIT_MEMBER_COUNT(HttpStatus.BAD_REQUEST, "Current members cannot exceed required members."),
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification not found."),
	NOTIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this notification."),
	NOTIFICATION_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Notification sending failed."),
	SESSION_SHARED_NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "Session shared note not found."),
	SESSION_NOTE_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "Session note block not found."),
	SESSION_NOTE_BLOCK_VERSION_CONFLICT(HttpStatus.CONFLICT, "Session note block version conflict."),
	SESSION_NOTE_BLOCK_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this session note block."),
	SESSION_NOTE_BLOCK_ALREADY_DELETED(HttpStatus.CONFLICT, "Session note block has already been deleted."),
	SESSION_NOTE_INVALID_BLOCK_ORDER(HttpStatus.BAD_REQUEST, "Invalid session note block order."),
	SESSION_NOTE_EDITING_PRESENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Session note editing presence not found."),
	RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please try again later."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found."),
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
