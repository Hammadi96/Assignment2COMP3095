package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.domain.EventPlan;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.EventPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class EventPlanServiceImpl implements EventPlanService {

    @Autowired
    EventPlanRepository eventPlanRepository;

    @Override
    public EventPlan createNew(EventPlan eventPlan) {
        return eventPlanRepository.save(eventPlan);
    }

    @Override
    public EventPlan update(EventPlan eventPlan) {
        return eventPlanRepository.save(eventPlan);
    }

    @Override
    public Optional<EventPlan> findById(Long id) {
        return eventPlanRepository.findById(id);
    }

    @Override
    public List<EventPlan> findAllForUser(Long userId) {
        return findAllForUser(User.builder().id(userId).build());
    }

    @Override
    public List<EventPlan> findAllForUser(User user) {
        return eventPlanRepository.findAllByUser(user);
    }

    @Override
    public List<EventPlan> findAllAfter(Instant after) {
        return eventPlanRepository.findTopByDueDateAfter(after);
    }

    @Override
    public List<EventPlan> findAllForUserAndAfter(User user, Instant after) {
        return eventPlanRepository.findTopByUserAndDueDateAfter(user, after);
    }

    @Override
    public List<EventPlan> findAllForUserIdAndAfter(Long userId, Instant after) {
        return findAllForUserAndAfter(User.builder().id(userId).build(), after);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            eventPlanRepository.deleteById(id);
        } catch (Exception e) {
            log.warn("Unable to delete EventPlan by id {}", id, e);
            return false;
        }
        return true;
    }
}
