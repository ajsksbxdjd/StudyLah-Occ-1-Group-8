package com.example.studylah;

import java.util.ArrayList;
import java.util.List;

public class TutorDataSingleton {
    private static TutorDataSingleton instance;
    private List<Tutor_List_Data> tutorList;

    private TutorDataSingleton() {
        tutorList = new ArrayList<>();
    }

    public static TutorDataSingleton getInstance() {
        if (instance == null) {
            instance = new TutorDataSingleton();
        }
        return instance;
    }

    public List<Tutor_List_Data> getTutorList() {
        return tutorList;
    }

    public void setTutorList(List<Tutor_List_Data> tutorList) {
        this.tutorList = tutorList;
    }
}


