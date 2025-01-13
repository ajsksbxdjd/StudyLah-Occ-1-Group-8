package com.example.studylah;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Flashcard with a front and back text.
 * Implements Parcelable to allow passing Flashcard objects between activities.
 */
public class Flashcard implements Parcelable {
    private String frontText; // ✅ Text displayed on the front of the flashcard
    private String backText;  // ✅ Text displayed on the back of the flashcard

    public Flashcard(String frontText, String backText) {
        this.frontText = frontText;
        this.backText = backText;
    }

    protected Flashcard(Parcel in) {
        frontText = in.readString(); // ✅ Read front text from parcel
        backText = in.readString();  // ✅ Read back text from parcel
    }

    public static final Creator<Flashcard> CREATOR = new Creator<Flashcard>() {
        @Override
        public Flashcard createFromParcel(Parcel in) {
            return new Flashcard(in); // ✅ Calls the protected constructor to recreate the object
        }

        @Override
        public Flashcard[] newArray(int size) {
            return new Flashcard[size]; // ✅ Creates an array of Flashcard objects
        }
    };

    public String getFrontText() {
        return frontText;
    }

    public String getBackText() {
        return backText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(frontText); // ✅ Write front text to parcel
        dest.writeString(backText);  // ✅ Write back text to parcel
    }
}
