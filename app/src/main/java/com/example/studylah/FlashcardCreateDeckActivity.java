package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for creating a new flashcard deck.
 * Users can enter a deck name and save or cancel the creation.
 */
public class FlashcardCreateDeckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_activity_create_deck);

        // ✅ Initialize UI elements
        EditText deckNameField = findViewById(R.id.deckNameField);
        Button createDeckButton = findViewById(R.id.addDeckButton);
        Button cancelDeckButton = findViewById(R.id.cancelDeckButton);
        ImageView backButton = findViewById(R.id.back_button);

        // ✅ Handle "Create" button click
        createDeckButton.setOnClickListener(view -> {
            String deckName = deckNameField.getText().toString().trim();
            if (!deckName.isEmpty()) {
                // Pass the result back to MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("deckName", deckName);
                setResult(RESULT_OK, resultIntent);
                finish(); // Close this activity
            } else {
                // Notify the user to enter a deck name
                Toast.makeText(FlashcardCreateDeckActivity.this, "Deck name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Cancel" button click
        cancelDeckButton.setOnClickListener(view -> {
            setResult(RESULT_CANCELED); // Send a canceled result back
            finish(); // Close this activity
        });

        // Set up the back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(FlashcardCreateDeckActivity.this, FlashcardMainActivity.class);
            startActivity(intent);
            finish(); // Optional: Close current activity to prevent going back to it
        });
    }
}
