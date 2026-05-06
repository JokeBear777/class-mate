package com.classmate.realtime.security;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.security.JwtTokenProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;

	public WebSocketAuthChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (accessor.getCommand() == StompCommand.CONNECT) {
			authenticate(accessor);
		}
		return message;
	}

	private void authenticate(StompHeaderAccessor accessor) {
		String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
		String token = extractBearerToken(authorization);

		try {
			jwtTokenProvider.validateToken(token);
			accessor.setUser(new StompPrincipal(
					jwtTokenProvider.getUserId(token),
					jwtTokenProvider.getEmail(token),
					jwtTokenProvider.getRole(token)
			));
		} catch (BusinessException exception) {
			throw new MessagingException("WebSocket authentication failed.");
		}
	}

	private String extractBearerToken(String authorization) {
		if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
			throw new MessagingException("WebSocket authentication is required.");
		}
		return authorization.substring(BEARER_PREFIX.length()).trim();
	}
}
