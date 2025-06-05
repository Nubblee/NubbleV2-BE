package dev.biddan.nubblev2.study.applicationfrom;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

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
}
