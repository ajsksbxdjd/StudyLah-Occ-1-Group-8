package com.example.studylah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowEventDetails extends AppCompatActivity {

    private Event currentEvent;

    private RatingBar ratingBar;
    private TextView TVRateDsc, TVLeaveComment;
    private EditText ETComment;
    private Button BtnSubmitBtn;
    private FloatingActionButton fabMarkAsJoined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_details);

        // Initialize Views
        TextView eventNameTitle = findViewById(R.id.TVEventNameTitle);

        ImageView eventImageDetails = findViewById(R.id.eventImageDetails);
        TextView eventNameDetails = findViewById(R.id.eventNameDetails);
        TextView eventTutorDetails = findViewById(R.id.eventTutorDetails);
//        TextView eventDateLocationDetails = findViewById(R.id.eventDateLocationDetails);

        TextView eventDate = findViewById(R.id.eventDate);
        TextView eventTime = findViewById(R.id.eventTime);
        TextView eventLocation = findViewById(R.id.eventLocation);
        TextView eventDescription = findViewById(R.id.eventDescription);
        TextView eventRegistrationLink = findViewById(R.id.eventRegistrationLink);
        TextView eventTutorEmail = findViewById(R.id.eventTutorEmail);
        TextView eventWebsiteLink = findViewById(R.id.eventWebsiteLink);

        Button exitButton = findViewById(R.id.exitButton);
        FloatingActionButton fabMarkAsJoined = findViewById(R.id.fabMarkAsJoined);

        // Initialize RatingBar, TextView, EditText, and Button
        ratingBar = findViewById(R.id.ratingBar);
        TVRateDsc = findViewById(R.id.TVRateDsc);
        TVLeaveComment = findViewById(R.id.TVLeaveComment);
        ETComment = findViewById(R.id.ETComment);
        BtnSubmitBtn = findViewById(R.id.BtnSubmitBtn);

        // Fetch Intent Data
        Intent intent = getIntent();
        String id = intent.getStringExtra("event_id");
        Log.d("Event ID from Intent", id);
        String name = intent.getStringExtra("event_name");
        String tutor = intent.getStringExtra("event_tutor");
//        String dateLocation = intent.getStringExtra("event_date_location");
        String base64Image = intent.getStringExtra("event_base64_image");

        String date = intent.getStringExtra("event_date");
        String time = intent.getStringExtra("event_time");
        String location = intent.getStringExtra("event_location");
        String description = intent.getStringExtra("event_description");
        String registrationLink = intent.getStringExtra("event_registration_link");
        String tutorEmail = intent.getStringExtra("event_tutor_email");
        String websiteLink = intent.getStringExtra("event_website_link");

        boolean isJoinedBoolean = intent.getBooleanExtra("is_joined", false);
        String isJoined = String.valueOf(isJoinedBoolean); // Converts boolean to "true" or "false"


        if (isJoined.equals("true")) {
            // Make RatingBar, TextView, EditText, and Button visible
            ratingBar.setVisibility(View.VISIBLE);
            TVRateDsc.setVisibility(View.VISIBLE);
            TVLeaveComment.setVisibility(View.VISIBLE);
            ETComment.setVisibility(View.VISIBLE);
            BtnSubmitBtn.setVisibility(View.VISIBLE);
            // Make the "Mark as Joined" button invisible
            fabMarkAsJoined.setVisibility(View.GONE);
        }

        // Set Data to Views
        eventNameTitle.setText(name);

        eventNameDetails.setText(name);
        eventTutorDetails.setText(tutor);
//        eventDateLocationDetails.setText(dateLocation);
        eventDate.setText(date);
        eventTime.setText(time);
        eventLocation.setText(location);
        eventDescription.setText(description);
        eventRegistrationLink.setText(registrationLink);
        eventTutorEmail.setText(tutorEmail);
        eventWebsiteLink.setText(websiteLink);

        // Decode the base64 image string to a Bitmap
        if (base64Image != null) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            eventImageDetails.setImageBitmap(decodedByte);
        }

        // Exit Button Click Listener
        exitButton.setOnClickListener(v -> finish());

        // Mark as Joined Button Click Listener
        fabMarkAsJoined.setOnClickListener(v -> {
            Log.d("Event ID in fabmarkasjoined listener", "ID: " + id);
            boolean Joined = true;
            markEventAsJoined(id, name, tutor, date, time, location, description, registrationLink, tutorEmail, websiteLink, base64Image, Joined);
        });

        BtnSubmitBtn.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = ETComment.getText().toString();
            Log.d("Rating", String.valueOf(rating));
            Log.d("Comment", comment);
