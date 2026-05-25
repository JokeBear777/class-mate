package com.classmate.realtime.security;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

	private static final Logger log = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;

	public WebSocketAuthChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor == null) {
			accessor = StompHeaderAccessor.wrap(message);
		}
		log.info(
				"WebSocket inbound STOMP message. command={} sessionId={} destination={} authorizationHeaderPresent={} userNull={} userClass={}",
				accessor.getCommand(),
				accessor.getSessionId(),
				accessor.getDestination(),
				StringUtils.hasText(accessor.getFirstNativeHeader(AUTHORIZATION_HEADER)),
				accessor.getUser() == null,
				accessor.getUser() == null ? null : accessor.getUser().getClass().getName()
		);
		if (accessor.getCommand() == StompCommand.CONNECT) {
			authenticate(accessor);
			return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
		}
		return message;
	}

	private void authenticate(StompHeaderAccessor accessor) {
		String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
		String token = extractBearerToken(authorization);

		try {
			jwtTokenProvider.validateToken(token);
			StompPrincipal principal = new StompPrincipal(
					jwtTokenProvider.getUserId(token),
					jwtTokenProvider.getEmail(token),
					jwtTokenProvider.getRole(token)
			);
			accessor.setUser(principal);
			log.info(
					"WebSocket STOMP CONNECT authenticated. sessionId={} userId={} email={} role={} userNull={} userClass={}",
					accessor.getSessionId(),
					principal.getUserId(),
					principal.getEmail(),
					principal.getRole(),
					accessor.getUser() == null,
					accessor.getUser() == null ? null : accessor.getUser().getClass().getName()
			);
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
