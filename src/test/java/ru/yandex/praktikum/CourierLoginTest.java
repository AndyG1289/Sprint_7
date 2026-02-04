package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

public class CourierLoginTest extends BaseTest {

    private Courier courier;

    @Test
    public void courierCanLogin() {
        courier = CourierGenerator.randomCourier();
        createCourier(courier);

        CourierCredentials credentials =
                CourierGenerator.credentialsFrom(courier);

        loginCourier(credentials)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    public void errorIfLoginOrPasswordIsWrong() {
        CourierCredentials wrongCredentials =
                new CourierCredentials("wrongLogin", "wrongPassword");

        loginCourier(wrongCredentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void errorIfPasswordIsMissing() {
        courier = CourierGenerator.randomCourier();
        createCourier(courier);

        Map<String, String> body = new HashMap<>();
        body.put("login", courier.getLogin());
        // password НЕ кладём

        given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(anyOf(is(400), is(504)));
    }

    @Test
    public void errorIfCourierDoesNotExist() {
        CourierCredentials credentials =
                new CourierCredentials("notExisting", "password");

        loginCourier(credentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void cleanUp() {
        if (courier != null) {
            deleteCourier(courier);
        }
    }

    // ---------- steps ----------

    @Step("Создание курьера")
    private void createCourier(Courier courier) {
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Логин курьера")
    private Response loginCourier(CourierCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера")
    private void deleteCourier(Courier courier) {
        CourierCredentials credentials =
                CourierGenerator.credentialsFrom(courier);

        Integer id =
                loginCourier(credentials)
                        .then()
                        .extract()
                        .path("id");

        if (id != null) {
            given()
                    .when()
                    .delete("/api/v1/courier/{id}", id);
        }
    }
}
