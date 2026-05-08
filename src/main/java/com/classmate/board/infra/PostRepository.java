package com.classmate.board.infra;

import com.classmate.board.domain.Post;
import com.classmate.board.domain.PostType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByLectureIdAndDeletedFalseOrderByCreatedAtDesc(Long lectureId);

	List<Post> findByLectureIdAndPostTypeAndDeletedFalseOrderByCreatedAtDesc(Long lectureId, PostType postType);

	Optional<Post> findByIdAndDeletedFalse(Long postId);
}
