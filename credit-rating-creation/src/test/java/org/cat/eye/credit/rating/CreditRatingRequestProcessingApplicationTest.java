package org.cat.eye.credit.rating;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"},
classes = {TestCreditRatingRequestProcessingApplication.class})
//@Import({TestCreditRatingRequestProcessingApplication.class})
class CreditRatingRequestProcessingApplicationTest {

    @Test
    void contextCreate() {

    }

}