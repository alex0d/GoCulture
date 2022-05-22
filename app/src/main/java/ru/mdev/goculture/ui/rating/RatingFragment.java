package ru.mdev.goculture.ui.rating;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import ru.mdev.goculture.R;
import ru.mdev.goculture.model.User;

public class RatingFragment extends Fragment {

    private DatabaseReference databaseReference;
    private Query scoreDatabaseOrder;
    private ChildEventListener scoreChildEventListener;

    private RatingAdapter ratingAdapter;

    private ArrayList<User> users = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ratingAdapter = new RatingAdapter(users);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        scoreDatabaseOrder = databaseReference.orderByChild("score").limitToLast(20);
        scoreChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                users.add(0, user);
                ratingAdapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User userChanged = snapshot.getValue(User.class);
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getUsername().equals(userChanged.getUsername())) {
                        users.set(i, userChanged);
                        ratingAdapter.notifyItemChanged(i);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("DatabaseOrder", "onChildRemoved: " + snapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("DatabaseOrder", "onChildMoved: " + previousChildName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DatabaseOrder", "onCancelled");
            }
        };
        scoreDatabaseOrder.addChildEventListener(scoreChildEventListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rating_list);
        recyclerView.setAdapter(ratingAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        scoreDatabaseOrder.removeEventListener(scoreChildEventListener);
    }
}
