package app.course.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.add_new_item.DialogAddNewItem;
import app.course.authorization.DataBaseHandler;
import app.course.category.Category;
import app.course.category.CategoryPrepare;
import app.course.history.FragmentHistory;
import app.course.income.FragmentIncome;
import app.course.menu_fragments.AmountsFragment;
import app.course.menu_fragments.CategoryFragment;
import app.course.menu_fragments.HomeFragment;
import app.course.menu_fragments.SettingsFragment;
import app.course.sub_category.SubCategory;


public class MainActivity extends AppCompatActivity {
    private DecimalFormat df = new DecimalFormat("#.#");
    private static MainActivity mainActivity = new MainActivity();
    private Handler handler;
    private int income_sum;
    private int press_count = 0;
    private long start_time;

    private Bundle extras = null;

    private ConstraintLayout main_layout;
    private ScrollView main_fragment;
    private LinearLayout shadow_layout, buttons_linear;
    private PieChart pieChart;
    private RelativeLayout pieChart_layout;
    private FrameLayout sub_fragment;
    private LinearLayout date_picker_shadow;

    private RelativeLayout all_time, one_day, week, month, range, year;

    private FragmentTransaction fragmentTransaction = null;
    private FragmentGeneral fragmentGeneral = null;
    private FragmentIncome fragmentIncome = null;

    private Button datePicker;
    private ImageButton add_button, minus_button, history_button;
    private Spinner dropDown;
    private TextView total_sum;
    private TextView income_tv;
    private TextView general_tv;

    private BottomNavigationView bottomNavigationView;

    private User user = User.getUser();
    private DataBaseHandler db = DataBaseHandler.getDataBaseHadler();
    private Connection conn = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    private ArrayList<SubCategory> sub_categories;
    private ArrayList<CategoryPrepare> categories_income;
    private ArrayList<CategoryPrepare> categories_expense;

    private ArrayList<Drawable> icons_income;
    private ArrayList<Integer> id_categories = new ArrayList<>();
    private String full_date = "";
    private String request_date_first = "";
    private String request_date_second = "";

    public static String date;
    private ArrayList<SubCategory>[] save_array = new ArrayList[1];

    private static HashMap<Integer, ArrayList<SubCategory>> map_of_sub_categories;
    private static HashMap<LocalDate, ArrayList<SubCategory>> map_of_history;

    private NavigationBarView.OnItemSelectedListener listener_nav = item -> {
        switch (item.getItemId()) {
            case R.id.home_menu:
                HomeFragment.newInstance();
                return true;
            case R.id.category_menu: {
                CategoryFragment.newInstance();
                return true;
            }
            case R.id.amounts_menu: {
                AmountsFragment.newInstance();
                return true;
            }
            case R.id.settings_menu: {
                SettingsFragment.newInstance();
                return true;
            }
        }

        return false;
    };

