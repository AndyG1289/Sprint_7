package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest extends BaseTest {

    private final List<String> colors;
    private Integer track;

    public OrderCreateTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвет самоката: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {null}
        });
    }

    @Test
    public void orderCanBeCreatedWithDifferentColors() {
        Order order = defaultOrder(colors);

        Response response = createOrder(order);

        track = response
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract()
                .path("track");
    }

    @After
    public void cancelOrder() {
        if (track != null) {
            cancelOrderByTrack(track);
        }
    }

    @Step("Создание заказа")
    private Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Отмена заказа")
    private void cancelOrderByTrack(int track) {
        given()
                .header("Content-type", "application/json")
                .body(Map.of("track", track))
                .when()
                .put("/api/v1/orders/cancel");
    }

    private Order defaultOrder(List<String> colors) {
        return new Order(
                "Ivan",
                "Ivanov",
                "Moscow, Red Square",
                "4",
                "+79990000000",
                5,
                LocalDate.now().plusDays(1).toString(),
                "Test order",
                colors
        );
    }
}
