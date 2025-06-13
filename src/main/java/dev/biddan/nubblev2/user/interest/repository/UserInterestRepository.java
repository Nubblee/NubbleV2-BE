package dev.biddan.nubblev2.user.interest.repository;

import dev.biddan.nubblev2.user.interest.domain.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
}
