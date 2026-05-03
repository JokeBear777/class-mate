package com.classmate.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다."),
	LECTURE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "강의에 접근할 수 없습니다."),
	LECTURE_ALREADY_JOINED(HttpStatus.CONFLICT, "이미 참여한 강의입니다."),
	INVALID_JOIN_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 참여 코드입니다."),
	SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "수업 세션을 찾을 수 없습니다."),
	SESSION_ALREADY_ENDED(HttpStatus.CONFLICT, "이미 종료된 수업 세션입니다."),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

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
