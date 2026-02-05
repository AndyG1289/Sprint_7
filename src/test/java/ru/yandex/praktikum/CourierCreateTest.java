package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;

public class CourierCreateTest extends BaseTest {

    private Courier courier;

    @Test
    public void courierCanBeCreated() {
        courier = CourierGenerator.randomCourier();

        Response response = createCourier(courier);

        response
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    public void cannotCreateTwoIdenticalCouriers() {
        courier = CourierGenerator.randomCourier();

        createCourier(courier)
                .then()
                .statusCode(201);

        createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    public void cannotCreateCourierWithoutPassword() {
        Courier courierWithoutPassword =
                new Courier("login_" + System.currentTimeMillis(), null, "Name");

        createCourier(courierWithoutPassword)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void errorIfLoginAlreadyExists() {
        courier = CourierGenerator.randomCourier();

        createCourier(courier)
                .then()
                .statusCode(201);

        Courier anotherCourierWithSameLogin =
                new Courier(
                        courier.getLogin(),
                        "anotherPassword",
                        "AnotherName"
                );

        createCourier(anotherCourierWithSameLogin)
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    public void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin =
                new Courier(null, "password123", "Name");

        createCourier(courierWithoutLogin)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void cleanUp() {
        if (courier != null) {
            deleteCourier(courier);
        }
    }

    @Step("Создание курьера")
    private Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Удаление курьера")
    private void deleteCourier(Courier courier) {
        CourierCredentials credentials =
                CourierGenerator.credentialsFrom(courier);

        Integer id =
                given()
                        .header("Content-type", "application/json")
                        .body(credentials)
                        .when()
                        .post("/api/v1/courier/login")
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
