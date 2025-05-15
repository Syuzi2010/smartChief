package com.example.smartchief;

import android.os.Bundle;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesLayout = findViewById(R.id.favorites_layout);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        favsRef = FirebaseDatabase.getInstance().getReference("favorites")
                .child(currentUser.getUid());

        loadFavorites();
    }

    private void loadFavorites() {
        favsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoritesLayout.removeAllViews();
                for (DataSnapshot favSnap : snapshot.getChildren()) {
                    Recipe recipe = favSnap.getValue(Recipe.class);
                    if (recipe != null) {
                        TextView recipeView = new TextView(FavoritesActivity.this);
                        recipeView.setText("🍲 " + recipe.getName() + "\n🔥 Calories: " + recipe.getCalories_per_serving());
                        recipeView.setPadding(10, 10, 10, 20);
                        favoritesLayout.addView(recipeView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoritesActivity.this, "Failed to load activity_favorites.xml", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

