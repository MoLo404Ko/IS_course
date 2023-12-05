package app.course.Main;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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

public class FragmentGeneral extends Fragment {
    private TextView salary_sum, salary_name, present_sum, present_name, grow_sum, grow_name;

    private ListView category_list;
    private List<String> category_names = new ArrayList<String>();
    private List<Integer> category_sum = new ArrayList<Integer>();
    private List<String> category_icons = new ArrayList<String>();


    private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private ExecutorService executorService = null;
    private ExecutorService executorService1 = null;
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

//        salary_category.setOnClickListener(v -> {
//        });

        return view;
    }

    private void init(View view) {
//        executorService = Executors.newSingleThreadExecutor();
//
//        executorService.execute(() ->{
//            try {
//                connection = dataBaseHandler.connect(connection);
//                statement = connection.createStatement();
//                resultSet = statement.executeQuery(Queries.getCategory(user));
//
//                if (!resultSet.next()) {
//                    new Thread(() -> {
//                        try {
//                            statement.executeUpdate(Queries.setDefSumCategory(user));
//
//                            category_names.add("Зарплата");
//                            category_names.add("Зарплата");
//                            category_names.add("Зарплата");
//
//                            category_sum.add(0);
//                            category_sum.add(0);
//                            category_sum.add(0);
//                        }
//                        catch (SQLException e) {
//                            Log.d("MyLog", e.getMessage() + " " + e.getStackTrace());
//                        }
//                    });

//                    category_names.add("Зарплата");
//                    category_names.add("Зарплата");
//                    category_names.add("Зарплата");
//
//                    category_sum.add(0);
//                    category_sum.add(0);
//                    category_sum.add(0);
//
//                    category_icons.add("R.drawable.ic_bag");
//                    category_icons.add("R.drawable.ic_present");
//                    category_icons.add("R.drawable.ic_grow");
//                }
//
//                else {
//                    while (resultSet.next()) {
//                        category_sum.add(resultSet.getInt(1));
//                        category_names.add(resultSet.getString(2));
//                        category_icons.add(resultSet.getString(3));
//                    }
//                }
//            }
//            catch (SQLException | ClassNotFoundException e) {
//                Log.d("MyLog", e.getMessage() + " " + e.getStackTrace());
//            }
//        });


//        for (int i = 0; i < category_names.size(); i++) {
//            category_date.add(new Category(category_icons.get(i), category_sum.get(i),
//                    category_names.get(i)));
//        }

//        salary_sum = view.findViewById(R.id.salary_sum);
//        present_sum = view.findViewById(R.id.present_sum);
//        grow_sum = view.findViewById(R.id.grow_sum);
//
//        salary_name = view.findViewById(R.id.salary_name);
//        present_name = view.findViewById(R.id.present_name);
//        grow_name = view.findViewById(R.id.grow_name);

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
