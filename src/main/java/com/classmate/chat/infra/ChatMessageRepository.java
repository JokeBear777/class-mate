package com.classmate.chat.infra;

import com.classmate.chat.domain.ChatMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

	List<ChatMessage> findBySessionIdOrderByRoomSeqDesc(Long sessionId, Pageable pageable);

	List<ChatMessage> findBySessionIdAndRoomSeqGreaterThanAndRoomSeqLessThanOrderByRoomSeqAsc(
			Long sessionId,
			Long afterSeq,
			Long beforeSeq
	);

	@Query("select max(c.roomSeq) from ChatMessage c where c.sessionId = :sessionId")
	Optional<Long> findMaxRoomSeqBySessionId(@Param("sessionId") Long sessionId);
}
