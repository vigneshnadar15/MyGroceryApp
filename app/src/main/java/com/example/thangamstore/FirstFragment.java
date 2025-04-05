package com.example.thangamstore;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;


import java.io.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class FirstFragment extends Fragment {

    private RecyclerView searchRecyclerView;
    private ViewPager viewPager;
    private GroceryAdapter groceryAdapter;
    private List<GroceryItem> originalList = new ArrayList<>();
    private List<GroceryItem> filteredList = new ArrayList<>();
    private ImagePagerAdapter adapter;
    public FirstFragment(){
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        adapter = new ImagePagerAdapter(getContext());
        viewPager.setAdapter(adapter);


        RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groceryAdapter = new GroceryAdapter(filteredList, getContext());
        searchRecyclerView.setAdapter(groceryAdapter);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getGroceryList().observe(getViewLifecycleOwner(), list -> {
            originalList.clear();
            originalList.addAll(list);

            filteredList.clear();
            groceryAdapter.notifyDataSetChanged();
            Log.d("FirstFragment", "Original List Size: " + originalList.size());
        });

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AutoScrollTask(), 3000, 3000);

        return view;
    }
    private class AutoScrollTask extends TimerTask {
        @Override
        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    int currentItem = viewPager.getCurrentItem();
                    int totalItems = adapter.getCount();
                    int nextItem = (currentItem + 1) % totalItems;
                    viewPager.setCurrentItem(nextItem, true);
                });
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            groceryAdapter.notifyDataSetChanged();
            return;
        }

        for (GroceryItem item : originalList) {
            if (item.getName() != null && item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        groceryAdapter.notifyDataSetChanged();
    }
    private void setContentView(int fragmentFirst) {
    }
}