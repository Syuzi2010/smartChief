package com.example.smartchief;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavoritesActivity extends AppCompatActivity {

    private LinearLayout favoritesLayout;
    private FirebaseUser currentUser;
    private DatabaseReference favsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesLayout = findViewById(R.id.favorites_layout);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        favsRef = FirebaseDatabase.getInstance().getReference("favorites")
                .child(currentUser.getUid());

        setupBottomBar();
        loadFavorites();
    }

    private void setupBottomBar() {
        // Ensure you have ImageButtons with these IDs in your bottom bar layout
        ImageButton navHome = findViewById(R.id.nav_home);
        ImageButton navFavorites = findViewById(R.id.nav_favorites);

        ImageButton navAccount = findViewById(R.id.nav_account);

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navFavorites.setOnClickListener(v -> {/* Already here */});
        navAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
    }

    private void loadFavorites() {
        favsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoritesLayout.removeAllViews();
                for (DataSnapshot favSnap : snapshot.getChildren()) {
                    Recipe recipe = favSnap.getValue(Recipe.class);
                    if (recipe != null) {
                        String dishName = recipe.getName();
                        if (dishName == null || dishName.trim().isEmpty()) {
                            dishName = "Unknown Dish";
                        }
                        final String finalDishName = dishName;  // <--- FIX: make a final copy for lambdas

                        // Create a vertical layout for each favorite
                        LinearLayout recipeLayout = new LinearLayout(FavoritesActivity.this);
                        recipeLayout.setOrientation(LinearLayout.VERTICAL);
                        recipeLayout.setPadding(20, 20, 20, 20);

                        StringBuilder sb = new StringBuilder();
                        sb.append("🍲 ").append(dishName).append("\n");
                        sb.append("🔥 Calories: ").append(recipe.getCalories_per_serving()).append("\n");
                        sb.append("🥑 Dietary Info: ").append(recipe.getDietary_info()).append("\n");
                        sb.append("📋 Ingredients: ");
                        if (recipe.getIngredients() != null) {
                            for (String ingredient : recipe.getIngredients()) {
                                sb.append(ingredient).append(", ");
                            }
                            // Remove trailing comma
                            if (!recipe.getIngredients().isEmpty())
                                sb.setLength(sb.length() - 2);
                        }
                        sb.append("\n");
                        sb.append("👩🍳 How to make:\n");
                        if (recipe.getRecipe_steps() != null) {
                            int i = 1;
                            for (String step : recipe.getRecipe_steps()) {
                                sb.append(i++).append(". ").append(step).append("\n");
                            }
                        }
                        ImageButton heartButton = new ImageButton(FavoritesActivity.this);
                        heartButton.setImageResource(R.drawable.ic_heart_filled);
                        heartButton.setOnClickListener(v -> {
                            favsRef.child(finalDishName).removeValue((error, ref) -> {
                                if (error == null) {
                                    Toast.makeText(FavoritesActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                    loadFavorites(); // Refresh the list
                                } else {
                                    Toast.makeText(FavoritesActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });

                        TextView recipeView = new TextView(FavoritesActivity.this);
                        recipeView.setText(sb.toString());
                        recipeView.setTextColor(Color.BLACK);
                        recipeView.setPadding(10, 10, 10, 20);

                        // Add filled heart button for removing from favorites
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        heartButton.setLayoutParams(params);
                        heartButton.setBackgroundColor(Color.TRANSPARENT);
                        heartButton.setImageResource(R.drawable.ic_heart_filled); // Use your filled heart drawable

                        // Remove from favorites on click
                        heartButton.setOnClickListener(v -> {
                            favsRef.child(finalDishName).removeValue((error, ref) -> {
                                if (error == null) {
                                    Toast.makeText(FavoritesActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                    loadFavorites(); // Refresh the list
                                } else {
                                    Toast.makeText(FavoritesActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });

                        recipeLayout.addView(recipeView);
                        recipeLayout.addView(heartButton);
                        favoritesLayout.addView(recipeLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoritesActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}