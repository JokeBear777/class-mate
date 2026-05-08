package com.classmate.board.application;

import com.classmate.auth.application.UserQueryService;
import com.classmate.board.domain.Post;
import com.classmate.board.domain.PostType;
import com.classmate.board.dto.request.CreatePostRequest;
import com.classmate.board.dto.request.UpdatePostRequest;
import com.classmate.board.dto.response.PostResponse;
import com.classmate.board.dto.response.PostSummaryResponse;
import com.classmate.board.infra.PostRepository;
import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.lecture.application.LectureAccessChecker;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BoardService {

	private final PostRepository postRepository;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final UserQueryService userQueryService;

	public BoardService(
			PostRepository postRepository,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			UserQueryService userQueryService
	) {
		this.postRepository = postRepository;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
		this.userQueryService = userQueryService;
	}

	@Transactional
	public PostResponse createPost(Long lectureId, CreatePostRequest request) {
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(lectureId, currentUserId);

		Post post = postRepository.save(Post.create(
				lectureId,
				currentUserId,
				request.postType(),
				request.title().trim(),
				request.content().trim()
		));

		return toResponse(post);
	}

	public List<PostSummaryResponse> getLecturePosts(Long lectureId, PostType postType) {
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(lectureId, currentUserId);

		List<Post> posts = postType == null
				? postRepository.findByLectureIdAndDeletedFalseOrderByCreatedAtDesc(lectureId)
				: postRepository.findByLectureIdAndPostTypeAndDeletedFalseOrderByCreatedAtDesc(lectureId, postType);

		return posts.stream()
				.map(this::toSummaryResponse)
				.toList();
	}

	public PostResponse getPost(Long postId) {
		Post post = getPostOrThrow(postId);
		lectureAccessChecker.validateParticipant(post.getLectureId(), currentUserId());
		return toResponse(post);
	}

	@Transactional
	public PostResponse updatePost(Long postId, UpdatePostRequest request) {
		Post post = getPostOrThrow(postId);
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(post.getLectureId(), currentUserId);
		validateAuthor(post, currentUserId);

		post.update(request.postType(), request.title().trim(), request.content().trim());
		return toResponse(post);
	}

	@Transactional
	public void deletePost(Long postId) {
		Post post = getPostOrThrow(postId);
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(post.getLectureId(), currentUserId);
		validateAuthorOrProfessorAssistant(post, currentUserId);
		post.delete();
	}

	private Post getPostOrThrow(Long postId) {
		return postRepository.findByIdAndDeletedFalse(postId)
				.orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
	}

	private void validateAuthor(Post post, Long currentUserId) {
		if (!post.getAuthorId().equals(currentUserId)) {
			throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
		}
	}

	private void validateAuthorOrProfessorAssistant(Post post, Long currentUserId) {
		if (post.getAuthorId().equals(currentUserId)) {
			return;
		}
		if (!lectureAccessChecker.isProfessorOrAssistant(post.getLectureId(), currentUserId)) {
			throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
		}
	}

	private PostSummaryResponse toSummaryResponse(Post post) {
		return PostSummaryResponse.from(post, userQueryService.getUserName(post.getAuthorId()));
	}

	private PostResponse toResponse(Post post) {
		return PostResponse.from(post, userQueryService.getUserName(post.getAuthorId()));
	}

	private Long currentUserId() {
		return currentUserProvider.getCurrentUserId();
	}
}
