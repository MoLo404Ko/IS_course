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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import app.course.authorization.Authorization;
import app.course.authorization.DataBaseHandler;
import app.course.category.CategoryPrepare;

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
    private ArrayList<String> names;
    private ArrayList<RelativeLayout> layouts;

    private boolean isNamed = false;
    private boolean hasIcon = false;
    private boolean hasColor = false;
    private boolean prevColor = false;
    private int prevPos = 0;

    private ColorAdapter colorAdapter;

    private int id_icon;
    private Bundle result = new Bundle();
    private Bundle bundle = new Bundle();

    private ArrayList<Integer> id_categories = new ArrayList<>();
    private ArrayList<String> new_names = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_income_category, container, false);
        init(view);

        selectIcon();
        selectColor();

        add_category.setOnClickListener(v -> {
            getName();
            hasColor = dialogAddIncomeCategory.isHasColor();
            if (hasIcon && isNamed && hasColor) {
                if (names != null) {
                    if (!names.contains(name_add_category_income_edit.getText().toString())) {
                        names.add(name_add_category_income_edit.getText().toString());
                        addCategory();
                    }

                    else {
                        Toast.makeText(getContext(), "Такая категория уже существует!", Toast.LENGTH_SHORT).show();
                        Authorization.moveAnim(name_add_category_income_edit);
                    }
                }

                else {
                    addCategory();
                }

            }
            else Toast.makeText(getContext(), "Не все выбрано!", Toast.LENGTH_SHORT).show();
        });

        btn_close_dialog_add_category.setOnClickListener(view1 -> {
            try {
                id_categories = getIdNewSubCategory(new_names);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Bundle args = new Bundle();
            args.putIntegerArrayList("id_categories", id_categories);
            args.putStringArrayList("names", new_names);
            getParentFragmentManager().setFragmentResult("update_id_categories", args);

            dismiss();
        });
        return view;
    }

    private void init(View view) {
        bundle = this.getArguments();
        names = bundle.getStringArrayList("income_category_names");

        btn_close_dialog_add_category = view.findViewById(R.id.btn_close_dialog_add_category);
        color_layout = view.findViewById(R.id.colors_add_category_income);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        color_layout.setLayoutManager(linearLayoutManager);
        SpacingItemDecorator spacingItemDecorator = new SpacingItemDecorator(10);
        color_layout.addItemDecoration(spacingItemDecorator);

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

        new_names.add(name_add_category_income_edit.getText().toString());
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

    // ---------------------------------------------------------------------------------------------
    private ArrayList<Integer> getIdNewSubCategory(ArrayList<String> names) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<ArrayList<Integer>> future = es.submit(new GetIdTask(names));

        es.shutdown();
        return future.get();
    }

    private static class GetIdTask implements Callable<ArrayList<Integer>> {
        private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
        private Connection connection = null;
        private PreparedStatement preparedStatement = null;
        private ResultSet resultSet = null;

        private ArrayList<String> names;
        private ArrayList<Integer> id_categories = new ArrayList<>();
        private String part_of_query = "";


        public GetIdTask(ArrayList<String> names) {
            this.names = names;
        }

        @Override
        public ArrayList<Integer> call() throws Exception {
            for (int i = 0; i < names.size(); i++) {
                if (i != names.size() - 1) part_of_query += "\'" + names.get(i) + "\',";
                else part_of_query += "\'" + names.get(i) + "\'";
            }

            try {
                connection = dataBaseHandler.connect(connection);
                preparedStatement = connection.prepareStatement(Queries.getNewIdCategories(part_of_query));
                preparedStatement.setInt(1, User.getUser().getID_user());

                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) id_categories.add(resultSet.getInt(1));
            }
            catch (SQLException e ) {
                Log.d("MyLog", e.getMessage());
            }
            finally {
                if (connection != null) dataBaseHandler.closeConnect(connection);
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            }

            return id_categories;
        }
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

    public boolean getPrevColor() {
        return prevColor;
    }

    public void setPrevColor(boolean prevColor) {
        this.prevColor = prevColor;
    }


    public int getPrevPos() {
        return prevPos;
    }

    public void setPrevPos(int prevPos) {
        this.prevPos = prevPos;
    }

    public ColorAdapter getColorAdapter() {
        return colorAdapter;
    }

    public void setColorAdapter(ColorAdapter colorAdapter) {
        this.colorAdapter = colorAdapter;
    }
}
