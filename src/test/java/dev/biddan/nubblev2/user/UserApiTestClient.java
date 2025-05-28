package dev.biddan.nubblev2.user;

import static io.restassured.RestAssured.given;

import dev.biddan.nubblev2.user.controller.UserApiRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
public class UserApiTestClient {

    public Response register(UserApiRequest.Register request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/users")
                .then()
                .log().ifError()
                .extract().response();
    }
}
