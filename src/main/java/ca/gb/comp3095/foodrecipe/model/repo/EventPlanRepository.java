package ca.gb.comp3095.foodrecipe.model.repo;

import ca.gb.comp3095.foodrecipe.model.domain.EventPlan;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface EventPlanRepository extends JpaRepository<EventPlan, Long> {
    List<EventPlan> findAllByUserId(Long userId);

    List<EventPlan> findAllByUser(User user);

    List<EventPlan> findTopByDueDateAfter(Instant dateOffset);

    List<EventPlan> findTopByUserAndDueDateAfter(User user, Instant dateOffset);

    List<EventPlan> findTopByDueDateBefore(Instant dateOffset);

    List<EventPlan> findTopByDueDateBetween(Instant startDate, Instant endDate);

}
