package ca.gb.comp3095.foodrecipe.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.Duration;

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

    @Column(columnDefinition="TEXT")
    String instructions;

    @Column(columnDefinition="TEXT")
    String ingredients;

    @OneToOne
    User user;

}
