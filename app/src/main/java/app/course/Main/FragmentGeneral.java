package app.course.Main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import app.course.DialogAddIncomeCategory;
import app.course.Queries;
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
    private TextView incomes_text;
    private TextView expense_text;
    private TextView general_sum;
    private RelativeLayout incomes_add_btn;

    private int income_sum = 0;
    private int expense_sum = 0;

    private FragmentManager fragmentManager;
    private ArrayList<Drawable> icons = new ArrayList<>();
    private Context context;

    private static FragmentGeneral fragmentGeneral;

    private DataBaseHandler dataBaseHandler = null;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getChildFragmentManager().setFragmentResultListener("requestKey", this,
                (requestKey, result) -> {
                    CategoryPrepare object = result.getParcelable("object");
                    categories_income_prepare.add(object);

                    String color = result.getString("color");
                    String name = result.getString("name");
                    int id_icon = result.getInt("icon");

                    Drawable bg = getResources().getDrawable(R.drawable.shape_item_bg, getContext().getTheme());
                    bg.setTint(Color.parseColor(color));

                    categories_income.add(new Category(bg, icons.get(id_icon), 0, "0", name));

                    if (categories_income_adapter == null) {
                        categories_income_adapter = new CategoryAdapter(getActivity(), R.layout.list_item, categories_income);
                        category_income_list.setAdapter(categories_income_adapter);
                    }

                    setHeightListView(category_income_list);
                    categories_income_adapter.notifyDataSetChanged();

                    new Thread(() -> {
                        try {
                            connection = dataBaseHandler.connect(connection);
                            preparedStatement = connection.prepareStatement(Queries.addNewIncomeCategory());
                            preparedStatement.setInt(1, User.getUser().getID_user());
                            preparedStatement.setInt(2, 0);
                            preparedStatement.setString(3, name);

                            preparedStatement.setInt(4, id_icon);
                            preparedStatement.setString(5, color);
                            preparedStatement.executeUpdate();
                        }
                        catch (SQLException | ClassNotFoundException e) {
                            Log.d("MyLog", e.getMessage());
                            e.printStackTrace();
                        }
                    }).start();
                });
    }

    public FragmentGeneral(Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);
        init(view);

        category_income_list.setOnItemClickListener((adapterView, view1, pos, l) -> {
            Bundle bundle = new Bundle();

            if (categories_income_prepare != null) {
                CategoryPrepare object = categories_income_prepare.get(pos);

                String color = object.getBg_color_category();
                String name = object.getName_category();
                int id_icon = object.getIcon_category();

                bundle.putString("color_sub", color);
                bundle.putString("name_sub", name);
                bundle.putInt("id_icon_sub", id_icon);
            }

            FragmentSubCategory fragmentSubCategory = new FragmentSubCategory(icons);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().
                    replace(R.id.sub_fragment, fragmentSubCategory);
            fragmentTransaction.commit();

            fragmentSubCategory.setArguments(bundle);


            getActivity().findViewById(R.id.piechart_layout).setVisibility(View.GONE);
            getActivity().findViewById(R.id.main_fragment).setVisibility(View.GONE);
            getActivity().findViewById(R.id.total_sum).setVisibility(View.GONE);
            getActivity().findViewById(R.id.shadow_layout).setVisibility(View.GONE);
        });

        incomes_add_btn.setOnClickListener(v -> {
            DialogAddIncomeCategory dialogAddIncomeCategory = new DialogAddIncomeCategory();
            dialogAddIncomeCategory.show(getChildFragmentManager(), "tag");
        });

        return view;
    }

    private void init(View view) {
        fragmentGeneral = new FragmentGeneral(getContext(), getParentFragmentManager());
        dataBaseHandler = DataBaseHandler.getDataBaseHadler();

        icons.add(getResources().getDrawable(R.drawable.ic_bag, getContext().getTheme()));
        icons.add(getResources().getDrawable(R.drawable.ic_basket, getContext().getTheme()));
        icons.add(getResources().getDrawable(R.drawable.ic_grow, getContext().getTheme()));

        incomes_add_btn = view.findViewById(R.id.incomes_add_btn);
        general_sum = new TextView(getActivity());

        categories_income = new ArrayList<>();
        categories_expense = new ArrayList<>();

        category_income_list = view.findViewById(R.id.category_income_list);
        category_expense_list = view.findViewById(R.id.category_expense_list);

        incomes_text = view.findViewById(R.id.incomes_text);
        expense_text = view.findViewById(R.id.expenses_text);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            categories_income_prepare = (ArrayList<CategoryPrepare>) bundle.getSerializable("categories_income");
            categories_expense_prepare = (ArrayList<CategoryPrepare>) bundle.getSerializable("categories_expense");
        }

        if (categories_income_prepare != null) {
            if (!categories_income_prepare.isEmpty()) {
                categories_income_adapter = setCategoryData(categories_income_prepare, categories_income, categories_income_adapter, category_income_list);

                for (Category c: categories_income) income_sum += c.getSum_category();
                incomes_text.setText(String.valueOf(income_sum));
                setHeightListView(category_income_list);
            }
        }

        if (categories_expense_adapter != null) {
            if (!categories_expense_adapter.isEmpty()) {
                categories_expense_adapter = setCategoryData(categories_expense_prepare, categories_expense, categories_expense_adapter, category_expense_list);

                for (Category c: categories_expense) expense_sum += c.getSum_category();
                expense_text.setText(String.valueOf(expense_sum));
                setHeightListView(category_expense_list);
            }
        }

        general_sum.setText(String.valueOf(income_sum - expense_sum));
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
    public CategoryAdapter setCategoryData(ArrayList<CategoryPrepare> prepare_categories, ArrayList<Category>
            categories, CategoryAdapter adapter, ListView listView) {
        for (int i = 0; i < prepare_categories.size(); i++) {
            Drawable item = AppCompatResources.getDrawable(getActivity().getBaseContext(), R.drawable.shape_item_bg);
            Drawable icon = icons.get(prepare_categories.get(i).getIcon_category());

            item.setTint(Color.parseColor(prepare_categories.get(i).getBg_color_category()));

            categories.add(new Category(item, icon, prepare_categories.get(i).getSum_category(),
                    prepare_categories.get(i).getCategory_procent(),
                    prepare_categories.get(i).getName_category()));

            adapter = new CategoryAdapter(getActivity(), R.layout.list_item, categories);
            listView.setAdapter(adapter);
        }
        return adapter;
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

        int len = listAdapter.getCount();

        for (int i = 0; i < len;  i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    // ---------------------------------------------------------------------------------------------

    /**
     * Метод, устанавливающий суммы для категорий и общую сумму из MainActivity
     */
    public void setSum(int income_sum, int expense_sum, int general, ArrayList<Integer> incomes_list) {
        DecimalFormat df = new DecimalFormat("#.#");
        int total_sum = 0;
        double procent = 0.0;

        incomes_text.setText(String.valueOf(income_sum));
        expense_text.setText(String.valueOf(expense_sum));
        general_sum.setText(String.valueOf(general));

        for (int i = 0; i < incomes_list.size(); i++) {
            total_sum += incomes_list.get(i);
        }

        for (int i = 0; i < incomes_list.size(); i++) {
            procent = (double) (categories_income.get(i).getSum_category() / total_sum) * 100;
            categories_income.get(i).setSum_category(incomes_list.get(i));
            categories_income.get(i).setCategory_procent(df.format(procent));
        }
        categories_income_adapter.notifyDataSetChanged();
    }

    public static void setFragmentGeneral(FragmentGeneral fragmentGeneral) {
        FragmentGeneral.fragmentGeneral = fragmentGeneral;
    }
}
