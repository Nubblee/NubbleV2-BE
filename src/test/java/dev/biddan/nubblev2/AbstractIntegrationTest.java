package dev.biddan.nubblev2;

import dev.biddan.nubblev2.auth.repository.AuthSessionRepository;
import dev.biddan.nubblev2.auth.repository.LoginLogRepository;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import dev.biddan.nubblev2.user.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestClockConfig.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    protected int port;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected LoginLogRepository loginLogRepository;

    @Autowired
    protected AuthSessionRepository authSessionRepository;

    @Autowired
    protected StudyGroupRepository studyGroupRepository;

    @Autowired
    protected StudyAnnouncementRepository studyAnnouncementRepository;

    @Autowired
    protected FixableClock systemClock;


    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void destroy() {
        databaseCleaner.cleanDatabase();
        systemClock.reset();
    }
}
