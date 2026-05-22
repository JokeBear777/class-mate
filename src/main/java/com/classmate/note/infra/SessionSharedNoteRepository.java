package com.classmate.note.infra;

import com.classmate.note.domain.SessionSharedNote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionSharedNoteRepository extends JpaRepository<SessionSharedNote, Long> {

	Optional<SessionSharedNote> findBySessionId(Long sessionId);

	boolean existsBySessionId(Long sessionId);
}
