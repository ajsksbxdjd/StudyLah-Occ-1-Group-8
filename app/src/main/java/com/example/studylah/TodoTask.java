package com.example.studylah;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoTask implements Parcelable {
    private String name;
    private String category;
    private String date;
    private String time;
    private String priority;
    private boolean isCompleted;

    public TodoTask(String name, String category, String date, String time, String priority, String reminderDate, String reminderTime) {
        this.name = name;
        this.category = category;
        this.date = date;
        this.time = time;
        this.priority = priority;
        this.isCompleted = false;
    }

    protected TodoTask(Parcel in) {
        name = in.readString();
        category = in.readString();
        date = in.readString();
        time = in.readString();
        priority = in.readString();
        isCompleted = in.readByte() != 0;
    }

    public static final Creator<TodoTask> CREATOR = new Creator<TodoTask>() {
        @Override
        public TodoTask createFromParcel(Parcel in) {
            return new TodoTask(in);
        }

        @Override
        public TodoTask[] newArray(int size) {
            return new TodoTask[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(priority);
        dest.writeByte((byte) (isCompleted ? 1 : 0));
    }
}
