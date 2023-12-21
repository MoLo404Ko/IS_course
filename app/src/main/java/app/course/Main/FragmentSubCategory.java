package app.course.Main;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import app.course.Queries;
import app.course.R;
import app.course.authorization.DataBaseHandler;
import app.course.sub_category.SubCategory;

public class FragmentSubCategory extends Fragment {
    private ConstraintLayout main_category;
    private Bundle bundle = new Bundle();
    private ArrayList<Drawable> icons;
    private ArrayList<SubCategory> sub_categories;

    private TextView main_category_name;
    private ImageView main_sub_category_icon;

    private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    public FragmentSubCategory(ArrayList<Drawable> icons) {
        this.icons = icons;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        bundle = this.getArguments();
        main_category = view.findViewById(R.id.main_category_sub_category);
        main_category_name = view.findViewById(R.id.main_sub_category_name);
        main_sub_category_icon = view.findViewById(R.id.main_sub_category_icon);

        setMainCategory(bundle);
    }

    /**
     * Метод, устанавливающий выбранную категорию
     * @param bundle
     */
    private void setMainCategory(Bundle bundle) {
        int color = Color.parseColor((String) bundle.get("color_sub"));
        String name = (String) bundle.get("name_sub");
        Drawable id_icon = icons.get((int)bundle.get("id_icon_sub"));


        main_category.setBackgroundColor(color);
        main_category_name.setText(name);
        main_sub_category_icon.setBackground(id_icon);
    }

    private void setSubListCategories() {
        new Thread(() -> {
            try {
                connection = dataBaseHandler.connect(connection);
                preparedStatement = connection.prepareStatement(Queries.getSubCategories());
                preparedStatement.setInt();
            }
            catch (SQLException | ClassNotFoundException e) {
                Log.d("MyLog", e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}