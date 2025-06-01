package dev.biddan.nubblev2.study.announcement;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class StudyAnnouncementApiTestClient {

    public static Response create(StudyAnnouncementApiRequest.Create request, String authSessionId) {
        return given()
                .cookie(AUTH_SESSION_COOKIE_NAME, authSessionId)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/study-announcements")
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response findList(StudyAnnouncementApiRequest.FindList request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/study-announcements")
                .then()
                .log().ifError()
                .extract().response();
    }
}
