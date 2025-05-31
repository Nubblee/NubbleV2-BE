package dev.biddan.nubblev2.study.announcement;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 공고 생성 테스트")
class StudyAnnouncementCreateTest extends AbstractIntegrationTest {

    private String ownerAuthSessionId;
    private String otherUserAuthSessionId;
    private Long studyGroupId;
    private LocalDate studyGroupEndDate;

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
                AUTH_SESSION_COOKIE_NAME);
        ownerAuthSessionId = ownerSessionCookie.getValue();

        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(createRequest, ownerAuthSessionId);
        studyGroupId = createResponse.jsonPath().getLong("studyGroup.id");
        studyGroupEndDate = LocalDate.parse(createResponse.jsonPath().getString("studyGroup.endDate"));

        // given: 다른 사용자 회원가입 및 로그인
        UserApiRequest.Register otherRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(otherRegisterRequest);

        AuthApiRequest.Login otherLoginRequest = new AuthApiRequest.Login(
                otherRegisterRequest.loginId(),
                otherRegisterRequest.password()
        );

        Response otherLoginResponse = AuthApiTestClient.login(otherLoginRequest);
        Cookie otherSessionCookie = otherLoginResponse.getDetailedCookie(
                AUTH_SESSION_COOKIE_NAME);
        otherUserAuthSessionId = otherSessionCookie.getValue();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessfulCreation {

        @Test
        @DisplayName("유효한 정보로 스터디 공고를 생성할 수 있다")
        void createAnnouncementWithValidData() {
            // given: 유효한 스터디 공고 생성 요청
            StudyAnnouncementApiRequest.Create request = StudyAnnouncementRequestFixture
                    .generateValidCreateRequest(studyGroupId, studyGroupEndDate);

            // when: 스터디 공고 생성
            Response response = StudyAnnouncementApiTestClient.create(request, ownerAuthSessionId);

            // then: 201 Created 응답과 공고 정보 반환
            response.then()
                    .statusCode(201)
                    .body("studyAnnouncement.id", notNullValue())
                    .body("studyAnnouncement.studyGroupId", equalTo(studyGroupId.intValue()))
                    .body("studyAnnouncement.title", equalTo(request.title()))
                    .body("studyAnnouncement.description", equalTo(request.description()))
                    .body("studyAnnouncement.recruitCapacity", equalTo(request.recruitCapacity()))
                    .body("studyAnnouncement.status", equalTo("RECRUITING"))
                    .body("studyAnnouncement.closedReason", nullValue())
                    .body("studyAnnouncement.createdAt", notNullValue())
                    .body("studyAnnouncement.closedAt", nullValue())
                    .body("studyAnnouncement.applicationForm", equalTo(request.applicationFormContent()));

            // then: DB에 저장 확인
            Long announcementId = response.jsonPath().getLong("studyAnnouncement.id");
            assertThat(studyAnnouncementRepository.findById(announcementId)).isPresent();
        }
    }

    @Nested
    @DisplayName("권한 및 인증 검증")
    class AuthenticationAndAuthorization {

        @Test
        @DisplayName("스터디장이 아닌 사용자가 공고를 생성하려고 하면 403을 반환한다")
        void createAnnouncementByNonOwnerShouldReturn403() {
            // given: 공고 생성 요청
            StudyAnnouncementApiRequest.Create request = StudyAnnouncementRequestFixture
                    .generateValidCreateRequest(studyGroupId, studyGroupEndDate);

            // when & then: 다른 사용자가 공고 생성 시도
            StudyAnnouncementApiTestClient.create(request, otherUserAuthSessionId)
                    .then()
                    .statusCode(403)
                    .body("detail", containsString("스터디 공고를 생성할 권한이 없습니다"));
        }

        @Test
        @DisplayName("인증되지 않은 사용자가 공고를 생성하려고 하면 401을 반환한다")
        void createAnnouncementWithoutAuthShouldReturn401() {
            // given: 공고 생성 요청
            StudyAnnouncementApiRequest.Create request = StudyAnnouncementRequestFixture
                    .generateValidCreateRequest(studyGroupId, studyGroupEndDate);

            // when & then: 인증 없이 공고 생성 시도
            StudyAnnouncementApiTestClient.create(request, null)
                    .then()
                    .statusCode(401);
        }
    }

    @Nested
    @DisplayName("비즈니스 규칙 검증")
    class BusinessRuleValidation {

        @Test
        @DisplayName("존재하지 않는 스터디 그룹으로 공고를 생성하려고 하면 404를 반환한다")
        void createAnnouncementWithNonExistentStudyGroupShouldReturn404() {
            // given: 존재하지 않는 스터디 그룹 ID
            Long nonExistentStudyGroupId = 99999L;
            StudyAnnouncementApiRequest.Create request = StudyAnnouncementRequestFixture
                    .generateValidCreateRequest(nonExistentStudyGroupId, studyGroupEndDate);

            // when & then: 존재하지 않는 스터디 그룹으로 공고 생성 시도
            StudyAnnouncementApiTestClient.create(request, ownerAuthSessionId)
                    .then()
                    .statusCode(404)
                    .body("detail", containsString("존재하지 않는 스터디 그룹입니다"));
        }

        @Test
        @DisplayName("이미 모집중인 공고가 있는 스터디 그룹에 추가 공고를 생성하려고 하면 409를 반환한다")
        void createDuplicateActiveAnnouncementShouldReturn409() {
            // given: 첫 번째 공고 등록
            StudyAnnouncementApiRequest.Create firstRequest = StudyAnnouncementRequestFixture
                    .generateValidCreateRequest(studyGroupId, studyGroupEndDate);
            StudyAnnouncementApiTestClient.create(firstRequest, ownerAuthSessionId);

            // given: 두 번째 공고 생성
            StudyAnnouncementApiRequest.Create secondRequest = StudyAnnouncementRequestFixture
                    .generateValidCreateRequest(studyGroupId, studyGroupEndDate);

            // when & then: 중복 공고 생성 시도
            StudyAnnouncementApiTestClient.create(secondRequest, ownerAuthSessionId)
                    .then()
                    .statusCode(409)
                    .body("detail", containsString("이미 모집중인 공고가 존재합니다"));

            // then: DB에는 첫 번째 공고만 존재
            assertThat(studyAnnouncementRepository.count()).isEqualTo(1);
        }

        // TODO: 향후 구현 예정 - 스터디 정원 관련 검증
        @Test
        @DisplayName("[TODO] 모집 인원이 스터디 그룹의 남은 정원보다 많으면 400을 반환한다")
        void createAnnouncementWithCapacityExceedingAvailableSlotsShouldReturn400() {
            // given: 스터디 그룹 정원이 10명이고 현재 8명이 참여중이라면, 모집 인원은 최대 2명까지만 가능
            // 현재는 StudyGroup에 현재 참여 인원 관리 기능이 없으므로 TODO로 표시

            // TODO: StudyGroupMember 엔티티 구현 후 테스트 활성화
            // given: 현재 스터디 그룹 정원 10명, 참여중 8명 상황 설정
            // StudyGroupMember member1 = createStudyGroupMember(studyGroupId, user1);
            // StudyGroupMember member2 = createStudyGroupMember(studyGroupId, user2);
            // ... (총 8명 추가)

            // given: 남은 정원(2명)보다 많은 모집 인원(5명) 요청
            StudyAnnouncementApiRequest.Create request = StudyAnnouncementApiRequest.Create.builder()
                    .studyGroupId(studyGroupId)
                    .title("정원 초과 테스트 공고")
                    .description("남은 정원보다 많은 인원을 모집하는 공고")
                    .recruitCapacity(15) // 스터디 그룹 정원(10명)보다도 많음
                    .startDateTime(LocalDateTime.now())
                    .endDateTime(studyGroupEndDate.atTime(20, 0))
                    .applicationFormContent("자유 기입: ")
                    .build();

            // when & then: 정원 초과 모집으로 공고 생성 시도
            // 현재는 구현되지 않았으므로 성공하지만, 향후 구현 시 400 에러 반환 예정
            StudyAnnouncementApiTestClient.create(request, ownerAuthSessionId)
                    .then()
                    .statusCode(201); // TODO: 구현 후 .statusCode(400)으로 변경
            // .body("detail", containsString("모집 인원이 스터디 그룹의 남은 정원을 초과합니다"));
        }
    }

}
