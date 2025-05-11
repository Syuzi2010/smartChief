package com.example.smartchief;
 public class Filter {
    private String dietaryPreference; // can be null or empty
    private Integer minCalories;      // can be null
    private Integer maxCalories;      // can be null

    public Filter(String dietaryPreference, Integer minCalories, Integer maxCalories) {
        this.dietaryPreference = dietaryPreference;
        this.minCalories = minCalories;
        this.maxCalories = maxCalories;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    public Integer getMinCalories() {
        return minCalories;
    }

    public void setMinCalories(Integer minCalories) {
        this.minCalories = minCalories;
    }

    public Integer getMaxCalories() {
        return maxCalories;
    }

    public void setMaxCalories(Integer maxCalories) {
        this.maxCalories = maxCalories;
    }

    public boolean matches(MainActivity.Recipe recipe) {
        boolean matchesCalories = (minCalories == null || recipe.getCalories_per_serving() >= minCalories) &&
                (maxCalories == null || recipe.getCalories_per_serving() <= maxCalories);

        boolean matchesDiet = (dietaryPreference == null || dietaryPreference.isEmpty() ||
                recipe.getDietary_info().equalsIgnoreCase(dietaryPreference));

        return matchesCalories && matchesDiet;
    }
}

