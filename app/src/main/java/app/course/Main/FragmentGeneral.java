package app.course.Main;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import app.course.DialogAddIncomeCategory;
import app.course.Queries;
import app.course.R;
import app.course.SpacingItemDecorator;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.category.Category;
import app.course.category.CategoryPrepare;
import app.course.category.CategoryAdapter;
import app.course.sub_category.SubCategory;

public class FragmentGeneral extends Fragment {
    private ArrayList<SubCategory> sub_categories;
    private ArrayList<CategoryPrepare> categories_income_prepare;
    private ArrayList<CategoryPrepare> categories_expense_prepare;
    private ArrayList<Category> categories_income;
    private ArrayList<Category> categories_expense;
    private ArrayList<Integer> id_categories;
    private ArrayList<String> removed_categories;

    private CategoryAdapter categories_income_adapter;
    private CategoryAdapter categories_expense_adapter;

    private RecyclerView category_income_recycler;
    private ListView category_expense_list;
    private TextView incomes_text;
    private TextView expense_text;
    private TextView general_sum;
    private RelativeLayout incomes_add_btn;

    private int income_sum = 0;
    private int expense_sum = 0;
    private HashMap<Integer, ArrayList<SubCategory>> hash_map_categories;

    private FragmentManager fragmentManager;
    private ArrayList<Drawable> icons = new ArrayList<>();
    private Context context;
    private Handler handler;
    private Bundle bundle;
    private DecimalFormat df = new DecimalFormat("#.#");

    private static FragmentGeneral fragmentGeneral;

    private DataBaseHandler dataBaseHandler = null;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private int id_account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("update_id_categories", this,
                (requestKey, result) -> {
            ArrayList<Integer> id_categories = result.getIntegerArrayList("id_categories");
            ArrayList<String> names = result.getStringArrayList("names");
            ArrayList<CategoryPrepare> temp = new ArrayList<>();

            for (String name: names) {
                for (CategoryPrepare categoryPrepare: categories_income_prepare) {
                    if (categoryPrepare.getName_category().equals(name)) {
                        temp.add(categoryPrepare);
                        break;
                    }
                }
            }

            for (int i = 0; i < temp.size(); i++)
                temp.get(i).setId_category(id_categories.get(i));

                });

        getParentFragmentManager().setFragmentResultListener("changedDate", this,
                (requestKey, result) -> {
            handler.post(() -> {
                ArrayList<Integer> amounts = result.getIntegerArrayList("amounts");
                ArrayList<String> names = result.getStringArrayList("names");
                int sumIndex = 0;
                int generalSum = 0;

                if (names.size() == 0) {
                    for (int i = 0; i < categories_income.size(); i++)
                        categories_income.get(i).setSum_category(0);

                    incomes_text.setText("0");
                }

                else {
                    boolean isHas;

                    for (int index = 0; index < categories_income.size(); index++) {
                        isHas = false;
                        for (String name: names) {
                            if (categories_income.get(index).getName_category().equals(name)) {
                                categories_income.get(index).setSum_category(amounts.get(sumIndex));
                                generalSum += amounts.get(sumIndex);
                                names.remove(name);

                                sumIndex++;
                                isHas = true;
                                break;
                            }
                        }
                        if (!isHas) categories_income.get(index).setSum_category(0);
                    }

                    incomes_text.setText(String.valueOf(generalSum));
                }

                updatePercent(categories_income);
                categories_income_adapter.notifyDataSetChanged();
            });
                });

