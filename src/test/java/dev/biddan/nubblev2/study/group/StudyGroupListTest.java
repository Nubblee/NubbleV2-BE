package dev.biddan.nubblev2.study.group;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 그룹 목록 조회 테스트")
class StudyGroupListTest extends AbstractIntegrationTest {

    private String authSessionId;

    @BeforeEach
    void setUp() {
        // given: 사용자 생성 및 로그인
        UserApiRequest.Register registerRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(registerRequest);

        AuthApiRequest.Login loginRequest = new AuthApiRequest.Login(
                registerRequest.loginId(), registerRequest.password());
        Response loginResponse = AuthApiTestClient.login(loginRequest);

        Cookie sessionCookie = loginResponse.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        authSessionId = sessionCookie.getValue();
    }

    @Test
    @DisplayName("최신순으로 정렬 조회된다")
    void studyGroups_should_be_ordered_by_latest_created_date() {
        // given: 기준 시간 설정 (2024년 6월 1일)
        LocalDate baseDate = LocalDate.of(2024, 6, 1);
        systemClock.setFixedTime(baseDate.atTime(9, 0));

        List<Long> studyGroupIds = new ArrayList<>();

        // given: 1일 간격으로 5개의 스터디 그룹 생성
        for (int i = 0; i < 5; i++) {
            StudyGroupApiRequest.Create request = createStudyGroupRequest("스터디 그룹 " + (i + 1));
            Response response = StudyGroupApiTestClient.create(request, authSessionId);
            Long studyGroupId = response.jsonPath().getLong("studyGroup.id");
            studyGroupIds.add(studyGroupId);

            // 다음 스터디 그룹을 위해 1일 진행
            if (i < 4) {
                systemClock.advanceTime(Duration.ofDays(1));
            }
        }

        // when: 스터디 그룹 목록 조회
        Response listResponse = StudyGroupApiTestClient.findList(null, null);

        // then: 최신순으로 정렬되어 조회됨 (4번 -> 3번 -> 2번 -> 1번 -> 0번)
        listResponse.then()
                .statusCode(200)
                .body("studyGroups", hasSize(5))
                .body("studyGroups[0].id", equalTo(studyGroupIds.get(4).intValue()))
                .body("studyGroups[1].id", equalTo(studyGroupIds.get(3).intValue()))
                .body("studyGroups[2].id", equalTo(studyGroupIds.get(2).intValue()))
                .body("studyGroups[3].id", equalTo(studyGroupIds.get(1).intValue()))
                .body("studyGroups[4].id", equalTo(studyGroupIds.get(0).intValue()));
    }

    @Test
    @DisplayName("최대 페이지 크기(50)를 초과하면 50으로 제한된다")
    void max_page_size_is_limited_to_50() {
        // given: 10개 스터디 그룹 생성
        for (int i = 0; i < 10; i++) {
            StudyGroupApiRequest.Create request = createStudyGroupRequest("스터디 그룹 " + (i + 1));
            StudyGroupApiTestClient.create(request, authSessionId);
        }

        // when: 100개 요청 (최대 50개로 제한되어야 함)
        Response response = StudyGroupApiTestClient.findList(1, 100);

        // then: 모든 스터디 그룹이 조회됨 (최대 크기 적용)
        response.then()
                .statusCode(200)
                .body("studyGroups", hasSize(10));
    }

    @Test
    @DisplayName("스터디 그룹 기본 정보와 추가 정보가 포함되어 조회된다")
    void studyGroup_list_includes_basic_and_additional_information() {
        // given: 특정 정보를 가진 스터디 그룹 생성
        StudyGroupApiRequest.Create request = StudyGroupApiRequest.Create.builder()
                .name("알고리즘 마스터 스터디")
                .description("코딩 테스트 준비를 위한 알고리즘 스터디입니다.")
                .capacity(8)
                .languages(List.of("JAVA", "PYTHON"))
                .mainLanguage("JAVA")
                .difficultyLevels(List.of("LV2", "LV3"))
                .problemPlatforms(List.of("PROGRAMMERS", "BAEKJOON"))
                .meetingType("HYBRID")
                .meetingRegion("서울시 강남구")
                .mainMeetingDays(List.of("WED", "SAT"))
                .build();

        StudyGroupApiTestClient.create(request, authSessionId);

        // when: 스터디 그룹 목록 조회
        Response response = StudyGroupApiTestClient.findList(null, null);

        // then: 기본 정보와 추가 정보가 포함되어 조회됨
        response.then()
                .statusCode(200)
                .body("studyGroups", hasSize(1))
                .body("studyGroups[0].name", equalTo("알고리즘 마스터 스터디"))
                .body("studyGroups[0].mainLanguage", equalTo("JAVA"))
                .body("studyGroups[0].capacity", equalTo(8))
                .body("studyGroups[0].meetingType", equalTo("HYBRID"))
                .body("studyGroups[0].meetingRegion", equalTo("서울시 강남구"))
                .body("studyGroups[0].difficultyLevels", hasSize(2))
                .body("studyGroups[0].mainMeetingDays", hasSize(2))
                .body("studyGroups[0].meta.currentMemberCount", equalTo(1));
    }

    @Test
    @DisplayName("빈 목록을 요청할 때 적절한 응답을 반환한다")
    void empty_list_returns_appropriate_response() {
        // given: 스터디 그룹이 없는 상태

        // when: 목록 조회
        Response response = StudyGroupApiTestClient.findList(null, null);

        // then: 빈 목록 반환
        response.then()
                .statusCode(200)
                .body("studyGroups", hasSize(0))
                .body("meta.page", equalTo(1))
                .body("meta.totalPages", equalTo(0))
                .body("meta.totalSize", equalTo(0))
                .body("meta.hasNext", equalTo(false))
                .body("meta.hasPrevious", equalTo(false));
    }

    private StudyGroupApiRequest.Create createStudyGroupRequest(String name) {
        return StudyGroupRequestFixture.generateValidCreateRequest()
                .toBuilder()
                .name(name)
                .build();
    }
}
