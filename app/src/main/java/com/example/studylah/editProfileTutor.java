package com.example.studylah;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class editProfileTutor extends AppCompatActivity {
    private TextView Username, Email, DisplayName, University, Description;
    private ImageButton editDisplayName, editUsername, editUniversity, editDescription;
    private RequestQueue requestQueue;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize services
        requestQueue = Volley.newRequestQueue(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Username = findViewById(R.id.Username);
        DisplayName = findViewById(R.id.DisplayName);
        Email = findViewById(R.id.Email);
        University = findViewById(R.id.University);
        Description = findViewById(R.id.Description);

        editDisplayName = findViewById(R.id.editDisplayName);
        editUsername = findViewById(R.id.editUsername);
        editUniversity = findViewById(R.id.editUniversity);
        editDescription = findViewById(R.id.editDescription);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String initialUsername = intent.getStringExtra("username");
        String initialDisplayName = intent.getStringExtra("display_name");
        String initialEmail = intent.getStringExtra("email");
        String initialUniversity = intent.getStringExtra("university");
        String initialTutorDescription = intent.getStringExtra("tutor_description");

        // Set TextViews
        Username.setText(initialUsername);
        DisplayName.setText(initialDisplayName);
        Email.setText(initialEmail);
        University.setText(initialUniversity);
        Description.setText(initialTutorDescription);

        // Set up click listeners for edit buttons
        editDisplayName.setOnClickListener(v -> handleFieldEdit("Display Name", DisplayName, "p_display_name", Username.getText().toString()));
        editUsername.setOnClickListener(v -> handleFieldEdit("Username", Username, "p_username", Username.getText().toString()));
        editUniversity.setOnClickListener(v -> handleFieldEdit("University", University, "p_university", Username.getText().toString()));
        editDescription.setOnClickListener(v -> handleFieldEdit("Description", Description, "p_tutor_description", Username.getText().toString()));
        Button editSubjectButton=findViewById(R.id.editSubjectButton);
        editSubjectButton.setOnClickListener(v -> fetchLatestSubjectData());
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Optional: Close current activity to prevent going back to it
        });
    }
    private void fetchLatestSubjectData() {
        try {
            // Encode the username to handle special characters and spaces
            String rawUsername = Username.getText().toString().trim();
            Log.d("EncodedUsername", "Raw Username: " + rawUsername);
            String encodedUsername = URLEncoder.encode(rawUsername, "UTF-8");

            String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users?username=" + encodedUsername;
            Log.d("RequestURL", "Request URL: " + url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            // Log the full server response for debugging
                            Log.d("ServerResponse", "Response: " + response.toString());
                            // Extract the "items" array from the response
                            JSONArray items = response.optJSONArray("items");
                            if (items == null || items.length() == 0) {
                                Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Find the user matching the provided username
                            JSONObject matchingUser = null;
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject user = items.getJSONObject(i);
                                if (rawUsername.equals(user.optString("username"))) {
                                    matchingUser = user;
                                    break;
                                }
                            }
                            // If no matching user is found
                            if (matchingUser == null) {
                                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Extract subject data for the matching user
                            int bm = matchingUser.optInt("bm", 0);
                            int bi = matchingUser.optInt("bi", 0);
                            int math = matchingUser.optInt("math", 0);
                            int add_math = matchingUser.optInt("add_math", 0);
                            int physics = matchingUser.optInt("physics", 0);
                            int chemistry = matchingUser.optInt("chemistry", 0);
                            int biology = matchingUser.optInt("biology", 0);
                            int sejarah = matchingUser.optInt("sejarah", 0);
                            int moral = matchingUser.optInt("moral", 0);
                            int islam = matchingUser.optInt("islam", 0);
                            int economy = matchingUser.optInt("economy", 0);
                            int accounting = matchingUser.optInt("accounting", 0);

                            // Navigate to editSubjectStudent activity with fetched data
                            Intent intent = new Intent(editProfileTutor.this, editSubjectTutor.class);
                            intent.putExtra("username", rawUsername);
                            intent.putExtra("bm", bm);
                            intent.putExtra("bi", bi);
                            intent.putExtra("math", math);
                            intent.putExtra("add_math", add_math);
                            intent.putExtra("physics", physics);
                            intent.putExtra("chemistry", chemistry);
                            intent.putExtra("biology", biology);
                            intent.putExtra("sejarah", sejarah);
                            intent.putExtra("moral", moral);
                            intent.putExtra("islam", islam);
                            intent.putExtra("economy", economy);
                            intent.putExtra("accounting", accounting);
                            startActivity(intent);

                        } catch (JSONException e) {
                            Log.e("JSONError", "Error parsing response: " + e.getMessage());
                            Toast.makeText(this, "Error parsing subject data.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = "Network Error: ";
                        if (error.networkResponse != null) {
                            errorMessage += "HTTP " + error.networkResponse.statusCode;
                        } else {
                            errorMessage += error.getMessage();
                        }
                        Log.e("VolleyError", errorMessage);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    });

            requestQueue.add(request);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Error encoding username.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Handles editing of fields dynamically
     */
    private void handleFieldEdit(String fieldName, TextView fieldTextView, String databaseFieldName, String currentUsername) {
        String currentValue = fieldTextView.getText().toString();
        showEditDialog(fieldName, currentValue, newValue -> {
            updateFieldInDatabase(databaseFieldName, newValue, currentUsername, (success, errorMessage) -> {
                if (success) {
                    fieldTextView.setText(newValue); // Update the TextView with the new value
                    Toast.makeText(this, fieldName + " updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, prepareResultIntent());
                } else {
                    Toast.makeText(this, "Failed to update " + fieldName + ": " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private Intent prepareResultIntent() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("username", Username.getText().toString());
        resultIntent.putExtra("display_name", DisplayName.getText().toString());
        resultIntent.putExtra("university", University.getText().toString());
        resultIntent.putExtra("tutor_description", Description.getText().toString());
        return resultIntent;
    }
    /**
     * Shows a dialog for editing a field
     */
    private void showEditDialog(String fieldName, String currentValue, OnFieldUpdatedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + fieldName);

        // Add input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentValue);
        builder.setView(input);

        // Automatically show the keyboard when dialog appears
        input.requestFocus();
        input.post(() -> inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT));

        // Set up dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty() && !newValue.equals(currentValue)) {
                listener.onFieldUpdated(newValue);
            } else {
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
            }

            // Hide the keyboard after saving
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
            dialog.cancel();
        });

        builder.show();
    }

    /**
     * Updates a field in the database
     */
    private void updateFieldInDatabase(String fieldName, String newValue, String currentUsername, UpdateCallback callback) {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users";

        try {
            JSONObject payload = new JSONObject();
            payload.put("current_username", currentUsername);  // Current username
            payload.put(fieldName, newValue);                 // New value for the specified field

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if ("success".equals(status)) {
                                callback.onUpdate(true, message);
                            } else {
                                callback.onUpdate(false, message);
                            }
                        } catch (JSONException e) {
                            callback.onUpdate(false, "Invalid response from server.");
                        }
                    },
                    error -> {
                        String errorMessage = "Network Error: ";
                        if (error.networkResponse != null) {
                            errorMessage += new String(error.networkResponse.data);
                        } else {
                            errorMessage += error.getMessage();
                        }
                        callback.onUpdate(false, errorMessage);
                    });

            requestQueue.add(request);
        } catch (JSONException e) {
            callback.onUpdate(false, "Error forming request.");
        }
    }

    /**
     * Listener for updated fields
     */
    private interface OnFieldUpdatedListener {
        void onFieldUpdated(String newValue);
    }

    /**
     * Callback interface for database update results
     */
    private interface UpdateCallback {
        void onUpdate(boolean success, String errorMessage);
    }
    @Override
    protected void onDestroy() {
        // Ensure no redundant callback is unregistered
        super.onDestroy();
    }
}
