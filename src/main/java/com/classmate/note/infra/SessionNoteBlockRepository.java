package com.classmate.note.infra;

import com.classmate.note.domain.SessionNoteBlock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionNoteBlockRepository extends JpaRepository<SessionNoteBlock, Long> {

	List<SessionNoteBlock> findByNoteIdAndDeletedFalseOrderByBlockOrderAsc(Long noteId);

	List<SessionNoteBlock> findBySessionIdAndDeletedFalseOrderByBlockOrderAsc(Long sessionId);

	Optional<SessionNoteBlock> findByIdAndNoteIdAndDeletedFalse(Long blockId, Long noteId);

	Optional<SessionNoteBlock> findTopByNoteIdAndDeletedFalseOrderByBlockOrderDesc(Long noteId);

	Optional<SessionNoteBlock> findFirstByNoteIdAndDeletedFalseAndBlockOrderGreaterThanOrderByBlockOrderAsc(
			Long noteId,
			int blockOrder
	);
}
