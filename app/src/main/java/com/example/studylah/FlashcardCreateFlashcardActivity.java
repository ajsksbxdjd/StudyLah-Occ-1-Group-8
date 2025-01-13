package com.example.studylah;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * Activity for creating flashcards within a specific deck.
 * Users can add multiple flashcards to a deck, and they are stored in FlashcardDeckManager.
 */
public class FlashcardCreateFlashcardActivity extends AppCompatActivity {

    private String deckName; // ✅ Name of the deck the flashcards belong to
    private EditText frontTextInput, backTextInput; // ✅ Input fields for flashcard content
    private ArrayList<Flashcard> newCards = new ArrayList<>(); // ✅ Store multiple flashcards

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_activity_create_flashcard);

        // ✅ Retrieve deck name from intent
        deckName = getIntent().getStringExtra("deckName");

        // ✅ Initialize UI elements
        TextView currentDeckTitle = findViewById(R.id.currentDeckName);
        frontTextInput = findViewById(R.id.frontInput);
        backTextInput = findViewById(R.id.backInput);
        Button addButton = findViewById(R.id.addCardButton);
        Button cancelButton = findViewById(R.id.cancelCardButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // ✅ Display deck name
        currentDeckTitle.setText("Current deck: " + deckName);

        // ✅ Handle "Add Card" button click
        addButton.setOnClickListener(view -> {
            String frontText = frontTextInput.getText().toString().trim();
            String backText = backTextInput.getText().toString().trim();

            // ✅ Ensure both fields are filled
            if (frontText.isEmpty() || backText.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            Flashcard newCard = new Flashcard(frontText, backText);
            // ✅ Prevent duplicate cards before adding
            boolean alreadyExists = false;
            for (Flashcard card : newCards) {
                if (card.getFrontText().equals(newCard.getFrontText()) &&
                        card.getBackText().equals(newCard.getBackText())) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                newCards.add(newCard);
                FlashcardDeckManager.addCardToDeck(deckName, newCard); // ✅ Save to DeckManager
            }
            Toast.makeText(this, "Card Added! Add more or go back.", Toast.LENGTH_SHORT).show();

            // ✅ Clear input fields for new entry
            frontTextInput.setText("");
            backTextInput.setText("");
        });

        backButton.setOnClickListener(view -> {
            setResult(RESULT_OK); // No need to pass back `newCards` if already saved in DeckManager
            finish();
        });

        cancelButton.setOnClickListener(view -> finish());
    }
}

