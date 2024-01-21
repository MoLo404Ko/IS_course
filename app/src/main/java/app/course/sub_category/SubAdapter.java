package app.course.sub_category;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.course.R;
import app.course.category.Category;
import app.course.category.CategoryPrepare;


public class SubAdapter extends RecyclerView.Adapter<SubAdapter.MyViewHolder> {
    private ArrayList<SubCategory> sub_categories;
    private CategoryPrepare category;
    private Context context;
    private FragmentManager fragmentManager;


    public SubAdapter(Context context, ArrayList<SubCategory> sub_categories, FragmentManager fragmentManager, CategoryPrepare category) {
        this.sub_categories = sub_categories;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.category = category;
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
        holder.category_name.setText(sub_categories.get(position).getName());
        holder.category_sum.setText(sub_categories.get(position).getSum());

        holder.constraintLayout.setOnClickListener(view -> {
            DialogAddSum dialogAddSum = new DialogAddSum();
            Bundle args = new Bundle();

            args.putParcelable("category", category);
            args.putParcelable("object", sub_categories.get(position));
            args.putInt("pos", position);
            args.putParcelableArrayList("categories", sub_categories);
            args.putString("sub_category_name", String.valueOf(holder.category_name.getText().toString()));
            args.putInt("sub_category_sum", Integer.parseInt(holder.category_sum.getText().toString()));

            Log.d("MyLog", "----------------");

            Log.d("MyLog", category.getSum_category() + " sum");
            Log.d("MyLog", sub_categories.get(position).getSum() + " sub_sum");
            Log.d("MyLog", position + " pos");


            Log.d("MyLog", "----------------");


            dialogAddSum.setArguments(args);
            dialogAddSum.show(fragmentManager, "tag");
        });
    }

    @Override
    public int getItemCount() {
        return sub_categories.size();
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
