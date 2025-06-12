package dev.biddan.nubblev2.study.applicationform;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.assertj.core.api.Java6BDDSoftAssertionsProvider;

public class ApplicationFormApiTestClient {

    public static Response submit(Long announcementId, ApplicationFormApiRequest.Submit request, String authSessionId) {
        return given()
                .cookie(AUTH_SESSION_COOKIE_NAME, authSessionId)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/study-announcements/{announcementId}/application-forms", announcementId)
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response findList(Long announcementId, Long lastId, LocalDateTime lastSubmittedAt, String status, String authSessionId) {
        RequestSpecification requestSpec = given();

        if (authSessionId != null) {
            requestSpec.cookie(AUTH_SESSION_COOKIE_NAME, authSessionId);
        }

        if (lastId != null && lastSubmittedAt != null) {
            requestSpec.queryParam("lastSubmittedAt", lastSubmittedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .queryParam("lastId", lastId);
        }

        if (status != null) {
            requestSpec.queryParam("status", status);
        }

        return requestSpec
                .when()
                .get("/api/v1/study-announcements/{announcementId}/application-forms", announcementId)
                .then()
                .log().ifError()
                .extract().response();
    }
}
