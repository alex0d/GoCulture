package ru.mdev.goculture.ui.profile;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.mdev.goculture.R;
import ru.mdev.goculture.ui.rating.OptionCallback;

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
                    break;
                case 3:
                    optionCallback.changePassword();
                    break;
                case 4:
                    optionCallback.signOut();
                    break;
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
