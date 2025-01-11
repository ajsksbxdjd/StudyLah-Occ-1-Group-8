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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tutor_Schedules extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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



        // Submit button setup
        Button submitButton = findViewById(R.id.BtnSubmit);
        EditText etLink = findViewById(R.id.ETLink); // The input field for GC Link

        submitButton.setOnClickListener(view -> {
            String gcLink = etLink.getText().toString().trim();
            String username = "test_user"; // Hardcoded username for testing

            if (gcLink.isEmpty()) {
                Toast.makeText(this, "Please enter your Google Calendar link.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform the HTTP POST operation
            new SubmitGCLinkTask(this, username, gcLink).execute();
        });
        setupNavigation();
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

            if (id == R.id.side_nav_find_tutor) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.side_nav_home) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.side_nav_events) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.side_nav_flashcards) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
                finish();
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
                finish();
            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(this, TutorHome.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_events) {
                intent = new Intent(this, TutorHome.class);
                startActivity(intent);
                finish();
            }

            return true;
        });
    }

        private static class SubmitGCLinkTask extends AsyncTask<Void, Void, String> {
        private final Context context;
        private final String username;
        private final String gcLink;

        SubmitGCLinkTask(Context context, String username, String gcLink) {
            this.context = context;
            this.username = username;
            this.gcLink = gcLink;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users"); // Replace with your endpoint
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", username);
                jsonBody.put("gc_link", gcLink);

                // Write the JSON body to the output stream
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(jsonBody.toString().getBytes());
                    os.flush();
                }

                // Read the response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        return response.toString();
                    }
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }
}