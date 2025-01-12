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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.HashMap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class editProfileStudent extends AppCompatActivity {
    private TextView Username, Email, DisplayName;
    private ImageButton editDisplayName, editUsername;
    private RequestQueue requestQueue;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Username = findViewById(R.id.Username);
        DisplayName = findViewById(R.id.DisplayName);
        Email = findViewById(R.id.Email);
        editDisplayName = findViewById(R.id.editDisplayName);
        editUsername = findViewById(R.id.editUsername);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String initialUsername = intent.getStringExtra("username");
        String initialDisplayName = intent.getStringExtra("display_name");
        String initialEmail = intent.getStringExtra("email");

        // Set TextViews
        Username.setText(initialUsername);
        DisplayName.setText(initialDisplayName);
        Email.setText(initialEmail);

        editDisplayName.setOnClickListener(v -> showEditDialog("Display Name", DisplayName.getText().toString(), newName -> {
            updateFieldInDatabase("p_display_name", newName, initialUsername,
                    (success, errorMessage) -> {
                        if (success) {
                            DisplayName.setText(newName);
                            Toast.makeText(this, "Display name updated successfully", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("username", newName);
                            resultIntent.putExtra("display_name", DisplayName.getText().toString());
                            setResult(RESULT_OK, resultIntent);
                        } else {
                            Toast.makeText(this, "Failed to update Display Name: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        }));

        editUsername.setOnClickListener(v -> showEditDialog("Username", Username.getText().toString(), newName -> {
            updateFieldInDatabase("p_username", newName, Username.getText().toString(), (success, errorMessage) -> {
                if (success) {
                    Username.setText(newName); // Update the TextView to reflect the new username
                    Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("username", newName);
                    resultIntent.putExtra("display_name", DisplayName.getText().toString());
                    setResult(RESULT_OK, resultIntent);

                } else {
                    Toast.makeText(this, "Failed to update Username: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }));
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

            String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/student/users?username=" + encodedUsername;
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
                            Intent intent = new Intent(editProfileStudent.this, editSubjectStudent.class);
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
        input.post(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // Set up buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty() && !newValue.equals(currentValue)) {
                listener.onFieldUpdated(newValue);
            } else {
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
            }

            // Hide keyboard after saving
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Hide keyboard when dialog is canceled
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
            dialog.cancel();
        });

        builder.setOnDismissListener(dialog -> {
            // Ensure keyboard is hidden when dialog is dismissed
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });

        builder.show();
    }


    private void updateFieldInDatabase(String fieldName, String newValue, String currentUsername, UpdateCallback callback) {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/student/users";

        try {
            JSONObject payload = new JSONObject();
            payload.put("current_username", currentUsername);  // Current username
            payload.put(fieldName, newValue);                 // New value for username or display name

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


    private interface OnFieldUpdatedListener {
        void onFieldUpdated(String newValue);
    }

    private interface UpdateCallback {
        void onUpdate(boolean success, String errorMessage);
    }

    @Override
    protected void onDestroy() {
        // Ensure no redundant callback is unregistered
        super.onDestroy();
    }
}
