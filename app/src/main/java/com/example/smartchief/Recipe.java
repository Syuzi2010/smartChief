package com.example.smartchief;

import java.util.List;

public class Recipe {
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

