package dev.biddan.nubblev2.auth.repository;

import dev.biddan.nubblev2.auth.domain.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

}