    /**
     * Обработка нажатия на системную клавишу back
     * @param keyCode
     * @param event
     * @return true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        press_count++;

        if (keyCode == KeyEvent.KEYCODE_BACK && press_count == 1) {
            Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
            start_time = System.currentTimeMillis();
            press_count++;
        }

        if (System.currentTimeMillis() - start_time <= 2000 && press_count == 3) finish();
        else if (System.currentTimeMillis() - start_time > 2000 && press_count == 3) {
            Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
            start_time = System.currentTimeMillis();
            press_count = 2;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().setFragmentResultListener("backBtn", this,
                (requestKey, result) -> {

            shadow_layout.setVisibility(View.VISIBLE);
            buttons_linear.setVisibility(View.VISIBLE);
            pieChart_layout.setVisibility(View.VISIBLE);
            dropDown.setVisibility(View.VISIBLE);
            datePicker.setVisibility(View.VISIBLE);
            total_sum.setVisibility(View.VISIBLE);
            date_picker_shadow.setVisibility(View.VISIBLE);
            main_fragment.setVisibility(View.VISIBLE);

            fragmentGeneral = new FragmentGeneral(getBaseContext(), getSupportFragmentManager());

            categories_income = (ArrayList<CategoryPrepare>)getIntent().getSerializableExtra("categories_income");
            categories_expense = (ArrayList<CategoryPrepare>)getIntent().getSerializableExtra("categories_expense");
            id_categories = getIntent().getIntegerArrayListExtra("id_categories");

            Bundle args = new Bundle();

            args.putSerializable("categories_income", categories_income);
            args.putSerializable("categories_expense", categories_expense);
            args.putIntegerArrayList("id_categories", id_categories);
            fragmentGeneral.setArguments(args);

            fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentGeneral);
            fragmentTransaction.commit();
        });

        getSupportFragmentManager().setFragmentResultListener("change_diff_sum", this,
                (requestKey, result) -> {
            boolean change_date = result.getBoolean("change_date");
            // Переменная сложения или вычитания, false - вычитание, true - сложение
            boolean action = result.getBoolean("action");
            int current_sum = 0;

            if (!total_sum.getText().toString().equals("")) {
                current_sum = Integer.parseInt(total_sum.getText().toString().replace("$", ""));
            }

            int diff_sum = result.getInt("sum");

            if (!change_date) {
                if (action) current_sum += diff_sum;
                else current_sum -= diff_sum;
            }
            else current_sum = diff_sum;

            Log.d("MyLog", current_sum + " ");
            total_sum.setText(current_sum + "$");
                });

        /**
         * Изменение списка мапы попдкатегорий при удалении или добавлении подкатегории и категории
         */
        getSupportFragmentManager().setFragmentResultListener("edit_map_of_sub_categories_by", this,
                (requestKey, result) -> {
            int key = result.getInt("key");
            boolean isSubCategory = result.getBoolean("isSubCategory");
            boolean remove = result.getBoolean("remove");
            SubCategory object = null;

            if (result.getParcelable("object") != null) {
                object = result.getParcelable("object");
            }

            save_array[0] = map_of_sub_categories.get(key);

            if (isSubCategory) {
                if (!remove) {
                    if (!map_of_sub_categories.containsKey(key)) {
                        ArrayList<SubCategory> list = new ArrayList<>();
                        list.add(object);
                        map_of_sub_categories.put(key, list);
                    }
                    else {
                        // тут происходит магия, я не понимаю почему, но оно работает
                        // скорее всего в список обратно добавляется item ,но не здесь и я не знаю где :(
//                        Objects.requireNonNull(map_of_sub_categories.get(key)).add(object);
                    }
                }
                else {
                    Objects.requireNonNull(map_of_sub_categories.get(key)).remove(object);
                }
            }

            else {
                if(!remove) map_of_sub_categories.put(key, save_array[0]);
                else map_of_sub_categories.remove(key);
            }
            setMap_of_sub_categories(map_of_sub_categories);
                });

        try {
            init();
        } catch (SQLException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        getSupportFragmentManager().setFragmentResultListener("addNewItemHistory", this,
                (requestKey, result) -> {
                    SubCategory object = (SubCategory) result.getParcelable("object");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    LocalDate key = LocalDate.parse(object.getDate_last_entry(), formatter);

                    if (map_of_history.containsKey(key)) {
                        Log.d("MyLog", "hasKey");
                        Objects.requireNonNull(map_of_history.get(key)).add(object);
                    }
                    else {
                        Log.d("MyLog", "hasn'tKey");
                        ArrayList<SubCategory> list = new ArrayList<>();
                        list.add(object);
                        map_of_history.put(key, list);
                    }

                    mainActivity.setMap_of_history(map_of_history);
                });

        setOnClickHistoryBtn();

        datePicker.setOnClickListener(view -> {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_dialog_date);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            one_day = dialog.findViewById(R.id.one_day_btn);
            range = dialog.findViewById(R.id.range_btn);
            all_time = dialog.findViewById(R.id.all_time_btn);


            one_day.setOnClickListener(view_1 -> setDatePicker(view_1, dialog));
            range.setOnClickListener(view_2 -> setDatePicker(view_2, dialog));
            all_time.setOnClickListener(view_5 -> setDatePicker(view_5, dialog));

            dialog.show();
        });

