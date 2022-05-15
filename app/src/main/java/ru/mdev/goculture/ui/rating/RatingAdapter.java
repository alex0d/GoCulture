package ru.mdev.goculture.ui.rating;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.mdev.goculture.R;
import ru.mdev.goculture.model.User;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewRow> {

    private ArrayList<User> arrayList;

    public RatingAdapter(ArrayList<User> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item,parent,false);
        return new ViewRow(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewRow holder, int position) {
        holder.number.setText(String.valueOf(position + 1));
        holder.username.setText(arrayList.get(position).getUsername());
        holder.score.setText(String.valueOf(arrayList.get(position).getScore()));
    }

    @Override
    public int getItemCount() {
        if(arrayList.size() > 20){
            return 20;
        }
        return arrayList.size();
    }

    public class ViewRow extends RecyclerView.ViewHolder {

        TextView number;
        TextView username;
        TextView score;

        public ViewRow(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            username = itemView.findViewById(R.id.username);
            score = itemView.findViewById(R.id.score);
        }
    }
}

