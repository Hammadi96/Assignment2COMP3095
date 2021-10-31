package ca.gb.comp3095.foodrecipe.model.repo;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRespository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findById(Long id);

    List<Recipe> findAllByUserId(Long userId);

    List<Recipe> findAll();
}
