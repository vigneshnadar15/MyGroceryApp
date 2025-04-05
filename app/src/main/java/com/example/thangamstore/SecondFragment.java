package com.example.thangamstore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;


public class SecondFragment extends Fragment {

    public SecondFragment() {

    }

    private RecyclerView recyclerView;
    private GroceryAdapter groceryAdapter;
    private List<GroceryItem> groceryItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getGroceryList().observe(getViewLifecycleOwner(), list -> {
            groceryAdapter = new GroceryAdapter(list, requireContext());
            recyclerView.setAdapter(groceryAdapter);
        });

        return view;
    }

}
