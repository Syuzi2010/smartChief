package com.example.smartchief;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClarifaiHelper {

    public interface ClarifaiCallback {
        void onSuccess(List<String> ingredients);
        void onFailure(Exception e);
    }

    public static void recognizeFood(Bitmap bitmap, ClarifaiCallback callback) {
        new FoodRecognitionTask(callback).execute(bitmap);
    }

    private static class FoodRecognitionTask extends AsyncTask<Bitmap, Void, List<String>> {
        private Exception exception;
        private final ClarifaiCallback callback;

        FoodRecognitionTask(ClarifaiCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<String> doInBackground(Bitmap... bitmaps) {
            HttpURLConnection connection = null;
            try {
                // 1. Convert bitmap to base64 (NO_WRAP is crucial)
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);
                String base64Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);

                // 2. Create the exact JSON structure Clarifai expects
                JSONObject requestBody = new JSONObject();
                JSONArray inputs = new JSONArray();
                JSONObject input = new JSONObject();
                JSONObject data = new JSONObject();
                JSONObject image = new JSONObject();

                image.put("base64", base64Image);
                data.put("image", image);
                input.put("data", data);
                inputs.put(input);
                JSONObject userAppId = new JSONObject();
                userAppId.put("user_id", "1w8itqnys3w9");
                userAppId.put("app_id", "image-moderation-f7fb56260be5");

                requestBody.put("user_app_id", userAppId);
                requestBody.put("inputs", inputs);

                URL url = new URL("https://api.clarifai.com/v2/models/food-item-recognition/versions/1d5fd481e0cf4826aa72ec3ff049e044/outputs");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Key 2eb14fad0d0a4b19854549752c06cf57");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Android App");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                // 4. Send the request with proper encoding
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] inputBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(inputBytes, 0, inputBytes.length);
                }

                // 5. Handle response
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    // Read error stream for detailed message
                    try (BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorResponse.append(line);
                        }
                        throw new IOException("HTTP " + responseCode + " - " + errorResponse.toString());
                    }
                }

                // 6. Parse successful response
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    return parseJsonResponse(response.toString());
                }

            } catch (Exception e) {
                this.exception = e;
                Log.e("ClarifaiAPI", "Error: ", e);
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (exception != null) {
                callback.onFailure(exception);
            } else {
                callback.onSuccess(result != null ? result : new ArrayList<>());
            }
        }

        private List<String> parseJsonResponse(String jsonResponse) {
            List<String> ingredients = new ArrayList<>();
            try {
                JSONObject response = new JSONObject(jsonResponse);
                JSONArray outputs = response.getJSONArray("outputs");
                JSONArray concepts = outputs.getJSONObject(0)
                        .getJSONObject("data")
                        .getJSONArray("concepts");

                for (int i = 0; i < concepts.length(); i++) {
                    JSONObject concept = concepts.getJSONObject(i);
                    if (concept.getDouble("value") > 0.40) {
                        ingredients.add(concept.getString("name"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ingredients;
        }
    }
}












