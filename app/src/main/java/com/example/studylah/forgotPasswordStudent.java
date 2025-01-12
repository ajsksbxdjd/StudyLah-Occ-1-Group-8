package com.example.studylah;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class forgotPasswordStudent extends AppCompatActivity {
    private EditText email;
    private Button resetButton;
    private int verificationCode;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.email);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isValidEmail(s.toString().trim())) {
                    email.setError("Invalid email format");
                } else {
                    email.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        resetButton = findViewById(R.id.updateButton);

        resetButton.setOnClickListener(v -> sendVerifyEmail());
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Optional: Close current activity to prevent going back to it
        });
    }
    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void sendVerifyEmail() {
        String emailAddress = email.getText().toString().trim();

        // Validate email format before proceeding
        if (!isValidEmail(emailAddress)) {
            email.setError("Please enter a valid email address.");
            Toast.makeText(this, "Invalid email address format. Please correct it.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fetchAllUrl = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/student/users"; // Your backend endpoint

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest fetchAllRequest = new JsonObjectRequest(Request.Method.GET, fetchAllUrl, null, response -> {
            try {
                // The backend returns a JSON object with an "items" key
                JSONArray itemsArray = response.getJSONArray("items");

                boolean emailExists = false;

                // Loop through the array to find the email
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject student = itemsArray.getJSONObject(i);
                    String emailInDatabase = student.getString("email");

                    if (emailAddress.equals(emailInDatabase)) {
                        emailExists = true;
                        username = student.getString("username"); // Retrieve the username
                        break;
                    }
                }

                if (!emailExists) {
                    Toast.makeText(forgotPasswordStudent.this, "Email does not exist in our records.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Email exists; proceed to send the verification code
                sendVerificationEmail(emailAddress);

            } catch (JSONException e) {
                Toast.makeText(forgotPasswordStudent.this, "Error parsing response. Try again.", Toast.LENGTH_SHORT).show();
                Log.e("ResponseError", e.toString());
            }
        }, error -> {
            String errorMessage = "Failed to fetch student data. ";
            if (error.networkResponse != null) {
                errorMessage += "Status code: " + error.networkResponse.statusCode + " ";
                if (error.networkResponse.data != null) {
                    errorMessage += "Response: " + new String(error.networkResponse.data);
                }
            } else {
                errorMessage += error.getMessage();
            }
            Toast.makeText(forgotPasswordStudent.this, errorMessage, Toast.LENGTH_SHORT).show();
            Log.e("FetchError", error.toString());
        });

        requestQueue.add(fetchAllRequest);


    }

    private void sendVerificationEmail(String emailAddress) {
        // Generate a verification code
        Random random = new Random();
        verificationCode = random.nextInt(8999) + 1000; // Generate a random 4-digit code
        String url = "http://10.0.2.2:80/resetPassword.php"; // Your PHP script URL

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("EmailResponse", response);
            Toast.makeText(forgotPasswordStudent.this, "Email sent successfully!", Toast.LENGTH_SHORT).show();

            // Show the dialog for entering the verification code
            showCodeVerificationDialog();

        }, error -> {
            String errorMessage = error.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "An unexpected error occurred. Please try again later.";
            }
            Toast.makeText(forgotPasswordStudent.this, errorMessage, Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailAddress);
                params.put("code", String.valueOf(verificationCode));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void showCodeVerificationDialog() {
        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify Your Email");

        // Create an EditText for entering the code
        EditText codeInput = new EditText(this);
        codeInput.setHint("Enter the verification code");
        codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(codeInput);

        // Add "Verify" button
        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredCode = codeInput.getText().toString().trim();
            if (!enteredCode.isEmpty() && enteredCode.equals(String.valueOf(verificationCode))) {
                // Correct code entered, proceed to the next page
                Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(forgotPasswordStudent.this, resetPasswordStudent.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish(); // Close current activity
            } else {
                // Incorrect code
                Toast.makeText(this, "Invalid code. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Add "Cancel" button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}