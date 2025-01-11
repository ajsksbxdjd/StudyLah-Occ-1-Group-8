package com.example.studylah;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginTutor extends AppCompatActivity {
    private EditText email, password;
    private Button loginButton;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);

        loginButton.setOnClickListener(v -> handleLogin());
        setUnderlineText(forgotPassword, "Forgot Password?");
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginTutor.this, forgotPasswordTutor.class);
                startActivity(intent);
            }
        });
    }
    private void setUnderlineText(TextView textView, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }
    private void handleLogin() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(LoginTutor.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(passwordInput);

        // Send login request to backend (login task)
        new LoginTutor.LoginTask(emailInput, hashedPassword).execute();
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
    private class LoginTask extends AsyncTask<Void, Void, String> {

        private String email, password;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // URL with query parameters (email and hashed password)
                String baseUrl = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users";
                String urlString = baseUrl + "?email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString(); // Return the response from backend
                } else {
                    return "Error: Unable to login, try again!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Check if the result is empty or null before parsing
                if (result == null || result.isEmpty()) {
                    Toast.makeText(LoginTutor.this, "Empty response from server", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d("Login Response", result); // Log the raw response for debugging

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(result);

                // Get the "items" array from the response
                JSONArray items = responseJson.getJSONArray("items");

                // Check if the array has any elements (users)
                if (items.length() > 0) {
                    boolean userFound = false;

                    // Loop through the "items" array to find a matching email and password
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject user = items.getJSONObject(i);
                        String storedEmail = user.getString("email");
                        String storedPassword = user.getString("password");

                        // Check if the input email and password match the stored ones
                        if (storedEmail.equals(email) && storedPassword.equals(password)) {
                            userFound = true;
                            Toast.makeText(LoginTutor.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            String username = user.getString("username");
                            String displayName = user.getString("display_name");
                            String email = user.getString("email");
                            String university=user.getString("university");
                            String tutor_description=user.getString("tutor_description");
                            int bm=user.getInt("bm");
                            int bi=user.getInt("bi");
                            int math=user.getInt("math");
                            int add_math=user.getInt("add_math");
                            int physics=user.getInt("physics");
                            int chemistry=user.getInt("chemistry");
                            int biology=user.getInt("biology");
                            int sejarah=user.getInt("sejarah");
                            int moral=user.getInt("moral");
                            int islam=user.getInt("islam");
                            int economy=user.getInt("economy");
                            int accounting=user.getInt("accounting");

                            // Pass the user data to HomepageStudent via intent
                            Intent intent = new Intent(LoginTutor.this, homepageTutor.class);
                            intent.putExtra("username", username);
                            intent.putExtra("display_name", displayName);
                            intent.putExtra("email", email);
                            intent.putExtra("university", university);
                            intent.putExtra("tutor_description", tutor_description);
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
                            finish();
                            break;
                        }
                    }

                    if (!userFound) {
                        Toast.makeText(LoginTutor.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginTutor.this, "No user found with this email", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("JSON Parsing Error", e.getMessage(), e);
                Toast.makeText(LoginTutor.this, "Error parsing response", Toast.LENGTH_LONG).show();
            }
        }
    }
}