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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.category.Category;
import app.course.category.CategoryPrepare;
import app.course.menu_fragments.AmountsFragment;
import app.course.menu_fragments.CategoryFragment;
import app.course.menu_fragments.HomeFragment;
import app.course.menu_fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity {
//    private TextView general_sum;
    private int income_sum;
//    private int expense_sum;
    private Bundle extras = null;
    private Bundle args = null;

    private ConstraintLayout main_layout;
    private ScrollView main_fragment;
    private LinearLayout shadow_layout, buttons_linear;
    private PieChart pieChart;
    private RelativeLayout pieChart_layout;
    private FrameLayout sub_fragment;

    RelativeLayout all_time, one_day, week, month, range, year;

    private FragmentTransaction fragmentTransaction = null;
    private FragmentGeneral fragmentGeneral = null;
    private Button datePicker;
    private ImageButton add_button, minus_button, history_button;
    private Spinner dropDown;
    private Calendar calendar;

    private BottomNavigationView bottomNavigationView;
    private ExecutorService executorService = null;

    private User user = User.getUser();
    private DataBaseHandler db = DataBaseHandler.getDataBaseHadler();
    private Connection conn = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    private ArrayList<CategoryPrepare> categories_income;
    private ArrayList<CategoryPrepare> categories_expense;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        datePicker.setOnClickListener(view -> {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_dialog_date);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            one_day = (RelativeLayout)dialog.findViewById(R.id.one_day_btn);

            one_day.setOnClickListener(view_1 -> {
                setDatePicker(view_1);
            });

            dialog.show();
        });
    }

    public void init() throws SQLException {
        add_button = findViewById(R.id.add_btn);

        sub_fragment = findViewById(R.id.sub_fragment);
        extras = getIntent().getExtras();

        main_layout = findViewById(R.id.main_layout);
        buttons_linear = findViewById(R.id.buttons_linear);

        main_fragment = findViewById(R.id.main_fragment);
        shadow_layout = findViewById(R.id.shadow_layout);

        pieChart = new PieChart(this);
        pieChart.setId(View.generateViewId());
        pieChart_layout = findViewById(R.id.piechart_layout);

        setHeightFragment(main_fragment, shadow_layout);

        calendar = Calendar.getInstance();
        datePicker = findViewById(R.id.date_picker);


        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(listener_nav);

        executorService = Executors.newSingleThreadExecutor();

        setDropDown();

        fragmentGeneral = new FragmentGeneral(sub_fragment);

        categories_income = (ArrayList<CategoryPrepare>)getIntent().getSerializableExtra("categories_income");
        categories_expense = (ArrayList<CategoryPrepare>)getIntent().getSerializableExtra("categories_expense");

        Bundle args = new Bundle();

        args.putSerializable("categories_income", categories_income);
        args.putSerializable("categories_expense", categories_expense);
        fragmentGeneral.setArguments(args);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragmentGeneral);
        fragmentTransaction.commit();

        setPieChart(buttons_linear, shadow_layout);
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
    }
    // ---------------------------------------------------------------------------------------------

    private void setDatePicker(View view) {
        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<Integer> list_incomes = new ArrayList<>();

        switch (view.getId()) {
            case R.id.one_day_btn: {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker dateP, int i, int i1, int i2) {

                        datePicker.setText(String.valueOf(i) + "." + String.valueOf(i1 + 1) + "." + String.valueOf(i2));
                        new Thread(() -> {

                            try {
                                conn = db.connect(conn);
                                st = conn.prepareStatement(Queries.getSumForOneDay());
                                st.setInt(1, user.getID_user());
                                st.setString(2, String.valueOf(datePicker.getText()));

                                rs = st.executeQuery();
                                while (rs.next()) {
                                    income_sum += rs.getInt(1);
                                    list_incomes.add(rs.getInt(1));
                                }
                            }
                            catch (SQLException | ClassNotFoundException e) {
                                Log.d("MyLog", e.getMessage() + " " + e.getStackTrace());
                            }
                            finally {
                                try {
                                    if (conn != null) db.closeConnect(conn);
                                    if (st != null) st.close();
                                    if (rs != null) rs.close();
                                }
                                catch (SQLException e) {
                                    Log.d("MyLog", e.getMessage() + " " + e.getStackTrace());
                                }
                            }
                            handler.post(() -> {
                                fragmentGeneral.setSum(income_sum, 0, 0, list_incomes);
                            });
                        }).start();


                    }
                });

                datePickerDialog.show();
            }
        }
    }

    private void setHeightFragment(ScrollView main_fragment, LinearLayout shadow_layout) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        main_fragment.getLayoutParams().height = (int)(displayMetrics.heightPixels / 2.75 - 40);
        main_fragment.setBackground(getResources().getDrawable(R.drawable.shape_bg_general_fragment, getTheme()));

        shadow_layout.setBackground(getResources().getDrawable(R.drawable.shadow_bg_fragment, getTheme()));
        shadow_layout.getLayoutParams().height = (int)(displayMetrics.heightPixels / 2.75 - 30);
    }

    private void setPieChart(View firstView, View secondView) {
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
        pieChart.addPieSlice(new PieModel("", 100,
                getResources().getColor(R.color.blue_general, getTheme())));
    }
}