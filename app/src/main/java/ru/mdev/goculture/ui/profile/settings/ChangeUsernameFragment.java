package ru.mdev.goculture.ui.profile.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mdev.goculture.R;


public class ChangeUsernameFragment extends Fragment {

    public static ChangeUsernameFragment newInstance() {
        return new ChangeUsernameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_username, container, false);
    }
}