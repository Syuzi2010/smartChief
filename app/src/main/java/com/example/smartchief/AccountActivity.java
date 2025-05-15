package com.example.smartchief;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {

    private TextView userEmailTextView;
    private Button btnFavorites, btnLogout;
    private ImageButton navHome, navFilter, navAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();

        userEmailTextView = findViewById(R.id.textViewEmail);
        btnFavorites = findViewById(R.id.btnFavorites);
        btnLogout = findViewById(R.id.btnLogout);

        // Show user email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmailTextView.setText(currentUser.getEmail());
        }

        // Logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AccountActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Go to Favorites
        btnFavorites.setOnClickListener(v -> {
            startActivity(new Intent(AccountActivity.this, FavoritesActivity.class));
        });

        // Bottom Nav
        navHome = findViewById(R.id.nav_home);
        navFilter = findViewById(R.id.nav_filter);
        navAccount = findViewById(R.id.nav_account);

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navFilter.setOnClickListener(v -> startActivity(new Intent(this, FilterActivity.class)));
        navAccount.setOnClickListener(v -> {}); // Already here
    }
}
