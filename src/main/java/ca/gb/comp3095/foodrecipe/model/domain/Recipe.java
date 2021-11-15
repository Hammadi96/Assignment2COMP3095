package ca.gb.comp3095.foodrecipe.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Builder
@ToString
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Recipe extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String title;

    String imageUrl;

    String description;

    Duration cookingTime;

    Long servings;

    String instructions;

    String ingredients;

    @OneToOne
    @ToString.Exclude
    User user;

    @ManyToMany(mappedBy = "likedRecipes", cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST
    }, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> likedBy = new HashSet<>();

}
