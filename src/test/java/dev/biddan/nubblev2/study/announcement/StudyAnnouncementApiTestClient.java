package dev.biddan.nubblev2.study.announcement;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;

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

    public static Response findList(List<String> statuses, Integer page, Integer size) {
        RequestSpecification requestSpec = given();

        if (statuses != null) {
            requestSpec.queryParam("statuses", statuses);
        }

        if (page != null) {
            requestSpec = requestSpec.queryParam("page", page);
        }

        if (size != null) {
            requestSpec = requestSpec.queryParam("size", size);
        }

        return requestSpec
                .when()
                .get("/api/v1/study-announcements")
                .then()
                .log().ifError()
                .extract().response();
    }
}
