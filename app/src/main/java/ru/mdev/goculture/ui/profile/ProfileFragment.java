package ru.mdev.goculture.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ru.mdev.goculture.R;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    private TextView testTextView;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        testTextView = view.findViewById(R.id.test_text_view);
        testTextView.setText(mAuth.getCurrentUser().getEmail());

        return view;
    }
}
