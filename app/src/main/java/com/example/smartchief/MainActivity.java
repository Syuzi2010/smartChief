
package com.example.smartchief;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private Button logoutButton;
    private TextView textView;
    private LinearLayout ingredientsContainer;
    private EditText ingredientsInput;
    private Button findDishesButton;
    private TextView resultTextView;
    private ImageButton btnCamera;

    private List<String> userIngredients = new ArrayList<>();

    private DatabaseReference databaseReference;
    private List<Recipe> recipes = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_FILTER = 101;
    private String currentDietFilter = null;
    LinearLayout resultsLayout;
    FirebaseUser currentUser;
    DatabaseReference favsRef;
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set content view first

        resultsLayout = findViewById(R.id.resultsLayout);
     resultTextView = findViewById(R.id.resultTextView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // No user is logged in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish(); // Prevent user from returning to MainActivity
            return;
        }

        // Safe to access getUid() now
        favsRef = FirebaseDatabase.getInstance().getReference("favorites")
                .child(currentUser.getUid());

        // Now, find the bottomBar view
        View bottomBar = findViewById(R.id.bottomBar);

        // Add a listener to detect layout changes (keyboard visibility)
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            int screenHeight = getWindow().getDecorView().getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;
            if (keypadHeight > screenHeight * 0.15) {
                bottomBar.setVisibility(View.GONE); // Keyboard is open
            } else {
                bottomBar.setVisibility(View.VISIBLE); // Keyboard is closed
            }
        });

        // Initialize other components
        initializeComponents();
        setupFirebase();
        setupListeners();
    }



    private void initializeComponents() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        logoutButton = findViewById(R.id.logout);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        resultTextView = findViewById(R.id.resultTextView);
        ingredientsInput = findViewById(R.id.ingredientsInput);
        findDishesButton = findViewById(R.id.findDishesButton);
        btnCamera = findViewById(R.id.btnCamera);

        if (user == null) {
            startActivity(new Intent(this, Login.class));
            finish();
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
                        recipe.setName(dishSnapshot.getKey());
                        recipes.add(recipe);
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
        // Account navigation button
        ImageButton navAccount = findViewById(R.id.nav_account);
        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
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

        findViewById(R.id.nav_filter).setOnClickListener(v -> openFilterActivity());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openFilterActivity() {
        Intent intent = new Intent(MainActivity.this, FilterActivity.class);
        startActivityForResult(intent, REQUEST_FILTER);
    }

 private void findMatchingDishes() {
        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            boolean matchesIngredients = userIngredients.containsAll(recipe.getKey_ingredients());
            boolean matchesDiet = currentDietFilter == null ||
                    currentDietFilter.equalsIgnoreCase("None") ||
                    recipe.getDietary_info().equalsIgnoreCase(currentDietFilter);

            if (matchesIngredients && matchesDiet) {
                matches.add(recipe);
            }
        }

        resultsLayout.removeAllViews(); // Clear previous results

        if (matches.isEmpty()) {
            TextView noResult = new TextView(this);
            noResult.setText("No matching dishes found");
            resultsLayout.addView(noResult);
            return;
        }

        for (Recipe recipe : matches) {
            LinearLayout recipeLayout = new LinearLayout(this);
            recipeLayout.setOrientation(LinearLayout.VERTICAL);
            recipeLayout.setPadding(20, 20, 20, 20);

            TextView recipeText = new TextView(this);
            StringBuilder sb = new StringBuilder();
            sb.append("🍲 Dish: ").append(recipe.getName()).append("\n");

            sb.append("Ingredients: ");
            for (String ingredient : recipe.getIngredients()) {
                if (userIngredients.contains(ingredient.toLowerCase())) {
                    sb.append(ingredient).append(", ");
                } else {
                    sb.append("[MISSING] ").append(ingredient).append(", ");
                }
            }
            sb.append("\n");

            sb.append("🥑 Dietary Info: ").append(recipe.getDietary_info()).append("\n");
            sb.append("🔥 Calories: ").append(recipe.getCalories_per_serving()).append("\n\n");
            sb.append("👩🍳 How to make:\n");
            int i = 1;
            for (String step : recipe.getRecipe_steps()) {
                sb.append(i++).append(". ").append(step).append("\n");
            }

            recipeText.setText(sb.toString());

            // Heart Button
            ImageButton heartButton = new ImageButton(this);
            heartButton.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            heartButton.setBackgroundColor(Color.TRANSPARENT);
            heartButton.setImageResource(R.drawable.ic_heart_outline); // Default

            // Check if this recipe is already favorited
            favsRef.child(recipe.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        heartButton.setImageResource(R.drawable.ic_heart_filled);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            // Handle click
            heartButton.setOnClickListener(v -> {
                favsRef.child(recipe.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            favsRef.child(recipe.getName()).removeValue();
                            heartButton.setImageResource(R.drawable.ic_heart_outline);
                        } else {
                            favsRef.child(recipe.getName()).setValue(recipe);
                            heartButton.setImageResource(R.drawable.ic_heart_filled);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            });

            recipeLayout.addView(recipeText);
            recipeLayout.addView(heartButton);
            resultsLayout.addView(recipeLayout);
        }
    }


    private void addIngredient(String ingredient) {
        userIngredients.add(ingredient.toLowerCase());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv = new TextView(this);
        tv.setText(ingredient);

        Button btn = new Button(this);
        btn.setText("X");
        btn.setOnClickListener(v -> {
            ingredientsContainer.removeView(layout);
            userIngredients.remove(ingredient.toLowerCase());
        });

        layout.addView(tv);
        layout.addView(btn);
        ingredientsContainer.addView(layout);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            if (resultTextView != null) {
                resultTextView.setText("Analyzing food...");
            } else {
                Log.e("MainActivity", "resultTextView is null!");
            }

            btnCamera.setEnabled(false);

            ClarifaiHelper.recognizeFood(imageBitmap, new ClarifaiHelper.ClarifaiCallback() {
                @Override
                public void onSuccess(List<String> ingredients) {
                    runOnUiThread(() -> {
                        btnCamera.setEnabled(true);
                        if (ingredients.isEmpty()) {
                            resultTextView.setText("No food items detected");
                        } else {
                            for (String ingredient : ingredients) {
                                if (!userIngredients.contains(ingredient.toLowerCase())) {
                                    addIngredient(ingredient);
                                }
                            }
                            resultTextView.setText("Added ingredients from photo!");
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> {
                        btnCamera.setEnabled(true);
                        resultTextView.setText("Error: " + e.getMessage());
                    });
                }
            });
        } else if (requestCode == REQUEST_FILTER && resultCode == RESULT_OK && data != null) {
            String selectedDiet = data.getStringExtra("selectedDiet");
            currentDietFilter = selectedDiet;
            Toast.makeText(this, "Filter applied: " + (selectedDiet == null ? "None" : selectedDiet), Toast.LENGTH_SHORT).show();
        }
    }


    // Recipe model
//    public static class Recipe {
//        private String name;
//        private String image_url;
//        private List<String> ingredients;
//        private int calories_per_serving;
//        private String dietary_info;
//        private List<String> recipe_steps;
//        private List<String> key_ingredients;
//
//        public Recipe() {}
//
//        public String getName() { return name; }
//        public void setName(String name) { this.name = name; }
//
//        public String getImage_url() { return image_url; }
//        public void setImage_url(String image_url) { this.image_url = image_url; }
//
//        public List<String> getIngredients() { return ingredients; }
//        public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
//
//        public int getCalories_per_serving() { return calories_per_serving; }
//        public void setCalories_per_serving(int calories_per_serving) { this.calories_per_serving = calories_per_serving; }
//
//        public String getDietary_info() { return dietary_info; }
//        public void setDietary_info(String dietary_info) { this.dietary_info = dietary_info; }
//
//        public List<String> getRecipe_steps() { return recipe_steps; }
//        public void setRecipe_steps(List<String> recipe_steps) { this.recipe_steps = recipe_steps; }
//
//        public List<String> getKey_ingredients() { return key_ingredients; }
//        public void setKey_ingredients(List<String> key_ingredients) { this.key_ingredients = key_ingredients; }
//    }
}
