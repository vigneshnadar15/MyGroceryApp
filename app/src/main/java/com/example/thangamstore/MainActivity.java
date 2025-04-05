package com.example.thangamstore;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    LoginFragment loginFragment = new LoginFragment();
    RegisterFragment registerFragment = new RegisterFragment();

    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    NavigationView navigationView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        List<GroceryItem> groceryItemList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groceryItemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);
                    String imageName = snapshot.child("imageName").getValue(String.class);
                    int stock = snapshot.child("stock").getValue(Integer.class);



                    int imageResId = 0;
                    if (imageName != null && !imageName.isEmpty()) {
                        imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                    } else {
                        imageResId = R.drawable.apple;
                    }

                    GroceryItem groceryItem = new GroceryItem(name, price, imageResId, Integer.parseInt(snapshot.getKey()), 1, stock);


                    groceryItemList.add(groceryItem);


                }

                sharedViewModel.setGroceryList(groceryItemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            setUserDetailsInNavDrawer();
        }else {
            clearUserDetailsInNavDrawer();
        }
        navigationView = findViewById(R.id.navigation_view);
        if (navigationView == null) {
            Log.e("MainActivity", "NavigationView is null");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, firstFragment)
                    .commit();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, firstFragment)
                        .commit();
                return true;
            } else if (itemId == R.id.category) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, secondFragment)
                        .commit();
                return true;
            } else if (itemId == R.id.cart) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, thirdFragment)
                        .commit();
                return true;
            }
            return false;
        });

        drawerLayout = findViewById(R.id.drawerlayout);
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToogle);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        buttonDrawerToggle.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
    public void setUserDetailsInNavDrawer() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        TextView textViewUserName = headerView.findViewById(R.id.textViewUserName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userName = user.getDisplayName();
            if (userName == null || userName.isEmpty()) {
                userName = user.getEmail();
            }
            textViewUserName.setText(userName);
        } else {
            textViewUserName.setText("Guest");
        }
    }

    public void clearUserDetailsInNavDrawer() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                TextView userNameTextView = headerView.findViewById(R.id.textViewUserName);
                if (userNameTextView != null) {
                    userNameTextView.setText("Guest");
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);


        if (itemId == R.id.login) {
            if (isLoggedIn){
                Toast.makeText(this, "You are already logged in", Toast.LENGTH_SHORT).show();
            }else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, loginFragment)
                        .commit();
                setUserDetailsInNavDrawer();
            }
            drawerLayout.closeDrawers();
            return true;
        } else if (itemId == R.id.register) {
            if (isLoggedIn){
                Toast.makeText(this, "You are already logged in", Toast.LENGTH_SHORT).show();

            }else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, loginFragment)
                        .commit();
                setUserDetailsInNavDrawer();
            }
            drawerLayout.closeDrawers();
            return true;
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, firstFragment)
                    .commit();

            clearUserDetailsInNavDrawer();
            drawerLayout.closeDrawers();
            return true;
        }
        return false;
    }
}
