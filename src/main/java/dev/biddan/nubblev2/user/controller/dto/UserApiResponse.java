package dev.biddan.nubblev2.user.controller.dto;

import dev.biddan.nubblev2.user.service.dto.UserInfo;

public class UserApiResponse {

    public record Private(
            UserInfo.Private user
    ) {

    }
}
