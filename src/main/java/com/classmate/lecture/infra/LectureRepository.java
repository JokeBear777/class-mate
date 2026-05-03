package com.classmate.lecture.infra;

import com.classmate.lecture.domain.Lecture;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

	Optional<Lecture> findByJoinCode(String joinCode);

	boolean existsByJoinCode(String joinCode);

	List<Lecture> findByProfessorId(Long professorId);

	List<Lecture> findByTitleContainingIgnoreCase(String keyword);

	List<Lecture> findBySemester(String semester);

	List<Lecture> findByTitleContainingIgnoreCaseAndSemester(String keyword, String semester);
}
