package ca.gb.comp3095.foodrecipe.controller.recipe;

import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
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
        recipeRespository.deleteAllById(recipeRespository.findAllByUserId(user.getId()).stream().map(Recipe::getId).collect(Collectors.toList()));
        userService.deleteUserById(user.getId());
    }

    @Test
    void shouldCreateRecipeWithRest() throws Exception {
        CreateRecipeCommand createRecipeCommand = CreateRecipeCommand.builder().title("Test Recipe").description("recipe description").userId(user.getId())
                .cookingTime(Duration.ofMinutes(10).toMinutes())
                .servings(4L)
                .ingredients("1 onion\n" +
                        "2 ginger")
                .cookingInstructions("1. cut onion\n" +
                        "2. cut ginger and mix")
                .ingredientsList(List.of("onions", "ginger"))
                .build();

        String urlTemplate = "/v1/recipe/create/user/" + user.getId();
        mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecipeCommand)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("testUser")
    void shouldCreateRecipe() throws Exception {
        CreateRecipeCommand createRecipeCommand = CreateRecipeCommand.builder().title("Test Recipe").description("recipe description").userId(user.getId())
                .cookingTime(Duration.ofMinutes(10).toMinutes())
                .servings(4L)
                .ingredients("1 onion\n" +
                        "2 ginger")
                .cookingInstructions("1. cut onion\n" +
                        "2. cut ginger and mix")
                .ingredientsList(List.of("onions", "ginger"))
                .build();

        String urlTemplate = "/view/recipe/create";
        mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "test title")
                .param("description", "descripton of recipe")
                .param("ingredientsList", "onion", "gingner")
                .param("cookingInstructionList", "dummy instrucitons")
                .param("ingredients", "")
                .param("cookingInstructions", "")
                .param("servings", "2")
                .param("imageUrl", "")
                .param("cookingTime", "10")
                .param("userId", "1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

    }

    @Test
    void recipeDtoSerialisation() throws JsonProcessingException {
        RecipeDto recipe1 = RecipeDto.builder().title("title").description("description1").ingredients("ingredients").cookingInstructions("instructions").imageUrl("http://image.url.com").cookingTime(10L).servings(2L).userId(1L).build();
        RecipeDto recipe2 = RecipeDto.builder().title("title").description("description1").ingredients("ingredients").cookingInstructions("instructions").imageUrl("http://image.url.com").cookingTime(10L).servings(2L).userId(1L).build();
        RecipeDto recipe3 = RecipeDto.builder().title("title").description("description1").ingredients("ingredients").cookingInstructions("instructions").imageUrl("http://image.url.com").cookingTime(10L).servings(2L).userId(1L).build();

        List<RecipeDto> recipeDtoList = List.of(recipe1, recipe2, recipe3);

        System.out.println(objectMapper.writeValueAsString(recipeDtoList));
    }
}