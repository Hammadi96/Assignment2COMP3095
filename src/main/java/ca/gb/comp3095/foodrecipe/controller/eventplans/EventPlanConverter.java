package ca.gb.comp3095.foodrecipe.controller.eventplans;

import ca.gb.comp3095.foodrecipe.model.domain.EventPlan;
import ca.gb.comp3095.foodrecipe.model.domain.User;

public class EventPlanConverter {

    public static EventPlanDto toDto(final EventPlan eventPlan) {
        return EventPlanDto.builder()
                .id(eventPlan.getId())
                .type(eventPlan.getType())
                .mealPlan(eventPlan.getMealsDescription())
                .attendees(eventPlan.getAttendees())
                .dueBy(eventPlan.getDueDate())
                .userId(eventPlan.getUser().getId())
                .build();
    }

    public static EventPlan toDomain(final EventPlanDto eventPlanDto) {
        return EventPlan.builder()
                .id(eventPlanDto.getId())
                .type(eventPlanDto.getType())
                .attendees(eventPlanDto.getAttendees())
                .mealsDescription(eventPlanDto.getMealPlan())
                .dueDate(eventPlanDto.getDueBy())
                .user(User.builder().id(eventPlanDto.getUserId()).build())
                .build();
    }
}
