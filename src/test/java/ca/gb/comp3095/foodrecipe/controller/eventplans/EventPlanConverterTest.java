package ca.gb.comp3095.foodrecipe.controller.eventplans;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


class EventPlanConverterTest {

    @Test
    void testLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.parse("2022-11-22T22:23");
        System.out.println(localDateTime);
    }
}