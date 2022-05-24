package ru.mdev.goculture.ui.profile.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mdev.goculture.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private final static String TAG = "ChangePasswordActivity";

    private FirebaseAuth mAuth;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText oldPasswordEditText = findViewById(R.id.old_password);
        EditText newPasswordEditText = findViewById(R.id.new_password);

        Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(v -> finish());

        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();

            if (oldPassword.isEmpty()) {
                oldPasswordEditText.setError(getResources().getString(R.string.password_empty));
                oldPasswordEditText.requestFocus();
                return;
            }
            if (newPassword.isEmpty()) {
                newPasswordEditText.setError(getResources().getString(R.string.password_empty));
                newPasswordEditText.requestFocus();
                return;
            }
            if (newPassword.length() < 6) {
                newPasswordEditText.setError(getResources().getString(R.string.password_too_short));
                newPasswordEditText.requestFocus();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(mAuth.getCurrentUser().getEmail(), oldPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Пароль изменён!", Toast.LENGTH_SHORT).show();
                                                    setResult(Activity.RESULT_OK);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.error_message, Toast.LENGTH_SHORT).show();
                                                }
                                                finish();
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Старый пароль введен неверно!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        });
    }
}
