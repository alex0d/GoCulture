package ru.mdev.goculture.ui.rating;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.mdev.goculture.R;
import ru.mdev.goculture.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment {

    private Context context;
    private RatingAdapter ratingAdapter;
    private String[] usernameUsers = new String[]{"Clouds",
            "Sun",
            "Partial clouds",
            "Snow",
            "Sleet",
            "Mist",
            "Clear",
            "Rain",
            "Rain thunder",
            "Fog",
            "Partial clouds",
            "Snow",
            "Sleet",
            "Mist",
            "Clear",
            "Rain",
            "Sleet",
            "Mist",
            "Clear",
            "Sleet",
            "Mist",
            "Clear",};
    private int[] scoreUsers = new int[]{123,
            43,
            23,
            54,
            7685,
            765,
            65,
            98,
            0,
            8634,
            23,
            54,
            7685,
            765,
            65,
            98,
            0,
            23,
            54,
            7685,
            765,
            65,
            98,
            0,
            23};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RatingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingFragment newInstance(String param1, String param2) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = inflater.getContext();
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rating_list);
        ratingAdapter = new RatingAdapter(generateData());
        recyclerView.setAdapter(ratingAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
    }

    private ArrayList<User> generateData() {
        ArrayList<User> list = new ArrayList<>();
        for(int i=0;i<usernameUsers.length;i++) {
            list.add(new User(usernameUsers[i], scoreUsers[i]));
        }
        Collections.sort(list, (user1, user2) -> user2.getScore() - user1.getScore());
        return list;
    }
}
