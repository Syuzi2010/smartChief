package com.example.smartchief;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button logoutButton;
    private LinearLayout ingredientsContainer;
    private AutoCompleteTextView ingredientsInput;
    private Button findDishesButton;
    private TextView resultTextView;
    private ImageButton btnCamera;
    private TextView textEmptyState;
    private ArrayAdapter<String> ingredientAdapter;
    private List<String> allIngredients = new ArrayList<>();
    private List<String> userIngredients = new ArrayList<>();
    private String currentDietFilter = null;
    private int minCalorieFilter = -1;
    private int maxCalorieFilter = -1;
    private DatabaseReference databaseReference;
    private List<Recipe> recipes = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_FILTER = 101;
    private NestedScrollView resultsScroll;
    private FirebaseUser currentUser;
    private DatabaseReference favsRef;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Initialize UI components
        resultsScroll = findViewById(R.id.resultsScroll);
        recyclerView = findViewById(R.id.recyclerView);
        textEmptyState = findViewById(R.id.textEmptyState);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        ingredientsInput = findViewById(R.id.ingredientsInput);
        btnCamera = findViewById(R.id.btnCamera);
        findDishesButton = findViewById(R.id.findDishesButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealAdapter = new MealAdapter(new ArrayList<>());
        recyclerView.setAdapter(mealAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startLogin();
            return;
        }

        initializeFirebase();
        setupAutocomplete();
        setupUIListeners();
        loadAllIngredientsFromRecipes();
    }

    private void startLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    private void initializeFirebase() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("dishes");
        favsRef = FirebaseDatabase.getInstance().getReference("favorites").child(currentUser.getUid());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for (DataSnapshot dishSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dishSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipe.setName(getSafeString(dishSnapshot.child("name")));
                        recipe.setDietary_info(getSafeString(dishSnapshot.child("dietary_info")));
                        recipe.setCalories_per_serving(getSafeInt(dishSnapshot.child("calories_per_serving")));
                        recipe.setIngredients(getStringList(dishSnapshot.child("ingredients")));
                        recipe.setKey_ingredients(getStringList(dishSnapshot.child("key_ingredients")));
                        recipes.add(recipe);
                    }
                }
                refreshMealList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Error: " + error.getMessage());
                resultTextView.setText("Error loading recipes");
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private String getSafeString(DataSnapshot snapshot) {
        return snapshot.exists() ? snapshot.getValue(String.class) : "";
    }

    private int getSafeInt(DataSnapshot snapshot) {
        return snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
    }

    private List<String> getStringList(DataSnapshot snapshot) {
        List<String> list = new ArrayList<>();
        if (snapshot.exists()) {
            for (DataSnapshot item : snapshot.getChildren()) {
                String value = item.getValue(String.class);
                if (value != null) list.add(value);
            }
        }
        return list;
    }

    private void setupAutocomplete() {
        ingredientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        ingredientsInput.setAdapter(ingredientAdapter);

        ingredientsInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase().trim();
                List<String> filtered = new ArrayList<>();
                if (!query.isEmpty()) {
                    for (String ingredient : allIngredients) {
                        if (ingredient.toLowerCase().startsWith(query)) {
                            filtered.add(ingredient);
                        }
                    }
                }
                updateAutocomplete(filtered);
            }
        });
    }

    private void updateAutocomplete(List<String> filtered) {
        ingredientAdapter.clear();
        if (!filtered.isEmpty()) {
            ingredientAdapter.addAll(filtered);
        }
        ingredientAdapter.notifyDataSetChanged();
    }

    private void setupUIListeners() {
        findViewById(R.id.nav_account).setOnClickListener(v ->
                startActivity(new Intent(this, AccountActivity.class)));

        findViewById(R.id.nav_filter).setOnClickListener(v -> openFilterActivity());

        btnCamera.setOnClickListener(v -> dispatchTakePictureIntent());

        findDishesButton.setOnClickListener(v -> refreshMealList());

        ingredientsInput.setOnEditorActionListener((v, actionId, event) -> {
            String ingredient = ingredientsInput.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                addIngredient(ingredient);
                ingredientsInput.setText("");
            }
            return true;
        });

        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() ->
                adjustBottomBarVisibility(findViewById(R.id.bottomBar)));
    }

    private void adjustBottomBarVisibility(View bottomBar) {
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;
        bottomBar.setVisibility(keypadHeight > screenHeight * 0.15 ? View.GONE : View.VISIBLE);
    }

    private void loadAllIngredientsFromRecipes() {
        DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes");
        dishesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> uniqueIngredients = new HashSet<>();
                for (DataSnapshot dishSnapshot : snapshot.getChildren()) {
                    DataSnapshot ingredientsSnap = dishSnapshot.child("ingredients");
                    for (DataSnapshot ing : ingredientsSnap.getChildren()) {
                        String ingredient = ing.getValue(String.class);
                        if (ingredient != null) uniqueIngredients.add(ingredient.trim());
                    }
                }
                allIngredients.clear();
                allIngredients.addAll(uniqueIngredients);
                ingredientAdapter.addAll(allIngredients);
                ingredientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Ingredients load error: " + error.getMessage());
            }
        });
    }

    private void refreshMealList() {
        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (matchesFilters(recipe)) {
                filteredRecipes.add(recipe);
            }
        }
        updateMealListUI(filteredRecipes);
        resultsScroll.setVisibility(filteredRecipes.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private boolean matchesFilters(Recipe recipe) {
        return hasAllIngredients(recipe) &&
                matchesDiet(recipe) &&
                matchesCalories(recipe);
    }

    private boolean hasAllIngredients(Recipe recipe) {
        if (userIngredients.isEmpty()) return true;
        for (String keyIngredient : recipe.getKey_ingredients()) {
            if (!userIngredients.contains(keyIngredient.toLowerCase())) return false;
        }
        return true;
    }

    private boolean matchesDiet(Recipe recipe) {
        if (currentDietFilter == null) return true;
        String[] selectedDiets = currentDietFilter.split(", ");
        for (String diet : selectedDiets) {
            if (recipe.getDietary_info().toLowerCase().contains(diet.toLowerCase())) return true;
        }
        return false;
    }

    private boolean matchesCalories(Recipe recipe) {
        int calories = recipe.getCalories_per_serving();
        boolean minMatch = minCalorieFilter == -1 || calories >= minCalorieFilter;
        boolean maxMatch = maxCalorieFilter == -1 || calories <= maxCalorieFilter;
        return minMatch && maxMatch;
    }

    private void updateMealListUI(List<Recipe> filteredRecipes) {
        mealAdapter.updateData(filteredRecipes);
        textEmptyState.setVisibility(filteredRecipes.isEmpty() ? View.VISIBLE : View.GONE);
        textEmptyState.setText(filteredRecipes.isEmpty() ? "No meals match your filters" : "");
    }

    private void addIngredient(String ingredient) {
        String lowerIngredient = ingredient.toLowerCase();
        if (!userIngredients.contains(lowerIngredient)) {
            userIngredients.add(lowerIngredient);
            addIngredientToUI(ingredient);
            refreshMealList();
        }
    }

    private void addIngredientToUI(String ingredient) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tv = new TextView(this);
        tv.setText(ingredient);
        tv.setTextSize(16);
        tv.setPadding(8, 8, 8, 8);

        Button btn = new Button(this);
        btn.setText("X");
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.RED);
        btn.setOnClickListener(v -> {
            ingredientsContainer.removeView(layout);
            userIngredients.remove(ingredient.toLowerCase());
            refreshMealList();
        });

        layout.addView(tv);
        layout.addView(btn);
        ingredientsContainer.addView(layout);
    }

    // ... [Rest of your camera and filter handling methods remain the same]

    private class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
        private List<Recipe> recipes;

        public MealAdapter(List<Recipe> recipes) {
            this.recipes = recipes;
        }

        public void updateData(List<Recipe> newRecipes) {
            this.recipes = newRecipes;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_meal, parent, false);
            return new MealViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
            holder.bind(recipes.get(position));
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }

        class MealViewHolder extends RecyclerView.ViewHolder {
            private final TextView nameTextView;
            private final TextView ingredientsTextView;
            private final TextView dietaryInfoTextView;
            private final TextView caloriesTextView;
            private final ImageButton heartButton;
            private final TextView recipe_stepsTextView;

            public MealViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.mealName);
                ingredientsTextView = itemView.findViewById(R.id.mealIngredients);
                recipe_stepsTextView = itemView.findViewById(R.id.mealRecipe_steps);
                dietaryInfoTextView = itemView.findViewById(R.id.mealDietaryInfo);
                caloriesTextView = itemView.findViewById(R.id.mealCalories);
                heartButton = itemView.findViewById(R.id.heartButton);
            }

            public void bind(Recipe recipe) {
                nameTextView.setText(recipe.getName() != null ? recipe.getName() : "Unnamed Recipe");
                recipe_stepsTextView.setText("How to make: " + recipe.getRecipe_steps());
                ingredientsTextView.setText(formatIngredients(recipe.getIngredients()));
                dietaryInfoTextView.setText("Dietary: " + (recipe.getDietary_info() != null ? recipe.getDietary_info() : ""));
                caloriesTextView.setText("Calories: " + recipe.getCalories_per_serving());

                heartButton.setOnClickListener(v -> {
                    // Toggle based on tag instead of drawable comparison
                    boolean isFilled = heartButton.getTag() != null && heartButton.getTag().equals("filled");

                    // Update immediately
                    if (isFilled) {
                        heartButton.setImageResource(R.drawable.ic_heart_outline);
                        heartButton.setTag("outline");
                    } else {
                        heartButton.setImageResource(R.drawable.ic_heart_filled);
                        heartButton.setTag("filled");
                    }

                    // Process database operation
                    toggleFavorite(recipe);
                });
                updateFavoriteButton(recipe);
            }

            private String formatIngredients(List<String> ingredients) {
                StringBuilder sb = new StringBuilder("Ingredients: ");
                if (ingredients != null) {
                    for (String ingredient : ingredients) {
                        sb.append(userIngredients.contains(ingredient.toLowerCase()) ?
                                ingredient : "[MISSING] " + ingredient).append(", ");
                    }
                }
                return sb.toString().replaceAll(", $", "");
            }

            private void updateFavoriteButton(Recipe recipe) {
                favsRef.child(recipe.getName()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    heartButton.setImageResource(R.drawable.ic_heart_filled);
                                    heartButton.setTag("filled");
                                } else {
                                    heartButton.setImageResource(R.drawable.ic_heart_outline);
                                    heartButton.setTag("outline");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

// With this:
                                Log.e("Favorite", "Error checking favorite: " + error.getMessage());                            }
                        }
                );
            }

            private void toggleFavorite(Recipe recipe) {
                DatabaseReference recipeRef = favsRef.child(recipe.getName());

                recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Remove from favorites
                            recipeRef.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        heartButton.setImageResource(R.drawable.ic_heart_outline);
                                        showToast("Removed from favorites");
                                    })
                                    .addOnFailureListener(e -> showToast("Failed to remove"));
                        } else {
                            // Add to favorites
                            recipeRef.setValue(recipe)
                                    .addOnSuccessListener(aVoid -> {
                                        heartButton.setImageResource(R.drawable.ic_heart_filled);
                                        showToast("Added to favorites");
                                    })
                                    .addOnFailureListener(e -> showToast("Failed to add"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showToast("Error: " + error.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    // Camera and Filter Methods Section
// =================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle camera result
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleImageCaptureResult(data);
        }
        // Handle filter result
        else if (requestCode == REQUEST_FILTER && resultCode == RESULT_OK) {
            handleFilterResult(data);
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                showToast("No camera app found!");
            }
        } catch (Exception e) {
            Log.e("CameraError", "Camera launch failed", e);
            showToast("Camera error: " + e.getMessage());
        }
    }

    private void handleImageCaptureResult(Intent data) {
        if (data == null || data.getExtras() == null) {
            Log.e("CameraResult", "No image data received");
            showToast("Failed to capture image");
            return;
        }

        try {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap == null) {
                throw new IllegalArgumentException("Null image bitmap");
            }

            updateUIForAnalysisStart();
            analyzeImageWithClarifai(imageBitmap);

        } catch (Exception e) {
            Log.e("ImageProcessing", "Error handling image", e);
            updateUIForAnalysisEnd();
            showToast("Image processing failed: " + e.getMessage());
        }
    }

    private void updateUIForAnalysisStart() {
        runOnUiThread(() -> {
            resultTextView.setText("Analyzing food...");
            btnCamera.setEnabled(false);
            resultsScroll.setVisibility(View.VISIBLE);
        });
    }

    private void analyzeImageWithClarifai(Bitmap imageBitmap) {
        ClarifaiHelper.recognizeFood(imageBitmap, new ClarifaiHelper.ClarifaiCallback() {
            @Override
            public void onSuccess(List<String> ingredients) {
                handleAnalysisSuccess(ingredients);
            }

            @Override
            public void onFailure(Exception e) {
                handleAnalysisFailure(e);
            }
        });
    }

    private void handleAnalysisSuccess(List<String> ingredients) {
        runOnUiThread(() -> {
            btnCamera.setEnabled(true);
            if (ingredients == null || ingredients.isEmpty()) {
                resultTextView.setText("No ingredients detected");
                return;
            }

            int newIngredients = processDetectedIngredients(ingredients);
            updateResultsUI(newIngredients);
        });
    }

    private int processDetectedIngredients(List<String> ingredients) {
        int addedCount = 0;
        for (String ingredient : ingredients) {
            String cleanIngredient = ingredient.trim().toLowerCase();
            if (!TextUtils.isEmpty(cleanIngredient) && !userIngredients.contains(cleanIngredient)) {
                userIngredients.add(cleanIngredient);
                addIngredientToUI(cleanIngredient);
                addedCount++;
            }
        }
        return addedCount;
    }

    private void updateResultsUI(int newIngredients) {
        if (newIngredients > 0) {
            resultTextView.setText(String.format("Added %d new ingredients!", newIngredients));
            refreshMealList();
        } else {
            resultTextView.setText("No new ingredients found");
        }
    }

    private void handleAnalysisFailure(Exception e) {
        runOnUiThread(() -> {
            btnCamera.setEnabled(true);
            resultTextView.setText("Analysis failed: " + e.getMessage());
            Log.e("ClarifaiError", "Food recognition failed", e);
        });
    }

    private void openFilterActivity() {
        try {
            Intent intent = new Intent(this, FilterActivity.class);
            prepareFilterIntentExtras(intent);
            startActivityForResult(intent, REQUEST_FILTER);
        } catch (Exception e) {
            Log.e("FilterError", "Failed to open filters", e);
            showToast("Error opening filters");
        }
    }

    private void prepareFilterIntentExtras(Intent intent) {
        // Pass current filter values
        if (currentDietFilter != null) {
            intent.putExtra("selectedDiets", currentDietFilter.split(", "));
        }
        intent.putExtra("currentMinCal", minCalorieFilter);
        intent.putExtra("currentMaxCal", maxCalorieFilter);
    }

    private void handleFilterResult(Intent data) {
        if (data == null) {
            Log.e("FilterResult", "Null filter data received");
            return;
        }

        if (data.getBooleanExtra("clearAll", false)) {
            resetAllFilters();
        } else {
            applyNewFilters(data);
        }

        refreshMealList();
        resultsScroll.setVisibility(View.VISIBLE);
    }

    private void resetAllFilters() {
        currentDietFilter = null;
        minCalorieFilter = -1;
        maxCalorieFilter = -1;
        showToast("All filters cleared");
    }

    private void applyNewFilters(Intent data) {
        // Process dietary filters
        ArrayList<String> selectedDiets = data.getStringArrayListExtra("selectedDiets");
        currentDietFilter = (selectedDiets != null && !selectedDiets.isEmpty())
                ? TextUtils.join(", ", selectedDiets)
                : null;

        // Process calorie filters
        minCalorieFilter = data.getIntExtra("minCalories", -1);
        maxCalorieFilter = data.getIntExtra("maxCalories", -1);

        // Validate calorie range
        if (maxCalorieFilter != -1 && minCalorieFilter != -1) {
            if (minCalorieFilter > maxCalorieFilter) {
                int temp = minCalorieFilter;
                minCalorieFilter = maxCalorieFilter;
                maxCalorieFilter = temp;
            }
        }
    }

// Utility Methods
// ===============

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(
                MainActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show());
    }

    private void updateUIForAnalysisEnd() {
        runOnUiThread(() -> {
            btnCamera.setEnabled(true);
            resultTextView.setText("");
        });
    }

}