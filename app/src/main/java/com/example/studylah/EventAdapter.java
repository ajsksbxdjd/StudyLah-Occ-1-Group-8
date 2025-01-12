package com.example.studylah;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventTutor.setText(event.getTutor());
        holder.eventDateLocation.setText(event.getDateLocation());

//        // Decode the base64 image string to a Bitmap
//        byte[] decodedString = Base64.decode(event.getBase64Image(), Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        holder.eventImage.setImageBitmap(decodedByte);

        // Check if base64Image is not null before decoding
        if (event.getBase64Image() != null) {
            byte[] decodedString = Base64.decode(event.getBase64Image(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.eventImage.setImageBitmap(decodedByte);
        }

        // Handle Item Click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowEventDetails.class);
            intent.putExtra("event_id", event.getEventId());
            Log.d("Event ID", event.getEventId());
            intent.putExtra("event_name", event.getName());
            intent.putExtra("event_tutor", event.getTutor());
            intent.putExtra("event_date_location", event.getDateLocation());
            intent.putExtra("event_base64_image", event.getBase64Image()); // Pass base64 image string
            intent.putExtra("event_date", event.getDate()); // Pass event date
            intent.putExtra("event_time", event.getTime()); // Add other event details as needed
            intent.putExtra("event_location", event.getLocation()); // Add other event details as needed
            intent.putExtra("event_description", event.getDescription()); // Add other event details as needed
            intent.putExtra("event_registration_link", event.getRegistrationLink()); // Add other event details as needed
            intent.putExtra("event_tutor_email", event.getTutorEmail()); // Add other event details as needed
            intent.putExtra("event_website_link", event.getWebsiteLink());

            intent.putExtra("is_joined", event.isJoined()); // Add other event details as needed

            // Add other event details as needed
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventTutor, eventDateLocation;
        ImageView eventImage;
        Button joinButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventTutor = itemView.findViewById(R.id.eventTutor);
            eventDateLocation = itemView.findViewById(R.id.eventDateLocation);
            eventImage = itemView.findViewById(R.id.eventImage);
        }
    }
}
