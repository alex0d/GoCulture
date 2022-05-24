package ru.mdev.goculture.ui.profile.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.mdev.goculture.R;
import ru.mdev.goculture.model.User;

public class ChangeEmailActivity extends AppCompatActivity {

    private final static String TAG = "ChangeEmailActivity";

    private FirebaseAuth mAuth;

    private User currentUser;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference("Users").
                child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Got current user instance");
                currentUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Current user database error: " + error.getMessage());
                finish();
            }
        });

        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText newEmailEditText = findViewById(R.id.new_email);
        EditText passwordEditText = findViewById(R.id.password);

        Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(v -> finish());

        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> {
            String newEmail = newEmailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (newEmail.isEmpty()) {
                newEmailEditText.setError(getResources().getString(R.string.email_empty));
                newEmailEditText.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                passwordEditText.setError(getResources().getString(R.string.password_empty));
                passwordEditText.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                newEmailEditText.setError(getResources().getString(R.string.email_invalid));
                newEmailEditText.requestFocus();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(mAuth.getCurrentUser().getEmail(), password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateEmail(newEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    currentUser.setEmail(newEmail);
                                                    FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).setValue(currentUser);
                                                    Toast.makeText(getApplicationContext(), "Почта успешно обновлена!", Toast.LENGTH_SHORT).show();

                                                    Intent data = new Intent();
                                                    data.putExtra("setting", "changeEmail");
                                                    data.putExtra("email", newEmail);
                                                    setResult(Activity.RESULT_OK, data);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.error_message, Toast.LENGTH_SHORT).show();
                                                }
                                                finish();
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Неправильный логин или пароль!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        });
    }
}
