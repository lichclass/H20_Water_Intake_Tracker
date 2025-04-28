package com.shysoftware.h20tracker.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;

public class SignInActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    TextView signUpRedirect;
    Button signInButton;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        signUpRedirect = findViewById(R.id.signUpText);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Event when Sign-in is clicked
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            Integer result = userViewModel.loginUser(email, password, SignInActivity.this);
        });

        // Redirect to Sign-up Page
        signUpRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

    }
}