package dev.biddan.nubblev2.user.repository;

import dev.biddan.nubblev2.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);
}
