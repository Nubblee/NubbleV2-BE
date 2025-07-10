package dev.biddan.nubblev2.study.problem.repository;

import dev.biddan.nubblev2.study.problem.domain.Problem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
    @Query("SELECT p FROM Problem p WHERE p.studyGroup.id = :studyGroupId AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Problem> findByStudyGroupIdOrderByCreatedAtDesc(@Param("studyGroupId") Long studyGroupId, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Problem p WHERE p.studyGroup.id = :studyGroupId AND p.deleted = false")
    long countByStudyGroupIdAndNotDeleted(@Param("studyGroupId") Long studyGroupId);
}