        getParentFragmentManager().setFragmentResultListener("result_sum", this,
                (requestKey, result) -> {
                    Bundle args = new Bundle();
                    int pos = result.getInt("pos");
                    int id_category = result.getInt("id_category");

                    sub_categories = result.getParcelableArrayList("sub_categories");

                    hash_map_categories.put(id_category, sub_categories);

                    handler.post(() -> {
                        int sum = 0;
                        int diff_sum = 0;

                        if (MainActivity.date.equals("Все время")) {
                            sum = result.getInt("sum");
                            diff_sum = sum - categories_income_prepare.get(pos).getSum_category();
                            Log.d("MyLog", diff_sum + " diff_sum");
                            args.putInt("sum", diff_sum);
                            args.putBoolean("action", true);

                            getParentFragmentManager().setFragmentResult("change_diff_sum", args);
                        }

                        else {
                            String[] date_range = MainActivity.date.split(":");

                            if (date_range.length == 1) {
                                for (SubCategory s: sub_categories) {
                                    if (s.getDate_last_entry().equals(date_range[0])) sum += Integer.parseInt(s.getSum());
                                }

                                diff_sum = sum - categories_income_prepare.get(pos).getSum_category();
                                args.putInt("sum", diff_sum);
                                args.putBoolean("action", true);
                                getParentFragmentManager().setFragmentResult("change_diff_sum", args);
                            }

                            if (date_range.length == 2) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                                LocalDate first_date = LocalDate.parse(date_range[0], formatter);
                                LocalDate second_date = LocalDate.parse(date_range[1], formatter);

                                for (SubCategory s: sub_categories) {
                                    LocalDate date = LocalDate.parse(s.getDate_last_entry(), formatter);
                                    if ((date.isAfter(first_date) || date.isEqual(first_date)) &&
                                            (date.isBefore(second_date) || date.isEqual(second_date))) {
                                        diff_sum = sum - categories_income_prepare.get(pos).getSum_category();
                                        args.putInt("sum", diff_sum);
                                        args.putBoolean("action", true);
                                        getParentFragmentManager().setFragmentResult("change_diff_sum", args);
                                    }
                                }
                            }
                        }

                        categories_income.get(pos).setSum_category(sum);
                        categories_income_prepare.get(pos).setSum_category(sum);

                        income_sum += diff_sum;

                        updatePercent(categories_income);

                        categories_income_adapter.notifyDataSetChanged();
                        incomes_text.setText(String.valueOf(income_sum));
                    });
                });

        getParentFragmentManager().setFragmentResultListener("requestKey", this,
                (requestKey, result) -> {
                    String color = result.getString("color");
                    String name = result.getString("name");
                    int id_icon = result.getInt("icon");

                    Drawable bg = getResources().getDrawable(R.drawable.shape_item_bg, getContext().getTheme());
                    bg.setTint(Color.parseColor(color));

                    if (categories_income_adapter == null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

                        category_income_recycler.setLayoutManager(linearLayoutManager);
                        categories_income_adapter = new CategoryAdapter(getActivity(), categories_income, categories_income_prepare,
                                id_categories, hash_map_categories, icons, fragmentManager);
                        category_income_recycler.setAdapter(categories_income_adapter);
                    }

                    categories_income.add(new Category(bg, icons.get(id_icon), 0, "0", name));
                    categories_income_prepare.add(new CategoryPrepare(color, id_icon, 0, "0", name, 0));

                    category_income_recycler.setAdapter(categories_income_adapter);

//                    setHeightListView(category_income_list);
                    categories_income_adapter.notifyDataSetChanged();

                    try {
                        id_categories.add(updateIdCategories(color, name, id_icon, id_account));
                    } catch (ExecutionException | InterruptedException e) {
                        Log.d("MyLog", e.getMessage());
                        e.printStackTrace();
                    }

                    Toast.makeText(getContext(), "Добавлено!", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePercent(ArrayList<Category> categories_income) {
        int sum_of_categories = 0;
        for (Category category: categories_income) {
            if (category.getSum_category() > 0) sum_of_categories += category.getSum_category();
            else sum_of_categories += category.getSum_category() * (-1);
        }

        for (int i = 0; i < categories_income_prepare.size(); i++) {
            if (sum_of_categories != 0) {
                double percent;
                percent = (double)categories_income.get(i).getSum_category() / sum_of_categories * 100;

                categories_income.get(i).setCategory_procent(df.format(percent));
                categories_income_prepare.get(i).setCategory_procent(df.format(percent));
            }
            else {
                categories_income.get(i).setCategory_procent("0");
                categories_income_prepare.get(i).setCategory_procent("0");
            }
        }
    }

    public FragmentGeneral(Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);
        init(view);

        incomes_add_btn.setOnClickListener(v -> {
            int id_new_category;
            ArrayList<String> names = new ArrayList<>();

            if (categories_income_prepare != null) {
                for (int i = 0; i < categories_income_prepare.size(); i++) {
                    names.add(categories_income_prepare.get(i).getName_category());
                }
            }

            bundle.putStringArrayList("income_category_names", names);

            DialogAddIncomeCategory dialogAddIncomeCategory = new DialogAddIncomeCategory();
            dialogAddIncomeCategory.show(getParentFragmentManager(), "tag");
            dialogAddIncomeCategory.setArguments(bundle);


        });

        return view;
    }

    private void init(View view) {
        hash_map_categories = new HashMap<>();
        removed_categories = new ArrayList<>();
        sub_categories = new ArrayList<>();
//        categories_income_adapter = new CategoryAdapter(getActivity(), categories_income, categories_income_prepare,
//                id_categories, hash_map_categories, icons, fragmentManager);

        handler = new Handler(Looper.getMainLooper());
        id_categories = new ArrayList<>();

        fragmentGeneral = new FragmentGeneral(getContext(), getParentFragmentManager());

        try {
            id_account = fragmentGeneral.getIdAccount();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        dataBaseHandler = DataBaseHandler.getDataBaseHadler();

        icons.add(getResources().getDrawable(R.drawable.ic_bag, getContext().getTheme()));
        icons.add(getResources().getDrawable(R.drawable.ic_basket, getContext().getTheme()));
        icons.add(getResources().getDrawable(R.drawable.ic_grow, getContext().getTheme()));

        incomes_add_btn = view.findViewById(R.id.incomes_add_btn);
        general_sum = new TextView(getActivity());

        categories_income = new ArrayList<>();
        categories_expense = new ArrayList<>();

        category_income_recycler = view.findViewById(R.id.category_income_recycler);
        category_expense_list = view.findViewById(R.id.category_expense_list);

        incomes_text = view.findViewById(R.id.incomes_text);
        incomes_text.setText("0");

        expense_text = view.findViewById(R.id.expenses_text);
        expense_text.setText("0");

        bundle = this.getArguments();

        if (bundle != null) {
            categories_income_prepare = (ArrayList<CategoryPrepare>) bundle.getSerializable("categories_income");
            categories_expense_prepare = (ArrayList<CategoryPrepare>) bundle.getSerializable("categories_expense");

            id_categories = bundle.getIntegerArrayList("id_categories");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        if (categories_income_prepare != null) {
            if (!categories_income_prepare.isEmpty()) {
                setCategoryData(categories_income_prepare, categories_income);
                categories_income_adapter = new CategoryAdapter(getActivity(), categories_income, categories_income_prepare,
                        id_categories, hash_map_categories, icons, fragmentManager);

                updatePercent(categories_income);
                categories_income_adapter.notifyDataSetChanged();

                for (Category c: categories_income) income_sum += c.getSum_category();
                incomes_text.setText(String.valueOf(income_sum));

                category_income_recycler.setLayoutManager(linearLayoutManager);
                category_income_recycler.setAdapter(categories_income_adapter);
//                setHeightListView(category_income_list);
            }
        }

        /**
         * Удаление подкатегории
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Bundle args = new Bundle();
                int position = viewHolder.getAdapterPosition();
                Category object = categories_income.get(position);
                CategoryPrepare object_prepare = categories_income_prepare.get(position);

                categories_income.remove(position);
                categories_income_prepare.remove(position);
                categories_income_adapter.notifyItemRemoved(position);
                removed_categories.add(object.getName_category());

                income_sum -= object_prepare.getSum_category();
                incomes_text.setText(String.valueOf(income_sum));

                args.putInt("sum", object_prepare.getSum_category());
                args.putBoolean("action", false);

                updatePercent(categories_income);
                categories_income_adapter.notifyDataSetChanged();

                getParentFragmentManager().setFragmentResult("change_diff_sum", args);

                args.clear();

                args.putBoolean("isSubCategory", false);
                args.putBoolean("remove", true);
                args.putInt("key", object_prepare.getId_category());

                getParentFragmentManager().setFragmentResult("edit_map_of_sub_categories_by", args);

                args.clear();

                new Thread(() -> {
                    try {
                        Connection connection = null;
                        PreparedStatement preparedStatement;

                        String removed_names_items = "";

                        for (int i = 0; i < removed_categories.size(); i++) {
                            if (i == removed_categories.size() - 1) removed_names_items += "\'" + removed_categories.get(i) + "\'";
                            else removed_names_items += "\'" + removed_categories.get(i) + "\'" + ",";
                        }

                        String query = "DELETE FROM category_income where id_user = " + User.getUser().getID_user() +
                                " and name_category in (" + removed_names_items +")";

                        connection = dataBaseHandler.connect(connection);
                        preparedStatement = connection.prepareStatement(query);

                        preparedStatement.executeUpdate();
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Log.d("MyLog", e.getMessage());
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            if (connection != null) dataBaseHandler.closeConnect(connection);
                            if (preparedStatement != null) preparedStatement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

                Snackbar.make(category_income_recycler, object.getName_category(), Snackbar.LENGTH_SHORT).
                        setAction("восстановить", v -> {
                            categories_income.add(position, object);
                            categories_income_prepare.add(position, object_prepare);

                            income_sum += object_prepare.getSum_category();
                            incomes_text.setText(String.valueOf(income_sum));

                            args.putInt("sum", object_prepare.getSum_category());
                            args.putBoolean("action", true);

                            getParentFragmentManager().setFragmentResult("change_diff_sum", args);

                            args.clear();

                            categories_income_adapter.notifyItemInserted(position);
                            removed_categories.remove(object.getName_category());

                            args.putBoolean("isSubCategory", false);
                            args.putBoolean("remove", false);
                            args.putInt("key", object_prepare.getId_category());

                            getParentFragmentManager().setFragmentResult("edit_map_of_sub_categories_by", args);

                            args.clear();

                            new Thread(() -> {
                                try {
                                    int id_account = getIdAccount();

                                    Connection connection = null;
                                    connection = dataBaseHandler.connect(connection);
                                    PreparedStatement preparedStatement = connection.prepareStatement(Queries.addNewIncomeCategory());
                                    preparedStatement.setInt(1, User.getUser().getID_user());
                                    preparedStatement.setInt(2, object_prepare.getSum_category());
                                    preparedStatement.setString(3, object_prepare.getName_category());
                                    preparedStatement.setInt(4, object_prepare.getIcon_category());
                                    preparedStatement.setString(5, object_prepare.getBg_color_category());
                                    preparedStatement.setInt(6, id_account);

                                    preparedStatement.executeUpdate();
                                    preparedStatement.clearParameters();

                                    String query = "INSERT INTO sub_category_income (ID_category, name_sub_category," +
                                            "sub_sum, date_last_entry) ";
                                    String values = "VALUES (";

                                    query += values;

                                    preparedStatement = connection.prepareStatement(query);
                                    preparedStatement.executeUpdate();
                                    preparedStatement.clearParameters();
                                }
                                catch (ClassNotFoundException | SQLException | ExecutionException |
                                       InterruptedException e) {
                                    Log.d("MyLog", e.getMessage());
                                    e.printStackTrace();
                                }
                                finally {
                                    try {
                                        if (connection != null) dataBaseHandler.closeConnect(connection);
                                        if (preparedStatement != null) preparedStatement.close();
                                    }
                                    catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }).show();
                }
            }).attachToRecyclerView(category_income_recycler);

        general_sum.setText(String.valueOf(income_sum - expense_sum));


        MainActivity mainActivity = MainActivity.getMainActivity();
        TextView total_sum = mainActivity.getTotal_sum();
        total_sum.setText(String.valueOf(income_sum - expense_sum) + "$");
        mainActivity.setTotal_sum(total_sum);
//        Bundle result = new Bundle();
//        result.putInt("sum", income_sum - expense_sum);
//        result.putBoolean("change_date", false);
//        result.putBoolean("action", true);
//
//        getParentFragmentManager().setFragmentResult("change_diff_sum", result);
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * Метод, загружающий данные в категории
     * 1. Загружается икнока по ID и устанавливается цвет на шаблон
     * 2. Создается новая категория
     * 3. Настравивается адаптер
     * @param prepare_categories подготовленные данные для ввода в список
     * @param categories категории, в которые добавляются данные
     */
    public void setCategoryData(ArrayList<CategoryPrepare> prepare_categories, ArrayList<Category>
            categories) {
        for (int i = 0; i < prepare_categories.size(); i++) {
            Drawable item = AppCompatResources.getDrawable(getActivity().getBaseContext(), R.drawable.shape_item_bg);
            Drawable icon = icons.get(prepare_categories.get(i).getIcon_category());

            item.setTint(Color.parseColor(prepare_categories.get(i).getBg_color_category()));

            categories.add(new Category(item, icon, prepare_categories.get(i).getSum_category(),
                    "0", prepare_categories.get(i).getName_category()));
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
     * Метод обновления данных id категорий после добавления
     * @return id категории
     */
    private int updateIdCategories(String color, String name, int id_icon, int id_account)
            throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> future = es.submit(new UpdateIdCategoriesTask(color, name, id_icon, id_account));

        es.shutdown();

        return future.get();
    }

    /**
     * Задача для возвращения добавленного id в БД
     */
    private static class UpdateIdCategoriesTask implements Callable<Integer> {
        private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
        private Connection connection = null;
        private PreparedStatement preparedStatement = null;
        private ResultSet resultSet = null;

        private String color;
        private String name;
        private int id_icon;
        private int id_category;
        private int id_account;

        public UpdateIdCategoriesTask(String color, String name, int id_icon, int id_account) {
            this.color = color;
            this.name = name;
            this.id_icon = id_icon;
            this.id_account = id_account;
        }
        @Override
        public Integer call() {
            try {
                connection = dataBaseHandler.connect(connection);
                preparedStatement = connection.prepareStatement(Queries.addNewIncomeCategory());
                preparedStatement.setInt(1, User.getUser().getID_user());
                preparedStatement.setInt(2, 0);
                preparedStatement.setString(3, name);

                preparedStatement.setInt(4, id_icon);
                preparedStatement.setString(5, color);
                preparedStatement.setInt(6, id_account);
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement(Queries.getCategoryIncome());
                preparedStatement.setInt(1, User.getUser().getID_user());
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    id_category = resultSet.getInt(5);
                }

            }
            catch (SQLException | ClassNotFoundException e) {
                Log.d("MyLog", e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) dataBaseHandler.closeConnect(connection);
                    if (preparedStatement != null) preparedStatement.close();
                    if (resultSet != null) resultSet.close();
                }
                catch (SQLException e) {
                    Log.d("MyLog", e.getMessage());
                    e.printStackTrace();
                }

            }
            return id_category;
        }
    }

    /**
     * Получение ID_account
     */
    public int getIdAccount() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> future = es.submit(new GetIdAccountTask());

        es.shutdown();
        return future.get();
    }

    private class GetIdAccountTask implements Callable<Integer> {
        private int id_account;
        private MainActivity mainActivity = MainActivity.getMainActivity();
        private String name = (String) mainActivity.getDropDown().getSelectedItem();
        @Override
        public Integer call() throws Exception {
            DataBaseHandler db = DataBaseHandler.getDataBaseHadler();
            Connection conn = null;
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                conn = db.connect(conn);
                st = conn.prepareStatement(Queries.getIdAccount());
                st.setInt(1, User.getUser().getID_user());
                st.setString(2, mainActivity.getDropDown().getSelectedItem().toString());

                rs = st.executeQuery();
                while (rs.next()) {
                    id_account = rs.getInt(1);
                    Log.d("MyLog", "result");
                }
            }
            catch (SQLException | ClassNotFoundException e) {
                Log.d("MyLog", e.getMessage());
            }
            finally {
                if (conn != null) db.closeConnect(conn);
                if (st != null) st.close();
                if (rs != null) rs.close();
            }

            return id_account;
        }
    }

    /**
     * При уничтожении активтити выполняется запрос на удаление выбранных категорий из БД
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
