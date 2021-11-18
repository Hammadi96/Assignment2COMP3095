package ca.gb.comp3095.foodrecipe.controller.eventplans;

import ca.gb.comp3095.foodrecipe.model.domain.EventPlan;
import ca.gb.comp3095.foodrecipe.model.domain.User;

public class EventPlanConverter {

    public static EventPlanDto toDto(final EventPlan eventPlan) {
        return EventPlanDto.builder()
                .id(eventPlan.getId())
                .title(eventPlan.getTitle())
                .description(eventPlan.getDescription())
                .dueBy(eventPlan.getDueDate())
                .userId(eventPlan.getUser().getId())
                .build();
    }

    public static EventPlan toDomain(final EventPlanDto eventPlanDto) {
        return EventPlan.builder()
                .id(eventPlanDto.getId())
                .title(eventPlanDto.getTitle())
                .description(eventPlanDto.getDescription())
                .dueDate(eventPlanDto.getDueBy())
                .user(User.builder().id(eventPlanDto.getUserId()).build())
                .build();
    }
}
