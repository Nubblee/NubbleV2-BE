package dev.biddan.nubblev2.user;

import dev.biddan.nubblev2.user.controller.UserApiRequest;
import java.util.UUID;

public class UserRequestFixture {

    public static UserApiRequest.Register generateValidUserRegisterRequest() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);

        return UserApiRequest.Register.builder()
                .loginId("testuser_" + randomSuffix)
                .nickname("테스트닉네임_" + randomSuffix)
                .password("password123!")
                .preferredArea("서울시 강남구")
                .email("test_" + randomSuffix + "@example.com")
                .build();
    }
}
