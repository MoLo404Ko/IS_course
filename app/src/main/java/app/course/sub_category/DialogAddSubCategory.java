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
import java.util.ArrayList;
import java.util.Date;

import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.Authorization;
import app.course.authorization.DataBaseHandler;

public class DialogAddSubCategory extends DialogFragment {
    private ImageButton btn_close_dialog_add_sub_category;
    private ImageView sub_category_date_btn;
    private Button add_sub_category_btn;
    private EditText name_add_sub_category_edit;
    private EditText sum_edit_text_add_dialog;
    private EditText date_edit_text_add_dialog;

    private SimpleDateFormat date_category_format = new SimpleDateFormat("dd-MM-yyyy");
    private DateFormat date_db_format = new SimpleDateFormat("yyyy-MM-dd");
    private Date today = new Date();

    private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;

    private String name_category;
    private ArrayList<String> names = new ArrayList<>();
//    private ArrayList<String> new_names = new ArrayList<>();
//    private ArrayList<Integer> id_sub_categories = new ArrayList<>();

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
//            id_sub_categories = bundle.getIntegerArrayList("sub_category_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View view = inflater.inflate(R.layout.dialog_add_sub_category, container, false);
        init(view);
        btn_close_dialog_add_sub_category.setOnClickListener(v -> {
            if (!name_add_sub_category_edit.getText().toString().isEmpty()) {
                SubCategory object = new SubCategory(name_add_sub_category_edit.getText().toString(),
                        date_edit_text_add_dialog.getText().toString(), sum_edit_text_add_dialog.getText().toString(),
                        id_category);
                new Thread(() -> {
                    Connection conn = null;
                    PreparedStatement st = null;
                    LocalDate date = LocalDate.now();

                    try {
                        conn = dataBaseHandler.connect(conn);
                        st = conn.prepareStatement(Queries.addHistoryItem());
                        st.setString(1, String.valueOf(date));
                        st.setString(2, name_add_sub_category_edit.getText().toString());
                        st.setInt(3, User.getUser().getID_user());
                        st.setString(4, name_category);
                        st.setInt(5, Integer.parseInt(sum_edit_text_add_dialog.getText().toString()));

                        st.executeUpdate();
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Log.d("MyLog", e.getMessage());
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            if (conn != null) dataBaseHandler.closeConnect(conn);
                            if (st != null) st.close();
                        }
                        catch (SQLException e) {
                            Log.d("MyLog", e.getMessage());
                        }
                    }
                }).start();

                Bundle args = new Bundle();
                args.putParcelable("object", object);
                getParentFragmentManager().setFragmentResult("addNewItemHistory", args);
            }

            dismiss();
        });

        add_sub_category_btn.setOnClickListener(v -> {
            boolean isFill = true;

            if (name_add_sub_category_edit.getText().toString().isEmpty()) {
                Authorization.moveAnim(name_add_sub_category_edit);
                isFill = false;
            }

            else if (sum_edit_text_add_dialog.getText().toString().isEmpty()) {
                Authorization.moveAnim(sum_edit_text_add_dialog);
                isFill = false;
            }

            else if (date_edit_text_add_dialog.getText().toString().isEmpty()) {
                Authorization.moveAnim(date_edit_text_add_dialog);
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
                    updateSum();

                    new Thread(() -> {
                        try {
                            Date date = date_category_format.parse(date_edit_text_add_dialog.getText().toString());
                            java.sql.Date sql_date = new java.sql.Date(date.getTime());

                            connection = dataBaseHandler.connect(connection);
                            preparedStatement = connection.prepareStatement(Queries.addSubCategory());

                            preparedStatement.setInt(1,id_category);
                            preparedStatement.setString(2, name_add_sub_category_edit.getText().toString());
                            preparedStatement.setInt(3, Integer.parseInt(sum_edit_text_add_dialog.getText().toString()));
                            preparedStatement.setDate(4, sql_date);

                            preparedStatement.executeUpdate();

                            preparedStatement.clearParameters();
                        }
                        catch (SQLException | ClassNotFoundException | ParseException e) {
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

        sub_category_date_btn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
            datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
                String month = "";
                String day = "";

                if (i2 < 10) day += "0" + i2;
                else day = String.valueOf(i2);

                if (i1 + 1 < 10) month += "0" + (int)(i1 + 1);
                else month = String.valueOf(i1 + 1);

                date_edit_text_add_dialog.setText(day + "-" + month + "-" + (i));
            });

            datePickerDialog.show();
        });

        return view;
    }

    private void init(View view) {
        name_category = bundle.getString("name_category");
        id_category = (int)bundle.get("id_category");
        pos = (int)bundle.get("pos");
        current_sum = (int)bundle.get("current_sum");

        btn_close_dialog_add_sub_category = view.findViewById(R.id.btn_close_dialog_add_sub_category);
        sub_category_date_btn = view.findViewById(R.id.sub_category_date_btn);
        add_sub_category_btn = view.findViewById(R.id.add_sub_category_btn);

        name_add_sub_category_edit = view.findViewById(R.id.name_add_sub_category_edit);
        date_edit_text_add_dialog = view.findViewById(R.id.date_edit_text_add_dialog);
        sum_edit_text_add_dialog = view.findViewById(R.id.sum_edit_text_add_dialog);

        date_edit_text_add_dialog.setText(date_category_format.format(today));
    }

    private void addCategory() {
        Bundle result = new Bundle();
        result.putString("name", name_add_sub_category_edit.getText().toString());
        result.putString("sum", sum_edit_text_add_dialog.getText().toString());
        result.putString("date", date_edit_text_add_dialog.getText().toString());
        result.putInt("id_category", id_category);

        names.add(name_add_sub_category_edit.getText().toString());
        getParentFragmentManager().setFragmentResult("add_sub_key", result);

        result.clear();

        SubCategory subCategory = new SubCategory(name_add_sub_category_edit.getText().toString(),
                date_edit_text_add_dialog.getText().toString(), sum_edit_text_add_dialog.getText().toString(), id_category);

        result.putInt("key", id_category);
        result.putParcelable("object", subCategory);
        result.putBoolean("remove", false);
        result.putBoolean("isSubCategory", true);

        getParentFragmentManager().setFragmentResult("edit_map_of_sub_categories_by", result);
    }

    /**
     * Метод, устанавливающий данные для прослушивателя в FragmentGeneral
     * Обновляет сумму для активной категории
     */
    private void updateSum() {
        Bundle result = new Bundle();

        int new_sum = Integer.parseInt(sum_edit_text_add_dialog.getText().toString()) + current_sum;
        current_sum = new_sum;
        result.putInt("new_sum", new_sum);
        result.putInt("pos", pos);
        getParentFragmentManager().setFragmentResult("fragmentSubKey", result);
    }
}
