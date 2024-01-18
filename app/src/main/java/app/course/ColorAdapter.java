package app.course;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.MyViewHolder> {
    private ArrayList<String> colors;
    private Context context;
    private DialogAddIncomeCategory dialogAddIncomeCategory = DialogAddIncomeCategory.getDialogAddIncomeCategory();
    private int selected_pos = 0;


    public ColorAdapter(ArrayList<String> colors, Context context) {
        this.colors = colors;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors.get(position))));
        int color = Color.parseColor(colors.get(position));
        holder.itemView.setBackgroundColor(selected_pos == position ? color : Color.TRANSPARENT);

        holder.img.setOnClickListener(view -> {
            dialogAddIncomeCategory.setIndex_color(position);
            dialogAddIncomeCategory.setHasColor(true);

            notifyItemChanged(selected_pos);
            selected_pos = holder.getAdapterPosition();
            notifyItemChanged(selected_pos);
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout layout;
        ImageButton img;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.color);
            layout = itemView.findViewById(R.id.big_circle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;
//
//            notifyItemChanged(selected_pos);
//            selected_pos = getAdapterPosition();
//            notifyItemChanged(selected_pos);
        }
    }
}
