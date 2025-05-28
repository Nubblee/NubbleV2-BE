package dev.biddan.nubblev2.user.service.dto;

import lombok.Builder;

public class UserCommand {

    @Builder
    public record Register(
            String loginId,
            String nickname,
            String password,
            String preferredArea,
            String email) {

    }
}
