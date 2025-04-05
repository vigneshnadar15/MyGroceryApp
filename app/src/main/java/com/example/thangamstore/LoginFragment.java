package com.example.thangamstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    TextView edittextEmail, edittextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    TextView textView;
    ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        edittextEmail = view.findViewById(R.id.email);
        edittextPassword = view.findViewById(R.id.password);
        buttonLogin = view.findViewById(R.id.btn_login);

        progressBar = view.findViewById(R.id.progressBar);
        View toRegister = view.findViewById(R.id.toRegister);

        toRegister.setOnClickListener(v -> {

            Fragment registerFragment = new RegisterFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, registerFragment)
                    .addToBackStack(null)
                    .commit();
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }


        buttonLogin.setOnClickListener(v -> {
            String email = String.valueOf(edittextEmail.getText());
            String password = String.valueOf(edittextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                navigateToMainActivity();
                            } else {
                                Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
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


}
