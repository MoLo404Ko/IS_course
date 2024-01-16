package app.course.history;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import app.course.Constants;
import app.course.R;
import app.course.add_new_item.NewItemAdapter;
import app.course.category.CategoryAdapter;
import app.course.category.CategoryPrepare;
import app.course.sub_category.SubCategory;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<History> list;
    private FragmentManager fragmentManager;
    private FragmentHistory fragmentHistory;
    private ArrayList<CategoryPrepare> categories_income;

    private HashMap<Integer, ArrayList<SubCategory>> map;

    public HistoryAdapter(Context context, ArrayList<History> list, FragmentManager fragmentManager,
                          FragmentHistory fragmentHistory, ArrayList<CategoryPrepare> categories_income) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
        this.fragmentHistory = fragmentHistory;
        this.categories_income = categories_income;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        map = fragmentHistory.getMap();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int color = Color.parseColor(list.get(position).getBg_color());

        Drawable item = context.getResources().getDrawable(R.drawable.shape_item_bg, context.getTheme());
        item.setTint(color);

        Drawable icon = context.getResources().getDrawable(Constants.INCOME_CATEGORIES[list.get(position).getIcon()], context.getTheme());

        holder.bg.setBackground(item);
        holder.icon.setBackground(icon);
        holder.name.setText(list.get(position).getName_sub_category());

        holder.bg.setOnClickListener(view -> {
            Bundle args = new Bundle();

            ArrayList<SubCategory> sub_categories = map.get(list.get(position).getId_category());

            args.putSerializable("sub_categories", sub_categories);
            args.putSerializable("categories_income", categories_income);
            args.putInt("id_category", list.get(position).getId_category());


            SubHistoryFragment subHistoryFragment = new SubHistoryFragment();
            subHistoryFragment.setArguments(args);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, subHistoryFragment);

            fragmentTransaction.detach(fragmentHistory);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout bg;
        ImageView icon;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bg = itemView.findViewById(R.id.category_layout);
            icon = itemView.findViewById(R.id.category_icon);
            name = itemView.findViewById(R.id.category_name);
        }
    }
}
