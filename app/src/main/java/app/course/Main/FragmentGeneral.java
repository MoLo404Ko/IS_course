package app.course.Main;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.category.Category;
import app.course.category.CategoryPrepare;
import app.course.category.CategoryAdapter;

public class FragmentGeneral extends Fragment {
    private ArrayList<CategoryPrepare> categories_income_prepare;
    private ArrayList<CategoryPrepare> categories_expense_prepare;
    private ArrayList<Category> categories_income;
    private ArrayList<Category> categories_expense;

    private CategoryAdapter categories_income_adapter;
    private CategoryAdapter categories_expense_adapter;

    private ListView category_income_list;
    private ListView category_expense_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        categories_income = new ArrayList<>();
        categories_expense = new ArrayList<>();

        category_income_list = view.findViewById(R.id.category_income_list);
        category_expense_list = view.findViewById(R.id.category_expense_list);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            categories_income_prepare = (ArrayList<CategoryPrepare>) bundle.getSerializable("categories_income");
            categories_expense_prepare = (ArrayList<CategoryPrepare>) bundle.getSerializable("categories_expense");
        }

        setCategoryData(categories_income_prepare, categories_income, categories_income_adapter, category_income_list);
        setCategoryData(categories_expense_prepare, categories_expense, categories_expense_adapter, category_expense_list);

        setHeightListView(category_expense_list);
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * Метод, загружающий данные в категории
     * 1. Загружается икнока по ID и устанавливается цвет на шаблон
     * 2. Создается новая категория
     * 3. Настравивается адаптер
     * @param prepare_categories подготовленные данные для ввода в список
     * @param categories категории, в которые добавляются данные
     * @param adapter адаптер списка
     * @param listView отображаемый список категорий
     */
    private void setCategoryData(ArrayList<CategoryPrepare> prepare_categories, ArrayList<Category>
            categories, CategoryAdapter adapter, ListView listView) {
        for (int i = 0; i < prepare_categories.size(); i++) {
            Drawable item = AppCompatResources.getDrawable(getActivity().getBaseContext(), R.drawable.shape_item_bg);
            Drawable icon = getActivity().getBaseContext().getResources().getDrawable(prepare_categories.
                    get(i).getIcon_category(), getActivity().getBaseContext().getTheme());

            item.setTint(Color.parseColor(prepare_categories.get(i).getBg_color_category()));

            categories.add(new Category(item, icon, prepare_categories.get(i).getSum_category(),
                    prepare_categories.get(i).getCategory_procent(),
                    prepare_categories.get(i).getName_category()));

            adapter = new CategoryAdapter(getActivity(), R.layout.list_item, categories);
            listView.setAdapter(adapter);
        }
    }
    // ---------------------------------------------------------------------------------------------


    // ---------------------------------------------------------------------------------------------
    /**
     * Метод, устанавливающий высоту для списка категорий
     * @param listView отображаемый список категорий
     */
    private void setHeightListView(ListView listView) {
        int totalHeight = 0;
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) return;

        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    // ---------------------------------------------------------------------------------------------
}
