package com.example.wildsight;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FavoritesActivity extends AppCompatActivity{
    LinearLayout itemContainer;
    RequestQueue requestQueue;
    private Dialog customProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animals_list);
        itemContainer = findViewById(R.id.itemContainer);
        requestQueue = Volley.newRequestQueue(this);
        fetchAnimals();
    }
    private void fetchAnimals() {
        customProgressDialog = new Dialog(this);
        customProgressDialog.setContentView(R.layout.custom_progress_dialog);
        customProgressDialog.setCancelable(false);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customProgressDialog.show();
        String url = "https://wildsight.onrender.com/fav_animals";
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray animalsArray = response.getJSONArray("animals");
                            for (int i = 0; i < animalsArray.length(); i++) {
                                JSONObject animal = animalsArray.getJSONObject(i);
                                addAnimalToView(animal);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        customProgressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void addAnimalToView(JSONObject animal) {
        try {
            View itemView = getLayoutInflater().inflate(R.layout.item_layout_favourite, null);
            final ImageView itemImage = itemView.findViewById(R.id.itemImage);
            TextView itemName = itemView.findViewById(R.id.itemName);
            TextView moreInfoButton = itemView.findViewById(R.id.moreInfoButton);
            TextView descriptionText = itemView.findViewById(R.id.descriptionText);

            itemName.setText(animal.getString("category"));
            descriptionText.setText(animal.getString("shortDescription"));

            String imageUrl = animal.getString("image");
            Picasso.get().load(imageUrl)
                    .into(itemImage);

            moreInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (descriptionText.getVisibility() == View.VISIBLE) {
                        descriptionText.setVisibility(View.GONE);
                    } else {
                        descriptionText.setVisibility(View.VISIBLE);
                    }
                }
            });

            itemContainer.addView(itemView);
            Space space = new Space(this);
            space.setMinimumHeight(20);
            itemContainer.addView(space);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
