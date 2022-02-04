package com.caffeine.e_commercedemo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.model.UserDetails;
import com.caffeine.e_commercedemo.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth auth;

    private String email, pass, accType = "Individual";
    public static final String EMAIL_PATTERN = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.individual.setOnClickListener(v -> {
            accType = "Individual";
            binding.individual.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.individual.setBackgroundResource(R.drawable.bg_orange_25);
            binding.admin.setTextColor(getResources().getColor(R.color.colorDarkGrey));
            binding.admin.setBackgroundResource(0);
        });

        binding.admin.setOnClickListener(v -> {
            accType = "Admin";
            binding.admin.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.admin.setBackgroundResource(R.drawable.bg_orange_25);
            binding.individual.setTextColor(getResources().getColor(R.color.colorDarkGrey));
            binding.individual.setBackgroundResource(0);
        });

        binding.loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        binding.signUpBtn.setOnClickListener(v -> {
            initialize();
            if (validate()){
                binding.signUpTxt.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                signUpUser();
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

        else if (pass.length() < 6){
            binding.password.setError("Password should be at least 6 characters");
        }

        return v;
    }

    private void signUpUser(){
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            sendDataToFirebase();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "An error occurred or email already registered", Toast.LENGTH_SHORT).show();
                            binding.signUpTxt.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void sendDataToFirebase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ECommerce").child("Users");
        UserDetails user = new UserDetails(auth.getUid(), email, pass, accType);
        ref.child(auth.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(SignUpActivity.this, DashboardActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "An error occurred try again later", Toast.LENGTH_SHORT).show();
                            binding.signUpTxt.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}