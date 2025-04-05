package com.example.thangamstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {
    private List<GroceryItem> groceryItemList;
    private ButtonStateManager buttonStateManager;

    public GroceryAdapter(List<GroceryItem> groceryItemList, Context context) {
        this.groceryItemList = groceryItemList;
        this.buttonStateManager = new ButtonStateManager(context);
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery, parent, false);
        return new GroceryViewHolder(view);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        GroceryItem groceryItem = groceryItemList.get(position);
        holder.itemName.setText(groceryItem.getName());
        holder.itemPrice.setText(groceryItem.getPrice());
        holder.itemImage.setImageResource(groceryItem.getImageResId());

        if (groceryItem.getStock() == 0) {
            holder.addToCartButton.setText("Out of Stock");
            holder.addToCartButton.setEnabled(false);
        } else {
            Integer state = buttonStateManager.getCartState(groceryItem.getId());
            holder.addToCartButton.setText(state != null && state == 1 ? "Remove from Cart" : "Add to Cart");
            holder.addToCartButton.setEnabled(true);

            holder.addToCartButton.setOnClickListener(v -> {
                boolean isAdded = state != null && state == 1;
                boolean newState = !isAdded;

                buttonStateManager.updateCartState(
                        groceryItem.getId(),
                        groceryItem.getName(),
                        groceryItem.getPrice(),
                        groceryItem.getImageResId(),
                        newState
                );
                groceryItemList.get(position).setAddedToCart(newState);
                holder.addToCartButton.setText(newState ? "Remove from Cart" : "Add to Cart");

                notifyItemChanged(position);
            });


        }
    }

    @Override
    public int getItemCount() {
        return groceryItemList.size();
    }

    static class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        ImageView itemImage;
        Button addToCartButton;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemImage = itemView.findViewById(R.id.item_image);
            addToCartButton = itemView.findViewById(R.id.button);
        }
    }

}
