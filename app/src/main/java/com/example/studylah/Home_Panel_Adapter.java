package com.example.studylah;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Home_Panel_Adapter extends RecyclerView.Adapter<Home_Panel_Adapter.PanelViewHolder> {

    private final Context context;
    private final List<HomePanel> panelList;

    public Home_Panel_Adapter(Context context, List<HomePanel> panelList) {
        this.context = context;
        this.panelList = panelList; // Initialize the panel list
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

            switch (position) {
                case 0: // Find a Tutor
                    intent = new Intent(context, Mentoring_Tutors_List.class);
                    break;
                case 1: // Flashcards
                    intent = new Intent(context, Mentoring_Tutors_List.class); // Replace with the actual activity
                    break;
                case 2: // Pomodoro Timer
                    intent = new Intent(context, Mentoring_Tutors_List.class); // Replace with the actual activity
                    break;
                case 3: // Upcoming Events
                    intent = new Intent(context, Mentoring_Tutors_List.class); // Replace with the actual activity
                    break;
            }

            if (intent != null) {
                context.startActivity(intent);
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

        public TutorPanelAdapter(Context context, List<HomePanel> tutorPanelList) {
            this.context = context;
            this.tutorPanelList = tutorPanelList;
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
                switch (position) {
                    case 0:
                        // Navigate to upcoming sessions
                        break;
                    case 1:
                        // Navigate to manage marketplace
                        break;
                    case 2:
                        //Navigate to events overview
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
