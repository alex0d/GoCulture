package ru.mdev.goculture.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ru.mdev.goculture.R;

public class PasswordResetActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailEditText;
    private Button submitButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);

        submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> resetPassword());

        progressBar = findViewById(R.id.progress_bar);
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError(getResources().getString(R.string.email_empty));
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getResources().getString(R.string.email_invalid));
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, getResources().getString(R.string.email_password_reset), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            }
            finish();
        });
    }
}
