package app.course.authorization;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import app.course.HashPassword;
import app.course.Main.MainActivity;
import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.category.Category;
import app.course.category.CategoryPrepare;
import app.course.spinner.SpinnerObject;

public class Authorization extends AppCompatActivity {
    private EditText login_field, password_field;
    private TextView forgot_password_tv, reg_btn_tv,  btn_without_login_tv;
    private Button btn_entry;

    private CheckBox check_box;
    private SharedPreferences sPref = null;
    private ExecutorService executorService = null;
    private Handler handler = null;

    private DataBaseHandler db;
    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet rs = null;

    private User user = User.getUser();

    private SpinnerObject[] amounts;
    private ArrayList<CategoryPrepare> categories_income;
    private ArrayList<CategoryPrepare> categories_expense;
    private int press_count = 0;
    long start_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        init();

        login_field.setOnFocusChangeListener((view, b) -> {
            if (b) login_field.setHint("");
            else login_field.setHint("Логин");
        });

        password_field.setOnFocusChangeListener((view, b) -> {
            if (b) password_field.setHint("");
            else password_field.setHint("Пароль");
        });

        forgot_password_tv.setOnClickListener(view -> {
        });

        reg_btn_tv.setOnClickListener(view -> {
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
            finish();
        });

        btn_entry.setOnClickListener(view ->{
            /**
             * Проверка на наличие символов в поле логин
             */
            if (login_field.getText().toString().isEmpty()) {
                errorField("login_field", false, login_field);
                moveAnim(login_field);
            }

            else {
                executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        conn = db.connect(conn);
                        statement = conn.prepareStatement(Queries.getLogin());
                        statement.setString(1, login_field.getText().toString());
                        rs = statement.executeQuery();

                        handler.post(() -> {
                            try {
                                /**
                                 * Проверка наличия логина в БД
                                 */
                                if (rs.next()) {
                                    errorField("login_field", true, login_field);

                                    if (password_field.getText().toString().isEmpty())
                                        errorField("password_field", false, password_field);
                                    else {
                                        new Thread(() -> {
                                            String password = "";
                                            try {
                                                statement = conn.prepareStatement(Queries.getPassword());
                                                statement.setString(1, login_field.getText().toString());
                                                rs = statement.executeQuery();

                                                if (rs.next()) password = rs.getString(1);

                                                statement = conn.prepareStatement(Queries.getIdUser());
                                                statement.setString(1, login_field.getText().toString());
                                                rs = statement.executeQuery();

                                                while (rs.next()) user.setID_user(rs.getInt(1));
                                            }
                                            catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }

                                            /**
                                             * Перевод пароля в ХЭШ и проверка на наличие такого же ХЭШ в БД
                                             */
                                            try {
                                                new Thread(() -> {
                                                    try {
                                                        amounts = getDropDown();
                                                    }
                                                    catch (ExecutionException |
                                                           InterruptedException e) {
                                                        Log.d("MyLog", e.getMessage());
                                                    }
                                                }).start();

                                                ArrayList<Integer> id_categories = new ArrayList<>();
                                                HashPassword hp = new HashPassword();
                                                boolean isCorrect = hp.validatePassword(password_field.getText().toString(), password);

                                                handler.post(()->{
                                                    if (isCorrect) {
                                                        errorField("password_field", true, password_field);

                                                        try {
                                                            categories_income = getCategoriesIncome();

//                                                            categories_expense = getCategoriesExpense();

                                                        } catch (ExecutionException | InterruptedException e) {
                                                            Log.d("MyLog", e.getMessage());
                                                            e.printStackTrace();
                                                        }

                                                        for (CategoryPrepare categoryPrepare: categories_income) {
                                                            id_categories.add(categoryPrepare.getId_category());
                                                        }

                                                        Intent intent = new Intent(Authorization.this, MainActivity.class);
                                                        intent.putExtra("amounts", amounts[0]);
                                                        intent.putExtra("categories_income", categories_income);
                                                        intent.putExtra("categories_expense", categories_expense);
                                                        intent.putExtra("id_categories", id_categories);

                                                        if (check_box.isChecked()) savedData(true);
                                                        else savedData(false);

                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    else {
                                                        errorField("login_field", false, password_field);
                                                        Toast.makeText(Authorization.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                                                e.getMessage();
                                            }
                                        }).start();
                                    }
                                }
                                else {
                                    errorField("login_field", false, login_field);
                                    Toast.makeText(Authorization.this, "Неверный логин", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
        executorService.shutdown();

        btn_without_login_tv.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
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

    private void init() {
        check_box = findViewById(R.id.check_box);

        categories_income = new ArrayList<>();
        categories_expense = new ArrayList<>();

        db = DataBaseHandler.getDataBaseHadler();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newSingleThreadExecutor();

        login_field = findViewById(R.id.login);
        password_field = findViewById(R.id.password);
        forgot_password_tv = findViewById(R.id.forgot_password);
        reg_btn_tv = findViewById(R.id.reg_btn);
        btn_entry = findViewById(R.id.BtnAuthReg);
        btn_without_login_tv = findViewById(R.id.btn_without_login);

        loadData();

        getSupportActionBar().hide();
    }

    private void errorField(String field, boolean correct, View view) {
        switch (field) {
            case "login_field": {
                login_field.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_phone, 0);
                login_field.setCompoundDrawablePadding(15);
                break;
            }

            case "password_field": {
                password_field.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock, 0);
                password_field.setCompoundDrawablePadding(15);
                break;
            }
        }

        if (correct) view.setBackground
                (getResources().getDrawable(R.drawable.shape_edit_text_correct, getTheme()));

        else {
            moveAnim(view);
            view.setBackground(getResources().getDrawable(R.drawable.shape_edit_text_error, getTheme()));
        }
    }


    /**
     * Метод отображения анимации
     * @param view
     */
    public static void moveAnim(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        TranslateAnimation mLeft = new TranslateAnimation(0, 15, 0,0);
        mLeft.setStartOffset(200);
        mLeft.setDuration(200);
        animationSet.addAnimation(mLeft);

        TranslateAnimation mRight = new TranslateAnimation(15, -15, 0,0);
        mRight.setDuration(200);
        animationSet.addAnimation(mRight);

        animationSet.setRepeatCount(4);

        view.startAnimation(animationSet);
    }
    // ---------------------------------------------------------------------------------------------
    /**
     * Метод получения счетов пользователя
     * 1. Создается новый поток
     * 2. В поток передается задача
     * 3. Возвращается список счетов
     */
    private SpinnerObject[] getDropDown() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<SpinnerObject[]> future = es.submit(new GetDropDownTask(conn));

        es.shutdown();

        return future.get();
    }


    /**
     * Задача получения из базы данных счетов пользователя
     * 1. Запрос счетов у базы данных
     * 2. Проверка наличия счетов
     */
    private static class GetDropDownTask implements Callable<SpinnerObject[]> {
        private Connection connection;
        private PreparedStatement statement = null;
        private ResultSet rs = null;
        private SpinnerObject[] amounts = new SpinnerObject[1];

        public GetDropDownTask(Connection connection) {
            this.connection = connection;
        }

        @Override
        public SpinnerObject[] call() throws Exception {
            boolean isFill = false;
            statement = connection.prepareStatement(Queries.getAmounts());
            statement.setInt(1, User.getUser().getID_user());
            rs = statement.executeQuery();

            while (rs.next()) {
                isFill = true;
                amounts[0] = new SpinnerObject(rs.getString(1), String.valueOf(rs.getInt(2)));
            }

            if (!isFill) {
                amounts[0] = new SpinnerObject("Основной счет", "0");

                new Thread(() -> {
                    DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
                    Connection conn = null;
                    PreparedStatement st = null;
                    try {
                        conn = dataBaseHandler.connect(conn);

                        st = conn.prepareStatement(Queries.addNewAccount());
                        st.setInt(1, User.getUser().getID_user());
                        st.setString(2, "Основной счет");
                        st.setDouble(3, 0.0);
                        st.setInt(4, 1);

                        st.executeUpdate();
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    finally {
                        try {
                            if (conn != null) dataBaseHandler.closeConnect(conn);
                            if (st != null) st.close();
                        }
                        catch (SQLException e) {
                            Log.d("MyLog", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            statement.close();
            rs.close();

            return amounts;
        }
    }
    // ---------------------------------------------------------------------------------------------
    /**
     * Метод получения списка категорий доходов
     * @return список категорий
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private ArrayList<CategoryPrepare> getCategoriesIncome() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();

        Future<ArrayList> future = es.submit(new getCategoriesTask(true, conn));
        es.shutdown();

        return future.get();
    }

    private ArrayList<CategoryPrepare> getCategoriesExpense() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<ArrayList> future = es.submit(new getCategoriesTask(false, conn));
        es.shutdown();

        return future.get();
    }

    /**
     * Класс получения списка категорий доходов
     * 1. Выполнение запроса на получение списка
     * 2. Если запрос не пустой - преобразование данных в нужный тип и распределение по спискам
     * 2.1 Получение процентного соотношения
     * 3. Если список пустой - добавление дефолтных категорий
     * 3.1 Выполнеие запроса на добавление категорий
     */
    private static class getCategoriesTask implements Callable<ArrayList> {
        private DecimalFormat df = new DecimalFormat("#.#");

        private DataBaseHandler db = DataBaseHandler.getDataBaseHadler();
        private Connection connection;
        private PreparedStatement statement = null;
        private ResultSet rs = null;

        private ArrayList<CategoryPrepare> categories = new ArrayList<>();
        private ArrayList<Integer> icons = new ArrayList<>();
        private ArrayList<String> items = new ArrayList<>();
        private ArrayList<Integer> sum = new ArrayList<>();
        private ArrayList<String> names = new ArrayList<>();
        private ArrayList<Integer> id_categories = new ArrayList<>();
        private boolean isIncoming;

        public getCategoriesTask(boolean isIncoming, Connection conn) {
            this.isIncoming = isIncoming;
            this.connection = conn;
        }

        @Override
        public ArrayList call() throws Exception {
            boolean isFill = false;
            int total_sum = 0;
            Double procent;

            if (categories != null) categories.clear();

            if (isIncoming) statement = connection.prepareStatement(Queries.getCategoryIncome());
            else statement = connection.prepareStatement(Queries.getCategoryExpense());
            statement.setInt(1, User.getUser().getID_user());
            rs = statement.executeQuery();

            while (rs.next()) {
                isFill = true;

                icons.add(rs.getInt(3));
                items.add(rs.getString(4));
                sum.add(rs.getInt(1));
                names.add(rs.getString(2));
                id_categories.add(rs.getInt(5));
            }

            for (int i: sum) total_sum += i;

            for (int i = 0; i < sum.size(); i++) {
                if (sum.get(i) != 0) {
                    procent = ((double)sum.get(i) / total_sum) * 100.0;
                }
                else procent = 0.0;

                CategoryPrepare categoryPrepare = new CategoryPrepare(items.get(i), icons.get(i), sum.get(i),
                        df.format(procent), names.get(i), id_categories.get(i));
                categories.add(categoryPrepare);
                categoryPrepare.setId_category(id_categories.get(i));
            }

            statement.close();
            rs.close();

            return categories;
        }
    }
    // ---------------------------------------------------------------------------------------------

    /**
     * Запоминание данных при нажатии чек-бокса
     */
    private void savedData(boolean isChecked) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        if (isChecked) {
            ed.putString("login", login_field.getText().toString());
            ed.putString("password", password_field.getText().toString());
        }
        else {
            ed.putString("login", "");
            ed.putString("password", "");
        }

        ed.commit();
    }

    /**
     * Загрузка данных логина и пароля при повторном входе
     */

    private void loadData() {
        sPref = getPreferences(MODE_PRIVATE);
        String login = sPref.getString("login", "");
        String password = sPref.getString("password", "");

        Log.d("MyLog", login);
        if (!login.isEmpty() && !password.isEmpty()) {
            login_field.setText(login);
            password_field.setText(password);
            check_box.setChecked(true);
        }
    }
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        try {
            if (conn != null) db.closeConnect(conn);
            if (statement != null) statement.close();
            if (rs != null) rs.close();
        }
        catch (SQLException e) {
            e.getMessage();
        }

        super.onDestroy();
    }
}