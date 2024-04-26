package com.exam.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilsTest {

    @Test
    void createToken() {
        String token = JwtUtils.createToken((1L), "ttt", 1000 * 60 * 60 * 24);
        System.out.println(token);
    }

    //eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJpZCI6MSwibWVzc2FnZSI6InR0dCIsInN1YiI6ImFsbC11c2VyIiwiZXhwIjoxNzA4OTQ2Mjc0LCJqdGkiOiI4MTRhMmY4My1lMzE4LTQ3NmYtYWNhMi1jMzY5OGFjMjFhYjkifQ.7V3UiTuoOxvVXwKER55SiA7L0b6dAd84kJyaSZbc_A4

    @Test
    void testCreateToken() {
    }

    @Test
    void testCreateToken1() {
    }

    @Test
    void checkToken() {
        System.out.println(JwtUtils.checkToken("eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJpZCI6MSwibWVzc2FnZSI6InR0dCIsInN1YiI6ImFsbC11c2VyIiwiZXhwIjoxNzA4OTQ2Mjc0LCJqdGkiOiI4MTRhMmY4My1lMzE4LTQ3NmYtYWNhMi1jMzY5OGFjMjFhYjkifQ.7V3UiTuoOxvVXwKER55SiA7L0b6dAd84kJyaSZbc_A4"));
    }

    @Test
    void getIdByToken() {
        System.out.println(JwtUtils.getIdByToken("eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJpZCI6MSwic3ViIjoiYWxsLXVzZXIiLCJleHAiOjE3MDk0NzEwMzUsImp0aSI6ImU5OTRjNDBhLWE0ZTUtNGQ3OC05YTA4LTM2N2U3MTdjNTRjNSJ9.N-r5dwBhop1qtKjIQjk4OyF_1Wcgt3U1fLeA_OOC0QY"));
    }

    @Test
    void getMessageByToken() {
        System.out.println(JwtUtils.getMessageByToken("eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJpZCI6MSwibWVzc2FnZSI6InR0dCIsInN1YiI6ImFsbC11c2VyIiwiZXhwIjoxNzA4OTQ2Mjc0LCJqdGkiOiI4MTRhMmY4My1lMzE4LTQ3NmYtYWNhMi1jMzY5OGFjMjFhYjkifQ.7V3UiTuoOxvVXwKER55SiA7L0b6dAd84kJyaSZbc_A4"));
    }
}