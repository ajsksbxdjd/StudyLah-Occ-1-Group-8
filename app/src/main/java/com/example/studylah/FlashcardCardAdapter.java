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

public class FlashcardCardAdapter extends RecyclerView.Adapter<FlashcardCardAdapter.CardViewHolder> {

    private List<Flashcard> cards; // ✅ Correct Type

    private final Set<Integer> revealedCards = new HashSet<>(); // Track revealed cards
    private final OnCardRevealedListener listener;

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

        // 🚀 Bind text content properly
        holder.bind(card, position);
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView frontText, backText;
        CardView cardView;
        boolean isFlipped = false; // Tracks flip state

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView; // ✅ Ensure it's correctly assigned

            frontText = itemView.findViewById(R.id.frontText);
            backText = itemView.findViewById(R.id.backText);

            // 🚀 Ensure initial visibility is correct
            frontText.setVisibility(View.VISIBLE);
            backText.setVisibility(View.GONE);

            // ✅ Attach click listener for flipping animation
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
            holder.cardView.setCameraDistance(8000);

            ObjectAnimator flipOut = ObjectAnimator.ofFloat(holder.cardView, "scaleX", 1f, 0f);
            flipOut.setDuration(150);

            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (!holder.isFlipped) {
                        holder.frontText.setVisibility(View.GONE);
                        holder.backText.setVisibility(View.VISIBLE);
                        holder.cardView.setCardBackgroundColor(Color.parseColor("#93B495"));

                        // 🔥 ✅ Notify StudyActivity that a card was flipped for the first time
                        if (!revealedCards.contains(position)) {
                            revealedCards.add(position);
                            listener.onCardRevealed(position); // 🚀 Updates progress bar
                        }
                    } else {
                        holder.frontText.setVisibility(View.VISIBLE);
                        holder.backText.setVisibility(View.GONE);
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(holder.cardView, "scaleX", 0f, 1f);
                    flipIn.setDuration(150);
                    flipIn.start();
                }
            });

            flipOut.start();
            holder.isFlipped = !holder.isFlipped;
        }
    }
}
