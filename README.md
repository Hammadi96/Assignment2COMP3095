# Read Me First
The following was discovered as part of building this project:

* The original package name 'ca.gb.comp3095.food-recipe' is invalid and this project uses 'ca.gb.comp3095.foodrecipe' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.5/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.5/gradle-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#using-boot-devtools)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-spring-mvc-template-engines)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### how to run

run spring boot app with gradle wrapper

#### Unix or Mac
```shell 
./gradlew bootRun
```

#### Windows
```shell 
gradlew bootRun
```

This will create a single user, and a dummy recipe. Sign in with using the following details:

```shell
user: testuser
password: test
```

This is configured in class `WebSecurityConfig` class.

Search for 'Dummy' recipe, from `http://localhost:8080` (in the web browser)

### Create Recipes

1. head to `http://localhost:8080/view/recipe/create`

### Edit recipe with ID
1. You can edit recipe if you know the recipe id from `http://localhost:8080/view/recipe/edit/{recipeId}` (replace {recipeId} with valid integer id for the recipe)

### Preloaded recipes
Recipes are already loaded in `recipe-list.json`. You add any number of recipes to the file and the application will load them at startup. 