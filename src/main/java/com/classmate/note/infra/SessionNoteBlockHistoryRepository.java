package com.classmate.note.infra;

import com.classmate.note.domain.SessionNoteBlockHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionNoteBlockHistoryRepository extends JpaRepository<SessionNoteBlockHistory, Long> {

	List<SessionNoteBlockHistory> findByBlockIdOrderByCreatedAtDesc(Long blockId);

	List<SessionNoteBlockHistory> findBySessionIdOrderByCreatedAtDesc(Long sessionId);
}
