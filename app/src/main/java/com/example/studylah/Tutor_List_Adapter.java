package com.example.studylah;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class Tutor_List_Adapter extends RecyclerView.Adapter<Tutor_List_Adapter.TutorViewHolder> {
    private List<Tutor_List_Data> tutorList;
    private Context context;

    public Tutor_List_Adapter(Context context, List<Tutor_List_Data> tutorList) {
        this.context = context;
        this.tutorList = tutorList;
    }

    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tutor_list_item, parent, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        Tutor_List_Data tutor = tutorList.get(position);

        // Decode Base64 profile picture
        String profilePictureBase64 = tutor.getProfilePicture();
        if (profilePictureBase64 != null && !profilePictureBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(profilePictureBase64, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profilePicture.setImageBitmap(decodedBitmap);
        } else {
            holder.profilePicture.setImageResource(R.drawable.mentoring_icon_student); // Default icon
        }

        holder.tutorName.setText(tutor.getDisplayName());
        holder.tutorEmail.setText(tutor.getEmail());
        holder.registrationDate.setText(tutor.getRegistrationDate());
        holder.university.setText(tutor.getUniversity());
        holder.subjects.setText(getSubjectsString(tutor.getSubjects()));

        // Handle card click
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Tutor_Desc_Activity.class);
            intent.putExtra("username", tutor.getUsername()); // Pass only the username
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    public static class TutorViewHolder extends RecyclerView.ViewHolder {
        TextView tutorName, tutorEmail, registrationDate, university, subjects;
        ImageView profilePicture;
        CardView cardView;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorName = itemView.findViewById(R.id.tutor_name);
            tutorEmail = itemView.findViewById(R.id.tutor_email);
            registrationDate = itemView.findViewById(R.id.tutor_registration_date);
            university = itemView.findViewById(R.id.tutor_university);
            subjects = itemView.findViewById(R.id.tutor_subjects);
            profilePicture = itemView.findViewById(R.id.tutor_propic);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    // Convert subjects map to a displayable string
    private String getSubjectsString(Map<String, Integer> subjectsMap) {
        StringBuilder subjectsString = new StringBuilder();
        for (Map.Entry<String, Integer> entry : subjectsMap.entrySet()) {
            if (entry.getValue() > 0) {
                if (subjectsString.length() > 0) {
                    subjectsString.append(", ");
                }
                subjectsString.append(entry.getKey());
            }
        }
        return subjectsString.toString();
    }
}
