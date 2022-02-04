package com.caffeine.e_commercedemo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.caffeine.e_commercedemo.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private String email, pass;
    public static final String EMAIL_PATTERN = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.signUpBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        binding.loginBtn.setOnClickListener(v -> {
            initialize();
            if (validate()){
                binding.loginTxt.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                signInUser();
            }
        });
    }

    private void initialize(){
        email = binding.email.getText().toString();
        pass = binding.password.getText().toString();
    }

    private boolean validate(){
        boolean v = true;

        if (email.isEmpty()){
            binding.email.setError("Filed cannot be empty");
            v=false;
        }

        else if (!email.matches(EMAIL_PATTERN)){
            binding.email.setError("Invalid email address");
            v=false;
        }

        else if (pass.isEmpty()){
            binding.password.setError("Filed cannot be empty");
        }

        return v;
    }

    private void signInUser(){
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }

                        else {
                            binding.loginTxt.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}