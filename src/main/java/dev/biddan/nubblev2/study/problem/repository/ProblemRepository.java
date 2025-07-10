package dev.biddan.nubblev2.study.problem.repository;

import dev.biddan.nubblev2.study.problem.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}