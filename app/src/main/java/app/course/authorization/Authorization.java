package app.course.authorization;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import app.course.R;

public class Authorization extends AppCompatActivity {

    private ImageView bg, logo;
    private EditText login, password;
    private TextView forgot_password, reg_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);
        
        init();

        login.setOnFocusChangeListener((view, b) -> {
            if (b) login.setHint("");
            else login.setHint("Логин");
        });

        password.setOnFocusChangeListener((view, b) -> {
            if (b) password.setHint("");
            else password.setHint("Пароль");
        });

        forgot_password.setOnClickListener(view -> {
        });

        reg_btn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
        });
    }

    private void init() {
        getSupportActionBar().hide();

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        forgot_password = findViewById(R.id.forgot_password);
        reg_btn = findViewById(R.id.reg_btn);
    }

//    public void btnEntry() {
//        if (!login.getText().equals("") && !password.getText().equals("")) {
//            Intent intent = new Intent(this, Main.class);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}