package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.domain.EventPlan;
import ca.gb.comp3095.foodrecipe.model.domain.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventPlanService {

    EventPlan createNew(EventPlan eventPlan);

    EventPlan update(EventPlan eventPlan);

    Optional<EventPlan> findById(Long id);

    List<EventPlan> findAllForUser(Long userId);

    List<EventPlan> findAllForUser(User user);

    List<EventPlan> findAllAfter(Instant after);

    List<EventPlan> findAllForUserAndAfter(User user, Instant after);

    List<EventPlan> findAllForUserIdAndAfter(Long userId, Instant after);

    boolean deleteById(Long id);

}
