package com.example.studylah;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages flashcard decks and their associated flashcards.
 * Provides methods for adding, retrieving, and deleting decks and cards.
 */
public class FlashcardDeckManager {
    private static HashMap<String, ArrayList<Flashcard>> deckStorage = new HashMap<>();

    public static void addNewDeck(String deckName) {
        if (!deckStorage.containsKey(deckName)) {
            deckStorage.put(deckName, new ArrayList<>());
        }
    }

    public static boolean deckExists(String deckName) {
        return deckStorage.containsKey(deckName);
    }

    public static ArrayList<String> getAllDeckNames() {
        return new ArrayList<>(deckStorage.keySet());
    }

    public static ArrayList<Flashcard> getDeckCards(String deckName) {
        return deckStorage.getOrDefault(deckName, new ArrayList<>());
    }

    public static void addCardToDeck(String deckName, Flashcard card) {
        if (!deckStorage.containsKey(deckName)) {
            deckStorage.put(deckName, new ArrayList<>());
        }

        ArrayList<Flashcard> deckCards = deckStorage.get(deckName);

        // ✅ Prevent duplicate flashcards from being added
        for (Flashcard existingCard : deckCards) {
            if (existingCard.getFrontText().equals(card.getFrontText()) &&
                    existingCard.getBackText().equals(card.getBackText())) {
                return; // 🚨 Exit if duplicate found
            }
        }
        deckCards.add(card); // ✅ Add only unique flashcards
    }

}
