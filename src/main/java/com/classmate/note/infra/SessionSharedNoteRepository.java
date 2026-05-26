package com.classmate.note.infra;

import com.classmate.note.domain.SessionSharedNote;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionSharedNoteRepository extends JpaRepository<SessionSharedNote, Long> {

	Optional<SessionSharedNote> findBySessionId(Long sessionId);

	boolean existsBySessionId(Long sessionId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update SessionSharedNote n
			   set n.documentRevision = n.documentRevision + 1,
			       n.updatedAt = :updatedAt
			 where n.id = :noteId
			""")
	int incrementDocumentRevision(
			@Param("noteId") Long noteId,
			@Param("updatedAt") LocalDateTime updatedAt
	);
}
