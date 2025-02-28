//
//package com.example.smartchief;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//    FirebaseAuth auth;
//    FirebaseUser user;
//    Button logoutButton;
//    TextView textView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();
//
//        textView = findViewById(R.id.user_details);
//        logoutButton = findViewById(R.id.logout);
//
//        if (user == null) {
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        } else {
//            textView.setText(user.getEmail());
//        }
//
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                auth.signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });
////main
//        EditText ingredientsInput = findViewById(R.id.ingredientsInput);
//        Button findDishesButton = findViewById(R.id.findDishesButton);
//        TextView resultTextView = findViewById(R.id.resultTextView);
//
//        // Sample list of recipes with their ingredients, calories, image URLs, and instructions
//        List<Recipe> recipes = new ArrayList<>();
//        recipes.add(new Recipe("Pasta", Arrays.asList("tomato", "onion", "garlic", "basil"), 250, "image_url_1", "Boil pasta, sauté vegetables, mix and serve."));
//        recipes.add(new Recipe("Salad", Arrays.asList("lettuce", "tomato", "cucumber", "olive oil"), 150, "image_url_2", "Chop vegetables, add olive oil, and toss."));
//        recipes.add(new Recipe("Pizza", Arrays.asList("flour", "tomato", "cheese", "olive oil"), 350, "image_url_3", "Prepare dough, add toppings, and bake."));
//        recipes.add(new Recipe("Soup", Arrays.asList("salt", "oil", "bread"), 200, "image_url_4", "Boil water, add ingredients, serve with bread."));
//        // Add more recipes as needed...
//
//        // Button click listener
//        findDishesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get user input
//                String input = ingredientsInput.getText().toString().trim();
//                if (!input.isEmpty()) {
//                    // Parse ingredients
//                    List<String> userIngredients = Arrays.asList(input.split("\\s*,\\s*"));
//
//                    // Find matching dishes
//                    List<Recipe> matchingRecipes = findDishes(recipes, userIngredients);
//
//                    // Display results
//                    if (!matchingRecipes.isEmpty()) {
//                        StringBuilder result = new StringBuilder();
//                        for (Recipe recipe : matchingRecipes) {
//                            result.append("Dish: ").append(recipe.getName()).append("\n")
//                                    .append("Calories: ").append(recipe.getCalories()).append("\n")
//                                    .append("Image: ").append(recipe.getImageUrl()).append("\n")
//                                    .append("How to make: ").append(recipe.getInstructions()).append("\n\n");
//                        }
//                        resultTextView.setText(result.toString());
//                    } else {
//                        resultTextView.setText("No dishes can be made with the given ingredients.");
//                    }
//                } else {
//                    resultTextView.setText("Please enter some ingredients.");
//                }     Button findDishesButton = findViewById(R.id.findDishesButton);
//                            }
//        });
//    }
//
//    // Method to find matching dishes
//    private List<Recipe> findDishes(List<Recipe> recipes, List<String> userIngredients) {
//        List<Recipe> matchingRecipes = new ArrayList<>();
//
//        for (Recipe recipe : recipes) {
//            List<String> requiredIngredients = recipe.getIngredients();
//
//            // Check if user has all the required ingredients
//            if (userIngredients.containsAll(requiredIngredients)) {
//                matchingRecipes.add(recipe);
//            }
//        }
//
//        return matchingRecipes;
//    }
//
//    // Recipe class to store details about each recipe
//    public static class Recipe {
//        private final String name;
//        private final List<String> ingredients;
//        private final int calories;
//        private final String imageUrl;
//        private final String instructions;
//
//        public Recipe(String name, List<String> ingredients, int calories, String imageUrl, String instructions) {
//            this.name = name;
//            this.ingredients = ingredients;
//            this.calories = calories;
//            this.imageUrl = imageUrl;
//            this.instructions = instructions;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public List<String> getIngredients() {
//            return ingredients;
//        }
//
//        public int getCalories() {
//            return calories;
//        }
//
//        public String getImageUrl() {
//            return imageUrl;
//        }
//
//        public String getInstructions() {
//            return instructions;
//        }
//    }
//}
//
//

package com.example.smartchief;
import com.example.smartchief.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    Button logoutButton;
    TextView textView;
    LinearLayout ingredientsContainer;
    List<String> userIngredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textView = findViewById(R.id.user_details);
        logoutButton = findViewById(R.id.logout);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

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
            }
        });

        // Handle ingredient input
        ingredientsInput.setOnEditorActionListener((v, actionId, event) -> {
            String ingredient = ingredientsInput.getText().toString().trim();
            if (!ingredient.isEmpty() && ingredient.split("\\s+").length == 1) {
                addIngredient(ingredient);
                ingredientsInput.setText("");
            }
            return true;
        });
    }

    private void addIngredient(String ingredient) {
        userIngredients.add(ingredient);

        // Create a new LinearLayout for the ingredient and cancel button
        LinearLayout ingredientLayout = new LinearLayout(this);
        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
        ingredientLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create TextView for the ingredient
        TextView ingredientTextView = new TextView(this);
        ingredientTextView.setText(ingredient);
        ingredientTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));

        // Create Button for canceling the ingredient
        Button cancelButton = new Button(this);
        cancelButton.setText("X");
        cancelButton.setOnClickListener(v -> {
            userIngredients.remove(ingredient);
            ingredientsContainer.removeView(ingredientLayout);
        });

        // Add TextView and Button to the LinearLayout
        ingredientLayout.addView(ingredientTextView);
        ingredientLayout.addView(cancelButton);

        // Add the LinearLayout to the ingredientsContainer
        ingredientsContainer.addView(ingredientLayout);
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
        private final String name;
        private final List<String> ingredients;
        private final int calories;
        private final String imageUrl;
        private final String instructions;

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