        add_button.setOnClickListener(view -> {
            Bundle args = new Bundle();

            DialogAddNewItem dialogAddNewItem = new DialogAddNewItem();
            args.putSerializable("incomes_list", categories_income);

            dialogAddNewItem.setArguments(args);
            dialogAddNewItem.show(getSupportFragmentManager(), "tag");
        });

    }

    public void init() throws SQLException, ExecutionException, InterruptedException {
        handler = new Handler(Looper.getMainLooper());
        add_button = findViewById(R.id.add_btn);
        history_button = findViewById(R.id.history_btn);

        total_sum = findViewById(R.id.total_sum);
        mainActivity.setTotal_sum(total_sum);

        sub_fragment = findViewById(R.id.sub_fragment);
        extras = getIntent().getExtras();

        main_layout = findViewById(R.id.main_layout);
        buttons_linear = findViewById(R.id.buttons_linear);

        main_fragment = findViewById(R.id.main_fragment);
        date_picker_shadow = findViewById(R.id.date_picker_shadow);
        shadow_layout = findViewById(R.id.shadow_layout);

        pieChart = new PieChart(this);
        pieChart.setId(View.generateViewId());
        pieChart_layout = findViewById(R.id.piechart_layout);

        setHeightFragment(main_fragment, shadow_layout);

        datePicker = findViewById(R.id.date_picker);

        datePicker.setText(R.string.all_time_date);
        MainActivity.date = datePicker.getText().toString();

        income_tv = findViewById(R.id.income_tv);
        general_tv = findViewById(R.id.general_tv);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(listener_nav);

        setDropDown();

        fragmentGeneral = new FragmentGeneral(getBaseContext(), getSupportFragmentManager());

        categories_income = (ArrayList<CategoryPrepare>)getIntent().getSerializableExtra("categories_income");
        categories_expense = (ArrayList<CategoryPrepare>)getIntent().getSerializableExtra("categories_expense");

        sub_categories = getSubCategories();
        mainActivity.setSub_categories(sub_categories);
        map_of_sub_categories = setHashMap();
        map_of_history = get_map_of_history();

        id_categories = getIntent().getIntegerArrayListExtra("id_categories");

        Bundle args = new Bundle();

        args.putSerializable("categories_income", categories_income);
        args.putSerializable("categories_expense", categories_expense);
        args.putIntegerArrayList("id_categories", id_categories);
        fragmentGeneral.setArguments(args);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragmentGeneral);
        fragmentTransaction.commit();

        clickLinearBtns();
        setPieChart();
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Метод установки списка счетов пользователя
     */
    private void setDropDown() {
        dropDown = findViewById(R.id.amounts_drop_down);

        List<String> amounts;
        amounts = extras.getStringArrayList("amounts");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, amounts);

        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        dropDown.setAdapter(adapter);
        mainActivity.setDropDown(dropDown);
    }
    // ---------------------------------------------------------------------------------------------

    private void setDatePicker(View view, Dialog dialog) {
        request_date_first = "";
        request_date_second = "";

        switch (view.getId()) {
            case R.id.range_btn: {
                DatePickerDialog firstDate = new DatePickerDialog(MainActivity.this);

                firstDate.setOnDateSetListener((dateP, i, i1, i2) -> {
                    String month1 = "";
                    String day1 = "";

                    if (i1 + 1 < 10) month1 = "0" + (i1 + 1);
                    else month1 = String.valueOf(i1 + 1);

                    if (i2 < 10) day1 = "0" + i2;
                    else day1 = String.valueOf(i2);

                    request_date_first = i + "-" + (i1 + 1) + "-" + i2;
                    full_date += day1 + "-" + month1 + "-" + i + ":";

                    DatePickerDialog secondDate = new DatePickerDialog(MainActivity.this);

                    secondDate.setOnDateSetListener((dateP1, year, i11, i21) -> {
                        String month2 = "";
                        String day2 = "";

                        if (i11 + 1 < 10) month2 = "0" + (i11 + 1);
                        else month2 = String.valueOf(i11 + 1);

                        if (i21 < 10) day2 = "0" + i21;
                        else day2 = String.valueOf(i21);

                        request_date_second = year + "-" + (i11 + 1) + "-" + i21;
                        full_date += day2 + "-" + month2 + "-" + year;
                        datePicker.setText(full_date);

                        setSumOfCategory(0);
                        MainActivity.date = full_date;
                        full_date = "";
                    });
                    secondDate.show();
                });
                firstDate.show();

                break;
            }

            case R.id.one_day_btn: {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                datePickerDialog.setOnDateSetListener((dateP, i, i1, i2) -> {
                    String month = "";
                    String day = "";

                    if (i1 + 1 < 10) month = "0" + (i1 + 1);
                    else month = String.valueOf(i1 + 1);

                    if (i2 < 10) day = "0" + i2;
                    else day = String.valueOf(i2);

                    request_date_first += i + "-" + (i1 + 1) + "-" + i2;
                    datePicker.setText(day + "-" + month + "-" + i);
                    setSumOfCategory(1);

                    MainActivity.date = day + "-" + month + "-" + i;
                });

                datePickerDialog.show();
                break;
            }

            case R.id.all_time_btn: {
                datePicker.setText("Все время");
                setSumOfCategory(5);
                MainActivity.date = "Все время";


                break;
            }
        }

        dialog.cancel();
    }

    private void setHeightFragment(ScrollView main_fragment, LinearLayout shadow_layout) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        main_fragment.getLayoutParams().height = (int)(displayMetrics.heightPixels / 2.75 - 40);
        main_fragment.setBackground(getResources().getDrawable(R.drawable.shape_bg_general_fragment, getTheme()));

        shadow_layout.setBackground(getResources().getDrawable(R.drawable.shadow_bg_fragment, getTheme()));
        shadow_layout.getLayoutParams().height = (int)(displayMetrics.heightPixels / 2.75 - 30);
    }

    private void setPieChart() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float heightDp = displayMetrics.heightPixels / 4;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                ((int)heightDp, (int)heightDp);
        layoutParams.setMargins(0, 20, 0, 20);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        addSlices(pieChart);
        pieChart.setLayoutParams(layoutParams);
        pieChart_layout.addView(pieChart);

        ConstraintSet set = new ConstraintSet();
        set.clone(main_layout);

        set.connect(pieChart.getId(), ConstraintSet.TOP, R.id.buttons_linear, ConstraintSet.BOTTOM);
        set.connect(pieChart.getId(), ConstraintSet.BOTTOM, R.id.shadow_layout, ConstraintSet.TOP);
        set.connect(pieChart.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(pieChart.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);

        set.connect(R.id.total_sum, ConstraintSet.TOP, R.id.piechart_layout, ConstraintSet.TOP);
        set.connect(R.id.total_sum, ConstraintSet.BOTTOM, R.id.piechart_layout, ConstraintSet.BOTTOM);
        set.connect(R.id.total_sum, ConstraintSet.RIGHT, R.id.piechart_layout, ConstraintSet.RIGHT);
        set.connect(R.id.total_sum, ConstraintSet.LEFT, R.id.piechart_layout, ConstraintSet.LEFT);

        set.applyTo(main_layout);
    }

    private void addSlices(PieChart pieChart) {
        pieChart.setInnerPadding(65f);

//        if (categories_income.size() == 0) {
            pieChart.addPieSlice(new PieModel("", 100,
                    getResources().getColor(R.color.blue_general, getTheme())));
//        }
//
//        else {
//            for (CategoryPrepare category: categories_income) {
//                int color = Color.parseColor(category.getBg_color_category());
//                Float percent = Float.parseFloat(category.getCategory_procent().replace(",", "."));
//                pieChart.addPieSlice(new PieModel("", percent,
//                        color));
//            }
//        }
    }

    public void updatePieChart(boolean action)
    {
        pieChart.clearChart();
    }

    private void updatePercent(ArrayList<CategoryPrepare> categories_income) {
        int sum_of_categories = 0;
        for (CategoryPrepare category: categories_income) {
            if (category.getSum_category() > 0) sum_of_categories += category.getSum_category();
            else sum_of_categories += category.getSum_category() * (-1);
        }

        for (int i = 0; i < categories_income.size(); i++) {
            if (sum_of_categories != 0) {
                double percent;
                percent = (double)categories_income.get(i).getSum_category() / sum_of_categories * 100;

                categories_income.get(i).setCategory_procent(df.format(percent));
            }
            else {
                categories_income.get(i).setCategory_procent("0");
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setHeightSubFragment();
    }

    private void setHeightSubFragment() {
        int[] location = new int[2];
        dropDown.getLocationOnScreen(location);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    @Override
    protected void onDestroy() {
        try {
            if (conn != null) db.closeConnect(conn);
            if (st != null) st.close();
            if (rs != null) rs.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }

    private void setSumOfCategory(int index) {
        new Thread(() -> {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            ArrayList<String> names = new ArrayList<>();
            ArrayList<Integer> amounts = new ArrayList<>();
            Bundle dateArgs = new Bundle();

            try {
                connection = db.connect(connection);

                switch (index) {
                    case 0: {
                        preparedStatement = connection.prepareStatement(Queries.getSumForRange());
                        preparedStatement.setInt(1, User.getUser().getID_user());
                        preparedStatement.setString(2, request_date_first);
                        preparedStatement.setString(3, request_date_second);

                        resultSet = preparedStatement.executeQuery();

                        setArgsForChangedDate(resultSet, names, amounts, dateArgs);
                        break;
                    }

                    case 1: {
                        preparedStatement = connection.prepareStatement(Queries.getSumForOneDay());
                        preparedStatement.setInt(1, User.getUser().getID_user());
                        preparedStatement.setString(2, request_date_first);

                        resultSet = preparedStatement.executeQuery();

                        setArgsForChangedDate(resultSet, names, amounts, dateArgs);
                        break;
                    }

                    case 5: {
                        preparedStatement = connection.prepareStatement(Queries.getSumForAllTime());
                        preparedStatement.setInt(1, User.getUser().getID_user());
                        resultSet = preparedStatement.executeQuery();

                        setArgsForChangedDate(resultSet, names, amounts, dateArgs);
                        break;
                    }

                }
            }
            catch (SQLException | ClassNotFoundException e) {
                Log.d("MyLog", e.getMessage());
                e.getStackTrace();
            } finally {
                try {
                    if (connection != null) db.closeConnect(connection);
                    if (preparedStatement != null) preparedStatement.close();
                    if (resultSet != null) resultSet.close();
                }
                catch (SQLException e) {
                    Log.d("MyLog", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setArgsForChangedDate(ResultSet resultSet, ArrayList<String> names, ArrayList<Integer> amounts, Bundle dateArgs) throws SQLException {
        while (resultSet.next()) {
            amounts.add(resultSet.getInt(2));
            names.add(resultSet.getString(1));
        }

        dateArgs.putIntegerArrayList("amounts", amounts);
        dateArgs.putStringArrayList("names", names);

        getSupportFragmentManager().setFragmentResult("changedDate", dateArgs);


        handler.post(() -> {
            int general_sum = 0;
            for (Integer amount: amounts) general_sum += amount;
            total_sum.setText(general_sum + "$");
        });
    }

    // --------------------------------------- HISTORY ---------------------------------------------
    /**
     * Обработка нажатия на кнопку "история"
     */
    private void setOnClickHistoryBtn() {
        history_button.setOnClickListener(view -> {
            if (map_of_sub_categories == null) {
                mainActivity.setMap_of_sub_categories(map_of_sub_categories);
            }

            shadow_layout.setVisibility(View.GONE);
            buttons_linear.setVisibility(View.GONE);
            pieChart_layout.setVisibility(View.GONE);
            dropDown.setVisibility(View.GONE);
            datePicker.setVisibility(View.GONE);
            total_sum.setVisibility(View.GONE);
            date_picker_shadow.setVisibility(View.GONE);
            main_fragment.setVisibility(View.GONE);


            FragmentHistory fragmentHistory = new FragmentHistory();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            categories_income = (ArrayList<CategoryPrepare>)this.getIntent().getSerializableExtra("categories_income");

            Bundle args = new Bundle();

            args.putSerializable("categories_income", categories_income);
            fragmentHistory.setArguments(args);

            transaction.replace(R.id.fragment_layout, fragmentHistory);
            transaction.detach(fragmentGeneral);
            transaction.commit();
        });
    }

    /**
     //     * Получение подкатегорий по ID категорий
     //     * @return
     //     * @throws ExecutionException
     //     * @throws InterruptedException
     //     */
    private ArrayList<SubCategory> getSubCategories() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<ArrayList<SubCategory>> future = es.submit(new getSubCategoriesTask(categories_income));

        es.shutdown();
        return future.get();
    }

    /**
     * Задача получения подкатегорий
     */
    private static class getSubCategoriesTask implements Callable<ArrayList<SubCategory>> {
        private ArrayList<CategoryPrepare> categories_income;
        private ArrayList<SubCategory> sub_categories = new ArrayList<>();

        private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
        private Connection connection = null;
        private PreparedStatement preparedStatement = null;
        private ResultSet resultSet = null;

        public getSubCategoriesTask(ArrayList<CategoryPrepare> list) {
            this.categories_income = list;
        }

        @Override
        public ArrayList<SubCategory> call() throws Exception {
            try {
                String part_of_query = "";

                connection = dataBaseHandler.connect(connection);

                for (int i = 0; i < categories_income.size(); i++) {
                    if (i != categories_income.size() - 1) part_of_query += categories_income.get(i).getId_category() + ",";
                    else part_of_query += categories_income.get(i).getId_category();
                }

                if (!part_of_query.isEmpty()) {
                    preparedStatement = connection.prepareStatement(Queries.getSubCategoriesById(part_of_query));
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.isBeforeFirst()) {
                        while (resultSet.next()) sub_categories.add(new SubCategory(resultSet.getString(1),
                                resultSet.getString(3), resultSet.getString(2), resultSet.getInt(4)));
                    }
                }
            }
            catch (SQLException e) {
                Log.d("MyLog", e.getMessage());
                e.printStackTrace();
            }
            finally {
                if (connection != null) dataBaseHandler.closeConnect(connection);
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            }

            return sub_categories;
        }
    }
    // ---------------------------------------------------------------------------------------------

    /**
     * Установка HashMap (id_категории - подкатегории)
     * 1. Перебирает все подкатегории и запоминает 1ый ID
     * 2. По этому id делает добавление в промежуточный список
     * 3. После того, как id меняется, производим добавление в список ключа и списка
     * @return
     */
    private HashMap<Integer, ArrayList<SubCategory>> setHashMap() {
        HashMap<Integer, ArrayList<SubCategory>> map = new HashMap<>();

        if (sub_categories.size() != 0) {
            int iterator_id = sub_categories.get(0).getId_category();
            ArrayList<SubCategory> list = new ArrayList<>();
            SubCategory s;

            for (int i = 0; i < sub_categories.size(); i++) {
                s = sub_categories.get(i);
                int current_id = s.getId_category();

                if (iterator_id == current_id) list.add(s);
                else {
                    if (map.containsKey(iterator_id)) {
                        map.get(iterator_id).add(s);
                    }
                    else {
                        map.put(iterator_id, new ArrayList<>(list));
                        iterator_id = current_id;

                        list.clear();
                        list.add(s);
                    }

                    if (i == sub_categories.size() - 1) map.put(iterator_id, list);
                }

                if ((iterator_id == current_id) && (i == sub_categories.size() - 1)) map.put(iterator_id, list);
            }
        }

        return map;
    }
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------

    /**
     * Метод получения хэш-таблицы истории ввода
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private HashMap<LocalDate, ArrayList<SubCategory>> get_map_of_history() throws ExecutionException, InterruptedException {
        map_of_history = new HashMap<>();
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<HashMap<LocalDate, ArrayList<SubCategory>>> future = es.submit(new GetMapOfHistoryTask(map_of_history, categories_income));

        es.shutdown();
        return future.get();
    }

    /**
     * Задача получения истории ввода
     */
    private static class GetMapOfHistoryTask implements Callable<HashMap<LocalDate, ArrayList<SubCategory>>> {
        private HashMap<LocalDate, ArrayList<SubCategory>> map_of_history;
        private ArrayList<CategoryPrepare> categories_income;
        private Connection connection = null;
        private PreparedStatement preparedStatement = null;
        private ResultSet resultSet = null;
        private String part_of_query = "";
        private DataBaseHandler db = DataBaseHandler.getDataBaseHadler();
        public GetMapOfHistoryTask(HashMap<LocalDate, ArrayList<SubCategory>> map_of_history, ArrayList<CategoryPrepare> categories_income) {
            this.map_of_history = map_of_history;
            this.categories_income = categories_income;
        }

        @Override
        public HashMap<LocalDate, ArrayList<SubCategory>> call() throws Exception {

            connection = db.connect(connection);

            for (int i = 0; i < categories_income.size(); i++) {
                if (i != categories_income.size() - 1) part_of_query +=
                        "\'" + categories_income.get(i).getName_category() + "\',";
                else part_of_query += "\'" + categories_income.get(i).getName_category() + "\'";
            }

            if (!part_of_query.isEmpty()) {
                preparedStatement = connection.prepareStatement(Queries.getHistoryMapIncome(part_of_query));
                preparedStatement.setInt(1, User.getUser().getID_user());

                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {

                    LocalDate key = resultSet.getDate(5).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (map_of_history.containsKey(key)) {
                        map_of_history.get(key).add(new SubCategory(resultSet.getString(1), resultSet.getString(3),
                                resultSet.getString(2), resultSet.getInt(4)));
                    }
                    else {
                        map_of_history.put(key, new ArrayList<>());
                        map_of_history.get(key).add(new SubCategory(resultSet.getString(1), resultSet.getString(3),
                                resultSet.getString(2), resultSet.getInt(4)));
                    }
                }
            }

            mainActivity.setMap_of_history(map_of_history);
            return map_of_history;
        }
    }
    // ---------------------------------------------------------------------------------------------

    /**
     * Обработка нажатия на кнопку доходы/расходы/общее
     */
    private void clickLinearBtns() {
        income_tv.setOnClickListener(new View.OnClickListener() {
            FragmentTransaction ft = null;

            @Override
            public void onClick(View view) {
                fragmentIncome = new FragmentIncome();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment, fragmentIncome);

                if (fragmentGeneral != null) ft.detach(fragmentGeneral);

                Bundle args = new Bundle();

                args.putSerializable("categories_income", categories_income);
                args.putIntegerArrayList("id_categories", id_categories);

                fragmentIncome.setArguments(args);

                general_tv.setTextColor(getResources().getColor(R.color.black, getApplicationContext().getTheme()));
                income_tv.setTextColor(getResources().getColor(R.color.blue_general, getApplicationContext().getTheme()));
                ft.commit();
            }
        });

        general_tv.setOnClickListener(new View.OnClickListener() {
            FragmentTransaction ft = null;
            @Override
            public void onClick(View view) {
                fragmentGeneral = new FragmentGeneral(getBaseContext(), getSupportFragmentManager());
                ft = getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();

                args.putSerializable("categories_income", categories_income);
                args.putSerializable("categories_expense", categories_expense);
                args.putIntegerArrayList("id_categories", id_categories);
                fragmentGeneral.setArguments(args);

                ft.replace(R.id.main_fragment, fragmentGeneral);

                if (fragmentIncome != null) ft.detach(fragmentIncome);

                income_tv.setTextColor(getResources().getColor(R.color.black, getApplicationContext().getTheme()));
                general_tv.setTextColor(getResources().getColor(R.color.blue_general, getApplicationContext().getTheme()));
                ft.commit();
            }
        });
    }

    public static HashMap<Integer, ArrayList<SubCategory>> getMap_of_sub_categories() {
        return map_of_sub_categories;
    }

    public static void setMap_of_sub_categories(HashMap<Integer, ArrayList<SubCategory>> map_of_sub_categories) {
        MainActivity.map_of_sub_categories = map_of_sub_categories;
    }

    public void setSub_categories(ArrayList<SubCategory> sub_categories) {
        this.sub_categories = sub_categories;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMap_of_history(HashMap<LocalDate, ArrayList<SubCategory>> map_of_history) {
        MainActivity.map_of_history = map_of_history;
    }

    public static HashMap<LocalDate, ArrayList<SubCategory>> getMap_of_history() {
        return map_of_history;
    }

    public Spinner getDropDown() {
        return dropDown;
    }

    public void setDropDown(Spinner dropDown) {
        this.dropDown = dropDown;
    }

    public TextView getTotal_sum() {
        return total_sum;
    }

    public void setTotal_sum(TextView total_sum) {
        this.total_sum = total_sum;
    }
}