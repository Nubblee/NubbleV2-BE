package dev.biddan.nubblev2.study.group;

import static org.hamcrest.Matchers.equalTo;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 그룹 업데이트 테스트")
class StudyGroupUpdateTest extends AbstractIntegrationTest {

    private String ownerAuthSessionId;
    private String otherUserAuthSessionId;
    private Long studyGroupId;

    @BeforeEach
    void setUp() {
        // given: 스터디 그룹 생성
        UserApiRequest.Register ownerRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(ownerRegisterRequest);

        AuthApiRequest.Login ownerLoginRequest = new AuthApiRequest.Login(
                ownerRegisterRequest.loginId(),
                ownerRegisterRequest.password()
        );

        Response ownerLoginResponse = AuthApiTestClient.login(ownerLoginRequest);

        Cookie ownerSessionCookie = ownerLoginResponse.getDetailedCookie(
                AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        ownerAuthSessionId = ownerSessionCookie.getValue();

        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(createRequest, ownerAuthSessionId);
        studyGroupId = createResponse.jsonPath().getLong("studyGroup.id");

        // given: 다른 사용자 회원가입 및 로그인
        UserApiRequest.Register otherRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(otherRegisterRequest);

        AuthApiRequest.Login otherLoginRequest = new AuthApiRequest.Login(
                otherRegisterRequest.loginId(),
                otherRegisterRequest.password()
        );

        Response otherLoginResponse = AuthApiTestClient.login(otherLoginRequest);

        Cookie otherSessionCookie = otherLoginResponse.getDetailedCookie(
                AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        otherUserAuthSessionId = otherSessionCookie.getValue();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessfulUpdate {

        @Test
        @DisplayName("일부 필드만 업데이트할 수 있다")
        void updatePartialFields() {
            // given: 기존 데이터 확인
            StudyGroup originalStudyGroup = studyGroupRepository.findById(studyGroupId).orElseThrow();
            String originalDescription = originalStudyGroup.getDescription().getValue();

            // given: 이름과 설명만 업데이트
            StudyGroupApiRequest.Create partialUpdateRequest = StudyGroupApiRequest.Create.builder()
                    .name("부분 업데이트된 스터디")
                    .capacity(20)
                    .build();

            // when: 부분 업데이트
            Response response = StudyGroupApiTestClient.update(studyGroupId, partialUpdateRequest, ownerAuthSessionId);

            // then: 200 OK 응답
            response.then()
                    .statusCode(200)
                    .body("studyGroup.name", equalTo("부분 업데이트된 스터디"))
                    .body("studyGroup.capacity", equalTo(20))
                    .body("studyGroup.description", equalTo(originalDescription));
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailedUpdate {

        @Test
        @DisplayName("다른 사용자가 스터디 그룹을 업데이트하려고 하면 403을 반환한다")
        void updateByNonOwnerShouldReturn403() {
            // given: 업데이트 요청
            StudyGroupApiRequest.Create updateRequest = StudyGroupRequestFixture.generateValidCreateRequest();

            // when & then: 다른 사용자가 업데이트 시도
            StudyGroupApiTestClient.update(studyGroupId, updateRequest, otherUserAuthSessionId)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("인증되지 않은 사용자가 업데이트하려고 하면 401을 반환한다")
        void updateWithoutAuthShouldReturn401() {
            // given: 업데이트 요청
            StudyGroupApiRequest.Create updateRequest = StudyGroupRequestFixture.generateValidCreateRequest();

            // when & then: 인증 없이 업데이트 시도
            StudyGroupApiTestClient.update(studyGroupId, updateRequest, null)
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("존재하지 않는 스터디 그룹을 업데이트하려고 하면 404를 반환한다")
        void updateNonExistentStudyGroupShouldReturn404() {
            // given: 존재하지 않는 스터디 그룹 ID
            Long nonExistentStudyGroupId = 99999L;
            StudyGroupApiRequest.Create updateRequest = StudyGroupRequestFixture.generateValidCreateRequest();

            // when & then: 존재하지 않는 스터디 그룹 업데이트 시도
            StudyGroupApiTestClient.update(nonExistentStudyGroupId, updateRequest, ownerAuthSessionId)
                    .then()
                    .statusCode(404);
        }
    }
}
