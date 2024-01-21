package app.course.sub_category;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.Authorization;
import app.course.authorization.DataBaseHandler;

public class DialogAddSubCategory extends DialogFragment {
    private ImageButton btn_close_dialog_add_sub_category;
    private Button add_sub_category_btn;
    private EditText name_add_sub_category_edit;
    private EditText sum_edit_text_add_dialog;
    private EditText date_edit_text_add_dialog;


    private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;

    private String name_category;
    private ArrayList<String> names = new ArrayList<>();

    private Bundle bundle = new Bundle();
    private int id_category = 0;
    private int pos;
    private int current_sum;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getArguments();

        if (bundle.getStringArrayList("sub_category_names") != null) {
            names = bundle.getStringArrayList("sub_category_names");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View view = inflater.inflate(R.layout.dialog_add_sub_category, container, false);
        init(view);
        btn_close_dialog_add_sub_category.setOnClickListener(v -> {
            dismiss();
        });

        add_sub_category_btn.setOnClickListener(v -> {
            boolean isFill = true;

            if (name_add_sub_category_edit.getText().toString().isEmpty()) {
                Authorization.moveAnim(name_add_sub_category_edit);
                isFill = false;
            }

            else if (!isFill) Toast.makeText(getContext(), "Не все поля заполнены!", Toast.LENGTH_SHORT).show();

            else {
                if (names.contains(name_add_sub_category_edit.getText().toString())) {
                    Toast.makeText(getContext(), "Такая подкатегория уже сущствует", Toast.LENGTH_SHORT).show();
                    Authorization.moveAnim(name_add_sub_category_edit);
                }

                else {
                    name_add_sub_category_edit.setBackground(getResources().getDrawable
                            (R.drawable.shape_edit_text_add_dialog, getContext().getTheme()));
                    addCategory();

                    new Thread(() -> {
                        try {
                            connection = dataBaseHandler.connect(connection);
                            preparedStatement = connection.prepareStatement(Queries.addSubCategory());

                            preparedStatement.setInt(1, id_category);
                            preparedStatement.setInt(2, 0);
                            preparedStatement.setString(3, name_add_sub_category_edit.getText().toString());

                            preparedStatement.executeUpdate();
                        }
                        catch (SQLException | ClassNotFoundException e) {
                            Log.d("MyLog", e.getMessage());
                        }
                        finally {
                            try {
                                if (connection != null) dataBaseHandler.closeConnect(connection);
                                if (preparedStatement != null) preparedStatement.close();
                            }
                            catch (SQLException e) {
                                Log.d("MyLog", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        return view;
    }

    private void init(View view) {
        name_category = bundle.getString("name_category");

        id_category = (int)bundle.get("id_category");
        pos = (int)bundle.get("pos");
        current_sum = (int)bundle.get("current_sum");

        btn_close_dialog_add_sub_category = view.findViewById(R.id.btn_close_dialog_add_sub_category);
        add_sub_category_btn = view.findViewById(R.id.add_sub_category_btn);

        name_add_sub_category_edit = view.findViewById(R.id.name_add_sub_category_edit);
    }

    private void addCategory() {
        Bundle result = new Bundle();
        result.putString("name", name_add_sub_category_edit.getText().toString());
        result.putInt("id_category", id_category);

        names.add(name_add_sub_category_edit.getText().toString());
        getParentFragmentManager().setFragmentResult("add_sub_key", result);

        result.clear();

        SubCategory subCategory = new SubCategory(name_add_sub_category_edit.getText().toString(),
                "0", id_category);

        result.putInt("key", id_category);
        result.putParcelable("object", subCategory);
        result.putBoolean("remove", false);
        result.putBoolean("isSubCategory", true);

        getParentFragmentManager().setFragmentResult("edit_map_of_sub_categories_by", result);
    }
}
