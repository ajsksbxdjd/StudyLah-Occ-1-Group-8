package com.example.studylah;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class subject_preference_tutor extends AppCompatActivity {
    private Button saveButton;
    private RequestQueue requestQueue;
    private Map<Integer, Boolean> subjectStates; // To track clicked states for subjects
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_preference_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requestQueue = Volley.newRequestQueue(this);

        // Retrieve username from Intent
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("username");
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "Error retrieving username. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        int[] buttonIds = {
                R.id.buttonHistory2,
                R.id.buttonPhysics2,
                R.id.buttonBio3,
                R.id.buttonEco,
                R.id.buttonAccounting,
                R.id.buttonIslam2,
                R.id.buttonMalay2,
                R.id.buttonChem,
                R.id.buttonMoral,
                R.id.buttonEnglish,
                R.id.buttonMaths,
                R.id.buttonAddmaths
        };
        subjectStates = new HashMap<>();

        // Loop through all buttons and set up click listeners
        for (int buttonId : buttonIds) {
            AppCompatButton button = findViewById(buttonId);
            if (button != null) {
                subjectStates.put(buttonId, false); // Default state is "not clicked"
                button.setOnClickListener(v -> toggleButtonState(button));
            } else {
                Log.e("ButtonError", "Button with ID " + buttonId + " is missing in the layout.");
            }
        }
        saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(v -> saveSubjectPreferences());
    }
    private void toggleButtonState(AppCompatButton button) {
        boolean isClicked = subjectStates.getOrDefault(button.getId(), false); // Get the current state, default to false if not found
        if (isClicked) {
            // Reset to original state
            button.setBackgroundResource(R.drawable.subject_bg);
            button.setTextColor(Color.parseColor("#0D0D0D"));
            subjectStates.put(button.getId(), false); // Update state to "not clicked"
        } else {
            // Set to clicked state
            button.setBackgroundColor(Color.parseColor("#698C56"));
            button.setTextColor(Color.WHITE);
            subjectStates.put(button.getId(), true); // Update state to "clicked"
        }
    }
    private void saveSubjectPreferences() {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users";

        try {
            JSONObject payload = new JSONObject();
            payload.put("current_username", currentUsername);

            // Add each subject's state to the payload
            payload.put("p_sejarah", subjectStates.getOrDefault(R.id.buttonHistory2,false) ? 1 : 0);
            payload.put("p_physics", subjectStates.getOrDefault(R.id.buttonPhysics2,false) ? 1 : 0);
            payload.put("p_biology", subjectStates.getOrDefault(R.id.buttonBio3,false) ? 1 : 0);
            payload.put("p_economy", subjectStates.getOrDefault(R.id.buttonEco,false) ? 1 : 0);
            payload.put("p_accounting", subjectStates.getOrDefault(R.id.buttonAccounting,false) ? 1 : 0);
            payload.put("p_islam", subjectStates.getOrDefault(R.id.buttonIslam2,false) ? 1 : 0);
            payload.put("p_bm", subjectStates.getOrDefault(R.id.buttonMalay2,false) ? 1 : 0);
            payload.put("p_chemistry", subjectStates.getOrDefault(R.id.buttonChem,false) ? 1 : 0);
            payload.put("p_moral", subjectStates.getOrDefault(R.id.buttonMoral,false) ? 1 : 0);
            payload.put("p_bi", subjectStates.getOrDefault(R.id.buttonEnglish,false) ? 1 : 0);
            payload.put("p_math", subjectStates.getOrDefault(R.id.buttonMaths,false) ? 1 : 0);
            payload.put("p_add_math", subjectStates.getOrDefault(R.id.buttonAddmaths,false) ? 1 : 0);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if ("success".equals(status)) {
                                Toast.makeText(this, "Subjects saved successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(subject_preference_tutor.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to save subjects: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Invalid response from server.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = "Network Error: ";
                        if (error.networkResponse != null) {
                            errorMessage += new String(error.networkResponse.data);
                        } else {
                            errorMessage += error.getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    });

            requestQueue.add(request);

        } catch (JSONException e) {
            Toast.makeText(this, "Error forming request.", Toast.LENGTH_SHORT).show();
        }
    }
}