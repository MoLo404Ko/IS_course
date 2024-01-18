package app.course.history;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.Inflater;

import app.course.R;
import app.course.sub_category.SubCategory;

public class SubHistoryAdapter extends RecyclerView.Adapter<SubHistoryAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LocalDate> key_set;
    private ArrayList<ArrayList<SubCategory>> big_list;
    private int index = 0;
    private int sub_index = 0;
    private int general_size;

    public SubHistoryAdapter(Context context, ArrayList<LocalDate> key_set,
                             ArrayList<ArrayList<SubCategory>> big_list, int general_size) {
        this.context = context;
        this.key_set = key_set;
        this.big_list = big_list;
        this.general_size = general_size;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (sub_index == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_sub_item_with_date,
                    parent,false);
        }

        else {
            if (sub_index == big_list.get(index).size()) {
                sub_index = 0;
                index++;
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_sub_item_with_date, parent,false);
            }

            else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_sub_item, parent, false);
            }
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (sub_index == 0) holder.global_date.setText(String.valueOf(key_set.get(index)));

        holder.bg.setBackground(context.getResources().getDrawable(R.drawable.shape_bg_sub_category, context.getTheme()));
        holder.name.setText(big_list.get(index).get(sub_index).getName());
        holder.sum.setText(big_list.get(index).get(sub_index).getSum());

        sub_index++;
    }

    @Override
    public int getItemCount() {
        return general_size;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout bg;
        TextView name;
        TextView sum;
        TextView global_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bg = itemView.findViewById(R.id.sub_history_layout);
            name = itemView.findViewById(R.id.sub_history_name);
            sum = itemView.findViewById(R.id.sub_history_sum);
            global_date = itemView.findViewById(R.id.sub_history_global_date);
        }
    }
}
