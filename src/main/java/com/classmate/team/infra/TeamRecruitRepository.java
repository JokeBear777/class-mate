package com.classmate.team.infra;

import com.classmate.team.domain.TeamRecruit;
import com.classmate.team.domain.TeamRecruitStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRecruitRepository extends JpaRepository<TeamRecruit, Long> {

	List<TeamRecruit> findByLectureIdAndDeletedFalseOrderByCreatedAtDesc(Long lectureId);

	List<TeamRecruit> findByLectureIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
			Long lectureId,
			TeamRecruitStatus status
	);

	Optional<TeamRecruit> findByIdAndDeletedFalse(Long teamRecruitId);
}
