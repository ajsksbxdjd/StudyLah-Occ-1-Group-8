package com.example.studylah;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventActivity extends AppCompatActivity {

//    private LinearLayout eventContainer;
//    private TextView noEventText;

    //    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;
//    private List<Event> eventList = new ArrayList<>();

    private Button btnUpcoming;
    private Button btnJoined;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Log.e("AlarmManager in EventActivity", "AlarmManager: " + alarmManager);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        // Check Alarm permission for Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle("Alarm Permission Required")
                        .setMessage("This app needs permission to schedule exact alarms. Please grant this permission.")
                        .setPositiveButton("Grant", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(this, "Alarm permission not granted", Toast.LENGTH_SHORT).show();
                        })
                        .setCancelable(false)
                        .show();
            }
        }

        // Check Notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission Required")
                        .setMessage("This app needs permission to send notifications. Please grant this permission.")
                        .setPositiveButton("Grant", (dialog, which) -> {
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show();
                        })
                        .setCancelable(false)
                        .show();
            }
        }




//        eventContainer = findViewById(R.id.eventContainer);
//        noEventText = findViewById(R.id.noEventText);

        // Step 1: Initialize Views
//        eventRecyclerView = findViewById(R.id.eventRecyclerView);
//        noEventText = findViewById(R.id.noEventText);

        FloatingActionButton fabAddEvent = findViewById(R.id.fabAddEvent);

        // Step 2: Setup RecyclerView
//        eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        eventAdapter = new EventAdapter(eventList);
//        eventRecyclerView.setAdapter(eventAdapter);

//        eventAdapter = new EventAdapter(eventList, EventActivity.this);
//        eventRecyclerView.setAdapter(eventAdapter);

        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, EventDetails.class);
                startActivity(intent);
            }
        });

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnJoined = findViewById(R.id.btnJoined);

        // Set the "upcoming" button to be bold by default
        btnUpcoming.setTypeface(null, Typeface.BOLD);
        btnJoined.setTypeface(null, Typeface.NORMAL);

        btnUpcoming.setOnClickListener(v -> {
            EventUpcoming upcoming = new EventUpcoming();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.FCVEvent, upcoming);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        btnJoined.setOnClickListener(v -> {
            EventJoined joined = new EventJoined();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.FCVEvent, joined);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // Example button click listener to clear joined events
        FloatingActionButton clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(v -> {
            clearJoinedEvents();
            Toast.makeText(EventActivity.this, "Joined events cleared", Toast.LENGTH_SHORT).show();
        });

        if (findViewById(R.id.FCVEvent) != null) {
            if (savedInstanceState != null) {
                return;
            }

//            upcoming referenceBookFragment = new upcoming();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.add(R.id.FCVEvent, referenceBookFragment);
//            fragmentTransaction.commit();

            EventUpcoming upcomingFragment = EventUpcoming.newInstance();
            replaceFragment(upcomingFragment);
        }


        getSupportFragmentManager().addOnBackStackChangedListener(this::updateButtonStyles);
        updateButtonStyles();

        // Fetch data from the API
//        new FetchEventsTask().execute("https://apex.oracle.com/pls/apex/wia2001_database_oracle/event/users");
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FCVEvent, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void showNotification(Context context, String taskName, String dueDate, String dueTime) {
        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return; // Don't show notification if permission is not granted
            }
        }
    }


