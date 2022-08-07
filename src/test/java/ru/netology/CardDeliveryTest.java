package ru.netology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void startBrowser() {
        open("http://localhost:9999/");
    }

    @Test
    public void cardDeliveryOrder() {

        String meetDate = generateDate(4);

        $x("//*[@placeholder='Город']").setValue("Краснодар");

        // Так и не понял, почему не отрабатывает .clear() с последующим вводом текста
        // Нагуглил, как очищать поле через "выделить все" с последующим "удалить". Получился костыль? Вроде работает))
        $x("//*[@placeholder='Дата встречи']").sendKeys(Keys.LEFT_CONTROL + "a");
        $x("//*[@placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $x("//*[@placeholder='Дата встречи']").setValue(meetDate);
        $x("//*[@placeholder='Дата встречи']").pressEscape();

        fillFullName("Изъяславский Всеволод");
        fillPhoneNum("+71112223334");
        enableAgreementCheckbox();
        clickToBookBtn();

        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .should(text("Встреча успешно забронирована на " + meetDate))
                .shouldBe(visible);
    }


    @Test
    public void cardDeliveryOrderWithSelectorAndCalendar() {

        String city = "Краснодар";

        LocalDate current = LocalDate.now();
        LocalDate required = LocalDate.now().plusDays(7);
        String formattedDate = required.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        // Оставить только 2 первые буквы от названия города: city.substring(0, 2)
        $x("//*[@placeholder='Город']").setValue(city.substring(0, 2));
        // Кликнуть по полному названию города в раскрывшемся списке
        $$(".menu-item__control").filterBy(text(city)).first().click();

        $x("//*[@placeholder='Дата встречи']/following-sibling::span").click();
        if (current.getMonthValue() != required.getMonthValue()) {
            $("[data-step='1']").click();
        }
        $$("tr td").findBy(text(String.valueOf(required.getDayOfMonth()))).click();

        fillFullName("Изъяславскийъ Всеволод");
        fillPhoneNum("+71112223335");
        enableAgreementCheckbox();
        clickToBookBtn();

        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .should(text("Встреча успешно забронирована на " + formattedDate))
                .shouldBe(visible);
    }

    private String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private void fillFullName(String fullName) {
        $x("//*[@name='name']").setValue(fullName);
    }

    private void fillPhoneNum(String phoneNum) {
        $x("//*[@name='phone']").setValue(phoneNum);
    }

    private void enableAgreementCheckbox() {
        $x("//*[@class='checkbox__box']").click();
    }

    private void clickToBookBtn() {
        $x("//button[.//span[text()='Забронировать']]").click();
    }
}
