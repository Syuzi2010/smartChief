package com.example.smartchief;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        EditText ingredientsInput = findViewById(R.id.ingredientsInput);
        Button findDishesButton = findViewById(R.id.findDishesButton);
        TextView resultTextView = findViewById(R.id.resultTextView);

        // Sample matrix with dish names and their required ingredients
        List<List<String>> dishesMatrix = new ArrayList<>();
        dishesMatrix.add(Arrays.asList("Pasta", "tomato", "onion", "garlic", "basil"));
        dishesMatrix.add(Arrays.asList("Salad", "lettuce", "tomato", "cucumber", "olive oil"));
        dishesMatrix.add(Arrays.asList("Pizza", "flour", "tomato", "cheese", "olive oil"));
        dishesMatrix.add(Arrays.asList("Soup", "salt", "oil", "bread"));
        // (Add all your dish data here, for brevity only a few are shown)

        // Button click listener
        findDishesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String input = ingredientsInput.getText().toString().trim();
                if (!input.isEmpty()) {
                    // Parse ingredients
                    List<String> userIngredients = Arrays.asList(input.split("\\s*,\\s*"));

                    // Find matching dishes
                    List<String> matchingDishes = findDishes(dishesMatrix, userIngredients);

                    // Display results
                    if (!matchingDishes.isEmpty()) {
                        resultTextView.setText("Dishes you can make: " + matchingDishes);
                    } else {
                        resultTextView.setText("No dishes can be made with the given ingredients.");
                    }
                } else {
                    resultTextView.setText("Please enter some ingredients.");
                }
            }
        });
    }

    // Method to find matching dishes
    private List<String> findDishes(List<List<String>> dishesMatrix, List<String> userIngredients) {
        List<String> matchingDishes = new ArrayList<>();

        for (List<String> dish : dishesMatrix) {
            String dishName = dish.get(0);
            List<String> requiredIngredients = dish.subList(1, dish.size());

            // Check if user has all the required ingredients
            if (userIngredients.containsAll(requiredIngredients)) {
                matchingDishes.add(dishName);
            }
        }

        return matchingDishes;
    }
}
