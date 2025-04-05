package com.example.thangamstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {

    EditText edittextEmail, edittextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    TextView textView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        edittextEmail = view.findViewById(R.id.email);
        edittextPassword = view.findViewById(R.id.password);
        buttonReg = view.findViewById(R.id.btn_registor);

        progressBar = view.findViewById(R.id.progressBar);

        View toLogin = view.findViewById(R.id.toLogin);



        toLogin.setOnClickListener(v -> {

            Fragment loginFragment = new LoginFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment,loginFragment )
                    .addToBackStack(null)
                    .commit();
        });


        // Check if a user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }

        // Set click listener for register button
        buttonReg.setOnClickListener(v -> {
            String email = String.valueOf(edittextEmail.getText());
            String password = String.valueOf(edittextPassword.getText());

            // Validate email and password
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "Account Created.", Toast.LENGTH_SHORT).show();
                                navigateToLoginFragment();
                                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                            } else {
                                Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);

                        }
                    });
        });

        return view;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
    private void navigateToLoginFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, new LoginFragment())
                .commit();
    }
}
