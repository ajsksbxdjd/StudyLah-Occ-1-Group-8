package com.example.studylah;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class FlashcardStudyActivity extends AppCompatActivity {

    private List<Flashcard> cards;
    private int revealedCount = 0; // 🚀 Tracks how many cards have been studied
    private ProgressBar progressBar;
    private TextView progressCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_activity_study);

        // 🔥 Get deck name from intent
        String deckName = getIntent().getStringExtra("deckName");
        if (deckName == null || deckName.isEmpty()) {
            deckName = "Flashcards";
        }

        // 🔥 Set the deck name dynamically
        TextView studyTitle = findViewById(R.id.studyTitle);
        studyTitle.setText(deckName + " Flashcards");

        // 🔥 Handle Back Button Click to return to DeckDetailsActivity
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            finish(); // ✅ Closes StudyActivity and returns to previous activity (DeckDetailsActivity)
        });

        // 🔥 Get cards from intent
        cards = getIntent().getParcelableArrayListExtra("cards");
        if (cards == null) {
            cards = new ArrayList<>();
        }

        // 🔥 Initialize UI components
        progressBar = findViewById(R.id.progressBar);
        progressCounter = findViewById(R.id.progressCounter);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // 🔥 Set progress bar max value
        progressBar.setMax(cards.size());
        updateProgress();

        // 🔥 Initialize and set up the adapter
        FlashcardCardAdapter adapter = new FlashcardCardAdapter(cards, position -> {
            revealedCount++;
            updateProgress();
        });

        viewPager.setAdapter(adapter);

        // ✅ Smooth Swipe & Peeking Side Cards
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer((page, position) -> {
            float absPosition = Math.abs(position);
            page.setScaleY(1 - (0.1f * absPosition));
            page.setAlpha(1 - (0.3f * absPosition));
            page.setRotationY(position * -10);
        });
    }



    private void updateProgress() {
        progressBar.setProgress(revealedCount);
        progressCounter.setText(revealedCount + " / " + cards.size());
    }
}
