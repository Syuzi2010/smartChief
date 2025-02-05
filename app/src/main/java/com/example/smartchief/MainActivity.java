package com.example.smartchief;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        EditText ingredientsInput = findViewById(R.id.ingredientsInput);
        Button findDishesButton = findViewById(R.id.findDishesButton);
        TextView resultTextView = findViewById(R.id.resultTextView);

        // Sample list of recipes with their ingredients, calories, image URLs, and instructions
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Pasta", Arrays.asList("tomato", "onion", "garlic", "basil"), 250, "image_url_1", "Boil pasta, sauté vegetables, mix and serve."));
        recipes.add(new Recipe("Salad", Arrays.asList("lettuce", "tomato", "cucumber", "olive oil"), 150, "image_url_2", "Chop vegetables, add olive oil, and toss."));
        recipes.add(new Recipe("Pizza", Arrays.asList("flour", "tomato", "cheese", "olive oil"), 350, "image_url_3", "Prepare dough, add toppings, and bake."));
        recipes.add(new Recipe("Soup", Arrays.asList("salt", "oil", "bread"), 200, "image_url_4", "Boil water, add ingredients, serve with bread."));
        // Add more recipes as needed...

        // Button click listener
        findDishesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String input = ingredientsInput.getText().toString().trim();
                if (!input.isEmpty()) {
                    // Parse ingredients
                    List<String> userIngredients = Arrays.asList(input.split("\\s*,\\s*"));

                    // Find matching dishes
                    List<Recipe> matchingRecipes = findDishes(recipes, userIngredients);

                    // Display results
                    if (!matchingRecipes.isEmpty()) {
                        StringBuilder result = new StringBuilder();
                        for (Recipe recipe : matchingRecipes) {
                            result.append("Dish: ").append(recipe.getName()).append("\n")
                                    .append("Calories: ").append(recipe.getCalories()).append("\n")
                                    .append("Image: ").append(recipe.getImageUrl()).append("\n")
                                    .append("How to make: ").append(recipe.getInstructions()).append("\n\n");
                        }
                        resultTextView.setText(result.toString());
                    } else {
                        resultTextView.setText("No dishes can be made with the given ingredients.");
                    }
                } else {
                    resultTextView.setText("Please enter some ingredients.");
                }
            }
        });
    }

    // Method to find matching dishes
    private List<Recipe> findDishes(List<Recipe> recipes, List<String> userIngredients) {
        List<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            List<String> requiredIngredients = recipe.getIngredients();

            // Check if user has all the required ingredients
            if (userIngredients.containsAll(requiredIngredients)) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }

    // Recipe class to store details about each recipe
    public static class Recipe {
        private String name;
        private List<String> ingredients;
        private int calories;
        private String imageUrl;
        private String instructions;

        public Recipe(String name, List<String> ingredients, int calories, String imageUrl, String instructions) {
            this.name = name;
            this.ingredients = ingredients;
            this.calories = calories;
            this.imageUrl = imageUrl;
            this.instructions = instructions;
        }

        public String getName() {
            return name;
        }

        public List<String> getIngredients() {
            return ingredients;
        }

        public int getCalories() {
            return calories;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getInstructions() {
            return instructions;
        }
    }
}
