package com.example.smartchief;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class FilterActivity extends AppCompatActivity {

    private Spinner dietSpinner;
    private Button applyFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        dietSpinner = findViewById(R.id.spinnerDietary);
        applyFilterButton = findViewById(R.id.btnApplyFilter);

        // Example dietary options
        String[] diets = {"None", "Vegetarian", "Vegan", "Gluten-Free", "Keto"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diets);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietSpinner.setAdapter(adapter);

        // Home navigation button
        ImageButton homeButton = findViewById(R.id.nav_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(FilterActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Account navigation button
        ImageButton navAccount = findViewById(R.id.nav_account);
        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(FilterActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        // Apply filter button
        applyFilterButton.setOnClickListener(v -> {
            String selectedDiet = dietSpinner.getSelectedItem().toString();

            if (selectedDiet.equals("None")) {
                selectedDiet = null;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedDiet", selectedDiet);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}
