package dev.biddan.nubblev2.study.problem;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.study.problem.controller.dto.ProblemApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ProblemApiTestClient {

    public static Response createProblem(Long studyGroupId, ProblemApiRequest.Create request, String authSessionId) {
        return given()
                .cookie(AUTH_SESSION_COOKIE_NAME, authSessionId)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/study-groups/{studyGroupId}/problems", studyGroupId)
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response getProblems(Long studyGroupId, int page, int limit) {
        return given()
                .queryParam("page", page)
                .queryParam("limit", limit)
                .when()
                .get("/api/v1/study-groups/{studyGroupId}/problems", studyGroupId)
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response deleteProblem(Long studyGroupId, Long problemId, String authSessionId) {
        return given()
                .cookie(AUTH_SESSION_COOKIE_NAME, authSessionId)
                .when()
                .delete("/api/v1/study-groups/{studyGroupId}/problems/{problemId}", studyGroupId, problemId)
                .then()
                .log().ifError()
                .extract().response();
    }
}