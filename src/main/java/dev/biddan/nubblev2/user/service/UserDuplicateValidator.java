package dev.biddan.nubblev2.user.service;

import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDuplicateValidator {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void validate(String loginId, String nickname) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new ConflictException(String.format("이미 사용중인 아이디입니다: %s", loginId));
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new ConflictException(String.format("이미 사용 중인 닉네임입니다: %s", nickname));
        }
    }
}
