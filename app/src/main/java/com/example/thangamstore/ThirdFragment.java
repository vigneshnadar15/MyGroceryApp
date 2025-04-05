package com.example.thangamstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;

import java.util.List;

public class ThirdFragment extends Fragment implements CartAdapter.UpdateTotalCostCallback {

    private RecyclerView recyclerViewCart;
    private TextView totalCostTextView;
    private Button orderNowButton;
    private ButtonStateManager buttonStateManager;
    private CartAdapter cartAdapter;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        progressBar=view.findViewById(R.id.progressBar);

        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        totalCostTextView = view.findViewById(R.id.totalCostTextView);
        orderNowButton = view.findViewById(R.id.orderNowButton);

        buttonStateManager = new ButtonStateManager(getContext());

        List<GroceryItem> cartItems = buttonStateManager.getCartItems();

        cartAdapter = new CartAdapter(cartItems, requireContext(), this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);

        updateTotalCost();


        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);


        orderNowButton.setOnClickListener(v -> {

            if (!isLoggedIn) {
                Toast.makeText(getContext(), "You must log in to place an order.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                showCustomDialog(cartItems);
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateTotalCost() {
        int totalCost = 0;
        for (GroceryItem item : buttonStateManager.getCartItems()) {
            String priceString = item.getPrice().replaceAll("[^\\d]", ""); // Remove non-numeric characters
            if (!priceString.isEmpty()) {
                int price = Integer.parseInt(priceString);
                totalCost += price* item.getQuantity();
            }
        }
        totalCostTextView.setText("Total Cost: ₹" + totalCost);
    }

    private void showCustomDialog(List<GroceryItem> cartItems) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null);

        ImageView closeButton = dialogView.findViewById(R.id.closeButton);
        RecyclerView recyclerViewCartItems = dialogView.findViewById(R.id.items); // Updated variable name
        EditText addressInput = dialogView.findViewById(R.id.address);           // Updated variable name
        EditText phoneInput = dialogView.findViewById(R.id.number);             // Updated variable name
        RadioGroup paymentOptions = dialogView.findViewById(R.id.paymentOptions);
        Button submitButton = dialogView.findViewById(R.id.dialogSubmit);


        StringBuilder productNames = new StringBuilder();
        for (GroceryItem item : cartItems) {
            productNames.append(item.getName()).append(' ');
        }

        CartAdapter cartAdapter = new CartAdapter(cartItems, requireContext(), this); // No listener for dialog
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCartItems.setAdapter(cartAdapter);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();
        closeButton.setOnClickListener(v -> dialog.dismiss());


        submitButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String address = addressInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            int selectedPaymentOptionId = paymentOptions.getCheckedRadioButtonId();

            if (address.isEmpty() || phone.isEmpty() || selectedPaymentOptionId == -1) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                String paymentOption = selectedPaymentOptionId == R.id.radioCOD ? "COD" : "Pay Later";


                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                String currentDate = dateFormat.format(System.currentTimeMillis());
                String currentTime = timeFormat.format(System.currentTimeMillis());


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ordersRef = database.getReference("orders");
                DatabaseReference counterRef = database.getReference("orderIdCounter");

                counterRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentCounter = snapshot.getValue(Integer.class);
                        if (currentCounter == null) {
                            currentCounter = 0;
                        }
                        int newOrderId = currentCounter + 1;

                        Order order = new Order(
                                String.valueOf(newOrderId),
                                address,
                                phone,
                                paymentOption,
                                cartItems,
                                currentDate,
                                currentTime
                        );

                        DatabaseReference newOrderRef = ordersRef.push();

                        ordersRef.child(String.valueOf(newOrderId)).setValue(order)
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {
                                        counterRef.setValue(newOrderId);
                                        Toast.makeText(getContext(), "Order placed successfully!\nPayment: " + paymentOption, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                                    }

                                    progressBar.setVisibility(View.GONE);
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch order counter. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(1000, 1680);
        }
    }
}
