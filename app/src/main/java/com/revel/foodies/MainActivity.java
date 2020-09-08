package com.revel.foodies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    TextView gridText;
    ImageView gridImage;
    GridView grid;
    ArrayAdapter adapter;
    ArrayList<String> arrayImage;
    ArrayList<String> arrayLabel;
    JSONObject object;
    ProgressBar progressBar;
    EditText searchBar;
    String dish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = findViewById(R.id.gridView);
        progressBar = findViewById(R.id.progressBar);
        searchBar = findViewById(R.id.searchBar);
        arrayImage = new ArrayList<>();
        arrayLabel = new ArrayList<>();
        dish = "Burger";
        makeRequest(dish);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                progressBar.setVisibility(View.VISIBLE);
                dish = searchBar.getText().toString();
                makeRequest(dish);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                return false;
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(MainActivity.this, FullContentActivity.class)
                        .putExtra("position", i)
                        .putExtra("dish", dish)
                        .putExtra("image", arrayImage.get(i))
                        .putExtra("label", arrayLabel.get(i)));
            }
        });

    }

    private void makeRequest(String dish) {
        String url = "https://api.edamam.com/search?app_id=5e45b489&app_key=13a97e39a726582cae5afdc597594163&q=";

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + dish, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                object = response;
                arrayLabel = new ArrayList<>();
                arrayImage = new ArrayList<>();

                try {
                    JSONArray jsonArray = response.getJSONArray("hits");
                    if (jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject recipe = jsonArray.getJSONObject(i).getJSONObject("recipe");
                            String label = recipe.getString("label");
                            String image = recipe.getString("image");
                            arrayImage.add(image);
                            arrayLabel.add(label);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Found nothing...", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                adapter = new MyArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayImage);
                progressBar.setVisibility(View.GONE);
                grid.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        queue.add(request);
    }

    private class MyArrayAdapter extends ArrayAdapter {
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.custom_grid, parent, false);
            gridImage = v.findViewById(R.id.gridImage);
            gridText = v.findViewById(R.id.gridText);
            Glide.with(MainActivity.this).load(arrayImage.get(position)).placeholder(R.drawable.loading).circleCrop().into(gridImage);
            gridText.setText(arrayLabel.get(position));
            return v;
        }

        public MyArrayAdapter(MainActivity mainActivity, int simple_list_item_1, ArrayList arrayList) {
            super(mainActivity, simple_list_item_1, arrayList);

        }
    }
}