//    private class FetchEventsTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            String response = null;
//            try {
//                URL url = new URL(urls[0]);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                InputStream inputStream = connection.getInputStream();
//                Scanner scanner = new Scanner(inputStream);
//                scanner.useDelimiter("\\A");
//
//                if (scanner.hasNext()) {
//                    response = scanner.next();
//                }
//                scanner.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return response;
//        }
//
////        @Override
////        protected void onPostExecute(String response) {
////            if (response == null || response.isEmpty()) {
////                noEventText.setVisibility(View.VISIBLE);
//////                eventContainer.setVisibility(View.GONE);
////                eventRecyclerView.setVisibility(View.GONE);
////                return;
////            }
////
////            try {
////                JSONObject jsonResponse = new JSONObject(response);
////                JSONArray items = jsonResponse.getJSONArray("items");
////
////                if (items.length() == 0) {
////                    noEventText.setVisibility(View.VISIBLE);
//////                    eventContainer.setVisibility(View.GONE);
////                    eventRecyclerView.setVisibility(View.GONE);
////                } else {
////                    noEventText.setVisibility(View.GONE);
//////                    eventContainer.setVisibility(View.VISIBLE);
////                    eventRecyclerView.setVisibility(View.VISIBLE);
////
////                    // Clear the old list and add new items
////                    eventList.clear();
////
////                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
////                    String currentDateStr = sdf.format(new Date());
////
////                    for (int i = 0; i < items.length(); i++) {
//////                        JSONObject event = items.getJSONObject(i);
//////
//////                        View eventView = getLayoutInflater().inflate(R.layout.event_item, eventContainer, false);
//////
//////                        TextView eventName = eventView.findViewById(R.id.eventName);
//////                        TextView eventTutor = eventView.findViewById(R.id.eventTutor);
//////                        TextView eventDateLocation = eventView.findViewById(R.id.eventDateLocation);
//////                        ImageView eventImage = eventView.findViewById(R.id.eventImage);
//////
//////                        eventName.setText(event.getString("event_name"));
//////
//////                        // Append "With Tutor " before the tutor's name
//////                        String tutorName = "With Tutor " + event.getString("tutor_display_name");
//////                        eventTutor.setText(tutorName);
//////
//////                        // Trim the date to show only dd-mm-yyyy
//////                        String fullDate = event.getString("event_date");
//////                        String[] dateParts = fullDate.split("T")[0].split("-");
//////                        String formattedDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0] + ", " + event.getString("event_location");
//////                        eventDateLocation.setText(formattedDate);
//////
//////                        // Decode Base64 image and set it to ImageView
//////                        String base64Image = event.getString("event_picture");
//////                        eventImage.setImageBitmap(decodeBase64Image(base64Image));
//////
//////                        eventContainer.addView(eventView);
////
////                        JSONObject event = items.getJSONObject(i);
////
////                        // Populate event object
////                        String eventId = event.getString("event_id");
////                        String eventName = event.getString("event_name");
////                        String tutorName = "With Tutor " + event.getString("tutor_display_name");
////
////                        String eventTime = event.getString("event_time");
////                        String eventDescription = event.getString("event_description");
////                        String eventWebsiteLink = event.getString("event_website_link");
////                        String registrationLink = event.getString("registration_link");
////                        String tutorEmail = event.getString("tutor_email");
////
////                        String fullDate = event.getString("event_date");
////                        String[] dateParts = fullDate.split("T")[0].split("-");
////                        String formattedDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
////
////                        String location = event.getString("event_location");
////                        String base64Image = event.getString("event_picture");
////                        String publishDate = event.getString("publish_date");
////
////                        // Check if location is a URL
////                        if (location.startsWith("http://") || location.startsWith("https://") || location.startsWith("www.")) {
////                            location = "Online";
////                        }
////
////                        // Decode Base64 image
////                        Bitmap image = decodeBase64Image(base64Image);
////
//////                        // Create an Event object and add it to the list
//////                        eventList.add(new Event(eventName, tutorName, formattedDate + ", " + location, base64Image));
////
////                        if (publishDate.compareTo(currentDateStr) <= 0) {
////                            eventList.add(new Event(eventName, tutorName, formattedDate + ", " + location, base64Image, fullDate, eventTime, location, eventDescription, eventWebsiteLink, registrationLink, tutorEmail));
////                        }
////                    }
////
////                    // Notify the adapter of data changes
////                    eventAdapter.notifyDataSetChanged();
////                }
////
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
//    }

//    private Bitmap decodeBase64Image(String base64Image) {
//        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//    }

    private void updateButtonStyles() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.FCVEvent);
            btnUpcoming.setTypeface(null, Typeface.BOLD);
            btnJoined.setTypeface(null, Typeface.NORMAL);
            if (currentFragment instanceof EventJoined) {
                btnUpcoming.setTypeface(null, Typeface.NORMAL);
                btnJoined.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    // Method to clear all joined events
    private void clearJoinedEvents() {
        SharedPreferences sharedPreferences = EventActivity.this.getSharedPreferences("joined_events", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data in SharedPreferences
        editor.apply(); // Apply the changes
    }

}
