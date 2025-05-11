//package com.example.smartchief;
//
//import android.graphics.Bitmap;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.util.ArrayList;
//import java.util.List;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import android.text.Html;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    // Firebase Authentication
//    private FirebaseAuth auth;
//    private FirebaseUser user;
//
//    // UI Components
//    private Button logoutButton;
//    private TextView textView;
//    private LinearLayout ingredientsContainer;
//    private EditText ingredientsInput;
//    private Button findDishesButton;
//    private TextView resultTextView;
//    private ImageButton btnCamera;
//
//    // Ingredients list
//    private List<String> userIngredients = new ArrayList<>();
//
//    // Firebase Database
//    private DatabaseReference databaseReference;
//    private List<Recipe> recipes = new ArrayList<>();
//    private String selectedDietFilter = null;  // null means no filter applied
//    private static final int REQUEST_FILTER = 101;
//    private String currentDietFilter = null;
//
//
//
//    // Camera request code
//    private static final int REQUEST_IMAGE_CAPTURE = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initializeComponents();
//        setupFirebase();
//        setupListeners();
//    }
//
//    private void initializeComponents() {
//        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();
//
//        textView = findViewById(R.id.user_details);
//        logoutButton = findViewById(R.id.logout);
//        ingredientsContainer = findViewById(R.id.ingredientsContainer);
//        resultTextView = findViewById(R.id.resultTextView);
//        ingredientsInput = findViewById(R.id.ingredientsInput);
//        findDishesButton = findViewById(R.id.findDishesButton);
//        btnCamera = findViewById(R.id.btnCamera);
//
//        if (user == null) {
//            startActivity(new Intent(this, Login.class));
//            finish();
//        } else {
//            textView.setText(user.getEmail());
//        }
//    }
//    private void openFilterActivity() {
//        Intent intent = new Intent(MainActivity.this, FilterActivity.class);
//        startActivityForResult(intent, REQUEST_FILTER);
//    }
//
//    private void setupFirebase() {
//        databaseReference = FirebaseDatabase.getInstance().getReference("dishes");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                recipes.clear();
//                for (DataSnapshot dishSnapshot : snapshot.getChildren()) {
//                    Recipe recipe = dishSnapshot.getValue(Recipe.class);
//                    if (recipe != null) {
//                        recipe.setName(dishSnapshot.getKey()); // Set dish name from key
//                        recipes.add(recipe);
//                        Log.d("FIREBASE", "Loaded: " + recipe.getName());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FIREBASE", "Error: " + error.getMessage());
//                resultTextView.setText("Error loading recipes");
//               // System.out.println("dnbpiSUBIFBDSIPU"+error.getMessage());
//            }
//        });
//    }
//
//    private void setupListeners() {
//
//        logoutButton.setOnClickListener(v -> {
//            auth.signOut();
//            startActivity(new Intent(this, Login.class));
//            finish();
//        });
//
//        btnCamera.setOnClickListener(v -> dispatchTakePictureIntent());
//
//        findDishesButton.setOnClickListener(v -> findMatchingDishes());
//
//        ingredientsInput.setOnEditorActionListener((v, actionId, event) -> {
//            String ingredient = ingredientsInput.getText().toString().trim();
//            if (!ingredient.isEmpty()) {
//                addIngredient(ingredient);
//                ingredientsInput.setText("");
//            }
//            return true;
//        });
//    }
//
//
//    private void findMatchingDishes() {
//        List<Recipe> matches = new ArrayList<>();
//
//        for (Recipe recipe : recipes) {
//            boolean matchesIngredients = userIngredients.containsAll(recipe.getKey_ingredients());
//
//            boolean matchesDiet = currentDietFilter == null ||
//                    currentDietFilter.equalsIgnoreCase("None") ||
//                    recipe.getDietary_info().equalsIgnoreCase(currentDietFilter);
//
//            if (matchesIngredients && matchesDiet) {
//                matches.add(recipe);
//            }
//        }
//
//        if (matches.isEmpty()) {
//            resultTextView.setText("No matching dishes found");
//        } else {
//            StringBuilder sb = new StringBuilder();
//            for (Recipe recipe : matches) {
//                sb.append("🍲 Dish: ").append(recipe.getName()).append("<br>")
//                        .append("Ingredients: ");
//
//                for (String ingredient : recipe.getIngredients()) {
//                    if (userIngredients.contains(ingredient.toLowerCase())) {
//                        sb.append(ingredient).append(", ");
//                    } else {
//                        sb.append("<font color='#FF0000'>").append(ingredient).append("</font>, ");
//                    }
//                }
//
//                sb.append("<br>")
//                        .append("🥑 Dietary Info: ").append(recipe.getDietary_info()).append("<br>")
//                        .append("🔥 Calories: ").append(recipe.getCalories_per_serving()).append("<br>")
//                        .append("<br>👩🍳 How to make:<br>");
//
//                List<String> steps = recipe.getRecipe_steps();
//                if (steps != null) {
//                    int stepNumber = 1;
//                    for (String step : steps) {
//                        sb.append(stepNumber++).append(". ").append(step).append("<br>");
//                    }
//                }
//                sb.append("<br><br>");
//            }
//
//            resultTextView.setText(Html.fromHtml(sb.toString()));
//        }
//    }
//
//
//    private void addIngredient(String ingredient) {
//        userIngredients.add(ingredient);
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.HORIZONTAL);
//
//        TextView tv = new TextView(this);
//        tv.setText(ingredient);
//
//        Button btn = new Button(this);
//        btn.setText("X");
//        btn.setOnClickListener(v -> {
//            ingredientsContainer.removeView(layout);
//            userIngredients.remove(ingredient);
//        });
//
//        layout.addView(tv);
//        layout.addView(btn);
//        ingredientsContainer.addView(layout);
//    }
//
//    // Camera methods
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // 1. Handle image capture
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//
//            resultTextView.setText("Analyzing food...");
//            btnCamera.setEnabled(false);
//
//            ClarifaiHelper.recognizeFood(imageBitmap, new ClarifaiHelper.ClarifaiCallback() {
//                @Override
//                public void onSuccess(List<String> ingredients) {
//                    runOnUiThread(() -> {
//                        btnCamera.setEnabled(true);
//                        if (ingredients.isEmpty()) {
//                            resultTextView.setText("No food items detected");
//                            return;
//                        }
//
//                        for (String ingredient : ingredients) {
//                            if (!userIngredients.contains(ingredient)) {
//                                addIngredient(ingredient);
//                            }
//                        }
//                        resultTextView.setText("Added ingredients from photo!");
//                    });
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    runOnUiThread(() -> {
//                        btnCamera.setEnabled(true);
//                        resultTextView.setText("Error: " + e.getMessage());
//                    });
//                }
//            });
//        }
//
//        // 2. Handle filter result
//        if (requestCode == REQUEST_FILTER && resultCode == RESULT_OK && data != null) {
//            String selectedDiet = data.getStringExtra("selectedDiet");
//
//            // Save the selected diet filter (null means no filter)
//            currentDietFilter = selectedDiet;
//            Toast.makeText(this, "Filter applied: " + (selectedDiet == null ? "None" : selectedDiet), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    // Recipe model class
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
//        // Getters and setters that match Firebase field names
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
//
//}

package com.example.smartchief;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

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

        findViewById(R.id.openFilterButton).setOnClickListener(v -> openFilterActivity());
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

        if (matches.isEmpty()) {
            resultTextView.setText("No matching dishes found");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Recipe recipe : matches) {
                sb.append("🍲 Dish: ").append(recipe.getName()).append("<br>");
                sb.append("Ingredients: ");
                for (String ingredient : recipe.getIngredients()) {
                    if (userIngredients.contains(ingredient.toLowerCase())) {
                        sb.append(ingredient).append(", ");
                    } else {
                        sb.append("<font color='#FF0000'>").append(ingredient).append("</font>, ");
                    }
                }
                sb.append("<br>");
                sb.append("🥑 Dietary Info: ").append(recipe.getDietary_info()).append("<br>");
                sb.append("🔥 Calories: ").append(recipe.getCalories_per_serving()).append("<br>");
                sb.append("<br>👩🍳 How to make:<br>");
                int i = 1;
                for (String step : recipe.getRecipe_steps()) {
                    sb.append(i++).append(". ").append(step).append("<br>");
                }
                sb.append("<br><br>");
            }
            resultTextView.setText(Html.fromHtml(sb.toString()));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            resultTextView.setText("Analyzing food...");
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
        }

        if (requestCode == REQUEST_FILTER && resultCode == RESULT_OK && data != null) {
            String selectedDiet = data.getStringExtra("selectedDiet");
            currentDietFilter = selectedDiet;
            Toast.makeText(this, "Filter applied: " + (selectedDiet == null ? "None" : selectedDiet), Toast.LENGTH_SHORT).show();
        }
    }

    // Recipe model
    public static class Recipe {
        private String name;
        private String image_url;
        private List<String> ingredients;
        private int calories_per_serving;
        private String dietary_info;
        private List<String> recipe_steps;
        private List<String> key_ingredients;

        public Recipe() {}

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
