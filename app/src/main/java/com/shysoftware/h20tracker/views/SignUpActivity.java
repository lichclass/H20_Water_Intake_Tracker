package com.shysoftware.h20tracker.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;


public class SignUpActivity extends AppCompatActivity {

    EditText emailField, createPasswordField, confirmPasswordField;
    Button signUpBtn;
    TextView signInRedirect;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailField = findViewById(R.id.email);
        createPasswordField = findViewById(R.id.create_password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        signUpBtn = findViewById(R.id.signup_button);
        signInRedirect = findViewById(R.id.sign_in_text);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Sign up button event
        signUpBtn.setOnClickListener(v -> {
            String email           = emailField.getText().toString().trim();
            String createPassword  = createPasswordField.getText().toString();
            String confirmPassword = confirmPasswordField.getText().toString();
            userViewModel.registerUser(this, email, createPassword, confirmPassword);
        });

        // Redirect to Sign-in Page
        signInRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });
    }
}
