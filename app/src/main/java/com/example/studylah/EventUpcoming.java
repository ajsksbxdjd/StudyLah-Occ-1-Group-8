package com.example.studylah;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventUpcoming#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventUpcoming extends Fragment {

    //    private LinearLayout eventContainer;
    private TextView noEventText;

    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();

    public EventUpcoming() {
        // Required empty public constructor
    }

    public static EventUpcoming newInstance() {
        return new EventUpcoming();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_upcoming, container, false);

        // Step 1: Initialize Views
        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        noEventText = view.findViewById(R.id.noEventText);

        // Step 2: Setup RecyclerView
        eventRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        eventAdapter = new EventAdapter(eventList, getContext());
        eventRecyclerView.setAdapter(eventAdapter);

        // Fetch data from the API
        new FetchEventsTask().execute("https://apex.oracle.com/pls/apex/wia2001_database_oracle/event/users");
        EventNotificationHelper.createNotificationChannel(getContext());

        return view;
    }

    private void showNotification(String title, String message) {
        EventNotificationHelper.sendNotification(getContext(), title, message);
    }

    private void loadEvents() {
        List<Event> filteredEvents = new ArrayList<>();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy'T'hh:mm a'Z'", Locale.getDefault()); // Adjusted to parse your format.
        Date currentDate = new Date();

        for (Event event : eventList) {
            try {
                // Construct the full date-time string and parse it
                String eventDateTimeString = event.getDate() + "T" + event.getTime() + "Z";
                Date eventDateTime = dateTimeFormat.parse(eventDateTimeString);

                // Filter events occurring after the current date and time
                if (eventDateTime != null && eventDateTime.after(currentDate)) {
                    filteredEvents.add(event);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Update RecyclerView with filtered events
        eventAdapter = new EventAdapter(filteredEvents, getContext());
        eventRecyclerView.setAdapter(eventAdapter);
    }

    // Clear all existing notifications
    private void clearAllScheduledNotifications() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            for (Event event : eventList) {
                Intent intent = new Intent(getContext(), EventNotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getContext(),
                        event.getEventId().hashCode(), // Unique ID for each event
                        intent,
                        PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
                );
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
    }

    // Update and schedule notifications for upcoming events
    private void updateAndScheduleNotifications() {
        clearAllScheduledNotifications(); // Step 1

        // Filter and schedule notifications for next-day events
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Get current date and add 1 day
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String nextDayString = dateFormat.format(calendar.getTime());

        for (Event event : eventList) {
            if (event.getDate().equals(nextDayString)) {
                scheduleNotification(event);
            }
        }
    }


    private class FetchEventsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                if (scanner.hasNext()) {
                    response = scanner.next();
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null || response.isEmpty()) {
                noEventText.setVisibility(View.VISIBLE);
//                eventContainer.setVisibility(View.GONE);
                eventRecyclerView.setVisibility(View.GONE);
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray items = jsonResponse.getJSONArray("items");

                if (items.length() == 0) {
                    noEventText.setVisibility(View.VISIBLE);
//                    eventContainer.setVisibility(View.GONE);
                    eventRecyclerView.setVisibility(View.GONE);
                } else {
                    noEventText.setVisibility(View.GONE);
//                    eventContainer.setVisibility(View.VISIBLE);
                    eventRecyclerView.setVisibility(View.VISIBLE);

                    // Clear the old list and add new items
                    eventList.clear();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    String currentDateStr = sdf.format(new Date());

                    for (int i = 0; i < items.length(); i++) {

                        JSONObject event = items.getJSONObject(i);

                        // Populate event object
                        String eventId = event.getString("event_id");
                        Log.d("Event ID", eventId);
                        String eventName = event.getString("event_name");
                        String tutorName = "With Tutor " + event.getString("tutor_display_name");

                        String eventTime = event.getString("event_time");
                        String eventDescription = event.getString("event_description");
                        String eventWebsiteLink = event.getString("event_website_link");
                        String registrationLink = event.getString("registration_link");
                        String tutorEmail = event.getString("tutor_email");

                        String fullDate = event.getString("event_date");
                        String[] dateParts = fullDate.split("T")[0].split("-");
                        String formattedDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

                        String location = event.getString("event_location");
                        String base64Image = event.getString("event_picture");
                        String publishDate = event.getString("publish_date");

                        // Check if location is a URL
                        if (location.startsWith("http://") || location.startsWith("https://") || location.startsWith("www.")) {
                            location = "Online";
                        }

                        // Decode Base64 image
                        Bitmap image = decodeBase64Image(base64Image);

//                        // Create an Event object and add it to the list
//                        eventList.add(new Event(eventName, tutorName, formattedDate + ", " + location, base64Image));

                        if (publishDate.compareTo(currentDateStr) <= 0) {
                            boolean Joined = false;
                            eventList.add(new Event(eventId, eventName, tutorName, formattedDate + ", " + location, base64Image, fullDate, eventTime, location, eventDescription, eventWebsiteLink, registrationLink, tutorEmail, Joined));
                            // Schedule notification for this event
                            scheduleNotification(new Event(eventId, eventName, tutorName, formattedDate + ", " + location, base64Image, fullDate, eventTime, location, eventDescription, eventWebsiteLink, registrationLink, tutorEmail, Joined));
                        }
                    }

                    // Notify the adapter of data changes
                    eventAdapter.notifyDataSetChanged();
                    updateAndScheduleNotifications(); // Call updateAndScheduleNotifications to schedule notifications
                    loadEvents(); // Call loadEvents to filter and display the events
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap decodeBase64Image(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    private void scheduleNotification(Event event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Context context = getContext(); // Ensure the context is not null
            if (context != null) {
                AlarmManager alarmManager = ContextCompat.getSystemService(context, AlarmManager.class);
                if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                }
            }
        }


        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy'T'hh:mm a'Z'", Locale.getDefault());
            String eventDateTimeString = event.getDate() + "T" + event.getTime() + "Z";
            Date eventDateTime = dateTimeFormat.parse(eventDateTimeString);

            if (eventDateTime == null) return;

            long notificationTime = eventDateTime.getTime() - 24 * 60 * 60 * 1000;

            // Log the scheduled alarm time
            Log.d("AlarmManager", "Alarm set for: " + new Date(notificationTime));

            if (notificationTime > System.currentTimeMillis()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

                    if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                        Intent intent = new Intent(getContext(), EventNotificationReceiver.class);
                        intent.putExtra("event_name", event.getName());
                        intent.putExtra("event_time", event.getTime());

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                getContext(),
                                event.getEventId().hashCode(),
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
                        Log.d("NotificationSchedule", "Scheduled notification for event: " + event.getName() +
                                " at: " + new Date(notificationTime));
                    } else {
                        Log.e("NotificationSchedule", "Exact alarms not permitted. Please grant SCHEDULE_EXACT_ALARM permission.");
                    }
                }
            } else {
                Log.d("NotificationSchedule", "Skipping scheduling for event: " + event.getName() +
                        " as the notification time is in the past.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("NotificationSchedule", "Error scheduling notification for event: " + event.getName(), e);
        }
    }

}
