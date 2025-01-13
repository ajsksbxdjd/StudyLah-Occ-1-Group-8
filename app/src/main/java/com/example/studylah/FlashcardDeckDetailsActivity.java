package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Activity for viewing and managing flashcards within a deck.
 * Users can add, search, edit, or delete flashcards.
 */
public class FlashcardDeckDetailsActivity extends AppCompatActivity {
    private String deckName; // ✅ Name of the deck
    private ArrayList<Flashcard> cards; // ✅ List of flashcards in the deck
    private ArrayList<Flashcard> filteredCards; // ✅ List of filtered flashcards (for search)
    private LinearLayout cardContainer; // ✅ Container for displaying flashcards
    private EditText searchCardsInput; // ✅ Search input field
    private boolean isReturningFromCreateFlashcard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_activity_deck_details);

        // ✅ Get deck name from intent
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deckName", deckName);
            resultIntent.putExtra("shouldRefresh", true); // ✅ Ensure MainActivity reloads data
            setResult(RESULT_OK, resultIntent);
            finish(); // ✅ Close DeckDetailsActivity
        });

        deckName = getIntent().getStringExtra("deckName");

        if (deckName == null || deckName.isEmpty()) {
            Toast.makeText(this, "Deck name is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cardContainer = findViewById(R.id.cardContainer);
        searchCardsInput = findViewById(R.id.searchCardsInput);
        TextView currentDeckTitle = findViewById(R.id.currentDeckTitle);
        currentDeckTitle.setText("Current Deck: " + deckName);

        cards = FlashcardDeckManager.getDeckCards(deckName); // ✅ Get only this deck's cards
        filteredCards = new ArrayList<>(cards); // ✅ Start with all cards visible
        updateCardContainer();

        ImageButton addCardButton = findViewById(R.id.addCardButton);
        addCardButton.setOnClickListener(view -> {
            isReturningFromCreateFlashcard = true; // ✅ Set flag before launching new activity
            Intent createCardIntent = new Intent(FlashcardDeckDetailsActivity.this, FlashcardCreateFlashcardActivity.class);
            createCardIntent.putExtra("deckName", deckName);
            createCardLauncher.launch(createCardIntent);
        });


        Button studyButton = findViewById(R.id.studyButton);
        studyButton.setOnClickListener(view -> {
            if (cards == null || cards.isEmpty()) {
                Toast.makeText(FlashcardDeckDetailsActivity.this, "No cards available to study!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent studyIntent = new Intent(FlashcardDeckDetailsActivity.this, FlashcardStudyActivity.class);
            studyIntent.putExtra("deckName", deckName); // ✅ Pass deck name
            studyIntent.putParcelableArrayListExtra("cards", cards);
            startActivity(studyIntent);

        });


        // ✅ Add search functionality
        searchCardsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterCards(charSequence.toString()); // 🔍 Filter flashcards as user types
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // 🔍 **Filter Flashcards Based on Search Input**
    private void filterCards(String query) {
        filteredCards.clear();
        for (Flashcard card : cards) {
            if (card.getFrontText().toLowerCase().contains(query.toLowerCase()) ||
                    card.getBackText().toLowerCase().contains(query.toLowerCase())) {
                filteredCards.add(card); // ✅ Keep matching flashcards
            }
        }
        updateCardContainer(); // ✅ Refresh UI with filtered results
    }

    private void updateCardContainer() {
        cardContainer.removeAllViews(); // ✅ Clear old UI before adding

        for (Flashcard card : filteredCards) { // ✅ Use filtered list
            addCard(cardContainer, card.getFrontText(), card.getBackText());
        }
    }


    private final ActivityResultLauncher<Intent> createCardLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        ArrayList<Flashcard> newCards = data.getParcelableArrayListExtra("newCards");

                        if (newCards != null && !newCards.isEmpty()) {
                            for (Flashcard card : newCards) {
                                if (!FlashcardDeckManager.getDeckCards(deckName).contains(card)) { // ✅ Prevent duplicates
                                    FlashcardDeckManager.addCardToDeck(deckName, card);
                                }
                            }
                        }
                        loadDeckCards(); // ✅ Refresh UI properly
                    }
                }
            });
    private void addCard(LinearLayout container, String front, String back) {
        View cardView = getLayoutInflater().inflate(R.layout.flashcard_card_item, container, false); // ✅ Correct way to inflate

        TextView frontText = cardView.findViewById(R.id.frontTextInput);
        TextView backText = cardView.findViewById(R.id.backTextInput);
        ImageButton menuButton = cardView.findViewById(R.id.menuButton);

        frontText.setText(front);
        backText.setText(back);

        menuButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.flashcard_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.editCard) {
                    frontText.setEnabled(true);
                    backText.setEnabled(true);
                    frontText.requestFocus();
                    Toast.makeText(this, "Edit mode enabled.", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.saveCard) {
                    String newFront = frontText.getText().toString().trim();
                    String newBack = backText.getText().toString().trim();

                    if (!newFront.isEmpty() && !newBack.isEmpty()) {
                        int index = container.indexOfChild(cardView);
                        cards.set(index, new Flashcard(newFront, newBack));
                        frontText.setEnabled(false);
                        backText.setEnabled(false);
                        Toast.makeText(this, "Card saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Both fields must be filled.", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.deleteCard) {
                    container.removeView(cardView);
                    int index = container.indexOfChild(cardView);
                    cards.remove(index);
                    Toast.makeText(this, "Card deleted", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            popupMenu.show();
        });

        container.addView(cardView);
    }


    private void loadDeckCards() {
        cards = new ArrayList<>(FlashcardDeckManager.getDeckCards(deckName));
        filteredCards = new ArrayList<>(cards); // ✅ Ensure filtered list updates
        updateCardContainer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDeckCards(); // ✅ Always reload fresh cards
    }
}
