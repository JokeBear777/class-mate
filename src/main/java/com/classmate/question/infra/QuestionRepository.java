package com.classmate.question.infra;

import com.classmate.question.domain.Question;
import com.classmate.question.domain.QuestionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findBySessionIdAndHiddenFalseOrderByPinnedDescCreatedAtDesc(Long sessionId);

	List<Question> findBySessionIdOrderByPinnedDescCreatedAtDesc(Long sessionId);

	long countBySessionIdAndStatus(Long sessionId, QuestionStatus status);

	long countBySessionIdAndHiddenFalse(Long sessionId);
}
