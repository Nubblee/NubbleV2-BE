package dev.biddan.nubblev2.user.controller.dto;

import dev.biddan.nubblev2.user.service.dto.UserCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class UserApiRequest {

    @Builder
    public record Register(
            @NotBlank(message = "아이디는 필수입니다")
            @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다")
            String loginId,

            @NotBlank(message = "닉네임은 필수입니다")
            @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하여야 합니다")
            String nickname,

            @NotBlank(message = "비밀번호는 필수입니다")
            @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하여야 합니다")
            @Pattern(
                    regexp = "^[A-Za-z0-9@$!%*?&]+$",
                    message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자(@$!%*?&)만 가능합니다"
            )
            String password,

            @NotBlank(message = "선호 지역은 필수입니다")
            @Size(max = 50, message = "선호 지역은 50자를 초과할 수 없습니다")
            String preferredArea,

            @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
            String email
    ) {

        public UserCommand.Register toRegisterCommand() {
            return UserCommand.Register.builder()
                    .loginId(loginId())
                    .nickname(nickname())
                    .password(password())
                    .preferredArea(preferredArea())
                    .email(email())
                    .build();
        }
    }


}
