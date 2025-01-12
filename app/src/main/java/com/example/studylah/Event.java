package com.example.studylah;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Event {
    private String eventId; // Add this field
    private String name;
    private String tutor;
    private String dateLocation;
    private Bitmap image;
    private String base64Image; // Add this field

    private String date;
    private String time;
    private String location;
    private String description;
    private String websiteLink;
    private String registrationLink;
    private String tutorEmail;

    private boolean isJoined;

    public Event(String eventId, String name, String tutor, String dateLocation, String base64Image, String date, String time, String location, String description, String websiteLink, String registrationLink, String tutorEmail, boolean isJoined) {
        this.eventId = eventId; // Initialize eventId
        this.name = name;
        this.tutor = tutor;
        this.dateLocation = dateLocation;
        this.base64Image = base64Image; // Initialize this field
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.websiteLink = websiteLink;
        this.registrationLink = registrationLink;
        this.tutorEmail = tutorEmail;
        this.isJoined = isJoined;

    }

    // New constructor with Bitmap parameter
    public Event(String name, String tutor, String date, String time, String location, String description, String registrationLink, String tutorEmail, String websiteLink, Bitmap image) {
        this.name = name;
        this.tutor = tutor;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.registrationLink = registrationLink;
        this.tutorEmail = tutorEmail;
        this.websiteLink = websiteLink;
        this.image = image;
        this.isJoined = false;
    }

    // Getters and setters for the new field
    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getTutor() {
        return tutor;
    }

    public String getDateLocation() {
        return dateLocation;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getBase64Image() {
        return base64Image; // Add this getter
    }

//    public String getDate(){
//        return date;
//    }

    public String getDate() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date; // Return the original date if parsing fails
        }
    }

    public String getTime(){
        return time;
    }

    public String getLocation(){
        return location;
    }

    public String getDescription(){
        return description;
    }

    public String getWebsiteLink(){
        return websiteLink;
    }

    public String getRegistrationLink(){
        return registrationLink;
    }

    public String getTutorEmail(){
        return tutorEmail;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }
}
