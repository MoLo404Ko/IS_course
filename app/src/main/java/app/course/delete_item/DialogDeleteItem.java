package app.course.delete_item;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.ICancelToken;

import org.checkerframework.checker.units.qual.C;
import org.eazegraph.lib.charts.PieChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import app.course.Constants;
import app.course.Main.FragmentGeneral;
import app.course.Main.MainActivity;
import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.add_new_item.NewItemAdapter;
import app.course.authorization.DataBaseHandler;
import app.course.category.Category;
import app.course.category.CategoryPrepare;
import app.course.spinner.SpinnerObject;

public class DialogDeleteItem extends DialogFragment {
    private static DialogDeleteItem dialogDeleteItem = new DialogDeleteItem();
    private int choose_pos = -1;
    private NewItemAdapter adapter;
    private ArrayList<CategoryPrepare> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_item, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        setRecycler(view);
        deleteBtn(view);
        closeBtn(view);
    }

    /**
     * Закрытие
     */
    private void closeBtn(View view) {
        ImageButton btn = view.findViewById(R.id.btn_close);
        btn.setOnClickListener(v -> {
            dismiss();
        });
    }

    /**
     * Устанавливаем Recycler
     */
    private void setRecycler(View view) {
        MainActivity mainActivity = MainActivity.getMainActivity();
        categories = mainActivity.getCategories_income();

        ArrayList<Drawable> icons = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for (CategoryPrepare category: categories) {
            icons.add(getResources().getDrawable(Constants.INCOME_CATEGORIES[category.getIcon_category()], getContext().getTheme()));
            names.add(category.getName_category());
        }

        adapter = new NewItemAdapter(icons, names);

        RecyclerView recyclerView = view.findViewById(R.id.delete_recycler);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Удаление категории
     * @param view
     */
    private void deleteBtn(View view) {
        Bundle args = this.getArguments();
        AppCompatButton btn = view.findViewById(R.id.delete_btn);

        btn.setOnClickListener(v -> {
            if (dialogDeleteItem.getChoose_pos() == -1)
                Toast.makeText(getContext(), "Выберите, что удалить", Toast.LENGTH_SHORT).show();
            else {
                args.putInt("pos", dialogDeleteItem.choose_pos);

                // Меняем значение счета
                SpinnerObject spinnerObject = this.getArguments().getParcelable("spinner_object");
                int sum_delete_category = categories.get(dialogDeleteItem.getChoose_pos()).getSum_category();
                int new_sum_for_spinner = Integer.parseInt(spinnerObject.getSum().toString()) - sum_delete_category;

                spinnerObject.setSum(String.valueOf(new_sum_for_spinner));

                args.putParcelable("spinner_object", spinnerObject);
                args.putInt("change_sum", sum_delete_category);

                getParentFragmentManager().setFragmentResult("change_amount", args);
                //

                getParentFragmentManager().setFragmentResult("remove_item", args);
                Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();


                // Апдейт графика
                MainActivity mainActivity = MainActivity.getMainActivity();
                PieChart pieChart = mainActivity.getPieChart();
                mainActivity.updatePieChart(pieChart, categories);

                // Удаление категории
                new Thread(() -> {
                    Connection connection = null;
                    PreparedStatement preparedStatement = null;
                    DataBaseHandler db = DataBaseHandler.getDataBaseHadler();

                    try {
                        connection = db.connect(connection);
                        preparedStatement = connection.prepareStatement(Queries.removeIncome(
                                categories.get(dialogDeleteItem.getChoose_pos()).getName_category()));

                        preparedStatement.setInt(1, User.getUser().getID_user());
                        preparedStatement.executeUpdate();
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Log.d("MyLog", e.getMessage());
                    }
                    finally {
                        try {
                            if (connection != null) connection.close();
                            if (preparedStatement != null) preparedStatement.close();
                        }
                        catch (SQLException e) {
                            Log.d("MyLog", e.getMessage());
                        }
                    }
                }).start();

                new Thread(() -> {
                    Connection connection = null;
                    PreparedStatement preparedStatement = null;
                    DataBaseHandler db = DataBaseHandler.getDataBaseHadler();

                    try {
                        connection = db.connect(connection);
                        preparedStatement = connection.prepareStatement(Queries.updateSumAmount());

                        preparedStatement.setInt(1, new_sum_for_spinner);
                        preparedStatement.setInt(2, User.getUser().getID_user());
                        preparedStatement.setString(3, spinnerObject.getName());

                        preparedStatement.executeUpdate();
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Log.d("MyLog", e.getMessage());
                    }
                    finally {
                        try {
                            if (connection != null) connection.close();
                            if (preparedStatement != null) preparedStatement.close();
                        }
                        catch (SQLException e) {
                            Log.d("MyLog", e.getMessage());
                        }
                    }
                }).start();

                // Удаляем категорию
                categories.remove(dialogDeleteItem.getChoose_pos());
                adapter.notifyItemRemoved(dialogDeleteItem.getChoose_pos());
            }
        });
    }

    public static DialogDeleteItem getDialogDeleteItem() {
        return dialogDeleteItem;
    }

    public int getChoose_pos() {
        return choose_pos;
    }

    public void setChoose_pos(int choose_pos) {
        this.choose_pos = choose_pos;
    }
}
