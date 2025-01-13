package com.example.studylah;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter for displaying flashcards in a RecyclerView.
 * Supports flipping animations and tracking revealed cards.
 */
public class FlashcardCardAdapter extends RecyclerView.Adapter<FlashcardCardAdapter.CardViewHolder> {

    private List<Flashcard> cards; // ✅ List of Flashcard objects to display
    private final Set<Integer> revealedCards = new HashSet<>(); // ✅ Tracks revealed cards to update progress
    private final OnCardRevealedListener listener; // ✅ Callback interface for handling card reveal events

    public interface OnCardRevealedListener {
        void onCardRevealed(int position);
    }

    public FlashcardCardAdapter(List<Flashcard> cards, OnCardRevealedListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item_flashcard, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Flashcard card = cards.get(position);
        holder.bind(card, position); // ✅ Set text and reset state
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }

    /**
     * ViewHolder class to manage individual flashcards.
     */
    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView frontText, backText; // ✅ TextViews for front and back of the card
        CardView cardView; // ✅ Container for the card
        boolean isFlipped = false; // ✅ Tracks whether the card is flipped

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView; // ✅ Ensure it's correctly assigned
            frontText = itemView.findViewById(R.id.frontText);
            backText = itemView.findViewById(R.id.backText);

            // ✅ Set initial visibility (front visible, back hidden)
            frontText.setVisibility(View.VISIBLE);
            backText.setVisibility(View.GONE);

            // ✅ Set up click listener to handle flipping animation
            cardView.setOnClickListener(view -> flipCard(this, getAdapterPosition()));
        }

        void bind(Flashcard card, int position) {
            if (card == null) return; // ✅ Prevent NullPointerException

            frontText.setText(card.getFrontText()); // ✅ Use getter method
            backText.setText(card.getBackText());   // ✅ Use getter method

            // ✅ Ensure correct visibility & reset state
            frontText.setVisibility(View.VISIBLE);
            backText.setVisibility(View.GONE);
            cardView.setCardBackgroundColor(Color.WHITE);
            isFlipped = false; // ✅ Reset flip state when reusing views
        }

        private void flipCard(CardViewHolder holder, int position) {
            holder.cardView.setCameraDistance(8000); // ✅ Adjust camera distance for 3D effect

            // ✅ Animation to "flip out" the card (scale down)
            ObjectAnimator flipOut = ObjectAnimator.ofFloat(holder.cardView, "scaleX", 1f, 0f);
            flipOut.setDuration(150);

            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (!holder.isFlipped) { // ✅ Flipping to back
                        holder.frontText.setVisibility(View.GONE);
                        holder.backText.setVisibility(View.VISIBLE);
                        holder.cardView.setCardBackgroundColor(Color.parseColor("#93B495"));

                        // ✅ Notify StudyActivity that a card was flipped for the first time
                        if (!revealedCards.contains(position)) {
                            revealedCards.add(position); // ✅ Mark as revealed
                            listener.onCardRevealed(position); // 🚀 Updates progress bar
                        }
                    } else { // ✅ Flipping back to front
                        holder.frontText.setVisibility(View.VISIBLE);
                        holder.backText.setVisibility(View.GONE);
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // ✅ Animation to "flip in" the card (scale up)
                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(holder.cardView, "scaleX", 0f, 1f);
                    flipIn.setDuration(150);
                    flipIn.start();
                }
            });

            flipOut.start(); // ✅ Start the flip-out animation
            holder.isFlipped = !holder.isFlipped; // ✅ Toggle flip state
        }
    }
}
