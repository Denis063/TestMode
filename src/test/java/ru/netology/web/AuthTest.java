package ru.netology.web;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.web.DataGenerator.Registration.*;

public class AuthTest {
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    private void sendLoginForm(String login, String password) {

        $("[data-test-id='login'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='login'] input").setValue(login);
        $("[data-test-id='password'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='password'] input").setValue(password);
        $$("[data-test-id='action-login'] .button__text").find(exactText("Продолжить")).click();
    }
    @Test
    void shouldSendValidData() {
        var validUser = generateUser("active");
        $("[data-test-id=login] input").setValue(validUser.getLogin());
        $("[data-test-id=password] input").setValue(validUser.getPassword());
        $("button[data-test-id=action-login]").click();
        $(withText("Личный кабинет")).shouldBe(visible);
    }

    @Test
    void shouldSendBlockedUser() {
        var blockedUser = generateUser("blocked");
        $("[data-test-id=login] input").setValue(blockedUser.getLogin());
        $("[data-test-id=password] input").setValue(blockedUser.getPassword());
        $("button[data-test-id=action-login]").click();
        $(withText("Пользователь заблокирован")).shouldBe(visible);
    }

    @Test
    void shouldGetErrorIfWrongPassword() {
        UserInfo wrongPasswordUser = generateWrongPasswordUser("active");
        $("[data-test-id=login] input").setValue(wrongPasswordUser.getLogin());
        $("[data-test-id=password] input").setValue(wrongPasswordUser.getPassword());
        $("button[data-test-id=action-login]").click();
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    void shouldGetErrorIfWrongLogin() {
        UserInfo wrongLoginUser = generateWrongLoginUser("active");
        $("[data-test-id=login] input").setValue(wrongLoginUser.getLogin());
        $("[data-test-id=password] input").setValue(wrongLoginUser.getPassword());
        $("button[data-test-id=action-login]").click();
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    void shouldCheckWithEmptyFields() {
        $("button[data-test-id=action-login]").click();
        $(withText("Поле обязательно для заполнения")).shouldBe(visible);
    }

    @Test
    void shouldUnregisteredUser() {
        UserInfo unregisteredUser = getUser("active");
        sendLoginForm(unregisteredUser.getLogin(), unregisteredUser.getPassword());
        $("[data-test-id='error-notification'] .notification__title").shouldBe(visible, Duration.ofSeconds(5));
        $("[data-test-id='error-notification'] .notification__title").shouldHave(Condition.text("Ошибка"), Duration.ofSeconds(5));
        $("[data-test-id='error-notification'] .notification__content").shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(5));
    }
}