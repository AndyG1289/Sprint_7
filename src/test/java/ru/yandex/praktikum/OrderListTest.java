package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest extends BaseTest {

    @Test
    public void orderListCanBeReceived() {
        Response response = getOrders();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получение списка заказов")
    private Response getOrders() {
        return given()
                .when()
                .get("/api/v1/orders");
    }
}
