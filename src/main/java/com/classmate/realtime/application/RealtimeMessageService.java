package com.classmate.realtime.application;

import com.classmate.realtime.dto.RealtimeFeedbackMessage;
import com.classmate.realtime.dto.RealtimeQuestionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealtimeMessageService {

	private static final Logger log = LoggerFactory.getLogger(RealtimeMessageService.class);

	private final SimpMessagingTemplate messagingTemplate;

	public RealtimeMessageService(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public void sendQuestionCreated(Long sessionId, RealtimeQuestionMessage message) {
		send("/topic/sessions/" + sessionId + "/questions", message);
	}

	public void sendFeedbackSubmitted(Long sessionId, RealtimeFeedbackMessage message) {
		send("/topic/sessions/" + sessionId + "/feedback", message);
	}

	public void sendAlertCreated(Long sessionId, Object message) {
		send("/topic/sessions/" + sessionId + "/alerts", message);
	}

	private void send(String topic, Object message) {
		try {
			messagingTemplate.convertAndSend(topic, message);
		} catch (RuntimeException exception) {
			log.warn("Failed to send realtime message. topic={}", topic, exception);
		}
	}
}
