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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import app.course.Main.MainActivity;
import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;

public class DialogAddSum extends DialogFragment {

    private EditText date_edit_text;
    private EditText sum_edit_text;
    private Button add_btn;

    private ArrayList<Integer> amounts;
    private ArrayList<LocalDate> dates;
    private LocalDate parse_date;

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
        date_edit_text = view.findViewById(R.id.dialog_date_edit_text_add_dialog);
        sum_edit_text = view.findViewById(R.id.dialog_sum_edit_text_add_dialog);
        add_btn = view.findViewById(R.id.dialog_add_sub_category_btn);

        amounts = new ArrayList<>();
        dates = new ArrayList<>();
    }

    /**
     * Закрытие диалога
     * @param view
     */
    private void clickOnCloseBtn(View view) {
        ImageButton closeBtn = view.findViewById(R.id.dialog_btn_close_dialog_add_sub_category);
        closeBtn.setOnClickListener(v -> {
            SubCategory category = (SubCategory) this.getArguments().getParcelable("object");

            if (!sum_edit_text.getText().toString().isEmpty() && !date_edit_text.getText().toString().isEmpty()) {
                SubCategory object = new SubCategory(category.getName(),
                        String.valueOf(LocalDate.now()), sum_edit_text.getText().toString(), category.getId_category());


                new Thread(() -> {
                    Connection conn = null;
                    PreparedStatement st = null;

                    try {
                        conn = dataBaseHandler.connect(conn);

                        st = conn.prepareStatement(Queries.updateSumSubCategory());
                        st.setInt(1, Integer.parseInt(sum_edit_text.getText().toString()) +
                                this.getArguments().getInt("sub_category_sum"));
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

                category.setSum(String.valueOf(Integer.parseInt(sum_edit_text.getText().toString()) + Integer.parseInt(category.getSum())));
//                Bundle args = new Bundle();
//                args.putParcelable("object", object);
//                getParentFragmentManager().setFragmentResult("addNewItemHistory", args);
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
                SubCategory category = (SubCategory) this.getArguments().getParcelable("object");

                int summa = Integer.parseInt(sum_edit_text.getText().toString());
                Log.d("MyLog", summa + " text_summa");
                amounts.add(summa);
                dates.add(parse_date);

                new Thread(() -> {
                    Connection conn = null;
                    PreparedStatement st = null;

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date = LocalDate.parse(date_edit_text.getText().toString(), formatter);

                    try {
                        conn = dataBaseHandler.connect(conn);
                        st = conn.prepareStatement(Queries.addHistoryItem());
                        st.setString(1, date.toString());
                        st.setString(2, category.getName());
                        st.setInt(3, User.getUser().getID_user());
                        st.setString(4, this.getArguments().getString("category_name"));
                        st.setInt(5, Integer.parseInt(sum_edit_text.getText().toString()));

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

                int global_summa = summa + Integer.parseInt(category.getSum());
                Log.d("MyLog", global_summa + " global_summa");

                Bundle result = new Bundle();
                SubCategory object = new SubCategory(category.getName(), date_edit_text.getText().toString(), String.valueOf(summa),
                        category.getId_category());

                result.putParcelable("object", object);
                result.putInt("id_category", object.getId_category());
                result.putInt("position", this.getArguments().getInt("pos"));

                getParentFragmentManager().setFragmentResult("addNewItemHistory", result);

                /////////////////////////////////
                MainActivity mainActivity = MainActivity.getMainActivity();

                HashMap<Integer, ArrayList<SubCategory>> map_of_categories = mainActivity.getMap_of_sub_categories();
                ArrayList<SubCategory> list = map_of_categories.get(object.getId_category());

                String sub_category_name = this.getArguments().getString("sub_category_name");

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().equals(sub_category_name)) {
                        Log.d("MyLog", global_summa + " global sAS");
                        list.get(i).setSum(String.valueOf(global_summa));
                        category.setSum(String.valueOf(global_summa));
                    }
                    else {
                        list.add(new SubCategory(sub_category_name, object.getDate_last_entry(), String.valueOf(global_summa), object.getId_category()));
                    }
                }
                /////////////////////////////////

                result.clear();

                result.putInt("pos", this.getArguments().getInt("pos"));
                result.putInt("sum", summa);

                getParentFragmentManager().setFragmentResult("setNewSumCategory", result);
                Toast.makeText(getContext(), "Добавлено!", Toast.LENGTH_SHORT).show();

            }
        });
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

                parse_date = LocalDate.parse(i + "-" + month + "-" + day);
                date_edit_text.setText(day + "-" + month + "-" + i);
            });

            dialog.show();
        });
    }


}
