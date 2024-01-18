package app.course.category;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.course.Main.FragmentSubCategory;
import app.course.R;
import app.course.sub_category.SubCategory;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private List<Category> categories;
    private Context context;
    private ArrayList<CategoryPrepare> categories_income_prepare;
    private ArrayList<Integer> id_categories;
    private HashMap<Integer, ArrayList<SubCategory>> hash_map_categories;
    private ArrayList<Drawable> icons;
    private FragmentManager fragmentManager;

    public CategoryAdapter(Context context, List<Category> categories, ArrayList<CategoryPrepare> categories_income_prepare,
                           ArrayList<Integer> id_categories, HashMap<Integer, ArrayList<SubCategory>> hash_map_categories,
                           ArrayList<Drawable> icons, FragmentManager fragmentManager) {
        this.context = context;
        this.categories = categories;
        this.categories_income_prepare = categories_income_prepare;
        this.id_categories = id_categories;
        this.hash_map_categories = hash_map_categories;
        this.icons = icons;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.category_layout.setBackground(categories.get(position).getBg_color_category());
        holder.category_icon.setBackground(categories.get(position).getIcon_category());
        holder.category_name.setText(categories.get(position).getName_category());
        holder.category_sum.setText(String.valueOf(categories.get(position).getSum_category()));
        holder.category_percent.setText(String.valueOf(categories.get(position).getCategory_procent()));

        holder.category_layout.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            if (categories_income_prepare != null) {
                CategoryPrepare object = categories_income_prepare.get(position);

                String color = object.getBg_color_category();
                String name = object.getName_category();

                int id_icon = object.getIcon_category();
                int id_category = id_categories.get(position);
                int sum = object.getSum_category();

                bundle.putString("category_name", name);
                bundle.putString("color_sub", color);
                bundle.putString("name_sub", name);
                bundle.putInt("id_icon_sub", id_icon);
                bundle.putInt("id_category", id_category);
                bundle.putInt("sum_sub", sum);

                if (hash_map_categories.get(id_category) != null) {
                    bundle.putSerializable("sub_categories", hash_map_categories.get(id_category));
                }
            }

            FragmentSubCategory fragmentSubCategory = new FragmentSubCategory(icons, position);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().
                    replace(R.id.sub_fragment, fragmentSubCategory);

            fragmentSubCategory.setArguments(bundle);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout category_layout;
        ImageView category_icon;
        TextView category_name;
        TextView category_sum;
        TextView category_percent;
        public MyViewHolder(@NonNull View view) {
            super(view);

            category_layout = view.findViewById(R.id.category_layout);
            category_icon = view.findViewById(R.id.category_icon);
            category_name = view.findViewById(R.id.category_name);
            category_sum = view.findViewById(R.id.category_sum);
            category_percent = view.findViewById(R.id.category_procent);
        }
    }
}
