package ru.yandex.praktikum;

import java.util.UUID;

public class CourierGenerator {

    public static Courier randomCourier() {
        return new Courier(
                "login_" + UUID.randomUUID(),
                "password_" + UUID.randomUUID(),
                "name_" + UUID.randomUUID()
        );
    }

    public static CourierCredentials credentialsFrom(Courier courier) {
        return new CourierCredentials(
                courier.getLogin(),
                courier.getPassword()
        );
    }
}
