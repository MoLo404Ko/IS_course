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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.course.Main.MainActivity;
import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.category.CategoryPrepare;
import app.course.history.History;

public class DialogAddSum extends DialogFragment {

    private EditText date_edit_text;
    private EditText sum_edit_text;
    private Button add_btn;
    private String category_name;


    private SubCategory _category;
    private CategoryPrepare categoryPrepare;
    private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_sum, container, false);

        init(view);
        clickOnCloseBtn(view);
        clickOnCalendarBtn(view);
        addSum();

        return view;
    }

    private void init(View view) {
        Bundle args = this.getArguments();

        _category = (SubCategory) args.getParcelable("object");
        categoryPrepare = (CategoryPrepare)args.getParcelable("category");

        date_edit_text = view.findViewById(R.id.dialog_date_edit_text_add_dialog);
        sum_edit_text = view.findViewById(R.id.dialog_sum_edit_text_add_dialog);
        add_btn = view.findViewById(R.id.dialog_add_sub_category_btn);
    }

    /**
     * Закрытие диалога
     * @param view
     */
    private void clickOnCloseBtn(View view) {
        ImageButton closeBtn = view.findViewById(R.id.dialog_btn_close_dialog_add_sub_category);
        closeBtn.setOnClickListener(v -> {


            if (!sum_edit_text.getText().toString().isEmpty() && !date_edit_text.getText().toString().isEmpty()) {
                SubCategory object = new SubCategory(_category.getName(), sum_edit_text.getText().toString(),
                        _category.getId_category());


                new Thread(() -> {
                    Connection conn = null;
                    PreparedStatement st = null;

                    try {
                        conn = dataBaseHandler.connect(conn);

                        st = conn.prepareStatement(Queries.updateSumSubCategory());
                        st.setInt(1, Integer.parseInt(_category.getSum()));
                        st.setInt(2, object.getId_category());
                        st.setString(3, object.getName());

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
            }

            dismiss();
        });
    }

    /**
     * Обработка нажатия на кнопку добавления
     */
    private void addSum() {
        add_btn.setOnClickListener(v -> {
            if (sum_edit_text.getText().toString().isEmpty() || date_edit_text.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            else {
                int summa = Integer.parseInt(sum_edit_text.getText().toString()); // Введенная сумма
                int global_summa = summa + Integer.parseInt(_category.getSum()); // Сумма введенного значения и текущего в объекте

                /**
                 * Запрос на добавление записи в историю
                 */
                new Thread(() -> {
                    Connection conn = null;
                    PreparedStatement st = null;

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate localDate = LocalDate.parse(date_edit_text.getText().toString(), formatter);
                    java.sql.Date date = java.sql.Date.valueOf(String.valueOf(localDate));

                    try {
                        conn = dataBaseHandler.connect(conn);

                        st = conn.prepareStatement(Queries.addHistoryItem());
                        st.setDate(1, date);
                        st.setString(2, categoryPrepare.getName_category());
                        st.setInt(3, Integer.parseInt(sum_edit_text.getText().toString()));
                        st.setInt(4, categoryPrepare.getId_category());

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

                Toast.makeText(getContext(), "Добавлено!", Toast.LENGTH_SHORT).show();
                addSubCategoryInMap(summa, global_summa);
                addNewHistoryItem();

            }
        });
    }

    private void addSubCategoryInMap(int summa, int global_summa) {
        Bundle result = new Bundle();

        MainActivity mainActivity = MainActivity.getMainActivity();

        HashMap<Integer, ArrayList<SubCategory>> map_of_categories = mainActivity.getMap_of_sub_categories();
        ArrayList<SubCategory> list = map_of_categories.get(_category.getId_category());

        boolean isHas = false;


        // Проверяется наличие подкатегории в списке подкатегорий, находящегося в HashMap
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(_category.getName())) {
                Log.d("MyLog", "isHas");
                isHas = true;
                list.get(i).setSum(String.valueOf(global_summa));
            }
        }

        if (!isHas) {
            Log.d("MyLog", "Isn't has");
            list.add(new SubCategory(_category.getName(), String.valueOf(global_summa), _category.getId_category()));
        }


        result.putInt("pos", this.getArguments().getInt("pos"));
        result.putInt("sum", summa);

        getParentFragmentManager().setFragmentResult("setNewSumCategory", result);
    }

    /**
     * Обработка нажатия на кнопку-календарь
     * @param view
     */
    private void clickOnCalendarBtn(View view) {
        ImageView calendar_btn = view.findViewById(R.id.dialog_sub_category_date_btn);

        calendar_btn.setOnClickListener(view1 -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext());
            dialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
                String month = String.valueOf(i1);
                String day = String.valueOf(i2);

                if (i1 + 1 < 10) month = "0" + (i1 + 1);
                if (i2 < 10) day = "0" + i2;

                date_edit_text.setText(day + "-" + month + "-" + i);
            });

            dialog.show();
        });
    }

    /**
     * Метод добавления нового объекта истории
     * @return
     */
    private void addNewHistoryItem() {
        Bundle result = new Bundle();

        History object = new History(categoryPrepare.getName_category(), categoryPrepare.getBg_color_category(),
                categoryPrepare.getIcon_category(), categoryPrepare.getId_category(),
                Integer.parseInt(sum_edit_text.getText().toString()));

        String date_entry = date_edit_text.getText().toString();

        result.putParcelable("object", object);
        result.putString("date_entry", date_entry);
        result.putInt("id_category", object.getId_category());
        result.putInt("position", this.getArguments().getInt("pos"));

        getParentFragmentManager().setFragmentResult("addNewItemHistory", result);
    }
}
