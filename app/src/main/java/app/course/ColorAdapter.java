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

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.MyViewHolder> {
    private ArrayList<String> colors;
    private Context context;

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
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton img;
        Bundle bundle = new Bundle();
        DialogAddIncomeCategory dialogAddIncomeCategory = DialogAddIncomeCategory.getDialogAddIncomeCategory();
        public MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.color);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddIncomeCategory.setIndex_color(getAdapterPosition());
                    dialogAddIncomeCategory.setHasColor(true);
                }
            });
        }
    }
}
