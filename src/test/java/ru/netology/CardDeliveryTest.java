package ru.netology;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @Test
    public void cardDeliveryOrder() {

        // Задать формат даты
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        // Добавить к текущему 4 дня
        c.add(Calendar.DATE, 4);
        String meetDate = sdf.format(c.getTime());

        open("http://localhost:9999/");

        $x("//*[@placeholder='Город']").setValue("Краснодар");

        // Так и не понял, почему не отрабатывает .clear() с последующим вводом текста
        // Нагуглил, как очищать поле через "выделить все" с последующим "удалить". Получился костыль? Вроде работает))
        $x("//*[@placeholder='Дата встречи']").sendKeys(Keys.LEFT_CONTROL + "a");
        $x("//*[@placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $x("//*[@placeholder='Дата встречи']").setValue(meetDate);
        $x("//*[@placeholder='Дата встречи']").pressEscape();

        $x("//*[@name='name']").setValue("Изъяславский Всеволод");
        $x("//*[@name='phone']").setValue("+71112223334");

        $x("//*[@class='checkbox__box']").click();
        $x("//button[.//span[text()='Забронировать']]").click();

        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .should(text("Встреча успешно забронирована на " + meetDate))
                .shouldBe(visible);

        System.out.println("Тест на создание заявки пройден успешно");
    }


    @Test
    public void cardDeliveryOrderWithSelectorAndCalendar() {

        String city = "Краснодар";

        // Задать формат даты
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        // Добавить к текущему 7 дней
        c.add(Calendar.DATE, 7);
        String meetDate = sdf.format(c.getTime());

        open("http://localhost:9999/");

        // Оставить только 2 первые буквы от названия города: city.substring(0, 2)
        $x("//*[@placeholder='Город']").setValue(city.substring(0, 2));
        // Кликнуть по полному названию города в раскрывшемся списке
        $$(".menu-item__control").filterBy(text(city)).first().click();

        // Пояснение к блоку:
        // 1. Кликнуть по иконке "календарь"
        $x("//*[@placeholder='Дата встречи']/following-sibling::span").click();
        // 2. Посчитать количество дней в месяце (все видимые даты)
        int numOfDays = $$x("//td[@role='gridcell' and @data-day or contains(@class,'calendar__day_type_off') or contains(@class,'calendar__day_type_weekend-off')]")
                .size();
        // 3. Получить текущее число месяца
        int currentDay = Integer.parseInt($x("//td[contains(@class,'calendar__day_state_today')]").getText());
        // Если до конца месяца более 6 дней, то кликнуть по дате на след неделе
        if ((numOfDays - currentDay) > 6) {
            $x("//td[text()='" + (currentDay + 7) + "']").click();
        } else {
            // В противном случае, переключить месяц и там кликнуть по соответствующей дате. Пример:
            // Сегодня 27 число, Всего дней в месяце: 31
            // 7 - (31 - 27) = 7 - 4 = 3. Итого: кликнуть по 3 числу
            $x("//*[@data-step='1']").click();
            $x("//td[text()='" + (7 - (numOfDays - currentDay)) + "']").click();
        }

        $x("//*[@name='name']").setValue("Изъяславский Всеволод");
        $x("//*[@name='phone']").setValue("+71112223334");

        $x("//*[@class='checkbox__box']").click();
        $x("//button[.//span[text()='Забронировать']]").click();

        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .should(text("Встреча успешно забронирована на " + meetDate))
                .shouldBe(visible);

        System.out.println("Тест на создание заявки с использованием календаря и выпадающего списка пройден успешно");
    }
}
