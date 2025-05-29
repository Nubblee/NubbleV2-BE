package dev.biddan.nubblev2.auth;

import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class AuthApiTestClient {

    public static Response login(AuthApiRequest.Login request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .log().ifError()
                .extract().response();
    }

    public static Response logout(Cookie sessionCookie) {
        return given()
                .cookie(sessionCookie)
                .when()
                .post("/api/v1/auth/logout")
                .then()
                .extract().response();
    }
}