//            Toast.makeText(this, "Rating: " + rating + ", Comment: " + comment, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Your feedback has been recorded.", Toast.LENGTH_SHORT).show();
            // Redirect to FeedbackSubmittedActivity
            Intent feedbackIntent = new Intent(getApplicationContext(), EventFeedbackSubmitted.class);
            startActivity(feedbackIntent);
        });
    }

    private void markEventAsJoined(String id, String name, String tutor, String date, String time, String location, String description, String registrationLink, String tutorEmail, String websiteLink, String base64Image, boolean isJoined) {
        SharedPreferences sharedPreferences = getSharedPreferences("joined_events", Context.MODE_PRIVATE);
        Log.d("Event ID in markEventAsJoined initially", "ID: " + id);
//        try {
//            // Retrieve existing joined events
//            String joinedEventsJson = sharedPreferences.getString("joined_events_list", "[]");
//            JSONArray joinedEventsArray = new JSONArray(joinedEventsJson);
//
//            // save all the joined event id saved in jsonarray into a list
//            List<String> joinedEventIds = new ArrayList<>();
//            for (int i = 0; i < joinedEventsArray.length(); i++) {
//                JSONObject eventObject = joinedEventsArray.getJSONObject(i);
//                joinedEventIds.add(eventObject.getString("event_id"));
//            }
//
//            // Check if the event is already joined
//            if (joinedEventIds.contains(id)) {
//                Toast.makeText(this, "Event is already joined", Toast.LENGTH_SHORT).show();
//            } else {
//                // Create a new event JSON object
//                JSONObject eventObject = new JSONObject();
//                eventObject.put("event_id", id);
//                eventObject.put("event_name", name);
//                eventObject.put("event_tutor", tutor);
//                eventObject.put("event_date", date);
//                eventObject.put("event_time", time);
//                eventObject.put("event_location", location);
//                eventObject.put("event_description", description);
//                eventObject.put("event_registration_link", registrationLink);
//                eventObject.put("event_tutor_email", tutorEmail);
//                eventObject.put("event_website_link", websiteLink);
//                eventObject.put("event_base64_image", base64Image);
//
//                // Add the new event to the list
//                joinedEventsArray.put(eventObject);
//
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                // Save the updated list back to SharedPreferences
//                editor.putString("joined_events_list", joinedEventsArray.toString());
//                editor.apply();
//
//                Toast.makeText(this, "Event marked as joined", Toast.LENGTH_SHORT).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("ShowEventDetails", "JSON error in markEventAsJoined: " + e.getMessage());
//            Toast.makeText(this, "Error processing event data", Toast.LENGTH_SHORT).show();
//        }

        try {
            // Retrieve existing joined events
            Log.d("markEventAsJoined", "Fetching joined_events_list...");
            String joinedEventsJson = sharedPreferences.getString("joined_events_list", "[]");
            Log.d("markEventAsJoined", "Joined events JSON: " + joinedEventsJson);
            JSONArray joinedEventsArray = new JSONArray(joinedEventsJson);

            // Save all the joined event IDs into a list
            Log.d("markEventAsJoined", "Parsing joined events array...");
            List<String> joinedEventIds = new ArrayList<>();
            for (int i = 0; i < joinedEventsArray.length(); i++) {
                JSONObject eventObject = joinedEventsArray.getJSONObject(i);
                Log.d("markEventAsJoined", "Event object: " + eventObject.toString());
                joinedEventIds.add(eventObject.getString("event_id")); // This might throw an error
            }

            // Check if the event is already joined
            Log.d("markEventAsJoined", "Checking if event ID is in joinedEventIds...");
            if (joinedEventIds.contains(id)) {
                Toast.makeText(this, "Event is already joined", Toast.LENGTH_SHORT).show();

            } else {
                // Create a new event JSON object
                Log.d("markEventAsJoined", "Creating new event object...");
                JSONObject eventObject = new JSONObject();
                eventObject.put("event_id", id);
                eventObject.put("event_name", name);
                eventObject.put("event_tutor", tutor);
                eventObject.put("event_date", date);
                eventObject.put("event_time", time);
                eventObject.put("event_location", location);
                eventObject.put("event_description", description);
                eventObject.put("event_registration_link", registrationLink);
                eventObject.put("event_tutor_email", tutorEmail);
                eventObject.put("event_website_link", websiteLink);
                eventObject.put("event_base64_image", base64Image);
                eventObject.put("is_joined", isJoined);

                Log.d("markEventAsJoined", "Adding new event to array...");
                // Add the new event to the list
                joinedEventsArray.put(eventObject);

                // Save the updated list back to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Log.d("markEventAsJoined", "Saving updated list to SharedPreferences...");
                editor.putString("joined_events_list", joinedEventsArray.toString());
                editor.apply();

                Toast.makeText(this, "Event marked as joined", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("markEventAsJoined", "JSON error: " + e.getMessage());
            Toast.makeText(this, "Error processing event data", Toast.LENGTH_SHORT).show();
        }

    }
}