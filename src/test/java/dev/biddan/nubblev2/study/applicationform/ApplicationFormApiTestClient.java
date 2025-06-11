package dev.biddan.nubblev2.study.applicationform;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
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

    public static Response findList(Long announcementId, String keyset, String status, String authSessionId) {
        RequestSpecification requestSpec = given();

        if (authSessionId != null) {
            requestSpec = requestSpec.cookie(AUTH_SESSION_COOKIE_NAME, authSessionId);
        }

        if (keyset != null) {
            requestSpec = requestSpec.queryParam("keysetPage", keyset);
        }

        if (status != null) {
            requestSpec = requestSpec.queryParam("status", status);
        }

        return requestSpec
                .when()
                .get("/api/v1/study-announcements/{announcementId}/application-forms", announcementId)
                .then()
                .log().ifError()
                .extract().response();
    }
}
