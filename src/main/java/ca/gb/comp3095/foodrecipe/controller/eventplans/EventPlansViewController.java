package ca.gb.comp3095.foodrecipe.controller.eventplans;

import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.EventPlanService;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import ca.gb.comp3095.foodrecipe.view.AttributeTags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/eventplans", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class EventPlansViewController implements WebMvcConfigurer {
    @Autowired
    UserService userService;

    @Autowired
    EventPlanService eventPlanService;

    @GetMapping(value = {"", "/", "/all"})
    @PreAuthorize("hasRole('USER')")
    public String getAllEventPlansForCurrentUser(final Principal principal, final Model model) {
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName());
            List<EventPlanDto> allEventPlans = eventPlanService.findAllForUser(userByName.orElseThrow(RuntimeException::new))
                    .stream().map(EventPlanConverter::toDto)
                    .collect(Collectors.toList());
            model.addAttribute(AttributeTags.EVENT_PLANS, allEventPlans);
        } catch (Exception e) {
            log.warn("unable to get eventplans for user {}", principal.getName(), e);
        }
        return "user/user";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public String getEventPlanForCurrentUser(@RequestParam("id") final Long eventPlanId, final Principal principal, final Model model) {
        try {
            User userByName = userService.getUserByName(principal.getName()).orElseThrow(RuntimeException::new);

            EventPlanDto eventPlanDto = eventPlanService.findById(eventPlanId)
                    .map(EventPlanConverter::toDto).orElseThrow(RuntimeException::new);
            model.addAttribute(AttributeTags.EVENT_PLAN, eventPlanDto);
        } catch (Exception e) {
            log.warn("unable to get eventplan by id: {} for user {}", eventPlanId, principal.getName(), e);
        }
        return "user/user";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String showEventPlanCreationForm() {
        return "";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String createEventPlan(final EventPlanDto eventPlanDto, final BindingResult bindingResult, final Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("Unable to create event plan {}, form has errors {}", eventPlanDto, bindingResult.getAllErrors());
            return "redirect:/";
        }

        EventPlanDto savedEventPlan;
        try {
            savedEventPlan = EventPlanConverter.toDto(eventPlanService.createNew(EventPlanConverter.toDomain(eventPlanDto)));
            model.addAttribute(AttributeTags.EVENT_PLAN, savedEventPlan);
            model.addAttribute(AttributeTags.SUCCESS, "Event plan created successfully!");
        } catch (Exception e) {
            log.warn("Unable to create event plan {}", eventPlanDto, e);
            model.addAttribute(AttributeTags.ERROR, "Unable to create new event plan!");
        }

        return "redirect:/";
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
            model.addAttribute(AttributeTags.EVENT_PLAN, updatedEventPlan);
            model.addAttribute(AttributeTags.SUCCESS, "Event plan updated successfully!");
        } catch (Exception e) {
            log.warn("Unable to update event plan {}", eventPlanDto, e);
            model.addAttribute(AttributeTags.ERROR, "Unable to update event plan!");
        }
        return "redirect:/";
    }


}
