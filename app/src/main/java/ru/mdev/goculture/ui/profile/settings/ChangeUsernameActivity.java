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

public class ChangeUsernameActivity extends AppCompatActivity {

    private final static String TAG = "ChangeUsernameActivity";

    private FirebaseAuth mAuth;

    private User currentUser;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

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

        EditText newUsernameEditText = findViewById(R.id.new_username);

        Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(v -> finish());

        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> {
            String newUsername = newUsernameEditText.getText().toString().trim();

            if (newUsername.isEmpty()) {
                newUsernameEditText.setError(getResources().getString(R.string.username_empty));
                newUsernameEditText.requestFocus();
                return;
            }

            currentUser.setUsername(newUsername);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(currentUser)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Имя пользователя успешно обновлено!", Toast.LENGTH_SHORT).show();

                            Intent data = new Intent();
                            data.putExtra("setting", "changeUsername");
                            data.putExtra("username", newUsername);
                            setResult(Activity.RESULT_OK, data);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_message, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    });
        });
    }
}