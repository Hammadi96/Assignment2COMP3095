package ca.gb.comp3095.foodrecipe.controller.recipe;

import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RecipeControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    RecipeRespository recipeRespository;

    User user;

    @BeforeEach
    void setUp() {
        user = userService.createNewUser(UserTestFactory.aRandomUser("test-user"));
    }

    @AfterEach
    void tearDown() {
        recipeRespository.deleteAll();
        userService.deleteUserById(user.getId());
    }

    @Test
    void shouldCreateRecipe() throws Exception {
        CreateRecipeCommand createRecipeCommand = CreateRecipeCommand.builder().title("Test Recipe").description("recipe description").userId(user.getId())
                .cookingTime(10L)
                .servings(4L)
                .ingredients("1 onion\n" +
                        "2 ginger")
                .cookingInstructions("1. cut onion\n" +
                        "2. cut ginger and mix")
                .build();
        mockMvc.perform(post("/recipe/create/user/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecipeCommand)))
                .andExpect(status().isCreated());
    }

}