package dev.biddan.nubblev2.user;

import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserApiTestClient {

    public static Response register(UserApiRequest.Register request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/users")
                .then()
                .log().ifError()
                .extract().response();
    }
}
