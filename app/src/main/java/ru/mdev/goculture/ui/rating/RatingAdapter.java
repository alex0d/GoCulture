package ru.mdev.goculture.ui.rating;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;
import io.getstream.avatarview.AvatarView;
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

        ImageLoader imageLoader = Coil.imageLoader(holder.itemView.getContext());
        ImageRequest request = new ImageRequest.Builder(holder.itemView.getContext())
                .data(arrayList.get(position).getAvatarUrl())
                .crossfade(true)
                .transformations(new CircleCropTransformation())
                .target(holder.avatarView)
                .build();
        imageLoader.enqueue(request);

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
        AvatarView avatarView;
        TextView score;

        public ViewRow(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            username = itemView.findViewById(R.id.username);
            avatarView = itemView.findViewById(R.id.avatar_view);
            score = itemView.findViewById(R.id.score);
        }
    }
}

