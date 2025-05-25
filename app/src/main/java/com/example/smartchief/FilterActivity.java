package com.example.smartchief;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private EditText minCaloriesEditText;
    private EditText maxCaloriesEditText;
    private Button applyFilterButton;
    private Button clearFiltersButton;
    private LinearLayout dietaryCheckboxContainer;
    private List<String> selectedDiets = new ArrayList<>();

    // Dietary options
    private final String[] DIETARY_OPTIONS = {"Vegetarian", "Vegan", "Gluten-Free", "Keto", "Paleo", "Dairy-Free"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        minCaloriesEditText = findViewById(R.id.editTextMinCalories);
        maxCaloriesEditText = findViewById(R.id.editTextMaxCalories);
        applyFilterButton = findViewById(R.id.btnApplyFilter);
        clearFiltersButton = findViewById(R.id.btnClearFilters);
        dietaryCheckboxContainer = findViewById(R.id.dietaryCheckboxContainer);

        // Create checkboxes for dietary options
        createDietaryCheckboxes();

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
            // Get calorie values
            String minCaloriesStr = minCaloriesEditText.getText().toString();
            String maxCaloriesStr = maxCaloriesEditText.getText().toString();

            Integer minCalories = null;
            Integer maxCalories = null;

            try {
                if (!minCaloriesStr.isEmpty()) {
                    minCalories = Integer.parseInt(minCaloriesStr);
                }
                if (!maxCaloriesStr.isEmpty()) {
                    maxCalories = Integer.parseInt(maxCaloriesStr);
                }
            } catch (NumberFormatException e) {
                // Handle invalid number input
                return;
            }

            // Validate that min is not greater than max if both are set
            if (minCalories != null && maxCalories != null && minCalories > maxCalories) {
                // Show error to user
                return;
            }

            Intent resultIntent = new Intent();

            // Add dietary filters if any are selected
            if (!selectedDiets.isEmpty()) {
                resultIntent.putStringArrayListExtra("selectedDiets", new ArrayList<>(selectedDiets));
            }

            // Add calorie filters
            if (minCalories != null) {
                resultIntent.putExtra("minCalories", minCalories);
            }

            if (maxCalories != null) {
                resultIntent.putExtra("maxCalories", maxCalories);
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        // Clear filters button
        clearFiltersButton.setOnClickListener(v -> {
            // Clear all checkboxes
            for (int i = 0; i < dietaryCheckboxContainer.getChildCount(); i++) {
                View view = dietaryCheckboxContainer.getChildAt(i);
                if (view instanceof CheckBox) {
                    ((CheckBox) view).setChecked(false);
                }
            }
            selectedDiets.clear();

            // Clear calorie fields
            minCaloriesEditText.setText("");
            maxCaloriesEditText.setText("");

            // Optionally send back a result indicating all filters are cleared
            Intent resultIntent = new Intent();
            resultIntent.putExtra("clearAll", true);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private void createDietaryCheckboxes() {
        for (String diet : DIETARY_OPTIONS) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(diet);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedDiets.add(diet);
                } else {
                    selectedDiets.remove(diet);
                }
            });
            dietaryCheckboxContainer.addView(checkBox);
        }
    }
}