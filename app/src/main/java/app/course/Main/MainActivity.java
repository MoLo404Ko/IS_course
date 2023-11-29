package app.course.Main;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.menu_fragments.AmountsFragment;
import app.course.menu_fragments.CategoryFragment;
import app.course.menu_fragments.HomeFragment;
import app.course.menu_fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;


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
    }

    public void init() throws SQLException {
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(listener_nav);

        setDropDown();
    }

    private void setDropDown() throws SQLException {
        Spinner dropDown = findViewById(R.id.amounts_drop_down);
        List<String> items = new ArrayList<>();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    conn = db.connect(conn);
                    st = conn.createStatement();
                }
                catch (SQLException | ClassNotFoundException e) {
                    e.getMessage();
                }
            }
        }, 1500);

        new Thread(() -> {
            try {
                rs = st.executeQuery(Queries.getAmounts(user));

                if (rs.next()) {
                    while (rs.next()) items.add(rs.getString(1));
                }
                else {
                    st.executeUpdate(Queries.setDefaultAmounts(user));
                    items.add("Основной счет");
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);

        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        dropDown.setAdapter(adapter);
    }
}