package app.course.Main;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import org.checkerframework.checker.units.qual.C;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.category.Category;
import app.course.category.CategoryAdapter;

public class FragmentGeneral extends Fragment {
    private TextView salary_sum, salary_name, present_sum, present_name, grow_sum, grow_name;

    private ArrayList<Category> categories;
    private CategoryAdapter categories_adapter;
    private ListView category_income_list;

    private List<String> category_names = new ArrayList<String>();
    private List<Integer> category_sum = new ArrayList<Integer>();
    private List<Integer> category_icons = new ArrayList<Integer>();
    private List<Double> category_procents = new ArrayList<Double>();
    private List<String> category_bg = new ArrayList<String>();

    private FrameLayout main;
    private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private ExecutorService executorService = null;
    private Handler handler = null;

    private User user = User.getUser();

    private int count_income = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        main = view.findViewById(R.id.main_layout);

        categories = new ArrayList<Category>();
        category_income_list = view.findViewById(R.id.category_income_list);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        executorService.execute(() ->{
            try {
                connection = dataBaseHandler.connect(connection);
                statement = connection.createStatement();
                resultSet = statement.executeQuery(Queries.getCategory(user));

                if (!resultSet.next()) {
                    Drawable item = AppCompatResources.getDrawable(getContext(), R.drawable.shape_item_bg);
                    item.setTint(Color.parseColor("#747474"));

                    categories.add(new Category(getResources().getDrawable(R.drawable.shape_green_bg, getContext().getTheme()),
                            R.drawable.ic_bag, 0, 0.0, "Зарплата"));
                    categories.add(new Category(getResources().getDrawable(R.drawable.shape_red_bg, getContext().getTheme()),
                            R.drawable.ic_present,0, 0.0, "Подарки"));
                    categories.add(new Category(getResources().getDrawable(R.drawable.shape_sea_bg, getContext().getTheme()),
                            R.drawable.ic_grow, 0, 0.0, "Инвестиции"));
                }

                else {
//                    while (resultSet.next()) {
//                        Drawable item = getResources().getDrawable(R.drawable.shape_item_bg);
//                        item.setTint(resultSet.getInt(4));
//
//                        categories.add(new Category(Color.parseColor(resultSet.getString(4)), resultSet.getInt(3),
//                                resultSet.getInt(1), 5.0, resultSet.getString(2)));
//                    }
                }
            }
            catch (SQLException | ClassNotFoundException e) {
                Log.d("MyLog", e.getMessage() + " " + e.getStackTrace());
            }

            handler.post(() -> {
                categories_adapter = new CategoryAdapter(getActivity(), R.layout.list_item, categories);
                category_income_list.setAdapter(categories_adapter);

                categories.clear();
            });
        });
//        executorService.shutdown();

//        executorService = Executors.newSingleThreadExecutor();
//        executorService1 = Executors.newSingleThreadExecutor();
//
//        handler = new Handler(getActivity().getMainLooper());
//
//        executorService.execute(() ->{
//            try {
//                connection = dataBaseHandler.connect(connection);
//                statement = connection.createStatement();
//                resultSet = statement.executeQuery(Queries.getSumCategory(user));
//            }
//            catch (SQLException | ClassNotFoundException e) {
//                Log.d("MyLog", e.getMessage());
//            }
//
//            handler.post(() ->{
//                try {
//                    if (!resultSet.next()) {
//                        executorService1.execute(() -> {
//                            try {
//                                Log.d("MyLog", "3");
//                                statement.executeUpdate(Queries.setSumCategory(user));
//                            }
//                            catch (SQLException e) {
//                                Log.d("MyLog", e.getMessage() + " " + e.getStackTrace());
//                            }
//                        });
//                        executorService1.shutdown();
//
//                        salary_sum.setText("0$");
//                        present_sum.setText("0$");
//                        grow_sum.setText("0$");
//
//                        salary_name.setText("Зарплата");
//                        present_name.setText("Подарки");
//                        grow_name.setText("Инвестиции");
//                    }
//
//                    else {
//                        Log.d("MyLog", "3");
//                        while (resultSet.next()) {
//                            salary_sum.setText(resultSet.getInt(0));
//                            salary_name.setText(resultSet.getString(1));
//                        }
//                    }
//                }
//                catch (SQLException e) {
//                    Log.d("MyLog", e.getMessage() + " 1");
//                }
//            });
//        });
    }
}
