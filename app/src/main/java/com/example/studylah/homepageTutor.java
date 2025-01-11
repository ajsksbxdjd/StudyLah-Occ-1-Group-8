package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class homepageTutor extends AppCompatActivity {
    private String username, displayName, email, university, tutor_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        displayName = intent.getStringExtra("display_name");
        email = intent.getStringExtra("email");
        university=intent.getStringExtra("university");
        tutor_description=intent.getStringExtra("tutor_description");
        int bm=intent.getIntExtra("bm",0);
        int bi=intent.getIntExtra("bi",0);
        int math=intent.getIntExtra("math",0);
        int add_math=intent.getIntExtra("add_math",0);
        int physics=intent.getIntExtra("physics",0);
        int chemistry=intent.getIntExtra("chemistry",0);
        int biology=intent.getIntExtra("biology",0);
        int sejarah=intent.getIntExtra("sejarah",0);
        int moral=intent.getIntExtra("moral",0);
        int islam=intent.getIntExtra("islam",0);
        int economy=intent.getIntExtra("economy",0);
        int accounting=intent.getIntExtra("accounting",0);
        Button editButton=findViewById(R.id.editProfileButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(homepageTutor.this, editProfileTutor.class);
                editIntent.putExtra("username", username);
                editIntent.putExtra("display_name", displayName);
                editIntent.putExtra("email", email);
                editIntent.putExtra("university", university);
                editIntent.putExtra("tutor_description", tutor_description);
                editIntent.putExtra("bm", bm);
                editIntent.putExtra("bi", bi);
                editIntent.putExtra("math", math);
                editIntent.putExtra("add_math", add_math);
                editIntent.putExtra("physics", physics);
                editIntent.putExtra("chemistry", chemistry);
                editIntent.putExtra("biology", biology);
                editIntent.putExtra("sejarah", sejarah);
                editIntent.putExtra("moral", moral);
                editIntent.putExtra("islam", islam);
                editIntent.putExtra("economy", economy);
                editIntent.putExtra("accounting", accounting);
                startActivityForResult(editIntent, 1);            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchLatestUserData(); // Fetch updated data from the server
        }
    }

    private void fetchLatestUserData() {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("ServerResponse", "Response: " + response.toString());
                        JSONArray items = response.getJSONArray("items");

                        // Filter the user data based on the email
                        JSONObject user = null;
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject currentUser = items.getJSONObject(i);
                            if (currentUser.optString("email").equals(email)) {
                                user = currentUser;
                                break;
                            }
                        }

                        if (user != null) {
                            // Update local variables
                            username = user.optString("username", username);
                            displayName = user.optString("display_name", displayName);
                            university=user.optString("university",university);
                            tutor_description=user.optString("tutor_description",tutor_description);

                            // Log the updated values
                            Log.d("UpdatedData", "Username: " + username + ", Display Name: " + displayName);
                        } else {
                            Log.e("FetchError", "No user data found for email: " + email);
                        }
                    } catch (JSONException e) {
                        Log.e("JSONError", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Error fetching user data: ";
                    if (error.networkResponse != null) {
                        errorMessage += new String(error.networkResponse.data);
                    } else {
                        errorMessage += error.getMessage();
                    }
                    Log.e("VolleyError", errorMessage);
                });

        Volley.newRequestQueue(this).add(request);
    }

}