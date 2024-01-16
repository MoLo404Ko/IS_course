package app.course.add_new_item;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.course.ColorAdapter;
import app.course.R;

public class NewItemAdapter extends RecyclerView.Adapter<NewItemAdapter.MyViewHolder> {
    private ArrayList<Drawable> icons;
    private ArrayList<String> names;

    public NewItemAdapter(ArrayList<Drawable> icons, ArrayList<String> names) {
        this.icons = icons;
        this.names = names;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_add_item, parent, false);
        return new NewItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.icon.setImageDrawable(icons.get(position));
        holder.name.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.new_add_item_iv);
            name = itemView.findViewById(R.id.new_add_item_tv);
        }
    }
}
