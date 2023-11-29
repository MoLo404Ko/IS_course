package app.course.authorization;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.course.HashPassword;
import app.course.Main.MainActivity;
import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.income.Income_activity;

public class Authorization extends AppCompatActivity {

    private EditText login_field, password_field;
    private TextView forgot_password_tv, reg_btn_tv;
    private Button btn_entry;

    private ExecutorService executorService = null;
    private ExecutorService executorService1 = Executors.newSingleThreadExecutor();
    private Handler handler = null;

    private DataBaseHandler db;
    private Connection conn = null;
    private Statement st = null;
    private ResultSet rs = null;

    private User user = User.getUser();

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
        });

        btn_entry.setOnClickListener(view ->{
            if (login_field.getText().toString().isEmpty()) {
                errorField("login_field", false, login_field);
                moveAnim(login_field);
            }

            else {
                executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        rs = st.executeQuery(Queries.getLogin(login_field.getText().toString()));

                        handler.post(() -> {
                            try {
                                if (rs.next()) {
                                    errorField("login_field", true, login_field);

                                    if (password_field.getText().toString().isEmpty())
                                        errorField("password_field", false, password_field);
                                    else {
                                        executorService1.execute(() -> {
                                            String password = "";
                                            try {
                                                rs = st.executeQuery(Queries.getPassword(login_field.getText().toString()));
                                                while (rs.next()) password = rs.getString(1);

                                                rs = st.executeQuery(Queries.getIdUser(login_field.getText().toString()));
                                                while (rs.next()) user.setID_user(rs.getInt(1));
                                            }
                                            catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }

                                            try {
                                                HashPassword hp = new HashPassword();
                                                boolean isCorrect = hp.validatePassword(password_field.getText().toString(), password);

                                                handler.post(()->{
                                                    if (isCorrect) {
                                                        errorField("password_field", true, password_field);
                                                        Intent intent = new Intent(Authorization.this, MainActivity.class);

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
                                        });
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
                    catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    private void init() {
        db = DataBaseHandler.getDataBaseHadler();
        handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                conn = db.connect(conn);
                st = conn.createStatement();
            }
            catch (SQLException | RuntimeException | ClassNotFoundException e) {
                e.getMessage();
            }
        }).start();

        login_field = findViewById(R.id.login);
        password_field = findViewById(R.id.password);
        forgot_password_tv = findViewById(R.id.forgot_password);
        reg_btn_tv = findViewById(R.id.reg_btn);
        btn_entry = findViewById(R.id.BtnAuthReg);

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

    private void moveAnim(View view) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}