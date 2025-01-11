package com.example.studylah;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.Iterator;
import java.util.Map;

public class Tutor_Desc_Activity extends AppCompatActivity {
    private TextView displayNameTextView, emailTextView, registrationDateTextView, universityTextView, subjectsTextView, descriptionTextView;
    private ImageView profilePictureImageView;
    private String gcLink; // Store the tutor's Google Calendar link

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_desc);

        // Initialize views
        displayNameTextView = findViewById(R.id.tutor_name);
        emailTextView = findViewById(R.id.tutor_email);
        registrationDateTextView = findViewById(R.id.tutor_registration_date);
        universityTextView = findViewById(R.id.tutor_university);
        subjectsTextView = findViewById(R.id.tutor_subjects);
        descriptionTextView = findViewById(R.id.tutor_bio);
        profilePictureImageView = findViewById(R.id.tutor_propic);

        // Retrieve username from intent
        String username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "No username provided!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch tutor details based on username
        fetchTutorDetails(username);

        // Set up the "Book Now" button
        Button bookNowButton = findViewById(R.id.BtnBookNow);
        bookNowButton.setOnClickListener(v -> {
            if (gcLink != null && !gcLink.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gcLink)); // Open the Google Calendar link
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No app found to handle the link", Toast.LENGTH_SHORT).show();
                    Log.e("BOOK_NOW", "Error opening link", e);
                }
            } else {
                Toast.makeText(this, "Google Calendar link not available", Toast.LENGTH_SHORT).show();
            }
        });
        // Set up the back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Tutor_Desc_Activity.this, Mentoring_Tutors_List.class);
            startActivity(intent);
            finish(); // Optional: Close current activity to prevent going back to it
        });
    }

    private void fetchTutorDetails(String username) {
        // Example: Use a backend API or local list to fetch details
        for (Tutor_List_Data tutor : TutorDataSingleton.getInstance().getTutorList()) { // Singleton contains tutorList
            if (tutor.getUsername().equals(username)) {
                // Populate UI with tutor details
                displayNameTextView.setText(tutor.getDisplayName());
                emailTextView.setText(tutor.getEmail());
                registrationDateTextView.setText(tutor.getRegistrationDate());
                universityTextView.setText(tutor.getUniversity());
                subjectsTextView.setText(getSubjectsString(tutor.getSubjects()));
                descriptionTextView.setText(tutor.getDescription());

                // Decode and set profile picture
                String profilePictureBase64 = tutor.getProfilePicture();
                if (profilePictureBase64 != null && !profilePictureBase64.isEmpty()) {
                    byte[] decodedString = Base64.decode(profilePictureBase64, Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    profilePictureImageView.setImageBitmap(decodedBitmap);
                } else {
                    profilePictureImageView.setImageResource(R.drawable.mentoring_icon_student);
                }

                // Save the tutor's Google Calendar link
                gcLink = tutor.getGcLink();
                return;
            }
        }

        // If no tutor was found with the given username
        Toast.makeText(this, "Tutor details not found!", Toast.LENGTH_SHORT).show();
    }

    // Helper method to convert subjects map to a displayable string
    private String getSubjectsString(Map<String, Integer> subjectsMap) {
        StringBuilder subjectsString = new StringBuilder();
        for (Map.Entry<String, Integer> entry : subjectsMap.entrySet()) {
            if (entry.getValue() > 0) {
                if (subjectsString.length() > 0) {
                    subjectsString.append(", ");
                }
                subjectsString.append(entry.getKey());
            }
        }
        return subjectsString.toString();
    }
}
