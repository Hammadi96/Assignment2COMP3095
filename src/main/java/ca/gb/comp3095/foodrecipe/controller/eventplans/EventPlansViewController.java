package ca.gb.comp3095.foodrecipe.controller.eventplans;

import ca.gb.comp3095.foodrecipe.model.domain.EventPlan;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.EventPlanService;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.ERROR;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.EVENT_PLAN;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.EVENT_PLANS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.MESSAGE;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.SUCCESS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;

@Controller
@RequestMapping(value = "/eventplans", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class EventPlansViewController implements WebMvcConfigurer {
    @Autowired
    UserService userService;

    @Autowired
    EventPlanService eventPlanService;

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test(final Model model) {
        model.addAttribute(MESSAGE, "this is test message");
        return "eventplans/eventplan";
    }

    @GetMapping("/test-all")
    @PreAuthorize("hasRole('USER')")
    public String allPlans(final Model model) {
        model.addAttribute(MESSAGE, "this is test message");
        return "eventplans/eventplans";
    }

    @GetMapping(value = {"", "/", "/all"})
    @PreAuthorize("hasRole('USER')")
    public String getAllEventPlansForCurrentUser(final Principal principal, final Model model) {
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName());
            List<EventPlanDto> allEventPlans = eventPlanService.findAllForUser(userByName.orElseThrow(RuntimeException::new))
                    .stream().map(EventPlanConverter::toDto)
                    .collect(Collectors.toList());
            model.addAttribute(EVENT_PLANS, allEventPlans);
            model.addAttribute(MESSAGE, allEventPlans.size() + " event plans found!");
        } catch (Exception e) {
            log.warn("unable to get eventplans for user {}", principal.getName(), e);
        }
        return "eventplans/eventplans";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public String getEventPlanForCurrentUser(@PathVariable("id") final Long eventPlanId, final Principal principal, final Model model) {
        try {
            User userByName = userService.getUserByName(principal.getName()).orElseThrow(RuntimeException::new);

            EventPlanDto eventPlanDto = eventPlanService.findById(eventPlanId)
                    .map(EventPlanConverter::toDto).orElseThrow(RuntimeException::new);
            model.addAttribute(EVENT_PLAN, eventPlanDto);
        } catch (Exception e) {
            log.warn("unable to get eventplan by id: {} for user {}", eventPlanId, principal.getName(), e);
            model.addAttribute(WARNING, "No eventplan found for " + eventPlanId);
        }
        return "eventplans/eventplan";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String showEventPlanCreationForm(final EventPlanDto eventPlanDto) {
        return "eventplans/new-eventplan";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String createEventPlan(final EventPlanDto eventPlanDto, final BindingResult bindingResult, final Model model, final Principal principal) {
        if (bindingResult.hasErrors()) {
            log.warn("Unable to create event plan {}, form has errors {}", eventPlanDto, bindingResult.getAllErrors());
            return "redirect:/";
        }

        Optional<User> userByName = userService.getUserByName(principal.getName());
        if (userByName.isEmpty()) {
            log.warn("no user found for {}, stopping operation", principal.getName());
            return "redirect:/";
        }
        User user = userByName.get();
        EventPlanDto savedEventPlan;
        try {
            EventPlan eventPlan = EventPlanConverter.toDomain(eventPlanDto);
            eventPlan.setUser(user);
            savedEventPlan = EventPlanConverter.toDto(eventPlanService.createNew(eventPlan));
            log.info("event plan created successfully {}", savedEventPlan);
            model.addAttribute(EVENT_PLAN, savedEventPlan);
            model.addAttribute(SUCCESS, "Event plan created successfully!");
        } catch (Exception e) {
            log.warn("Unable to create event plan {}", eventPlanDto, e);
            model.addAttribute(ERROR, "Unable to create new event plan!");
        }

        return "redirect:/eventplans";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('USER')")
    public String editEventPlan(final EventPlanDto eventPlanDto, final BindingResult bindingResult, final Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("Unable to edit event plan {}, form has errors {}", eventPlanDto, bindingResult.getAllErrors());
            return "redirect:/";
        }
        EventPlanDto updatedEventPlan;
        try {
            updatedEventPlan = EventPlanConverter.toDto(eventPlanService.update(EventPlanConverter.toDomain(eventPlanDto)));
            model.addAttribute(EVENT_PLAN, updatedEventPlan);
            model.addAttribute(SUCCESS, "Event plan updated successfully!");
        } catch (Exception e) {
            log.warn("Unable to update event plan {}", eventPlanDto, e);
            model.addAttribute(ERROR, "Unable to update event plan!");
            return "redirect:/eventplans/";
        }
        return "redirect:/eventplans";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteEventPlan(@PathVariable("id") final Long eventPlanId, final Principal principal, final Model model) {
        try {
            User userByName = userService.getUserByName(principal.getName()).orElseThrow(RuntimeException::new);
            if (!eventPlanService.deleteById(eventPlanId)) {
                log.warn("Unable to delete eventplan id");
                return ResponseEntity.noContent().build();
            } else {
                log.info("deleted successfully");
                return ResponseEntity.ok("Successfully deleted event plan with id " + eventPlanId);
            }
        } catch (Exception e) {
            log.warn("unable to get eventplan by id: {} for user {}", eventPlanId, principal.getName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
