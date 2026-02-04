package ru.yandex.praktikum;

import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }
}
