package com.example.studylah;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPVerification extends AppCompatActivity {
    private EditText num1, num2, num3, num4;
    private Button verifyButton, cancelButton;
    private TextView resendButton, emailText;
    private int generatedCode;
    private String emailAddress;
    private static final String SHARED_PREFS = "CooldownPrefs";
    private static final String TIMER_START_TIME = "TimerStartTime";
    private static final long COOLDOWN_PERIOD = 30000; // 30 seconds cooldown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        // Initialize views
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        verifyButton = findViewById(R.id.verifyButton);
        resendButton = findViewById(R.id.resendButton);
        cancelButton=findViewById(R.id.cancelButton);
        emailText = findViewById(R.id.emailText); // TextView to show confirmation message

        // Retrieve extras from the Intent
        generatedCode = getIntent().getIntExtra("generatedCode", -1);
        emailAddress = getIntent().getStringExtra("email");

        // Display initial email message
        emailText.setText("We have sent a verification code to " + emailAddress);

        // Underline the initial "Resend OTP" text
        setUnderlineText(resendButton, "Resend OTP");

        // Set up TextWatchers for OTP input fields
        addTextWatcher(num1, num2);
        addTextWatcher(num2, num3);
        addTextWatcher(num3, num4);

        // Set listeners for buttons
        verifyButton.setOnClickListener(v -> verifyCode());
        resendButton.setOnClickListener(v -> {
            if (resendButton.isEnabled()) {
                resendOTP();
                startCooldownTimer();
            }
        });

        cancelButton.setOnClickListener(v -> handleCancel());

        // Check cooldown status on activity start
        checkCooldown();
    }

    private void resendOTP() {
        // Generate a new OTP
        Random random = new Random();
        generatedCode = random.nextInt(8999) + 1000;

        // Simulate sending OTP to email
        String url = "http://10.0.2.2:80/sendEmail.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            // Show success message and update emailText
            Toast.makeText(this, "OTP resent successfully!", Toast.LENGTH_SHORT).show();
            emailText.setText("We have sent a verification code to " + emailAddress);
        }, error -> {
            // Show error message if email sending fails
            String errorMessage = error.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "An unexpected error occurred. Please try again later.";
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Send email and OTP code to the server
                Map<String, String> params = new HashMap<>();
                params.put("email", emailAddress); // Email address
                params.put("code", String.valueOf(generatedCode)); // Generated OTP
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void verifyCode() {
        String enteredCode = getCombinedNumber();

        if (enteredCode.isEmpty() || enteredCode.length() < 4) {
            Toast.makeText(this, "Please enter all 4 digits.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int enteredCodeInt = Integer.parseInt(enteredCode);

            if (enteredCodeInt == generatedCode) {
                Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OTPVerification.this, Register.class);

                // Pass data back with verification flag
                passDataToIntent(intent, true);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Incorrect code. Please try again.", Toast.LENGTH_SHORT).show();
                clearInputFields();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid code format. Please enter a valid 4-digit code.", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleCancel() {
        Intent intent = new Intent(OTPVerification.this, Register.class);

        // Pass data back without verification
        passDataToIntent(intent, false);
        startActivity(intent);
        finish();
    }

    private void passDataToIntent(Intent intent, boolean isVerified) {
        intent.putExtra("email", emailAddress);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        intent.putExtra("displayName", getIntent().getStringExtra("displayName"));
        intent.putExtra("university", getIntent().getStringExtra("university"));
        intent.putExtra("password", getIntent().getStringExtra("password"));
        intent.putExtra("confirmPassword", getIntent().getStringExtra("confirmPassword"));
        intent.putExtra("role", getIntent().getStringExtra("role"));
        intent.putExtra("verified", isVerified);
    }

    private void startCooldownTimer() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        prefs.edit().putLong(TIMER_START_TIME, System.currentTimeMillis()).apply();
        checkCooldown();
    }

    private void checkCooldown() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        long startTime = prefs.getLong(TIMER_START_TIME, 0);

        long elapsedTime = System.currentTimeMillis() - startTime;
        long remainingTime = COOLDOWN_PERIOD - elapsedTime;

        if (remainingTime > 0) {
            resendButton.setEnabled(false);

            new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long secondsRemaining = millisUntilFinished / 1000;
                    resendButton.setText("Resend OTP in 0:" + (secondsRemaining < 10 ? "0" : "") + secondsRemaining);
                }

                @Override
                public void onFinish() {
                    resendButton.setEnabled(true);
                    setUnderlineText(resendButton, "Resend OTP");
                }
            }.start();
        } else {
            resendButton.setEnabled(true);
            setUnderlineText(resendButton, "Resend OTP");
        }
    }

    private void setUnderlineText(TextView textView, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    private String getCombinedNumber() {
        return num1.getText().toString().trim() +
                num2.getText().toString().trim() +
                num3.getText().toString().trim() +
                num4.getText().toString().trim();
    }

    private void clearInputFields() {
        num1.setText("");
        num2.setText("");
        num3.setText("");
        num4.setText("");
        num1.requestFocus();
    }

    private void addTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}