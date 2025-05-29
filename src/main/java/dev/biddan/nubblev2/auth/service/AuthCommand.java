package dev.biddan.nubblev2.auth.service;

import lombok.Builder;

public class AuthCommand {

    @Builder
    public record Login(
            String loginId,
            String password,
            String clientIp,
            String userAgent
    ) {
    }
}
