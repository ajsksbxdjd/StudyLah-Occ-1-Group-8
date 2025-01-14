package com.example.studylah;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class Tutor_Schedules extends AppCompatActivity {

    private String username;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_schedules);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mentoring Schedules");
        }

        // Drawer setup
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
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
        username = getIntent().getStringExtra("username");
        Toast.makeText(this, "Username retrieved successfully.", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(this, "A valid GC Link is entered.", Toast.LENGTH_SHORT).show();
                submitGcLink(gcLink);
            }
        });

        setupNavigation();
    }

    private void setupNavigation() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.tool_bar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Intent intent;

            if (id == R.id.side_nav_schedules) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            } else if (id == R.id.side_nav_home) {
                intent = new Intent(this, TutorHome.class);
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
        bottomNavigationView.setSelectedItemId(R.id.nav_mentoring);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;

            if (id == R.id.nav_home) {
                intent = new Intent(this, TutorHome.class);
                startActivity(intent);

            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(this, Market_MainActivityTutor.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else if (id == R.id.nav_events) {
                intent = new Intent(this, TutorHome.class);
                startActivity(intent);
            }

            return true;
        });
    }

    private void submitGcLink(String gcLink) {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users";

            try {
                JSONObject payload = new JSONObject();
                payload.put("current_username", username);

                payload.put("p_gc_link", gcLink);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload,
                        response -> {
                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");

                                if ("success".equals(status)) {
                                    Toast.makeText(this, "Link saved successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to save link: " + message, Toast.LENGTH_SHORT).show();
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

