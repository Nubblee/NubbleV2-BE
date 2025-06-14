package dev.biddan.nubblev2.study.applicationform;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.study.announcement.StudyAnnouncementApiTestClient;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.ClosedReason;
import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest.Register;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 지원서 수락 테스트")
class StudyApplicationFormApproveTest extends AbstractIntegrationTest {

    private String ownerAuthSessionId;
    private String applicantAuthSessionId;
    private Long ownerUserId;
    private Long applicantUserId;
    private Long announcementId;
    private Long applicationFormId;

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
        ownerUserId = ownerLoginResponse.jsonPath().getLong("user.id");
        Cookie ownerSessionCookie = ownerLoginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        ownerAuthSessionId = ownerSessionCookie.getValue();

        // given: 스터디 그룹 생성
        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(createRequest, ownerAuthSessionId);
        Long studyGroupId = createResponse.jsonPath().getLong("studyGroup.id");

        // given: 스터디 1명 모집 공고 생성
        StudyAnnouncementApiRequest.Create announcementRequest = StudyAnnouncementApiRequest.Create.builder()
                .studyGroupId(studyGroupId)
                .title("Java 백엔드 개발자 스터디 모집")
                .description("Spring Boot와 JPA를 활용한 백엔드 개발 스터디입니다. " +
                        "실무 프로젝트를 통해 함께 성장해요!")
                .recruitCapacity(1)
                .endDate(LocalDate.now().plusDays(1))
                .applicationFormContent(
                        "알고리즘 학습 경험:\n지원 동기:\n사용 가능한 프로그래밍 언어:\n코딩테스트 풀이 경험:\n참여 가능 시간:\n주당 투자 가능 시간:\n")
                .build();

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

