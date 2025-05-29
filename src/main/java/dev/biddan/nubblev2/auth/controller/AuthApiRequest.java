package dev.biddan.nubblev2.auth.controller;

import dev.biddan.nubblev2.auth.service.AuthCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthApiRequest {

    public record Login(
            @NotBlank(message = "아이디는 필수입니다")
            @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다")
            String loginId,

            @NotBlank(message = "비밀번호는 필수입니다")
            @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
            @Pattern(
                    regexp = "^[A-Za-z0-9@$!%*?&]+$",
                    message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자(@$!%*?&)만 가능합니다"
            )
            String password
    ) {

        public AuthCommand.Login toLoginCommand(
                String clientIp, String userAgent
        ) {
            return AuthCommand.Login.builder()
                    .loginId(loginId())
                    .password(password())
                    .clientIp(clientIp)
                    .userAgent(userAgent)
                    .build();
        }
    }
}
