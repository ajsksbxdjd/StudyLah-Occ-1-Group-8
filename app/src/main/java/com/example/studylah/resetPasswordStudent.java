package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class resetPasswordStudent extends AppCompatActivity {
    private EditText password, confirmPassword;
    private Button updateButton;
    private RequestQueue requestQueue;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        updateButton = findViewById(R.id.updateButton);
        requestQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error retrieving username. Please try again.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity to prevent further issues
            return;
        }

        updateButton.setOnClickListener(v -> validateAndUpdatePassword());
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Optional: Close current activity to prevent going back to it
        });
    }
    private void validateAndUpdatePassword() {
        String newPassword = password.getText().toString().trim();
        String newConfirmPassword=confirmPassword.getText().toString().trim();

        // Validate input
        if (newPassword.isEmpty() || newConfirmPassword.isEmpty()) {
            Toast.makeText(this, "Password fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(newConfirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash the new password
        String hashedPassword = hashPassword(newPassword);

        // Send update request
        updatePasswordInDatabase(username, hashedPassword, (success, message) -> {
            if (success) {
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                Toast.makeText(this, "Failed to update password: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String hashPassword(String password) {
        // Example of hashing with SHA-256
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private void updatePasswordInDatabase(String username, String hashedPassword, UpdateCallback callback) {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/student/users";

        try {
            JSONObject payload = new JSONObject();
            payload.put("current_username", username);  // Current username
            payload.put("p_password", hashedPassword);  // Hashed password field

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
    private void navigateToLogin() {
        Intent intent = new Intent(resetPasswordStudent.this, LoginStudent.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current activity
    }
    private interface UpdateCallback {
        void onUpdate(boolean success, String message);
    }
    @Override
    protected void onDestroy() {
        // Ensure no redundant callback is unregistered
        super.onDestroy();
    }
}