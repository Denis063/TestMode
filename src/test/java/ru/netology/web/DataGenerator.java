package ru.netology.web;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {

    private static final Faker faker = new Faker(new Locale("en"));
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private DataGenerator() {
    }

    static void makeRequest(UserInfo userInfo) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateUser(String status) {
            var user = new UserInfo(faker.name().fullName(), faker.internet().password(), status);
            makeRequest(user);
            return user;
        }

        public static UserInfo generateWrongLoginUser(String status) {
            var password = faker.internet().password();
            makeRequest(new UserInfo(faker.name().firstName(), password, status));
            return new UserInfo(faker.name().firstName(), password, status);
        }

        public static UserInfo generateWrongPasswordUser(String status) {
            var login = faker.name().firstName();
            makeRequest(new UserInfo(login, faker.internet().password(), status));
            return new UserInfo(login, faker.internet().password(), status);
        }
    }
}