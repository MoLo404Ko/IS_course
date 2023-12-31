package app.course.sub_category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import app.course.R;
import app.course.category.Category;


public class SubAdapter extends ArrayAdapter<SubCategory> {
    private LayoutInflater inflater;
    private int layout;
    private List<SubCategory> categories;

    public SubAdapter(Context context, int resource, List<SubCategory> categoryList) {
        super(context, resource, categoryList);
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.categories = categoryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(this.layout, parent, false);

        ConstraintLayout category_layout = view.findViewById(R.id.sub_category_layout);
        TextView category_name = view.findViewById(R.id.sub_category_name);
        TextView category_sum = view.findViewById(R.id.sub_category_sum);
        TextView category_date = view.findViewById(R.id.sub_category_date);

        SubCategory category = categories.get(position);

        category_layout.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.shape_bg_sub_category));
        category_name.setText(category.getName());
        category_sum.setText(category.getSum());
        category_date.setText(category.getDate_last_entry());

        return view;
    }
}
