package com.revel.foodies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

public class FullContentActivity extends AppCompatActivity {
    ImageView fullImage;
    TextView recipeText, ingredientsText, textFacts;
    LinearLayout bottomSheet;
    BottomSheetBehavior behavior;
    Button facts;
    String urlSource;
    String healthLabels = null, cautions = null;
    int calories = 0, totalWeight = 0;
    boolean factsSet = false;
    int fatQuantity, carbsQuantity, fiberQuantity, sugarQuantity, proteinQuantity, cholesterolQuantity;
    String fatUnit, carbsUnit, fiberUnit, sugarUnit, proteinUnit, cholesterolUnit;
    ProgressBar progressBarSheet, progressBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_content);
        bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        facts = findViewById(R.id.btnIngredients);
        textFacts = findViewById(R.id.textFacts);
        ingredientsText = findViewById(R.id.ingredientsText);
        recipeText = findViewById(R.id.recipeText);
        fullImage = findViewById(R.id.fullImage);
        progressBarSheet = findViewById(R.id.progressBarSheet);
        progressBarText = findViewById(R.id.progressBarText);
        Bundle bundle = getIntent().getExtras();
        final int position = bundle.getInt("position");
        final String dish = bundle.getString("dish");
        makeRequest(dish, position);
        String label = bundle.getString("label");
        ingredientsText.setText(label + ":\n\n");
        String image = bundle.getString("image");
        Glide.with(this).load(image).placeholder(R.drawable.loading).fitCenter().into(fullImage);

        recipeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FullContentActivity.this, WebActivity.class).putExtra("url", urlSource));
            }
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (!factsSet) {
                        setFacts(dish, position);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        facts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

    }

    private void setFacts(String dish, final int position) {
        factsSet = true;
        String url = "https://api.edamam.com/search?app_id=5e45b489&app_key=13a97e39a726582cae5afdc597594163&q=";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + dish, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject recipe;
                try {
                    recipe = response.getJSONArray("hits").getJSONObject(position).getJSONObject("recipe");
                    healthLabels = recipe.getString("healthLabels")
                            .replace("[", "")
                            .replace("]", "")
                            .replace(",", "\n\n");
                    textFacts.append("Health Labels - " + healthLabels + "\n\n");
                    cautions = recipe.getString("cautions")
                            .replace("[", "")
                            .replace("]", "")
                            .replace(",", "\n\n");
                    textFacts.append("Cautions - " + cautions + "\n\n");
                    calories = recipe.getInt("calories");
                    textFacts.append("Calories - " + calories + "\n\n");
                    totalWeight = recipe.getInt("totalWeight");
                    textFacts.append("Total Weight - " + totalWeight + "\n\n");
                    JSONObject totalNutrients = recipe.getJSONObject("totalNutrients");
                    JSONObject FAT = totalNutrients.getJSONObject("FAT");
                    fatQuantity = FAT.getInt("quantity");
                    fatUnit = FAT.getString("unit");
                    textFacts.append("Fat - " + fatQuantity + " " + fatUnit + "\n\n");
                    JSONObject CHOCDF = totalNutrients.getJSONObject("CHOCDF");
                    carbsQuantity = CHOCDF.getInt("quantity");
                    carbsUnit = CHOCDF.getString("unit");
                    textFacts.append("Carbs - " + carbsQuantity + " " + carbsUnit + "\n\n");
                    JSONObject FIBTG = totalNutrients.getJSONObject("FIBTG");
                    fiberQuantity = FIBTG.getInt("quantity");
                    fiberUnit = FIBTG.getString("unit");
                    textFacts.append("Fiber - " + fiberQuantity + " " + fiberUnit + "\n\n");
                    JSONObject SUGAR = totalNutrients.getJSONObject("SUGAR");
                    sugarQuantity = SUGAR.getInt("quantity");
                    sugarUnit = SUGAR.getString("unit");
                    textFacts.append("Sugar - " + sugarQuantity + " " + sugarUnit + "\n\n");
                    JSONObject PROCNT = totalNutrients.getJSONObject("PROCNT");
                    proteinQuantity = PROCNT.getInt("quantity");
                    proteinUnit = PROCNT.getString("unit");
                    textFacts.append("Protein - " + proteinQuantity + " " + proteinUnit + "\n\n");
                    JSONObject CHOLE = totalNutrients.getJSONObject("CHOLE");
                    cholesterolQuantity = CHOLE.getInt("quantity");
                    cholesterolUnit = CHOLE.getString("unit");
                    textFacts.append("Cholesterol - " + cholesterolQuantity + " " + cholesterolUnit + "\n\n");
                    progressBarSheet.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FullContentActivity.this, "error" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    private void makeRequest(String dish, final int position) {
        String url = "https://api.edamam.com/search?app_id=5e45b489&app_key=13a97e39a726582cae5afdc597594163&q=";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + dish, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject recipe;
                try {
                    recipe = response.getJSONArray("hits").getJSONObject(position).getJSONObject("recipe");
                    String ingredientsLines = recipe.getString("ingredientLines");
                    ingredientsLines = ingredientsLines.replace("[", "")
                            .replace("]", "")
                            .replace("\\", "")
                            .replace(",", "\n");
                    ingredientsText.append("Ingredients:\n\n" + ingredientsLines);
                    urlSource = recipe.getString("url");
                    progressBarText.setVisibility(View.GONE);
                    recipeText.setText("View recipe:-\n");
                    recipeText.setTextColor(Color.parseColor("#0000fd"));
                    recipeText.append(urlSource);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FullContentActivity.this, "error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }
}