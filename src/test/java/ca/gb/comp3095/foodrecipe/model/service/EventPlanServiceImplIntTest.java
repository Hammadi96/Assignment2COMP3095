package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.domain.EventPlan;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventPlanServiceImplIntTest {

    @Autowired
    EventPlanService eventPlanService;

    @Autowired
    UserService userService;

    User testUser;
    final Consumer<EventPlan> deleteEventConsumer = e -> eventPlanService.deleteById(e.getId());

    @BeforeEach
    void setUp() {
        testUser = userService.saveUser(User.builder().name("test-user-event").build());
    }

    @AfterEach
    void tearDown() {
        userService.deleteUserById(testUser.getId());
    }

    @Test
    void itLoads() {
        assertThat(eventPlanService).isNotNull();
        assertThat(userService).isNotNull();
    }


    @Test
    void itCreatesEventsForUser() {
        EventPlan event1 = EventPlan.builder().dueDate(Instant.now().plus(100, ChronoUnit.DAYS)).user(testUser).build();
        EventPlan event2 = EventPlan.builder().dueDate(Instant.now().plus(10, ChronoUnit.DAYS)).user(testUser).build();
        EventPlan event3 = EventPlan.builder().dueDate(Instant.now().plus(1, ChronoUnit.DAYS)).user(testUser).build();

        assertThat(eventPlanService.findAllForUser(testUser)).isNullOrEmpty();

        event1 = eventPlanService.createNew(event1);
        event2 = eventPlanService.createNew(event2);
        event3 = eventPlanService.createNew(event3);

        List<EventPlan> allForUser = eventPlanService.findAllForUser(testUser);
        assertThat(allForUser).contains(event1, event2, event3);

        allForUser.forEach(deleteEventConsumer);
    }

    @Test
    void itDeletesUserWithEvents() {
        EventPlan event1 = EventPlan.builder().dueDate(Instant.now().plus(100, ChronoUnit.DAYS)).user(testUser).build();
        EventPlan event2 = EventPlan.builder().dueDate(Instant.now().plus(10, ChronoUnit.DAYS)).user(testUser).build();
        event1 = eventPlanService.createNew(event1);
        event2 = eventPlanService.createNew(event2);
        List<EventPlan> eventsForTestUser = eventPlanService.findAllForUser(testUser);
        assertThat(eventsForTestUser).contains(event1, event2);

        User dummyUserAgain = userService.saveUser(User.builder().name("dummyUserAgain").build());
        EventPlan event3 = EventPlan.builder().dueDate(Instant.now().plus(1, ChronoUnit.DAYS)).user(dummyUserAgain).build();
        EventPlan event4 = EventPlan.builder().dueDate(Instant.now().plus(10, ChronoUnit.DAYS)).user(dummyUserAgain).build();
        event3 = eventPlanService.createNew(event3);
        event4 = eventPlanService.createNew(event4);

        List<EventPlan> eventsForDummyTestUser = eventPlanService.findAllForUser(dummyUserAgain);
        assertThat(eventsForDummyTestUser).contains(event3, event4);
        assertThat(eventsForDummyTestUser).doesNotContain(event1, event2);

        userService.deleteUserById(dummyUserAgain.getId());
        eventsForTestUser.forEach(deleteEventConsumer);
    }
}