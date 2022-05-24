package ru.mdev.goculture.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;
import io.getstream.avatarview.AvatarView;
import ru.mdev.goculture.InformationActivity;
import ru.mdev.goculture.R;
import ru.mdev.goculture.model.User;
import ru.mdev.goculture.ui.login.LoginActivity;
import ru.mdev.goculture.ui.profile.settings.ChangeEmailActivity;
import ru.mdev.goculture.ui.profile.settings.ChangePasswordActivity;
import ru.mdev.goculture.ui.profile.settings.ChangeUsernameActivity;
import ru.mdev.goculture.ui.rating.OptionCallback;

public class ProfileFragment extends Fragment implements OptionCallback {

    private static final String TAG = "ProfileFragment";
    private Context context;

    private FirebaseAuth mAuth;
    private StorageReference storageRef;

    private User currentUser;

    private Toolbar toolbar;
    private AvatarView avatarView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView scoreTextView;

    private final ActivityResultLauncher<Intent> registerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() == null) {
                        return;
                    }
                    Intent data = result.getData();
                    switch (data.getStringExtra("setting")) {
                        case "changeUsername":
                            usernameTextView.setText(data.getStringExtra("username"));
                            break;
                        case "changeEmail":
                            emailTextView.setText(data.getStringExtra("email"));
                            break;
                        default:
                            break;
                    }
                }
            }
    );

    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), this::onCropImageResult);

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("avatars");

        FirebaseDatabase.getInstance().getReference("Users").
                child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Got current user instance");
                currentUser = snapshot.getValue(User.class);
                updateProfileInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Current user database error: " + error.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = inflater.getContext();

        toolbar = view.findViewById(R.id.profile_toolbar);
        toolbar.inflateMenu(R.menu.profile_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.about_app) {
                    startActivity(new Intent(context, InformationActivity.class));
                }
                return false;
            }
        });

        avatarView = view.findViewById(R.id.avatar_view);
        avatarView.setOnClickListener(v -> changeUserAvatar());

        usernameTextView = view.findViewById(R.id.username);

        emailTextView = view.findViewById(R.id.email);

        scoreTextView = view.findViewById(R.id.score);

        OptionAdapter optionAdapter = buildOptionAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.options_recycler);
        recyclerView.setAdapter(optionAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    private void updateProfileInfo() {
        loadAvatarView(currentUser.getAvatarUrl());
        usernameTextView.setText(currentUser.getUsername());
        emailTextView.setText(currentUser.getEmail());
        scoreTextView.setText(getString(R.string.user_score, String.valueOf(currentUser.getScore())));
    }

    private void loadAvatarView(String data) {
        ImageLoader imageLoader = Coil.imageLoader(context);
        ImageRequest request = new ImageRequest.Builder(context)
                .data(data)
                .crossfade(true)
                .transformations(new CircleCropTransformation())
                .target(avatarView)
                .build();
        imageLoader.enqueue(request);
    }

    private void changeUserAvatar() {
        CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions())
                .setAspectRatio(1, 1)
                .setRequestedSize(600, 600)
                .setCropShape(CropImageView.CropShape.OVAL);

        cropImage.launch(options);
    }

    private void uploadImageToDatabase(@NonNull String uri) {
        final StorageReference ref = storageRef.child("avatar_" + mAuth.getCurrentUser().getUid());

        ref.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User image uploaded successfully");

                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(mAuth.getCurrentUser().getUid())
                                .child("avatarUrl").setValue(task.getResult().toString());
                        currentUser.setAvatarUrl(task.getResult().toString());

                        Log.d(TAG, "Download link received successfully");
                        Toast.makeText(context, "Фото профиля обновлено!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "User image upload failed");
            }
        });
    }

    private void handleCropImageResult(@NonNull String uri) {
        loadAvatarView(uri);
        uploadImageToDatabase(uri);
    }

    private void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            handleCropImageResult(Objects.requireNonNull(result.getUriContent())
                    .toString()
                    .replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            Log.d(TAG, "Cropping image was cancelled by the user");
        } else {
            Log.d(TAG, "Cropping image failed");
        }
    }

    private OptionAdapter buildOptionAdapter() {
        ArrayList<Drawable> icons = new ArrayList<>();
        ArrayList<Integer> strings = new ArrayList<>();

        icons.add(AppCompatResources.getDrawable(context, R.drawable.baseline_rename_outline_24));
        strings.add(R.string.change_username);

        icons.add(AppCompatResources.getDrawable(context, R.drawable.baseline_mail_outline_24));
        strings.add(R.string.change_email);

        icons.add(AppCompatResources.getDrawable(context, R.drawable.baseline_check_24));
        strings.add(R.string.confirm_email);

        icons.add(AppCompatResources.getDrawable(context, R.drawable.baseline_password_24));
        strings.add(R.string.change_password);

        icons.add(AppCompatResources.getDrawable(context, R.drawable.baseline_logout_24));
        strings.add(R.string.sign_out);

        return new OptionAdapter(this, icons, strings);
    }

    @Override
    public void changeUsername() {
        Intent changeUsernameIntent = new Intent(context, ChangeUsernameActivity.class);
        registerActivityResultLauncher.launch(changeUsernameIntent);
    }

    @Override
    public void changeEmail() {
        Intent changeEmailIntent = new Intent(context, ChangeEmailActivity.class);
        registerActivityResultLauncher.launch(changeEmailIntent);
    }

    @Override
    public void confirmEmail() {
        if (mAuth.getCurrentUser().isEmailVerified()) {
            Toast.makeText(context, "Почта уже подтверждена!", Toast.LENGTH_LONG).show();
        } else {
            mAuth.getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(task -> Toast.makeText(context, "Письмо подтверждения выслано!", Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void changePassword() {
        Intent changePasswordIntent = new Intent(context, ChangePasswordActivity.class);
        registerActivityResultLauncher.launch(changePasswordIntent);
    }

    @Override
    public void signOut() {
        mAuth.signOut();
        startActivity(new Intent(context, LoginActivity.class));
        getActivity().finish();
    }

}
