package dev.biddan.nubblev2.user.interest;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.user.interest.controller.dto.UserInterestApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserInterestApiTestClient {

    public static Response set(UserInterestApiRequest.Set request, String authSessionId) {
        return given()
                .contentType(ContentType.JSON)
                .cookie(AUTH_SESSION_COOKIE_NAME, authSessionId)
                .body(request)
                .when()
                .put("/api/v1/user/interests")
                .then()
                .log().ifError()
                .extract().response();
    }
}
