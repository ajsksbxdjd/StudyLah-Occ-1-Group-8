package com.example.studylah;

import java.util.Map;

public class Tutor_List_Data {
    private String username; // Unique identifier for the tutor
    private String displayName;
    private String email;
    private String registrationDate;
    private String university;
    private Map<String, Integer> subjects; // Map for subjects and their statuses
    private String tutorDescription;
    private String gcLink;
    private String profilePicture;

    public Tutor_List_Data(String username, String displayName, String email, String registrationDate,
                           String university, Map<String, Integer> subjects, String tutorDescription,
                           String profilePicture, String gcLink) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.registrationDate = registrationDate;
        this.university = university;
        this.subjects = subjects;
        this.tutorDescription = tutorDescription;
        this.profilePicture = profilePicture;
        this.gcLink = gcLink;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getUniversity() {
        return university;
    }

    public String getDescription() {
        return tutorDescription;
    }

    public String getGcLink() {
        return gcLink;
    }

    public String getSubjectsString() {
        if (subjects == null || subjects.isEmpty()) return "None";

        StringBuilder subjectsBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : subjects.entrySet()) {
            if (entry.getValue() == 1) { // Only add subjects that have value 1
                if (subjectsBuilder.length() > 0) subjectsBuilder.append(", ");
                subjectsBuilder.append(entry.getKey());
            }
        }
        return subjectsBuilder.toString();
    }

    public Map<String, Integer> getSubjects() {
        return subjects;
    }
}
