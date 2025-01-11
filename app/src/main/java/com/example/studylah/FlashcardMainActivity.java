package com.example.studylah;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class FlashcardMainActivity extends AppCompatActivity {
    private GridLayout deckGrid;
    private EditText searchField; // ✅ Search input field
    private List<String> allDecks = new ArrayList<>(); // ✅ Store all decks (unfiltered)
    private List<String> filteredDecks = new ArrayList<>(); // ✅ Filtered list of decks

    private final ActivityResultLauncher<Intent> createDeckLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String newDeckName = data.getStringExtra("deckName");
                        if (!FlashcardDeckManager.deckExists(newDeckName)) {
                            FlashcardDeckManager.addNewDeck(newDeckName);
                            allDecks.add(newDeckName);
                            filteredDecks.add(newDeckName);
                            updateDeckGrid();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> openDeckLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        boolean shouldRefresh = data.getBooleanExtra("shouldRefresh", false);
                        if (shouldRefresh) {
                            updateDeckGrid();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_activity_main);

        deckGrid = findViewById(R.id.deckGrid);
        searchField = findViewById(R.id.searchField); // ✅ Initialize search field
        ImageButton addDeckButton = findViewById(R.id.addDeckButton);

        addDeckButton.setOnClickListener(view -> {
            Intent intent = new Intent(FlashcardMainActivity.this, FlashcardCreateDeckActivity.class);
            createDeckLauncher.launch(intent);
        });

        allDecks = FlashcardDeckManager.getAllDeckNames(); // ✅ Load all decks initially
        filteredDecks = new ArrayList<>(allDecks); // ✅ Start with all decks visible
        updateDeckGrid();

        // ✅ Add search functionality
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterDecks(charSequence.toString()); // 🔍 Filter decks as user types
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set up the back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
           Intent intent = new Intent(FlashcardMainActivity.this, StudentHome.class);
            startActivity(intent);
            finish(); // Optional: Close current activity to prevent going back to it
        });
    }

    // 🔍 **Filter Decks Based on Search Input**
    private void filterDecks(String query) {
        filteredDecks.clear();
        for (String deckName : allDecks) {
            if (deckName.toLowerCase().contains(query.toLowerCase())) {
                filteredDecks.add(deckName); // ✅ Keep matching decks
            }
        }
        updateDeckGrid(); // ✅ Refresh UI with filtered results
    }

    private void updateDeckGrid() {
        Typeface ralewayFont = ResourcesCompat.getFont(this, R.font.raleway_semibold);

        deckGrid.removeAllViews(); // ✅ Prevent duplicate decks
        if (filteredDecks.isEmpty()) {
            return; // ✅ Exit if no decks match the search
        }

        for (String deckName : filteredDecks) { // ✅ Use filtered list
            MaterialCardView cardView = new MaterialCardView(this);
            cardView.setRadius(24);
            cardView.setCardElevation(8);
            cardView.setStrokeColor(Color.parseColor("#4A683B"));
            cardView.setStrokeWidth(2);
            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setLayoutParams(new GridLayout.LayoutParams());

            TextView deckText = new TextView(this);
            int cardCount = FlashcardDeckManager.getDeckCards(deckName).size();
            deckText.setText(deckName + "\n\nCards: " + cardCount);
            deckText.setTypeface(ralewayFont);
            deckText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            deckText.setGravity(Gravity.CENTER);
            deckText.setTextColor(Color.parseColor("#4A683B"));
            deckText.setTextSize(18);
            deckText.setPadding(20, 20, 20, 20);

            cardView.addView(deckText);

            cardView.setOnClickListener(view -> {
                Intent intent = new Intent(FlashcardMainActivity.this, FlashcardDeckDetailsActivity.class);
                intent.putExtra("deckName", deckName);
                openDeckLauncher.launch(intent);
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(16, 16, 16, 16);
            params.width = 355;
            params.height = 355;
            cardView.setLayoutParams(params);

            deckGrid.addView(cardView);
        }
    }
}
