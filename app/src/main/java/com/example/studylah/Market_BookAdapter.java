package com.example.studylah;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Market_BookAdapter extends RecyclerView.Adapter<Market_BookAdapter.ViewHolder> {

    private List<Market_BookItem> bookList;

    public Market_BookAdapter(List<Market_BookItem> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_individual_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Market_BookItem item = bookList.get(position);
        holder.imageView.setImageBitmap(item.getImage());
        holder.titleTextView.setText(item.getTitle());
        holder.priceTextView.setText(item.getPrice());

        View.OnClickListener navigateToItemDetails = v -> {
            Intent intent = new Intent(v.getContext(), Market_ItemDetails.class);
            intent.putExtra("itemId", item.getId());
            v.getContext().startActivity(intent);
        };

        holder.imageView.setOnClickListener(navigateToItemDetails);
        holder.titleTextView.setOnClickListener(navigateToItemDetails);
        holder.priceTextView.setOnClickListener(navigateToItemDetails);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}