package dev.biddan.nubblev2.study.applicationform;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.study.announcement.StudyAnnouncementApiTestClient;
import dev.biddan.nubblev2.study.announcement.StudyAnnouncementRequestFixture;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 지원서 제출 테스트")
class StudyApplicationFormSubmitTest extends AbstractIntegrationTest {

    private String ownerAuthSessionId;
    private String applicantAuthSessionId;
    private Long announcementId;
    private Long applicantUserId;

    @BeforeEach
    void setUp() {
        // given: 스터디 그룹 소유자 회원가입 및 로그인
        UserApiRequest.Register ownerRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(ownerRegisterRequest);

        AuthApiRequest.Login ownerLoginRequest = new AuthApiRequest.Login(
                ownerRegisterRequest.loginId(),
                ownerRegisterRequest.password()
        );

        Response ownerLoginResponse = AuthApiTestClient.login(ownerLoginRequest);
        Cookie ownerSessionCookie = ownerLoginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        ownerAuthSessionId = ownerSessionCookie.getValue();

        // given: 스터디 그룹 생성
        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(createRequest, ownerAuthSessionId);
        Long studyGroupId = createResponse.jsonPath().getLong("studyGroup.id");

        // given: 스터디 공고 생성
        StudyAnnouncementApiRequest.Create announcementRequest = StudyAnnouncementRequestFixture
                .generateValidCreateRequest(studyGroupId);
        Response announcementResponse = StudyAnnouncementApiTestClient.create(announcementRequest, ownerAuthSessionId);
        announcementId = announcementResponse.jsonPath().getLong("studyAnnouncement.id");

        // given: 지원자 회원가입 및 로그인
        UserApiRequest.Register applicantRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(applicantRegisterRequest);

        AuthApiRequest.Login applicantLoginRequest = new AuthApiRequest.Login(
                applicantRegisterRequest.loginId(),
                applicantRegisterRequest.password()
        );

        Response applicantLoginResponse = AuthApiTestClient.login(applicantLoginRequest);
        applicantUserId = applicantLoginResponse.jsonPath().getLong("user.id");
        Cookie applicantSessionCookie = applicantLoginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        applicantAuthSessionId = applicantSessionCookie.getValue();
    }

    @Test
    @DisplayName("지원서를 제출할 수 있다")
    void submitApplicationFormWithValidData() {
        // given: 유효한 지원서 제출 요청
        ApplicationFormApiRequest.Submit request = ApplicationFormRequestFixture.generateValidSubmitRequest();

        // when: 지원서 제출
        Response response = ApplicationFormApiTestClient.submit(announcementId, request, applicantAuthSessionId);

        // then: 201 Created 응답과 지원서 정보 반환
        response.then()
                .statusCode(201)
                .body("applicationForm.id", notNullValue())
                .body("applicationForm.announcementId", equalTo(announcementId.intValue()))
                .body("applicationForm.applicant.id", equalTo(applicantUserId.intValue()))
                .body("applicationForm.content", equalTo(request.content()))
                .body("applicationForm.status", equalTo("SUBMITTED"))
                .body("applicationForm.submittedAt", notNullValue());

        // then: DB에 저장 확인
        Long applicationFormId = response.jsonPath().getLong("applicationForm.id");
        assertThat(studyApplicationFormRepository.findById(applicationFormId)).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 공고에 지원하려고 하면 404를 반환한다")
    void submitToNonExistentAnnouncementShouldReturn404() {
        // given: 존재하지 않는 공고 ID
        Long nonExistentAnnouncementId = 99999L;
        ApplicationFormApiRequest.Submit request = ApplicationFormRequestFixture.generateValidSubmitRequest();

        // when & then: 존재하지 않는 공고에 지원 시도
        ApplicationFormApiTestClient.submit(nonExistentAnnouncementId, request, applicantAuthSessionId)
                .then()
                .statusCode(404)
                .body("detail", containsString("존재하지 않는 모집 공고입니다"));
    }

    @Test
    @DisplayName("이미 지원한 사용자가 중복 지원하려고 하면 409를 반환한다")
    void submitDuplicateApplicationShouldReturn409() {
        // given: 첫 번째 지원서 제출
        ApplicationFormApiRequest.Submit firstRequest = ApplicationFormRequestFixture.generateValidSubmitRequest();
        ApplicationFormApiTestClient.submit(announcementId, firstRequest, applicantAuthSessionId);

        // given: 같은 사용자의 두 번째 지원서
        ApplicationFormApiRequest.Submit secondRequest = new ApplicationFormApiRequest.Submit("중복 지원 테스트입니다.");

        // when & then: 중복 지원 시도
        ApplicationFormApiTestClient.submit(announcementId, secondRequest, applicantAuthSessionId)
                .then()
                .statusCode(409)
                .body("detail", containsString("이미 지원한 공고입니다"));
    }

    @Test
    @DisplayName("[TODO] 이미 스터디 멤버인 사용자가 지원하려고 하면 409를 반환한다")
    void studyMemberCannotApplyToSameStudyAnnouncement() {
        // TODO: StudyGroupMember 엔티티 구현 후 테스트 활성화
        ApplicationFormApiRequest.Submit request = ApplicationFormRequestFixture.generateValidSubmitRequest();

        // when & then: 이미 멤버인 사용자가 지원 시도, owner는 이미 스터디 그룹 멤버
        // 현재는 구현되지 않았으므로 성공하지만, 향후 구현 시 409 에러 반환 예정
        ApplicationFormApiTestClient.submit(announcementId, request, ownerAuthSessionId)
                .then()
                .statusCode(201); // TODO: 구현 후 .statusCode(409)로 변경
        // .body("detail", containsString("이미 스터디 그룹의 멤버입니다"));
    }

    @Test
    @DisplayName("[TODO] 마감된 공고에 지원하려고 하면 409을 반환한다")
    void cannotApplyToClosedAnnouncement() {
        // given: 마감된 공고
        StudyAnnouncementApiTestClient.close(announcementId, ownerAuthSessionId);

        // given: 신청서 요청 생성
        ApplicationFormApiRequest.Submit request = ApplicationFormRequestFixture.generateValidSubmitRequest();

        // when & then: 마감된 공고에 지원 시도
        // 현재는 구현되지 않았으므로 성공하지만, 향후 구현 시 400 에러 반환 예정
        ApplicationFormApiTestClient.submit(announcementId, request, applicantAuthSessionId)
                .then()
                .statusCode(409)
                .body("detail", containsString("마감된 공고에는 지원할 수 없습니다"));
    }
}
