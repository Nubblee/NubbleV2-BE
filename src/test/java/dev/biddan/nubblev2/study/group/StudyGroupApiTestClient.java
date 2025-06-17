package dev.biddan.nubblev2.study.group;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Java6BDDSoftAssertionsProvider;

public class StudyGroupApiTestClient {

    public static Response create(StudyGroupApiRequest.Create request, String authSessionId) {
        return given()
                .contentType(ContentType.JSON)
                .cookie(AUTH_SESSION_COOKIE_NAME, authSessionId)
                .body(request)
                .when()
                .post("/api/v1/study-groups")
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response update(Long studyGroupId, StudyGroupApiRequest.Create request, String authSessionId) {
        return given()
                .contentType(ContentType.JSON)
                .cookie(AUTH_SESSION_COOKIE_NAME, authSessionId)
                .body(request)
                .when()
                .patch("/api/v1/study-groups/{id}", studyGroupId)
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response getMyStudyGroups(String authSessionId) {
        RequestSpecification requestSpec = given();

        if (authSessionId != null) {
            requestSpec.cookie(AUTH_SESSION_COOKIE_NAME, authSessionId);
        }

        return requestSpec
                .when()
                .get("/api/v1/user/study-groups")
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response getById(Long studyGroupId) {
        return given()
                .when()
                .get("/api/v1/study-groups/{id}", studyGroupId)
                .then()
                .log().ifError()
                .extract().response();
    }
}
