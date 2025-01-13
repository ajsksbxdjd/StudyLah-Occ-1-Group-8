package com.example.studylah;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class Home_Panel_Adapter extends RecyclerView.Adapter<Home_Panel_Adapter.PanelViewHolder> {

    private final Context context;
    private final List<HomePanel> panelList;
    private BottomNavigationView bottomNavigationView;

    public Home_Panel_Adapter(Context context, List<HomePanel> panelList, BottomNavigationView bottomNavigationView) {
        this.context = context;
        this.panelList = panelList;
        this.bottomNavigationView = bottomNavigationView;
    }

    @NonNull
    @Override
    public PanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_panel_item, parent, false);
        return new PanelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanelViewHolder holder, int position) {
        HomePanel panel = panelList.get(position);
        holder.title.setText(panel.getTitle());
        holder.description.setText(panel.getDescription());
        holder.image.setImageResource(panel.getImageResId());

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            Intent intent = null;
            int navItemId = -1;

            switch (panel.getTitle()) {
                case "Find a Tutor":
                    intent = new Intent(context, Mentoring_Tutors_List.class);
                    navItemId = R.id.nav_mentoring;
                    break;
                case "Flashcards":
                    intent = new Intent(context, FlashcardMainActivity.class);
                    break;
                case "Pomodoro Timer":
                    intent = new Intent(context, Pomodoro.class);
                    break;
                case "Upcoming Events":
                    intent = new Intent(context, EventActivityStudent.class);
                    navItemId = R.id.nav_events;
                    break;
            }

            if (intent != null) {
                context.startActivity(intent);
                if (navItemId != -1) {
                    bottomNavigationView.setSelectedItemId(navItemId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return panelList.size();
    }

    static class PanelViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;
        CardView cardView;

        public PanelViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.panel_title);
            description = itemView.findViewById(R.id.panel_description);
            image = itemView.findViewById(R.id.panel_image);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
    public static class TutorPanelAdapter extends RecyclerView.Adapter<TutorPanelAdapter.TutorViewHolder> {

        private final Context context;
        private final List<HomePanel> tutorPanelList;
        private BottomNavigationView bottomNavigationView;

        public TutorPanelAdapter(Context context, List<HomePanel> tutorPanelList,BottomNavigationView bottomNavigationView) {
            this.context = context;
            this.tutorPanelList = tutorPanelList;
            this.bottomNavigationView = bottomNavigationView;
        }

        @NonNull
        @Override
        public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.home_panel_item, parent, false);
            return new TutorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
            HomePanel panel = tutorPanelList.get(position);
            holder.title.setText(panel.getTitle());
            holder.description.setText(panel.getDescription());
            holder.image.setImageResource(panel.getImageResId());

            // Handle click events for the tutor page
            holder.cardView.setOnClickListener(v -> {
                Log.d("TutorPanelAdapter", "Clicked on position: " + position);
                Intent intent = null;
                int navItemId = -1;

                switch (panel.getTitle()) {
                    case "Upcoming Sessions":
                        intent = new Intent(context, Tutor_Schedules.class);
                        navItemId = R.id.nav_mentoring;
                        break;
                    case "Manage Marketplace":
                        intent = new Intent(context, Market_MainActivityTutor.class);
                        navItemId = R.id.nav_marketplace;
                        break;
                    case "Events Overview":
                        intent = new Intent(context, EventActivity.class);
                        navItemId = R.id.nav_events;
                        break;
                }

                if (intent != null) {
                    context.startActivity(intent);
                    if (navItemId != -1) {
                        bottomNavigationView.setSelectedItemId(navItemId);
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return tutorPanelList.size();
        }

        static class TutorViewHolder extends RecyclerView.ViewHolder {
            TextView title, description;
            ImageView image;
            CardView cardView;

            public TutorViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.panel_title);
                description = itemView.findViewById(R.id.panel_description);
                image = itemView.findViewById(R.id.panel_image);
                cardView = itemView.findViewById(R.id.cardView);
            }
        }
    }
}
