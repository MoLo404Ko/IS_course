package app.course.authorization;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import app.course.HashPassword;
import app.course.Queries;
import app.course.R;
import app.course.User;

public class Registration extends AppCompatActivity {
    private ImageButton back_btn;
    private Button reg_btn;
    private EditText name_field, login_field, password_field, repeat_password_field;

    private DataBaseHandler dataBaseHandler;
    private Connection conn = null;
    private Statement statement= null;
    private ResultSet rs = null;

    private ExecutorService executorService = null;
    private Handler handler = null;
    private boolean isExist = true;
    private String phone_regex = "\\d{11}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        try {
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        back_btn.setOnClickListener(view -> {
            finish();
        });

        reg_btn.setOnClickListener(view -> {

            if (name_field.getText().toString().isEmpty()) {
                errorField("name_field", false, name_field);
            }

            else {
                errorField("name_field", true, name_field);

                executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        rs = statement.executeQuery("SELECT login from user WHERE login = \'"
                                + login_field.getText().toString() + "\'");

                        handler.post(() -> {
                            try {

                                if (login_field.getText().toString().isEmpty()) errorField("login_field", false, login_field);
                                else {
                                    if ((Patterns.EMAIL_ADDRESS.matcher(login_field.getText().toString()).matches() ||
                                            login_field.getText().toString().matches(phone_regex))) {
                                        if (rs.next()) {
                                            isExist = true;

                                            errorField("login_field", false, login_field);
                                            Toast.makeText(this, "Пользователь с таким логином уже существует",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            isExist = false;
                                            errorField("login_field", true, login_field);
                                        }

                                    }
                                    else {
                                        Toast.makeText(this, "Неверный формат почты или телефона", Toast.LENGTH_SHORT).show();
                                        errorField("login_field", false, login_field);
                                    }
                                }


                                if (password_field.getText().toString().isEmpty())
                                    errorField("password_field", false, password_field);

                                else if (password_field.getText().toString().length() < 6) {
                                    errorField("password_field", false, password_field);
                                    Toast.makeText(this, "Пароль должен быть не короче 6 символов",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else errorField("password_field", true, password_field);


                                if (repeat_password_field.getText().toString().isEmpty()) {
                                    errorField("repeat_password_field", false, repeat_password_field);
                                }
                                else if (!repeat_password_field.getText().toString().equals(
                                        password_field.getText().toString())) {
                                    errorField("repeat_password_field", false, repeat_password_field);
                                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    errorField("repeat_password_field", true, repeat_password_field);

                                    new Thread(() -> {
                                        try {
                                            if (!isExist) {
                                                HashPassword hp = new HashPassword(password_field.getText().toString());
                                                String password = hp.getHash();

                                                statement.executeUpdate(Queries.newUser(login_field.getText().toString(),
                                                        password, name_field.getText().toString()));
                                                finish();
                                            }
                                        } catch (SQLException | NoSuchAlgorithmException
                                                 | InvalidKeySpecException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }).start();
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                executorService.shutdown();
            }
        });
    }

    private void init() throws SQLException {
        back_btn = findViewById(R.id.back_btn);
        reg_btn = findViewById(R.id.reg_btn);
        name_field = findViewById(R.id.name);
        login_field = findViewById(R.id.login_reg);
        password_field = findViewById(R.id.password);
        repeat_password_field = findViewById(R.id.repeat_password);

        dataBaseHandler = DataBaseHandler.getDataBaseHadler();
        handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                conn = dataBaseHandler.connect(conn);
                statement = conn.createStatement();
            }
            catch (ClassNotFoundException | SQLException e) {
                e.getMessage();
            }
        }).start();

        getSupportActionBar().hide();
    }

    private void errorField(String field, boolean correct, View view) {
        switch (field) {
            case "name_field": {
                name_field.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_user, 0);
                name_field.setCompoundDrawablePadding(15);
                break;
            }

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

            case "repeat_password_filed": {
                repeat_password_field.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock, 0);
                repeat_password_field.setCompoundDrawablePadding(15);
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
        try {
            if (conn != null) dataBaseHandler.closeConnect(conn);
            if (statement != null) statement.close();
            if (rs != null) rs.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }
}

