package app.course.sub_category;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import app.course.ColorAdapter;
import app.course.DialogAddIncomeCategory;
import app.course.R;
import app.course.category.Category;


public class SubAdapter extends RecyclerView.Adapter<SubAdapter.MyViewHolder> {
    private ArrayList<SubCategory> categories;
    private Context context;
    private FragmentManager fragmentManager;

    public SubAdapter(Context context, ArrayList<SubCategory> categories, FragmentManager fragmentManager) {
        this.categories = categories;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sub_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.constraintLayout.setBackground(context.getResources().getDrawable(R.drawable.shape_bg_sub_category, context.getTheme()));
        holder.category_name.setText(categories.get(position).getName());
        holder.category_sum.setText(categories.get(position).getSum());
        holder.category_date.setText(categories.get(position).getDate_last_entry());

        holder.constraintLayout.setOnClickListener(view -> {
            DialogAddSum dialogAddSum = new DialogAddSum();
            Bundle args = new Bundle();

            args.putParcelable("object", categories.get(position));
            args.putInt("pos", position);
            args.putParcelableArrayList("categories", categories);

            dialogAddSum.setArguments(args);
            dialogAddSum.show(fragmentManager, "tag");
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        TextView category_name;
        TextView category_sum;
        TextView category_date;
        public MyViewHolder(View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.sub_category_layout);
            category_date = itemView.findViewById(R.id.sub_category_date);
            category_sum = itemView.findViewById(R.id.sub_category_sum);
            category_name = itemView.findViewById(R.id.sub_category_name);

        }
    }
}
