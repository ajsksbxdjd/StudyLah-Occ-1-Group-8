package com.example.studylah;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tutor_Schedules extends AppCompatActivity {


    private String username, gcLink;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutor_schedules);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutors Schedules");
        }

        // Drawer and Navigation setup
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // VideoView setup
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_google_calendar);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start(); // Start playing the video
        requestQueue = Volley.newRequestQueue(this);

        // Retrieve username from Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.d("Tutor_Schedules", "Current Username: " + username);

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error retrieving username. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Submit button setup
        Button submitButton = findViewById(R.id.BtnSubmit);
        EditText etLink = findViewById(R.id.ETLink); // The input field for GC Link

        submitButton.setOnClickListener(v -> {
            String gcLink = etLink.getText().toString().trim();
            if (gcLink.isEmpty()) {
                Toast.makeText(this, "Please enter a valid GC Link.", Toast.LENGTH_SHORT).show();
            } else {
                // Call AsyncTask to update the GC Link
                submitGcLink(gcLink);
            }
        });
        setupNavigation();
        new ItemDetailsTask().execute();
    }
    private void setupNavigation() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Side Navigation Item Click Handling
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Intent intent;

            if (id == R.id.side_nav_schedules) {
                intent = new Intent(this, Tutor_Schedules.class);
                startActivity(intent);

            } else if (id == R.id.side_nav_home) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);

            } else if (id == R.id.side_nav_events) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);

            } else if (id == R.id.side_nav_marketplace) {
                intent = new Intent(this, Market_MainActivityTutor.class);
                startActivity(intent);

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation Item Click Handling
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set selected item to 'Mentoring'
        bottomNavigationView.setSelectedItemId(R.id.nav_mentoring);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;

            if (id == R.id.nav_home) {
                intent = new Intent(this, TutorHome.class);
                startActivity(intent);

            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(this, Market_MainActivityTutor.class);
                startActivity(intent);
            } else if (id == R.id.nav_events) {
                intent = new Intent(this, TutorHome.class);
                startActivity(intent);
            }

            return true;
        });
    }

    private class ItemDetailsTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

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

        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray items = jsonResponse.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        if (username.equals(item.getString("username"))) {
                            submitGcLink(gcLink);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void submitGcLink(String xxx) {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users"; // Update URL if needed

        try {
            // Prepare JSON payload
            JSONObject payload = new JSONObject();
            payload.put("username", username);
            payload.put("p_gc_link", gcLink);

            // Create request
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if ("success".equals(status)) {
                                Toast.makeText(this, "GC Link updated successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, MainActivity.class); // Replace with your desired activity
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to update GC Link: " + message, Toast.LENGTH_SHORT).show();
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