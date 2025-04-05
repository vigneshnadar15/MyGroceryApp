package com.example.thangamstore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<GroceryItem> cartItems;
    private final ButtonStateManager dbHelper;
    private final UpdateTotalCostCallback callback;


    public CartAdapter(List<GroceryItem> cartItems, Context context, UpdateTotalCostCallback callback) {
        this.cartItems = cartItems;
        this.dbHelper = new ButtonStateManager(context);
        this.callback = callback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        GroceryItem item = cartItems.get(position);
        holder.productName.setText(item.getName());
        holder.productPrice.setText(item.getPrice());
        holder.productImage.setImageResource(item.getImageResId());
        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            holder.quantityText.setText(String.valueOf(newQuantity));


            dbHelper.updateQuantity(item.getId(), newQuantity);
            notifyItemChanged(position);
            if (callback != null) {
                callback.updateTotalCost();
            }


        });

        holder.decreaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity < 1) {
                if (position >= 0 && position < cartItems.size()) {
                    dbHelper.removeItem(item.getId());
                    cartItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartItems.size());

                    Toast.makeText(holder.itemView.getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();

                    if (cartItems.isEmpty() && callback != null) {
                        callback.updateTotalCost();
                        Toast.makeText(holder.itemView.getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                item.setQuantity(newQuantity);
                holder.quantityText.setText(String.valueOf(newQuantity));
                dbHelper.updateQuantity(item.getId(), newQuantity);
                notifyItemChanged(position);
            }
            if (callback != null) {
                callback.updateTotalCost();
            }

        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, quantityText;
        ImageView productImage;
        Button increaseButton, decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            quantityText = itemView.findViewById(R.id.quantity);
            increaseButton = itemView.findViewById(R.id.add);
            decreaseButton = itemView.findViewById(R.id.minus);
        }
    }
    public interface UpdateTotalCostCallback {
        void updateTotalCost();
    }

}
