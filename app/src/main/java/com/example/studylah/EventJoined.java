package com.example.studylah;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventJoined#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventJoined extends Fragment {

    private TextView eventNameDetails;
    private TextView eventTutorDetails;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventLocation;
    private TextView eventDescription;
    private TextView eventRegistrationLink;
    private TextView eventTutorEmail;
    private TextView eventWebsiteLink;
    private ImageView eventImageDetails;

    private RecyclerView joinedRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> joinedEventList = new ArrayList<>();

    public EventJoined() {
        // Required empty public constructor
    }

    public static EventJoined newInstance() {
        return new EventJoined();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment joined.
     */
    // TODO: Rename and change types and number of parameters
    public static EventJoined newInstance(String param1, String param2) {
        EventJoined fragment = new EventJoined();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_joined, container, false);

        // Initialize RecyclerView
        joinedRecyclerView = view.findViewById(R.id.joinedRecyclerView);
        joinedRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        eventAdapter = new EventAdapter(joinedEventList, getContext());
        joinedRecyclerView.setAdapter(eventAdapter);

        // Load joined events from SharedPreferences
        loadJoinedEvents();

        return view;
    }

    private void loadJoinedEvents() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("joined_events", Context.MODE_PRIVATE);
        String joinedEventsJson = sharedPreferences.getString("joined_events_list", "[]");

        try {
            JSONArray joinedEventsArray = new JSONArray(joinedEventsJson);
            for (int i = 0; i < joinedEventsArray.length(); i++) {
                JSONObject eventObject = joinedEventsArray.getJSONObject(i);

                String id = eventObject.getString("event_id");
                Log.d("joined", "Event ID: " + id);
                String name = eventObject.getString("event_name");
                String tutor = eventObject.getString("event_tutor");
                String date = eventObject.getString("event_date");
                String time = eventObject.getString("event_time");
                String location = eventObject.getString("event_location");
                String description = eventObject.getString("event_description");
                String registrationLink = eventObject.getString("event_registration_link");
                String tutorEmail = eventObject.getString("event_tutor_email");
                String websiteLink = eventObject.getString("event_website_link");
                String base64Image = eventObject.getString("event_base64_image");

                // Log the base64Image string
                Log.d("joined", "Base64 Image: " + base64Image);

                // Decode the base64 image string to a Bitmap
                Bitmap image = null;
                if (base64Image != null && !base64Image.isEmpty()) {
                    try {
                        // Decode Base64 image
                        image = decodeBase64Image(base64Image);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        Log.e("joined", "Failed to decode base64 image: " + e.getMessage());
                    }
                } else {
                    Log.e("joined", "Base64 image string is null or empty");
                }

                // Create an Event object and add it to the list
//                joinedEventList.add(new Event(name, tutor, date + ", " + location, base64Image, date, time, location, description, websiteLink, registrationLink, tutorEmail));
                Event event = new Event(id, name, tutor, date + ", " + location, base64Image, date, time, location, description, websiteLink, registrationLink, tutorEmail, false);
                event.setJoined(true); // Mark as joined
                joinedEventList.add(event);
            }

            // Notify the adapter of data changes
            eventAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeBase64Image(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


}