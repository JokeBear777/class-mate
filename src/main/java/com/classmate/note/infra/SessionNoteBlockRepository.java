package com.classmate.note.infra;

import com.classmate.note.domain.SessionNoteBlock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionNoteBlockRepository extends JpaRepository<SessionNoteBlock, Long> {

	List<SessionNoteBlock> findByNoteIdAndDeletedFalseOrderByBlockOrderAsc(Long noteId);

	List<SessionNoteBlock> findBySessionIdAndDeletedFalseOrderByBlockOrderAsc(Long sessionId);

	Optional<SessionNoteBlock> findByIdAndNoteId(Long blockId, Long noteId);

	Optional<SessionNoteBlock> findByIdAndNoteIdAndDeletedFalse(Long blockId, Long noteId);

	Optional<SessionNoteBlock> findTopByNoteIdAndDeletedFalseOrderByBlockOrderDesc(Long noteId);

	Optional<SessionNoteBlock> findFirstByNoteIdAndDeletedFalseAndBlockOrderGreaterThanOrderByBlockOrderAsc(
			Long noteId,
			int blockOrder
	);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update SessionNoteBlock b
			   set b.content = :content,
			       b.version = b.version + 1,
			       b.updatedBy = :userId,
			       b.updatedAt = :updatedAt
			 where b.id = :blockId
			   and b.noteId = :noteId
			   and b.version = :requestVersion
			   and b.deleted = false
			""")
	int updateContentIfVersionMatches(
			@Param("noteId") Long noteId,
			@Param("blockId") Long blockId,
			@Param("requestVersion") long requestVersion,
			@Param("content") String content,
			@Param("userId") Long userId,
			@Param("updatedAt") LocalDateTime updatedAt
	);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update SessionNoteBlock b
			   set b.deleted = true,
			       b.deletedAt = :deletedAt,
			       b.version = b.version + 1,
			       b.updatedBy = :userId,
			       b.updatedAt = :deletedAt
			 where b.id = :blockId
			   and b.noteId = :noteId
			   and b.version = :requestVersion
			   and b.deleted = false
			""")
	int softDeleteIfVersionMatches(
			@Param("noteId") Long noteId,
			@Param("blockId") Long blockId,
			@Param("requestVersion") long requestVersion,
			@Param("userId") Long userId,
			@Param("deletedAt") LocalDateTime deletedAt
	);
}
