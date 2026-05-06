package com.classmate.event.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class EventSerializationUtils {

	private final ObjectMapper objectMapper;

	public EventSerializationUtils(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public String toJson(Object payload) {
		try {
			return objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Failed to serialize event payload.", exception);
		}
	}

	public <T> T fromJson(String json, Class<T> type) {
		try {
			return objectMapper.readValue(json, type);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Failed to deserialize event payload.", exception);
		}
	}
}