        // given: 지원서 제출
        ApplicationFormApiRequest.Submit submitRequest = ApplicationFormRequestFixture.generateValidSubmitRequest();
        Response submitResponse = ApplicationFormApiTestClient.submit(announcementId, submitRequest,
                applicantAuthSessionId);
        applicationFormId = submitResponse.jsonPath().getLong("applicationForm.id");
    }

    @Test
    @DisplayName("스터디장은 제출된 지원서를 수락할 수 있다")
    void approveApplicationFormByOwner() {
        // when: 지원서 수락
        Response response = ApplicationFormApiTestClient.approve(announcementId, applicationFormId, ownerAuthSessionId);

        // then: 200 OK와 수락된 지원서 정보 반환
        response.then()
                .statusCode(200)
                .body("applicationForm.id", equalTo(applicationFormId.intValue()))
                .body("applicationForm.status", equalTo("APPROVED"))
                .body("applicationForm.reviewedAt", notNullValue())
                .body("applicationForm.reviewedBy.id", equalTo(ownerUserId.intValue()));

        // then: 지원자가 스터디 그룹 멤버로 추가됨
        boolean isMember = studyGroupMemberRepository.existsMember(
                announcementId, applicantUserId,
                dev.biddan.nubblev2.study.member.domain.StudyGroupMember.MemberRole.MEMBER
        );

        assertThat(isMember).isTrue();
    }

    @Test
    @DisplayName("정원이 모두 차면 공고가 자동으로 마감된다")
    void announcementClosesWhenCapacityReached() {
        // when: 첫 번째 지원자 수락 (정원 충족)
        ApplicationFormApiTestClient.approve(announcementId, applicationFormId, ownerAuthSessionId);

        // then: 공고가 자동 마감됨
        StudyAnnouncement announcement = studyAnnouncementRepository.findById(announcementId).orElseThrow();
        assertThat(announcement.getStatus()).isEqualTo(AnnouncementStatus.CLOSED);
        assertThat(announcement.getClosedReason()).isEqualTo(ClosedReason.AUTO_CAPACITY_REACHED);

        // given: 두 번째 지원자 생성 및 로그인
        Register secondApplicantRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(secondApplicantRegisterRequest);

        AuthApiRequest.Login applicantLoginRequest = new AuthApiRequest.Login(
                secondApplicantRegisterRequest.loginId(),
                secondApplicantRegisterRequest.password()
        );

        Response secondApplicantLoginResponse = AuthApiTestClient.login(applicantLoginRequest);
        Cookie applicantSessionCookie = secondApplicantLoginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        String secondApplicantAuthSessionId = applicantSessionCookie.getValue();

        // when: 두번째 지원자 지원서 제출
        ApplicationFormApiRequest.Submit submitRequest = ApplicationFormRequestFixture.generateValidSubmitRequest();
        Response response = ApplicationFormApiTestClient.submit(
                announcementId, submitRequest, secondApplicantAuthSessionId);

        // then: 첫번쨰 지원자 수락으로 마감된 공고라 지원 불가
        response.then()
                .statusCode(409)
                .body("detail", containsString("마감된 공고에는 지원할 수 없습니다"));
    }

    @Test
    @DisplayName("스터디장이 아닌 사용자가 지원서를 수락하려고 하면 403을 반환한다")
    void approveByNonOwnerShouldReturn403() {
        // when & then: 다른 사용자가 지원서 수락 시도
        ApplicationFormApiTestClient.approve(announcementId, applicationFormId, applicantAuthSessionId)
                .then()
                .statusCode(403)
                .body("detail", containsString("지원서를 수락할 권한이 없습니다"));
    }

    @Test
    @DisplayName("이미 수락된 지원서를 다시 수락하려고 하면 409를 반환한다")
    void approveAlreadyApprovedApplicationShouldReturn409() {
        // given: 지원서를 먼저 수락
        ApplicationFormApiTestClient.approve(announcementId, applicationFormId, ownerAuthSessionId);

        // when & then: 다시 수락 시도
        ApplicationFormApiTestClient.approve(announcementId, applicationFormId, ownerAuthSessionId)
                .then()
                .statusCode(409)
                .body("detail", containsString("이미 처리된 지원서입니다"));
    }

    @Test
    @DisplayName("거절된 지원서를 수락하려고 하면 409를 반환한다")
    void approveRejectedApplicationShouldReturn409() {
        // todo: 지원서 거절 기능 만든 후 테스트
        // given: 지원서를 먼저 거절
//        ApplicationFormApiTestClient.reject(applicationFormId, "기준 미달", ownerAuthSessionId);

        // when & then: 거절된 지원서 수락 시도
//        ApplicationFormApiTestClient.approve(announcementId, applicationFormId, ownerAuthSessionId)
//                .then()
//                .statusCode(409)
//                .body("detail", containsString("이미 처리된 지원서입니다"));
    }

    @Test
    @DisplayName("마감된 공고의 지원서를 수락하려고 하면 409를 반환한다")
    void approveApplicationForClosedAnnouncementShouldReturn409() {
        // given: 공고를 마감
        StudyAnnouncementApiTestClient.close(announcementId, ownerAuthSessionId);

        // when & then: 마감된 공고의 지원서 수락 시도
        ApplicationFormApiTestClient.approve(announcementId, applicationFormId, ownerAuthSessionId)
                .then()
                .statusCode(409)
                .body("detail", containsString("마감된 공고의 지원서는 수락할 수 없습니다"));
    }


    @Test
    @DisplayName("취소된 지원서를 수락하려고 하면 409를 반환한다")
    void approveCancelledApplicationShouldReturn409() {
        // todo: 지원서 취소 만든 후, 테스트
        // given: 지원서를 먼저 취소
//        ApplicationFormApiTestClient.cancel(applicationFormId, applicantAuthSessionId);

        // when & then: 취소된 지원서 수락 시도
//        ApplicationFormApiTestClient.approve(applicationFormId, ownerAuthSessionId)
//                .then()
//                .statusCode(404)
//                .body("detail", containsString("존재하지 않는 지원서입니다"));
    }
}
