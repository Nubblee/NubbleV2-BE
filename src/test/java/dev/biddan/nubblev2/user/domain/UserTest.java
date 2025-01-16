package dev.biddan.nubblev2.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("User 도메인")
class UserTest {

    @DisplayName("생성자 검증")
    @Nested
    class Constructor {

        @DisplayName("유효한 파라미터인 경우, User를 생성한다")
        @Test
        void success() {
            User user = UserFixture.aUser().build();

            assertThat(user).isNotNull();
            assertThat(user.getLoginId()).isEqualTo(UserFixture.DEFAULT_LOGIN_ID);
            assertThat(user.getNickname()).isEqualTo(UserFixture.DEFAULT_NICKNAME);
            assertThat(user.getPassword()).isEqualTo(UserFixture.DEFAULT_PASSWORD);
            assertThat(user.getPreferredArea()).isEqualTo(UserFixture.DEFAULT_PREFERRED_AREA);
            assertThat(user.getEmail()).isEqualTo(UserFixture.DEFAULT_EMAIL);
        }

        @DisplayName("유효하지 않은 파라미터가 포함된 경우, User 생성시 예외를 발생시킨다")
        @ParameterizedTest(name = "{0}")
        @MethodSource("invalidUserParameters")
        void throwException(String testDescription, UserFixture userFixture) {
            assertThatThrownBy(userFixture::build)
                    .isInstanceOf(IllegalArgumentException.class);
        }

        private static Stream<Arguments> invalidUserParameters() {
            return Stream.of(
                    Arguments.of("로그인 ID가 빈 문자열인 경우", UserFixture.aUser().withLoginId("")),
                    Arguments.of("로그인 ID가 최소 길이 미만인 경우", UserFixture.aUser().withLoginId("u".repeat(3))),
                    Arguments.of("로그인 ID가 최대 길이 초과인 경우", UserFixture.aUser().withLoginId("u".repeat(21))),

                    Arguments.of("닉네임이 빈 문자열인 경우", UserFixture.aUser().withNickname("")),
                    Arguments.of("닉네임이 최대 길이 초과인 경우", UserFixture.aUser().withNickname("n".repeat(21))),

                    Arguments.of("비밀번호가 최소 길이 미만인 경우", UserFixture.aUser().withPassword("p".repeat(7))),
                    Arguments.of("비밀번호가 최대 길이 초과인 경우", UserFixture.aUser().withPassword("p".repeat(21))),

                    Arguments.of("주소가 빈 문자열인 경우", UserFixture.aUser().withPreferredArea("")),
                    Arguments.of("주소가 최대 길이 초과인 경우", UserFixture.aUser().withPreferredArea("a".repeat(101))),

                    Arguments.of("이메일이 최대 길이 초과인 경우", UserFixture.aUser().withEmail("e".repeat(255)))
            );
        }
    }
}
