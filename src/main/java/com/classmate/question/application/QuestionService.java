package com.classmate.question.application;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.question.domain.Question;
import com.classmate.question.dto.request.AnswerQuestionRequest;
import com.classmate.question.dto.request.CreateQuestionRequest;
import com.classmate.question.dto.response.QuestionResponse;
import com.classmate.question.infra.QuestionRepository;
import com.classmate.realtime.application.RealtimeMessageService;
import com.classmate.realtime.dto.RealtimeQuestionMessage;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final RealtimeMessageService realtimeMessageService;

	public QuestionService(
			QuestionRepository questionRepository,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			RealtimeMessageService realtimeMessageService
	) {
		this.questionRepository = questionRepository;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
		this.realtimeMessageService = realtimeMessageService;
	}

	@Transactional
	public QuestionResponse createQuestion(Long sessionId, CreateQuestionRequest request) {
		Long currentUserId = currentUserId();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateActiveSession(session);
		lectureAccessChecker.validateParticipant(session.getLectureId(), currentUserId);

		Question question = questionRepository.save(Question.create(
				session.getId(),
				session.getLectureId(),
				currentUserId,
				anonymousKey(currentUserId),
				request.content().trim()
		));
		realtimeMessageService.sendQuestionCreated(session.getId(), RealtimeQuestionMessage.from(question));
		// TODO: replace direct WebSocket send with Redis Stream event publishing after event pipeline is implemented.

		return QuestionResponse.from(question);
	}

	public List<QuestionResponse> getSessionQuestions(Long sessionId) {
		Long currentUserId = currentUserId();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateProfessorOrAssistant(session.getLectureId(), currentUserId);

		return questionRepository.findBySessionIdOrderByPinnedDescCreatedAtDesc(sessionId)
				.stream()
				.map(QuestionResponse::from)
				.toList();
	}

	public QuestionResponse getQuestion(Long questionId) {
		Question question = getQuestionOrThrow(questionId);
		lectureAccessChecker.validateProfessorOrAssistant(question.getLectureId(), currentUserId());
		return QuestionResponse.from(question);
	}

	@Transactional
	public QuestionResponse answerQuestion(Long questionId, AnswerQuestionRequest request) {
		Question question = getQuestionOrThrow(questionId);
		lectureAccessChecker.validateProfessorOrAssistant(question.getLectureId(), currentUserId());
		question.answer(request.answer().trim());
		return QuestionResponse.from(question);
	}

	@Transactional
	public QuestionResponse changePinned(Long questionId, boolean pinned) {
		Question question = getQuestionOrThrow(questionId);
		lectureAccessChecker.validateProfessorOrAssistant(question.getLectureId(), currentUserId());
		question.changePinned(pinned);
		return QuestionResponse.from(question);
	}

	@Transactional
	public QuestionResponse changeHidden(Long questionId, boolean hidden) {
		Question question = getQuestionOrThrow(questionId);
		lectureAccessChecker.validateProfessorOrAssistant(question.getLectureId(), currentUserId());
		question.changeHidden(hidden);
		return QuestionResponse.from(question);
	}

	private Question getQuestionOrThrow(Long questionId) {
		return questionRepository.findById(questionId)
				.orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
	}

	private Long currentUserId() {
		return currentUserProvider.getCurrentUserId();
	}

	private String anonymousKey(Long userId) {
		return "anon-" + userId;
	}
}
