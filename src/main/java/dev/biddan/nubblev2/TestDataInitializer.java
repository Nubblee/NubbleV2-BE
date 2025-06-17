package dev.biddan.nubblev2;

import dev.biddan.nubblev2.study.announcement.service.StudyAnnouncementService;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementCommand;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo.Basic;
import dev.biddan.nubblev2.study.applicationform.service.ApplicationFormService;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormCommand;
import dev.biddan.nubblev2.study.group.service.StudyGroupService;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import dev.biddan.nubblev2.user.service.UserService;
import dev.biddan.nubblev2.user.service.dto.UserCommand;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import dev.biddan.nubblev2.user.service.dto.UserInfo.Private;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class TestDataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final StudyGroupService studyGroupService;
    private final StudyAnnouncementService studyAnnouncementService;
    private final ApplicationFormService applicationFormService;

    public TestDataInitializer(UserService userService, StudyGroupService studyGroupService,
            StudyAnnouncementService studyAnnouncementService, ApplicationFormService applicationFormService) {
        this.userService = userService;
        this.studyGroupService = studyGroupService;
        this.studyAnnouncementService = studyAnnouncementService;
        this.applicationFormService = applicationFormService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Private> users = createUsers();

        List<TestStudyGroup> studyGroups = createStudyGroups(users);

        List<TestAnnouncement> studyAnnouncements = createStudyAnnouncements(studyGroups);

        createApplicationForms(users, studyAnnouncements);
    }

    private List<UserInfo.Private> createUsers() {
        List<UserCommand.Register> userCommands = List.of(
                // leader1
                UserCommand.Register.builder()
                        .loginId("leader1").nickname("알고마스터")
                        .password("password123!").preferredArea("서울시 강남구")
                        .email("leader1@example.com").build(),
                // member1 ~ member6
                UserCommand.Register.builder()
                        .loginId("member1").nickname("열공학생")
                        .password("password123!").preferredArea("서울시 강남구")
                        .email("member1@example.com").build(),
                UserCommand.Register.builder()
                        .loginId("member2").nickname("신입개발자")
                        .password("password123!").preferredArea("서울시 역삼동")
                        .email("member2@example.com").build(),
                UserCommand.Register.builder()
                        .loginId("member3").nickname("프론트개발자")
                        .password("password123!").preferredArea("인천시 부평구")
                        .email("member3@example.com").build(),
                UserCommand.Register.builder()
                        .loginId("member4").nickname("자바개발자")
                        .password("password123!").preferredArea("서울시 마포구")
                        .email("member4@example.com").build(),
                UserCommand.Register.builder()
                        .loginId("member5").nickname("파이썬러버")
                        .password("password123!").preferredArea("서울시 홍대입구")
                        .email("member5@example.com").build(),
                UserCommand.Register.builder()
                        .loginId("member6").nickname("CPP고수")
                        .password("password123!").preferredArea("서울시 서초구")
                        .email("member6@example.com").build()
        );

        List<UserInfo.Private> users = new ArrayList<>();

        userCommands.forEach(command -> {
            Private user = userService.register(command);
            users.add(user);
        });

        return users;
    }

    private List<TestStudyGroup> createStudyGroups(List<UserInfo.Private> users) {
        List<StudyGroupCommand.Create> studyGroupCommands = List.of(
                StudyGroupCommand.Create.builder()
                        .name("프로그래머스 레벨업 스터디")
                        .description("Level 1~3 문제를 단계별로 푸는 스터디")
                        .capacity(8).languages(List.of("JAVA","PYTHON"))
                        .mainLanguage("JAVA").difficultyLevels(List.of("LV1","LV2","LV3"))
                        .problemPlatforms(List.of("PROGRAMMERS"))
                        .meetingType("HYBRID").meetingRegion("서울시 강남구 역삼동")
                        .mainMeetingDays(List.of("WED","SAT")).build(),

                StudyGroupCommand.Create.builder()
                        .name("백준 골드 달성 스터디")
                        .description("실버→골드 달성을 목표로 하는 스터디")
                        .capacity(6).languages(List.of("CPP","JAVA"))
                        .mainLanguage("CPP").difficultyLevels(List.of("LV3", "LV4"))
                        .problemPlatforms(List.of("BAEKJOON"))
                        .meetingType("OFFLINE").meetingRegion("서울시 서초구 서초동")
                        .mainMeetingDays(List.of("SUN")).build(),

                StudyGroupCommand.Create.builder()
                        .name("리트코드 마스터 스터디")
                        .description("해외 취업 목표 LeetCode 집중 스터디")
                        .capacity(12).languages(List.of("PYTHON","JAVASCRIPT"))
                        .mainLanguage("PYTHON").difficultyLevels(List.of("LV2","LV3","LV4"))
                        .problemPlatforms(List.of("LEET_CODE"))
                        .meetingType("ONLINE").meetingRegion(null)
                        .mainMeetingDays(List.of("MON","WED","FRI")).build()
        );

        List<TestStudyGroup> studyGroups = new ArrayList<>();
        List<Long> leaderIds = List.of(
                users.get(0).id(),
                users.get(0).id(),
                users.get(0).id()
        );

        for (int i = 0; i < studyGroupCommands.size(); i++) {
            Long creatorId = leaderIds.get(i);
            StudyGroupInfo.Detail info = studyGroupService.create(creatorId, studyGroupCommands.get(i));
            studyGroups.add(new TestStudyGroup(info, creatorId));
        }

        return studyGroups;
    }


    private List<TestAnnouncement> createStudyAnnouncements(List<TestStudyGroup> studyGroups) {
        List<StudyAnnouncementCommand.Create> announcementCommands = List.of(
                StudyAnnouncementCommand.Create.builder()
                        .title("프로그래머스 레벨업 스터디 3기 모집")
                        .description("""
                                이번 3기에서는 Level 2~3 문제 위주로 진행할 예정입니다.
                                매주 수요일 저녁 7시, 토요일 오후 2시에 모임을 가집니다.
                                오프라인 모임은 강남역 근처 스터디룸에서 진행됩니다.
                                """)
                        .recruitCapacity(3)
                        .endDate(LocalDate.now().plusDays(7))
                        .applicationFormContent("""
                                1. 현재 프로그래밍 경력:
                                2. 프로그래머스 최고 레벨:
                                3. 주로 사용하는 언어:
                                4. 스터디 참여 동기:
                                5. 주당 투자 가능 시간:
                                """)
                        .build(),

                StudyAnnouncementCommand.Create.builder()
                        .title("백준 골드 달성 스터디 신규 모집")
                        .description("""
                                현재 실버 티어이신 분들을 대상으로 합니다.
                                골드 티어 달성까지 함께 갑시다!
                                주 3회 온라인 문제 풀이, 주 1회 오프라인 리뷰 진행합니다.
                                """)
                        .recruitCapacity(2)
                        .endDate(LocalDate.now().plusDays(5))
                        .applicationFormContent("""
                                1. 백준 아이디:
                                2. 현재 티어:
                                3. 하루 풀이 가능한 문제 수:
                                4. 오프라인 참석 가능 여부:
                                5. 목표 달성 기간:
                                """)
                        .build()
        );

        List<TestAnnouncement> announcements = new ArrayList<>();

        for (int i = 0; i < announcementCommands.size(); i++) {
            TestStudyGroup studyGroup = studyGroups.get(i);

            Basic announcement = studyAnnouncementService.create(
                    studyGroup.studyGroup.id(),
                    studyGroup.leaderId(),
                    announcementCommands.get(i)
            );
            announcements.add(new TestAnnouncement(studyGroup, announcement));
        }

        return announcements;
    }


    private void createApplicationForms(List<UserInfo.Private> users, List<TestAnnouncement> announcements) {
        Long announcementId = announcements.get(1).announcement().id();

        String template = """
            1. 백준 아이디: member%d_baekjoon
            2. 현재 티어: 실버 %d
            3. 하루 풀이 가능한 문제 수: %d
            4. 오프라인 참석 가능 여부: 가능
            5. 목표 달성 기간: 6개월
            """;

        IntStream.rangeClosed(1, 6).forEach(i -> {
            Long applicantId = users.get(i).id();

            String content = template.formatted(i, i, i);

            applicationFormService.submit(
                    announcementId,
                    applicantId,
                    new ApplicationFormCommand.Submit(content)
            );
        });
    }

    public record TestStudyGroup(
            StudyGroupInfo.Detail studyGroup,
            Long leaderId
    ) {

    }

    public record TestAnnouncement(

            TestStudyGroup studyGroup, Basic announcement) {

    }

    private record ApplicationFormData(Long announcementId, Long applicantId, String content) {

    }
}
