package dev.biddan.nubblev2.user.repository;

import dev.biddan.nubblev2.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}
