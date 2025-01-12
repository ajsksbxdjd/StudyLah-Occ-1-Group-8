package com.example.studylah;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.text.style.UnderlineSpan;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.InputStream;


public class Register extends AppCompatActivity {
    private EditText username, displayName, university, email, password, confirmPassword;
    private RadioGroup roleGroup;
    private Button registerButton;
    int code;
    private TextView verifyEmail, universityText;
    private boolean isVerified = false;
    private static final String SHARED_PREFS = "CooldownPrefs";
    private static final String TIMER_START_TIME = "TimerStartTime";
    private static final long COOLDOWN_PERIOD = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = findViewById(R.id.username);
        displayName = findViewById(R.id.displayName);
        university = findViewById(R.id.university);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        roleGroup = findViewById(R.id.role);
        registerButton = findViewById(R.id.updateButton);
        verifyEmail = findViewById(R.id.verifyEmail);
        universityText = findViewById(R.id.textUniversity);

        restoreData();

        // Handle role selection visibility
        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.roleTutor) {
                university.setVisibility(View.VISIBLE);
                universityText.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.roleStudent) {
                university.setVisibility(View.GONE);
                universityText.setVisibility(View.GONE);
            }
        });

        // Handle registration button click
        registerButton.setOnClickListener(v -> handleRegister());

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Optional: Close current activity to prevent going back to it
        });

        // Handle email validation
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

        // Check if email is verified
        Intent intent = getIntent();
        isVerified = intent.getBooleanExtra("verified", false);
        String previousEmail = intent.getStringExtra("email");

        if (previousEmail != null) {
            email.setText(previousEmail);
        }

        if (isVerified) {
            verifyEmail.setText("Verified");
            verifyEmail.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            verifyEmail.setEnabled(false);
            email.setEnabled(false);
            email.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            setUnderlineText(verifyEmail, "Verify your email");
            verifyEmail.setOnClickListener(v -> {
                if (verifyEmail.isEnabled()) {
                    sendVerifyEmail();
                }
            });
            checkCooldown();
        }
    }
    private void handleRegister() {
        String user = username.getText().toString().trim();
        String display = displayName.getText().toString().trim();
        String uni = university.getVisibility() == View.VISIBLE ? university.getText().toString().trim() : ""; // Handle university visibility
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        String role = (selectedRoleId == R.id.roleTutor) ? "Tutor" : "Student";

        if (user.isEmpty() || display.isEmpty() || mail.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (role.equals("Tutor") && uni.isEmpty()) {
            Toast.makeText(this, "University is required for tutors!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isVerified) {
            Toast.makeText(this, "Please verify your email before registering!", Toast.LENGTH_SHORT).show();
            return;
        }


        // Send data to the server
        new RegisterTask(user, display, uni, mail, pass, role).execute();
    }

    private class RegisterTask extends AsyncTask<Void, Void, String> {

        private String username, displayName, university, email, password, role;

        public RegisterTask(String username, String displayName, String university, String email, String password, String role) {
            this.username = username;
            this.displayName = displayName;
            this.university = university;
            this.email = email;
            this.password = password;
            this.role = role;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String baseUrl = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/";
                String endpoint = role.equals("Tutor") ? "tutor/users" : "student/users";
                URL url = new URL(baseUrl + endpoint);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Prepare the JSON data for registration
                JSONObject json = new JSONObject();
                json.put("USERNAME", username);
                json.put("DISPLAY_NAME", displayName);
                json.put("EMAIL", email);
                json.put("PASSWORD", hashPassword(password)); // Ensure password is not null

                if (role.equals("Tutor")) {
                    json.put("UNIVERSITY", university);
                }

                // Send the request
                OutputStream os = connection.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();

                // Check response status code
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return "Error during registration: " + responseCode;
                }

                // Read the response from the server
                String response = readInputStream(connection.getInputStream());

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }
        private String readInputStream(InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while (true) {
                try {
                    if (!((line = reader.readLine()) != null)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                result.append(line);
            }
            reader.close();
            return result.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                // Check if the result is empty or null before parsing
                if (result == null || result.isEmpty()) {
                    Toast.makeText(Register.this, "Empty response from server", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if the response appears to be a JSON string (basic check)
                if (!result.startsWith("{") || !result.endsWith("}")) {
                    Toast.makeText(Register.this, "Invalid response format", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject response = new JSONObject(result);
                String status = response.getString("status");
                String message = response.getString("message");

                if (status.equals("success")) {
                    // Show success dialog
                    new AlertDialog.Builder(Register.this)
                            .setTitle("Registration Successful")
                            .setMessage(message)
                            .setPositiveButton("OK", (dialog, which) -> {
                                if (role.equals("Student")) {
                                    // Navigate to subject_preference_student
                                    Intent intent = new Intent(Register.this, subject_preference_student.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                } else if (role.equals("Tutor")) {
                                    // Navigate to TutorDashboard (or similar activity)
                                    Intent intent = new Intent(Register.this, subject_preference_tutor.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }

                                // Close the Register activity
                                finish();
                            })
                            .setCancelable(false) // Prevent dialog dismissal without clicking "OK"
                            .show();
                } else {
                    // Show error message
                    Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // Handle parsing errors and log the full response
                Log.e("RegisterTask", "Error parsing JSON response: " + result);
                Toast.makeText(Register.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void sendVerifyEmail() {
        String emailAddress = email.getText().toString().trim();

        // Validate email format before proceeding
        if (!isValidEmail(emailAddress)) {
            email.setError("Please enter a valid email address.");
            Toast.makeText(this, "Invalid email address format. Please correct it.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with sending the email
        Random random = new Random();
        int code = random.nextInt(8999) + 1000; // Generate a random 4-digit code
        String url = "http://10.0.2.2:80/sendEmail.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("EmailResponse", response);
            Toast.makeText(Register.this, "Email sent successfully!", Toast.LENGTH_SHORT).show();

            // Start the cooldown timer only after a successful email send
            startCooldownTimer();

            // Navigate to OTPVerification
            Intent intent = new Intent(Register.this, OTPVerification.class);
            intent.putExtra("generatedCode", code);
            intent.putExtra("email", emailAddress);
            intent.putExtra("username", username.getText().toString().trim());
            intent.putExtra("displayName", displayName.getText().toString().trim());
            intent.putExtra("university", university.getText().toString().trim());
            intent.putExtra("password", password.getText().toString().trim());
            intent.putExtra("confirmPassword", confirmPassword.getText().toString().trim());
            intent.putExtra("role", roleGroup.getCheckedRadioButtonId() == R.id.roleTutor ? "Tutor" : "Student");
            startActivity(intent);
        }, error -> {
            String errorMessage = error.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "An unexpected error occurred. Please try again later.";
            }
            Toast.makeText(Register.this, errorMessage, Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailAddress);
                params.put("code", String.valueOf(code));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void startCooldownTimer() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        prefs.edit().putLong(TIMER_START_TIME, System.currentTimeMillis()).apply();
        checkCooldown();
    }

    private void checkCooldown() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        long startTime = prefs.getLong(TIMER_START_TIME, 0);

        if (isVerified) {
            verifyEmail.setText("Verified");
            verifyEmail.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            verifyEmail.setEnabled(false);
            return;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        long remainingTime = COOLDOWN_PERIOD - elapsedTime;

        if (remainingTime > 0) {
            verifyEmail.setEnabled(false);
            new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    verifyEmail.setText("Resend OTP in 0:" + (millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    verifyEmail.setEnabled(true);
                    setUnderlineText(verifyEmail, "Resend OTP");
                }
            }.start();
        } else {
            verifyEmail.setEnabled(true);
            setUnderlineText(verifyEmail, "Verify your email");
        }
    }

    private void setUnderlineText(TextView textView, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void restoreData() {
        Intent intent = getIntent();

        String usernameValue = intent.getStringExtra("username");
        String displayNameValue = intent.getStringExtra("displayName");
        String universityValue = intent.getStringExtra("university");
        String passwordValue = intent.getStringExtra("password");
        String confirmPasswordValue = intent.getStringExtra("confirmPassword");
        String roleValue = intent.getStringExtra("role");

        if (usernameValue != null) username.setText(usernameValue);
        if (displayNameValue != null) displayName.setText(displayNameValue);
        if (universityValue != null) university.setText(universityValue);
        if (passwordValue != null) password.setText(passwordValue);
        if (confirmPasswordValue != null) confirmPassword.setText(confirmPasswordValue);

        if (roleValue != null) {
            if (roleValue.equals("Tutor")) {
                ((RadioButton) findViewById(R.id.roleTutor)).setChecked(true);
                university.setVisibility(View.VISIBLE);
                universityText.setVisibility(View.VISIBLE);
            } else if (roleValue.equals("Student")) {
                ((RadioButton) findViewById(R.id.roleStudent)).setChecked(true);
                university.setVisibility(View.GONE);
                universityText.setVisibility(View.GONE);
            }
        }
    }
    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
