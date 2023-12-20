package app.course;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.course.authorization.Authorization;

public class DialogAddIncomeCategory extends DialogFragment {
    private static DialogAddIncomeCategory dialogAddIncomeCategory = new DialogAddIncomeCategory();
    private int index_color;
    private RecyclerView color_layout;
    private Button add_category;
    private EditText name_add_category_income_edit;
    private ImageButton icon_1, icon_2, icon_3, icon_4, icon_5, icon_6;
    private ImageButton no_color;
    private ImageButton btn_close_dialog_add_category;
    private ArrayList<String> colors;

    private boolean isNamed = false;
    private boolean hasIcon = false;
    private boolean hasColor = false;

    private ColorAdapter colorAdapter;

    private int id_icon;
    private Bundle result = new Bundle();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_income_category, container, false);
        init(view);

        selectIcon();
        selectColor();

        add_category.setOnClickListener(v -> {
            getName();
            hasColor = dialogAddIncomeCategory.isHasColor();
            if (hasIcon && isNamed && hasColor) addCategory();
            else Toast.makeText(getContext(), "Не все выбрано!", Toast.LENGTH_SHORT).show();
        });

        btn_close_dialog_add_category.setOnClickListener(view1 -> {
            dismiss();
        });
        return view;
    }

    private void init(View view) {
        btn_close_dialog_add_category = view.findViewById(R.id.btn_close_dialog_add_category);
        color_layout = view.findViewById(R.id.colors_add_category_income);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        color_layout.setLayoutManager(linearLayoutManager);
        color_layout.setHasFixedSize(true);

        no_color = view.findViewById(R.id.no_color);

        colors = new ArrayList<>();
        colors.add("#DFFF83");
        colors.add("#D46761");
        colors.add("#565656");
        colors.add("#EE9797");
        colors.add("#D491DF");
        colors.add("#6079FB");

        colorAdapter = new ColorAdapter(colors, getActivity());
        color_layout.setAdapter(colorAdapter);

        icon_1 = view.findViewById(R.id.icon_1);
        icon_2 = view.findViewById(R.id.icon_2);
        icon_3 = view.findViewById(R.id.icon_3);
        icon_4 = view.findViewById(R.id.icon_4);
        icon_5 = view.findViewById(R.id.icon_5);
        icon_6 = view.findViewById(R.id.icon_6);

        add_category = view.findViewById(R.id.add_income_btn);
        name_add_category_income_edit = view.findViewById(R.id.name_add_category_income_edit);
    }

    /**
     * Метод, отвечающий за нажатие кнопки добавить
     * Отправляет выбранные данные (название, иконка, цвет) во FragmentGeneral
     */
    private void addCategory() {
        String color = colors.get(dialogAddIncomeCategory.getIndex_color());
        result.putString("color", color);
        result.putInt("icon", id_icon);
        result.putString("name", name_add_category_income_edit.getText().toString());
        getParentFragmentManager().setFragmentResult("requestKey", result);
    }

    /**
     * Метод, отвечающий за получение названия категории
     * Проверяет наличие названия и его длину
     */
    private void getName() {
        if (name_add_category_income_edit.getText().toString().isEmpty()) {
            name_add_category_income_edit.setBackground(getResources().getDrawable(R.drawable.shape_edit_text_error,
                    getContext().getTheme()));
            Authorization.moveAnim(name_add_category_income_edit);
            isNamed = false;
        }

        else {
            if (name_add_category_income_edit.getText().toString().length() > 20) {
                Toast.makeText(getContext(), "Длина названия не должна превышать 20 символов!",
                        Toast.LENGTH_SHORT).show();
                name_add_category_income_edit.setBackground(getResources().getDrawable(R.drawable.shape_edit_text_error,
                        getContext().getTheme()));
                Authorization.moveAnim(name_add_category_income_edit);
                isNamed = false;
            }

            else {
                name_add_category_income_edit.setBackground(getResources().getDrawable(R.drawable.shape_edit_text,
                        getContext().getTheme()));
                isNamed = true;
            }
        }
    }

    /**
     * Метод, отвечающий за выбор иконки
     * @return
     */
    private void selectIcon() {
        icon_1.setOnClickListener(view -> {
            id_icon = 0;
            hasIcon = true;
        });

        icon_2.setOnClickListener(view -> {
            id_icon = 1;
            hasIcon = true;
        });

        icon_3.setOnClickListener(view -> {
            id_icon = 2;
            hasIcon = true;
        });

        icon_4.setOnClickListener(view -> {
            id_icon = 0;
            hasIcon = true;
        });

        icon_5.setOnClickListener(view -> {
            id_icon = 1;
            hasIcon = true;
        });

        icon_6.setOnClickListener(view -> {
            id_icon = 2;
            hasIcon = true;
        });
    }


    /**
     * Метод, отвечающий за выбор цвета
     */
    private void selectColor() {
        no_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public static DialogAddIncomeCategory getDialogAddIncomeCategory() {
        return dialogAddIncomeCategory;
    }

    public int getIndex_color() {
        return index_color;
    }

    public void setIndex_color(int index_color) {
        this.index_color = index_color;
    }

    public boolean isHasColor() {
        return hasColor;
    }

    public void setHasColor(boolean hasColor) {
        this.hasColor = hasColor;
    }
}
