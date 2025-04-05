package com.example.thangamstore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<GroceryItem>> groceryList = new MutableLiveData<>();

    public void setGroceryList(List<GroceryItem> list) {
        groceryList.setValue(list);
    }

    public LiveData<List<GroceryItem>> getGroceryList() {
        return groceryList;
    }
}
