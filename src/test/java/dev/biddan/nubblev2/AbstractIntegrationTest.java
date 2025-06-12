package dev.biddan.nubblev2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.biddan.nubblev2.auth.repository.AuthSessionRepository;
import dev.biddan.nubblev2.auth.repository.LoginLogRepository;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.applicationform.repository.StudyApplicationFormRepository;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import dev.biddan.nubblev2.user.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
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

    @Autowired
    protected StudyApplicationFormRepository studyApplicationFormRepository;


    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    static {
        postgres.start();

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())               // Java 8 날짜/시간 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 배열 → ISO-8601

        RestAssured.config = RestAssured.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> mapper));
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
