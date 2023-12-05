package app.course.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.menu_fragments.AmountsFragment;
import app.course.menu_fragments.CategoryFragment;
import app.course.menu_fragments.HomeFragment;
import app.course.menu_fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity {
    private FragmentTransaction fragmentTransaction = null;
    private Button datePicker;
    private Spinner dropDown;
    private Calendar calendar;

    private BottomNavigationView bottomNavigationView;
    private ExecutorService executorService = null;

    private User user = User.getUser();
    private DataBaseHandler db = DataBaseHandler.getDataBaseHadler();
    private Connection conn = null;
    private Statement st = null;
    private ResultSet rs = null;

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

            RelativeLayout all_time, one_day, week, month, range, year;

            one_day = (RelativeLayout)dialog.findViewById(R.id.all_time_btn);

            one_day.setOnClickListener(dialog_view -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker dateP, int i, int i1, int i2) {
                        datePicker.setText(String.valueOf(i) + "." + String.valueOf(i1) + "." + String.valueOf(i2));

                        executorService.execute(() -> {

                        });

                    }
                });

                datePickerDialog.show();
            });


            dialog.show();
        });
    }

    public void init() throws SQLException {
        calendar = Calendar.getInstance();
        datePicker = findViewById(R.id.date_picker);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(listener_nav);

        executorService = Executors.newSingleThreadExecutor();

        setDropDown();

        FragmentGeneral fragmentGeneral = new FragmentGeneral();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragmentGeneral);
        fragmentTransaction.commit();


    }

    private void setDropDown() throws SQLException {
        dropDown = findViewById(R.id.amounts_drop_down);
        List<String> items = new ArrayList<>();
        Handler handler = new Handler();

        executorService.execute(() -> {
            try {
                conn = db.connect(conn);
                st = conn.createStatement();

                rs = st.executeQuery(Queries.getAmounts(user));

                while (rs.next()) {
                    items.add(rs.getString(1));
                }
            }
            catch (SQLException | ClassNotFoundException e) {
                e.getMessage();
            }

            handler.post(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, items);

                adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                dropDown.setAdapter(adapter);
            });
        });
    }

    private void setDatePicker() {
        datePicker.setOnClickListener(view -> {

        });
    }
}