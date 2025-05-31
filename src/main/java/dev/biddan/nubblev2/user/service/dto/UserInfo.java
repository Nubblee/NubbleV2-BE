package dev.biddan.nubblev2.user.service.dto;

import dev.biddan.nubblev2.user.domain.User;
import lombok.Builder;

public class UserInfo {

    @Builder
    public record Private(
            Long id,
            String loginId,
            String nickname,
            String preferredArea,
            String email
    ) {

        public static Private from(User user) {
            return Private.builder()
                    .id(user.getId())
                    .loginId(user.getLoginId())
                    .nickname(user.getNickname())
                    .preferredArea(user.getPreferredArea())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Builder
    public record Public(
            Long id,
            String nickname
    ) {
        public static Public from(User user) {
            return Public.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .build();
        }
    }
}
