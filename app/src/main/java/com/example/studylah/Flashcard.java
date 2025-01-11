package com.example.studylah;

import android.os.Parcel;
import android.os.Parcelable;

public class Flashcard implements Parcelable {
    private String frontText;
    private String backText;

    public Flashcard(String frontText, String backText) {
        this.frontText = frontText;
        this.backText = backText;
    }

    protected Flashcard(Parcel in) {
        frontText = in.readString();
        backText = in.readString();
    }

    public static final Creator<Flashcard> CREATOR = new Creator<Flashcard>() {
        @Override
        public Flashcard createFromParcel(Parcel in) {
            return new Flashcard(in);
        }

        @Override
        public Flashcard[] newArray(int size) {
            return new Flashcard[size];
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
        dest.writeString(frontText);
        dest.writeString(backText);
    }
}
