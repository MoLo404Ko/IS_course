package app.course.add_new_item;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.course.Constants;
import app.course.R;
import app.course.category.CategoryPrepare;

public class DialogAddNewItem extends DialogFragment {
    // ---------------------------------------- UI ELEMENTS ----------------------------------------
    private RadioButton income_rb, expense_rb;
    private ImageButton btn_close_add_dialog;
    // ---------------------------------------------------------------------------------------------

    // ----------------------------------------- RECYCLER ------------------------------------------
    private ArrayList<CategoryPrepare> categories_income;
    private RecyclerView new_item_recycler;
    private ArrayList<Drawable> icons;
    private ArrayList<Integer> id_icons;
    private ArrayList<String> names;
    private NewItemAdapter newItemAdapter;
    // ---------------------------------------------------------------------------------------------

    private ConstraintLayout main_layout;
    private ConstraintSet constraintSet;

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

        setCategories();
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
        constraintSet.connect(R.id.new_item_recycler, ConstraintSet.TOP, R.id.radio_income_expense, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.new_item_recycler, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(R.id.new_item_recycler, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        constraintSet.applyTo(main_layout);
    }
}
