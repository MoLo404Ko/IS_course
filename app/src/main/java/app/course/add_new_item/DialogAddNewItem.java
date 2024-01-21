package app.course.add_new_item;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import app.course.Constants;
import app.course.Main.MainActivity;
import app.course.R;
import app.course.category.CategoryPrepare;
import app.course.history.History;
import app.course.spinner.SpinnerObject;
import app.course.sub_category.SubCategory;

public class DialogAddNewItem extends DialogFragment {
    // ---------------------------------------- UI ELEMENTS ----------------------------------------
    private RadioButton income_rb, expense_rb;
    private ImageButton btn_close_add_dialog;
    private Button add_btn;
    private EditText date_edit_text;
    private EditText sum_edit_text;
    // ---------------------------------------------------------------------------------------------

    // ----------------------------------------- RECYCLER ------------------------------------------
    private ArrayList<CategoryPrepare> categories_income;
    private RecyclerView new_item_recycler;
    private ArrayList<Drawable> icons;
    private ArrayList<Integer> id_icons;
    private ArrayList<String> names;
    private NewItemAdapter newItemAdapter;
    private int choose_pos = -1;
    // ---------------------------------------------------------------------------------------------

    private ConstraintLayout main_layout;
    private ConstraintSet constraintSet;
    private static DialogAddNewItem dialogAddNewItem = new DialogAddNewItem();

    private Bundle args = new Bundle();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add, container, false);

        init(view);
        btn_close_add_dialog.setOnClickListener(v -> dismiss());

        return view;
    }

    private void init(View view) {
        args = this.getArguments();

        main_layout = view.findViewById(R.id.dialog_add_new_item_main_layout);
        constraintSet = new ConstraintSet();

        id_icons = new ArrayList<>();
        icons = new ArrayList<>();
        names = new ArrayList<>();

        categories_income = new ArrayList<>();
        categories_income = args.getParcelableArrayList("incomes_list");

        btn_close_add_dialog = view.findViewById(R.id.btn_close_add_dialog);
        new_item_recycler = view.findViewById(R.id.new_item_recycler);
        add_btn = view.findViewById(R.id.add_btn);
        date_edit_text = view.findViewById(R.id.date_edit_text);
        sum_edit_text = view.findViewById(R.id.sum_edit_text);

        setCategories();
        clickOnCalendarBtn(view);
        addSumCategory();
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
     * Устанавливает категории доходов/расходов
     */
    private void setCategories() {
        for (CategoryPrepare c: categories_income) id_icons.add(c.getIcon_category());
        for (int i = 0; i < categories_income.size(); i++) names.add(categories_income.get(i).getName_category());

        for (int i: id_icons) {
            Drawable icon = getResources().getDrawable(Constants.INCOME_CATEGORIES[i], getContext().getTheme());
            icons.add(icon);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);

        new_item_recycler.setLayoutManager(gridLayoutManager);

        newItemAdapter = new NewItemAdapter(icons, names);
        new_item_recycler.setAdapter(newItemAdapter);

        constraintSet.clone(main_layout);
        constraintSet.connect(R.id.new_item_recycler, ConstraintSet.TOP, R.id.sum_and_date_layout_edit_add_dialog, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.new_item_recycler, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(R.id.new_item_recycler, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        constraintSet.applyTo(main_layout);
    }

    /**
     * Метод добавления суммы в категорию
     */
    private void addSumCategory() {

        add_btn.setOnClickListener(view -> {
            Log.d("MyLog", dialogAddNewItem.getChoose_pos() + " po "  + sum_edit_text.getText().toString() +  " " + date_edit_text.getText().toString());
            if (sum_edit_text.getText().toString().isEmpty() || date_edit_text.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Не все заполнено!", Toast.LENGTH_SHORT).show();
            else {
                if (dialogAddNewItem.getChoose_pos() == -1) Toast.makeText(getContext(), "Выберите куда добавлять", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getContext(), "Добавлено!", Toast.LENGTH_SHORT).show();
                    addNewHistoryItem(Integer.parseInt(sum_edit_text.getText().toString()), date_edit_text.getText().toString());

                    Bundle result = new Bundle();
                    // Значение суммы категории и введенного значения
                    int sum = categories_income.get(dialogAddNewItem.getChoose_pos()).getSum_category() +
                            Integer.parseInt(sum_edit_text.getText().toString());

                    result.putInt("id_category", dialogAddNewItem.getChoose_pos());
                    result.putInt("sum", sum);
                    result.putInt("pos", dialogAddNewItem.getChoose_pos());

                    getParentFragmentManager().setFragmentResult("result_sum", result);

                    Bundle args = new Bundle();

                    SpinnerObject spinnerObject = this.getArguments().getParcelable("spinner_object");
                    int new_sum = Integer.parseInt(spinnerObject.getSum()) + Integer.parseInt(sum_edit_text.getText().toString());

                    spinnerObject.setSum(String.valueOf(new_sum));
                    args.putParcelable("spinner_object", spinnerObject);

                    getParentFragmentManager().setFragmentResult("change_amount", args);
                }
            }
        });
    }


    /**
     * Добавление в историю
     */
    private void addNewHistoryItem(int sum, String date_entry) {
        Bundle result = new Bundle();
        CategoryPrepare chosen_object = categories_income.get(dialogAddNewItem.getChoose_pos());

        History object = new History(chosen_object.getName_category(), chosen_object.getBg_color_category(),
                chosen_object.getIcon_category(), chosen_object.getId_category(),
                Integer.parseInt(sum_edit_text.getText().toString()));


        result.putParcelable("object", object);
        result.putString("date_entry", date_entry);
        result.putInt("id_category", chosen_object.getId_category());
        result.putInt("position", choose_pos);

        setNewSumCategory(sum);
        getParentFragmentManager().setFragmentResult("addNewItemHistory", result);
    }

    private void setNewSumCategory(int summa) {
        Bundle result = new Bundle();

        result.putInt("pos", choose_pos);
        result.putInt("sum", summa);

        getParentFragmentManager().setFragmentResult("setNewSumCategory", result);
    }

    public void setChoose_pos(int choose_pos) {
        this.choose_pos = choose_pos;
    }

    public int getChoose_pos() {
        return choose_pos;
    }

    public static DialogAddNewItem getDialogAddNewItem() {
        return dialogAddNewItem;
    }
}
