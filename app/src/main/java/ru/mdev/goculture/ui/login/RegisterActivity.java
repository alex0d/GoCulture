package ru.mdev.goculture.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.j256.ormlite.stmt.query.In;

import ru.mdev.goculture.MainActivity;
import ru.mdev.goculture.R;
import ru.mdev.goculture.model.User;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.register);
        progressBar = findViewById(R.id.progress_bar);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty()) {
            usernameEditText.setError(getResources().getString(R.string.username_empty));
            usernameEditText.requestFocus();
            return;
        }
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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(username, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.welcome_message) + username, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    } else {
                                        Log.e("Firebase1", "onComplete: Failed=" + task.getException().getMessage());
                                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.e("Firebase2", "onComplete: Failed=" + task.getException().getMessage());
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        progressBar.setVisibility(View.GONE);
    }
}