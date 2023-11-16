package com.example.wildsight;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;
public class FunFactsActivity extends AppCompatActivity {

    ImageView primaryImage;
    ImageView secondaryImage;
    TextView fact;

    ImageView next;
    ImageView prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fun_facts);

        primaryImage = findViewById(R.id.primaryImage);
        secondaryImage = findViewById(R.id.imageView4);
        fact = findViewById(R.id.textView);
        next = findViewById(R.id.imageView3);
        prev = findViewById(R.id.prev);

        next.setOnClickListener(View -> loadInfo());
        prev.setOnClickListener(View -> loadInfo());

    }

    private void loadInfo() {

        RequestQueue volleyQueue = Volley.newRequestQueue(FunFactsActivity.this);

        String url = "https://wildsight.onrender.com/random_fact";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(

                Request.Method.GET,
                url,
                null,

                (Response.Listener<JSONObject>) response -> {

                    JSONObject randomFact;
                    String factUrl;
                    String primaryUrl;
                    String secondaryUrl;

                    try {
                        randomFact = response.getJSONObject("random_fact");
                        primaryUrl = randomFact.getString("primaryImage");
                        secondaryUrl = randomFact.getString("secondaryImage");
                        factUrl = randomFact.getString("fact");
                        Glide.with(FunFactsActivity.this).load(primaryUrl).into(primaryImage);
                        Glide.with(FunFactsActivity.this).load(secondaryUrl).into(secondaryImage);
                        fact.setText(factUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                (Response.ErrorListener) error -> Toast.makeText(FunFactsActivity.this, "Some error occurred! " + error, Toast.LENGTH_LONG).show()
        );

        volleyQueue.add(jsonObjectRequest);
    }

}

