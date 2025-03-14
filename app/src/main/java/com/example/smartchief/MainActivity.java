//
//package com.example.smartchief;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class  MainActivity extends AppCompatActivity {
//
//    FirebaseAuth auth;
//    FirebaseUser user;
//    Button logoutButton;
//    TextView textView;
//    LinearLayout ingredientsContainer;
//    List<String> userIngredients = new ArrayList<>();
//
//    // Camera request code
//    private static final int REQUEST_IMAGE_CAPTURE = 1;
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
//        ingredientsContainer = findViewById(R.id.ingredientsContainer);
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
//
//        EditText ingredientsInput = findViewById(R.id.ingredientsInput);
//        Button findDishesButton = findViewById(R.id.findDishesButton);
//        TextView resultTextView = findViewById(R.id.resultTextView);
//
//        // Initialize the ImageButton and set its click listener
//        ImageButton btnCamera = findViewById(R.id.btnCamera);
//        btnCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dispatchTakePictureIntent();
//            }
//        });
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
//                // Find matching dishes
//                List<Recipe> matchingRecipes = findDishes(recipes, userIngredients);
//
//                // Display results
//                if (!matchingRecipes.isEmpty()) {
//                    StringBuilder result = new StringBuilder();
//                    for (Recipe recipe : matchingRecipes) {
//                        result.append("Dish: ").append(recipe.getName()).append("\n")
//                                .append("Calories: ").append(recipe.getCalories()).append("\n")
//                                .append("Image: ").append(recipe.getImageUrl()).append("\n")
//                                .append("How to make: ").append(recipe.getInstructions()).append("\n\n");
//                    }
//                    resultTextView.setText(result.toString());
//                } else {
//                    resultTextView.setText("No dishes can be made with the given ingredients.");
//                }
//            }
//        });
//
//        // Handle ingredient input
//        ingredientsInput.setOnEditorActionListener((v, actionId, event) -> {
//            String ingredient = ingredientsInput.getText().toString().trim();
//            if (!ingredient.isEmpty() && ingredient.split("\\s+").length == 1) {
//                addIngredient(ingredient);
//                ingredientsInput.setText("");
//            }
//            return true;
//        });
//    }
//
//    // Method to open the camera
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    // Handle the camera result
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            // The image was captured successfully
//            Bundle extras = data.getExtras();
//            if (extras != null) {
//                // Get the captured image as a Bitmap
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//
//                // Do something with the image, e.g., display it in an ImageView
//                // Example:
//                // ImageView imageView = findViewById(R.id.imageView);
//                // imageView.setImageBitmap(imageBitmap);
//            }
//        }
//    }
//
//    private void addIngredient(String ingredient) {
//        userIngredients.add(ingredient);
//
//        // Create a new LinearLayout for the ingredient and cancel button
//        LinearLayout ingredientLayout = new LinearLayout(this);
//        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
//        ingredientLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//
//        // Create TextView for the ingredient
//        TextView ingredientTextView = new TextView(this);
//        ingredientTextView.setText(ingredient);
//        ingredientTextView.setLayoutParams(new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                1));
//
//        // Create Button for canceling the ingredient
//        Button cancelButton = new Button(this);
//        cancelButton.setText("X");
//        cancelButton.setOnClickListener(v -> {
//            userIngredients.remove(ingredient);
//            ingredientsContainer.removeView(ingredientLayout);
//        });
//
//        // Add TextView and Button to the LinearLayout
//        ingredientLayout.addView(ingredientTextView);
//        ingredientLayout.addView(cancelButton);
//
//        // Add the LinearLayout to the ingredientsContainer
//        ingredientsContainer.addView(ingredientLayout);
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
package com.example.smartchief;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Firebase Authentication
    private FirebaseAuth auth;
    private FirebaseUser user;

    // UI Components
    private Button logoutButton;
    private TextView textView;
    private LinearLayout ingredientsContainer;
    private EditText ingredientsInput;
    private Button findDishesButton;
    private TextView resultTextView;
    private ImageButton btnCamera;

    // Ingredients list
    private List<String> userIngredients = new ArrayList<>();

    // Firebase Database
    private DatabaseReference databaseReference;
    private List<Recipe> recipes = new ArrayList<>();

    // Camera request code
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        setupFirebase();
        setupListeners();
    }

    private void initializeComponents() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textView = findViewById(R.id.user_details);
        logoutButton = findViewById(R.id.logout);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        resultTextView = findViewById(R.id.resultTextView);
        ingredientsInput = findViewById(R.id.ingredientsInput);
        findDishesButton = findViewById(R.id.findDishesButton);
        btnCamera = findViewById(R.id.btnCamera);

        if (user == null) {
            startActivity(new Intent(this, Login.class));
            finish();
        } else {
            textView.setText(user.getEmail());
        }
    }

    private void setupFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("dishes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for (DataSnapshot dishSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dishSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipe.setName(dishSnapshot.getKey()); // Set dish name from key
                        recipes.add(recipe);
                        Log.d("FIREBASE", "Loaded: " + recipe.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Error: " + error.getMessage());
                resultTextView.setText("Error loading recipes");
            }
        });
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        });

        btnCamera.setOnClickListener(v -> dispatchTakePictureIntent());

        findDishesButton.setOnClickListener(v -> findMatchingDishes());

        ingredientsInput.setOnEditorActionListener((v, actionId, event) -> {
            String ingredient = ingredientsInput.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                addIngredient(ingredient);
                ingredientsInput.setText("");
            }
            return true;
        });
    }

    private void findMatchingDishes() {
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (userIngredients.containsAll(recipe.getKey_ingredients()) &&
                    userIngredients.containsAll(recipe.getIngredients())) {
                matches.add(recipe);
            }
        }

        if (matches.isEmpty()) {
            resultTextView.setText("No matching dishes found");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Recipe recipe : matches) {
                sb.append("Dish: ").append(recipe.getName()).append("\n")
                        .append("Key Ingredients: ").append(recipe.getKey_ingredients()).append("\n")
                        .append("Calories: ").append(recipe.getCalories_per_serving()).append("\n\n");
            }
            resultTextView.setText(sb.toString());
        }
    }

    private void addIngredient(String ingredient) {
        userIngredients.add(ingredient);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv = new TextView(this);
        tv.setText(ingredient);

        Button btn = new Button(this);
        btn.setText("X");
        btn.setOnClickListener(v -> {
            ingredientsContainer.removeView(layout);
            userIngredients.remove(ingredient);
        });

        layout.addView(tv);
        layout.addView(btn);
        ingredientsContainer.addView(layout);
    }

    // Camera methods
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Handle image capture
        }
    }

    // Recipe model class
    public static class Recipe {
        private String name;
        private String image_url;
        private List<String> ingredients;
        private int calories_per_serving;
        private String dietary_info;
        private List<String> recipe_steps;
        private List<String> key_ingredients;

        public Recipe() {}

        // Getters and setters that match Firebase field names
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getImage_url() { return image_url; }
        public void setImage_url(String image_url) { this.image_url = image_url; }

        public List<String> getIngredients() { return ingredients; }
        public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

        public int getCalories_per_serving() { return calories_per_serving; }
        public void setCalories_per_serving(int calories_per_serving) { this.calories_per_serving = calories_per_serving; }

        public String getDietary_info() { return dietary_info; }
        public void setDietary_info(String dietary_info) { this.dietary_info = dietary_info; }

        public List<String> getRecipe_steps() { return recipe_steps; }
        public void setRecipe_steps(List<String> recipe_steps) { this.recipe_steps = recipe_steps; }

        public List<String> getKey_ingredients() { return key_ingredients; }
        public void setKey_ingredients(List<String> key_ingredients) { this.key_ingredients = key_ingredients; }
    }
}