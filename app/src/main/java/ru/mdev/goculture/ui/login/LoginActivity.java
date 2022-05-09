package ru.mdev.goculture.ui.login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mdev.goculture.MainActivity;
import ru.mdev.goculture.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailEditText, passwordEditText;
    private Button signInButton;
    private TextView forgotPasswordTextView, registerTextView;
    private ProgressBar progressBar;

    private final ActivityResultLauncher<Intent> registerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData().getBooleanExtra("isRegistered", false)) {
                        finish();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        signInButton = findViewById(R.id.sign_in);
        signInButton.setOnClickListener(v -> signInUser());

        forgotPasswordTextView = findViewById(R.id.forgot_password);

        registerTextView = findViewById(R.id.register);
        registerTextView.setOnClickListener(v -> {
            Intent registerActivityIntent = new Intent(this, RegisterActivity.class);
            registerActivityResultLauncher.launch(registerActivityIntent);
        });

        progressBar = findViewById(R.id.progress_bar);
    }

    private void signInUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            emailEditText.setError(getResources().getString(R.string.email_empty));
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError(getResources().getString(R.string.password_empty));
            passwordEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getResources().getString(R.string.email_invalid));
            emailEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError(getResources().getString(R.string.password_too_short));
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }
}