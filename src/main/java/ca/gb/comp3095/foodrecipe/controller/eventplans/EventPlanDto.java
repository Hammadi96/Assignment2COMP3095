package ca.gb.comp3095.foodrecipe.controller.eventplans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class EventPlanDto {
    Instant createdOn;

    Instant lastUpdated;

    Long id;

    String title;

    String description;

    Instant dueBy;

    Long userId;
}
