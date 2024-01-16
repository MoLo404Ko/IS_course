package app.course.sub_category;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import app.course.R;
import app.course.authorization.Authorization;

public class DialogAddSum extends DialogFragment {

    private EditText date;
    private EditText sum;
    private Button add_btn;

    private ArrayList<Integer> amounts;
    private ArrayList<LocalDate> dates;
    private LocalDate parse_date;

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
        date = view.findViewById(R.id.dialog_date_edit_text_add_dialog);
        sum = view.findViewById(R.id.dialog_sum_edit_text_add_dialog);
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
            if (!sum.getText().toString().isEmpty() && date.getText().toString().isEmpty()) {

            }

            dismiss();
        });
    }

    /**
     * Обработка нажатия на кнопку добавления
     */
    private void addSum() {
        add_btn.setOnClickListener(v -> {
            if (sum.getText().toString().isEmpty() || date.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            else {
                int summa = Integer.parseInt(sum.getText().toString());
                amounts.add(summa);
                dates.add(parse_date);

                SubCategory category = (SubCategory) this.getArguments().getParcelable("object");

                Bundle result = new Bundle();
                SubCategory object = new SubCategory(category.getName(), date.getText().toString(), String.valueOf(summa),
                        category.getId_category());
                result.putParcelable("object", object);
                getParentFragmentManager().setFragmentResult("addNewItemHistory", result);

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
                date.setText(day + "-" + month + "-" + i);
            });

            dialog.show();
        });
    }


}
