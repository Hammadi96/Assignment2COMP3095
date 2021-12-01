package ca.gb.comp3095.foodrecipe.controller.eventplans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class EventPlanDto {
    Instant createdOn;

    Instant lastUpdated;

    Long id;

    String type;

    Long attendees;

    String mealPlan;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime dueBy;

    Long userId;
}
