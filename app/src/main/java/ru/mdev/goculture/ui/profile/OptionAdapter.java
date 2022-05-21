package ru.mdev.goculture.ui.profile;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.stmt.query.In;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import io.getstream.avatarview.AvatarView;
import ru.mdev.goculture.R;
import ru.mdev.goculture.ui.rating.OptionCallback;
import ru.mdev.goculture.ui.rating.RatingAdapter;
import ru.mdev.goculture.ui.rating.RatingFragment;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewRow> {

    private ArrayList<Drawable> icons;
    private ArrayList<Integer> strings;

    private OptionCallback optionCallback;

    public OptionAdapter(OptionCallback optionCallback, ArrayList<Drawable> icons, ArrayList<Integer> strings) {
        this.optionCallback = optionCallback;
        this.icons = icons;
        this.strings = strings;
    }

    @NonNull
    @Override
    public ViewRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_item, parent,false);
        return new OptionAdapter.ViewRow(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewRow holder, int position) {
        holder.optionIcon.setImageDrawable(icons.get(position));
        holder.optionLabel.setText(strings.get(position));

        holder.itemView.setOnClickListener(v -> {
            switch (position) {
                case 0:
                    optionCallback.changeUsername();
                    break;
                case 1:
                    optionCallback.changeEmail();
                    break;
                case 2:
                    optionCallback.confirmEmail();
                case 3:
                    optionCallback.changePassword();
                case 4:
                    optionCallback.signOut();
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public class ViewRow extends RecyclerView.ViewHolder {

        ImageView optionIcon;
        TextView optionLabel;

        public ViewRow(@NonNull View itemView) {
            super(itemView);
            optionIcon = itemView.findViewById(R.id.option_icon);
            optionLabel = itemView.findViewById(R.id.option_label);
        }
    }
}

