package app.course.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import app.course.R;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private LayoutInflater inflater;
    private int layout;
    private List<Category> categories;

    public CategoryAdapter(Context context, int resource, List<Category> categories) {
        super(context, resource, categories);
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(this.layout, parent, false);

        ConstraintLayout category_layout = view.findViewById(R.id.category_layout);
        ImageView category_icon = view.findViewById(R.id.category_icon);
        TextView category_name = view.findViewById(R.id.category_name);
        TextView category_sum = view.findViewById(R.id.category_sum);
        TextView category_procent = view.findViewById(R.id.category_procent);

        Category category = categories.get(position);

        category_layout.setBackground(category.getBg_color_category());
        category_icon.setBackground(category.getIcon_category());
        category_name.setText(category.getName_category());
        category_sum.setText(String.valueOf(category.getSum_category()));
        category_procent.setText(String.valueOf(category.getCategory_procent()));

        return view;
    }
}
