package com.example.studylah;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class dataTest extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_test);

        resultTextView = findViewById(R.id.resultTextView);

        new APICallTask().execute();
    }

    private class APICallTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {

                URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                // Parse the response and display it
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray items = jsonResponse.getJSONArray("items");
                    if (items.length() > 0) {
                        // Loop through each item and get all column data
                        StringBuilder displayText = new StringBuilder();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);

                            // Retrieve and log each column's data
                            String username = item.getString("username");
                            String email = item.getString("email");
                            String password = item.getString("password");
                            String registrationDate = item.getString("registration_date");
                            String university = item.getString("university");
                            String displayName = item.getString("display_name");
                            String profilePicture = item.getString("profile_picture");
                            String tutorDescription = item.getString("tutor_description");
                            String bm = item.getString("bm");
                            String bi = item.getString("bi");
                            String math = item.getString("math");
                            String addMath = item.getString("add_math");
                            String physics = item.getString("physics");
                            String chemistry = item.getString("chemistry");
                            String biology = item.getString("biology");
                            String sejarah = item.getString("sejarah");
                            String moral = item.getString("moral");
                            String islam = item.getString("islam");
                            String gcLink = item.getString("gc_link");

                            // Append the values to displayText
                            displayText.append("Username: ").append(username).append("\n")
                                    .append("Email: ").append(email).append("\n")
                                    .append("Password: ").append(password).append("\n")
                                    .append("Registration Date: ").append(registrationDate).append("\n")
                                    .append("University: ").append(university).append("\n")
                                    .append("Display Name: ").append(displayName).append("\n")
                                    .append("Profile Picture: ").append(profilePicture).append("\n")
                                    .append("Tutor Description: ").append(tutorDescription).append("\n\n")
                                    .append("BM: ").append(bm).append("\n")
                                    .append("BI: ").append(bi).append("\n")
                                    .append("Math: ").append(math).append("\n")
                                    .append("Add Math: ").append(addMath).append("\n")
                                    .append("Physics: ").append(physics).append("\n")
                                    .append("Chemistry: ").append(chemistry).append("\n")
                                    .append("Biology: ").append(biology).append("\n")
                                    .append("Sejarah: ").append(sejarah).append("\n")
                                    .append("Moral: ").append(moral).append("\n")
                                    .append("Islam: ").append(islam).append("\n")
                                    .append("GC Link: ").append(gcLink).append("\n")
                            ;
                        }

                        // Set the result text
                        resultTextView.setText("Success! Data:\n\n" + displayText.toString());
                    } else {
                        resultTextView.setText("No data found.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resultTextView.setText("Error parsing response.");
                }
            } else {
                resultTextView.setText("Failed to connect or retrieve data.");
            }
        }
    }
}