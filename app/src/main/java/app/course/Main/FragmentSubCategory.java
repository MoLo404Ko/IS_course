package app.course.Main;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.course.Queries;
import app.course.R;
import app.course.authorization.DataBaseHandler;
import app.course.category.Category;
import app.course.category.CategoryAdapter;
import app.course.sub_category.DialogAddSubCategory;
import app.course.sub_category.SubAdapter;
import app.course.sub_category.SubCategory;

public class FragmentSubCategory extends Fragment {
    private int pos;

    private ConstraintLayout main_category;
    private ConstraintLayout fragment_sub_categories;
    private RelativeLayout sub_add_btn;

    private Bundle bundle = new Bundle();
    private ArrayList<Drawable> icons;
    private ArrayList<SubCategory> sub_categories;
    private SubAdapter adapter = null;

    private ArrayList<String> names;
    private ArrayList<String> date_last_entry;
    private ArrayList<Integer> sum;
    private ListView sub_categories_list;

    private TextView main_category_name;
    private TextView main_category_sum;
    private ImageView main_sub_category_icon;
    private ImageButton btn_close;

    private DataBaseHandler dataBaseHandler = DataBaseHandler.getDataBaseHadler();
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private ExecutorService executorService;
    private Handler handler;

    private int current_sum;
    private int sum_category;
    private int id_category;

    public FragmentSubCategory(ArrayList<Drawable> icons, int pos) {
        this.icons = icons;
        this.pos = pos;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("add_sub_key", this,
                (requestKey, result) -> {

                    String name = result.getString("name");
                    String sum = result.getString("sum");
                    String date = result.getString("date");
                    int id_category = result.getInt("id_category");

                    sub_categories.add(new SubCategory(name, date, sum, id_category));

                    if (adapter == null) {
                        adapter = new SubAdapter(getActivity(), R.layout.list_sub_item, sub_categories);
                        sub_categories_list.setAdapter(adapter);
                    }

                    current_sum += Integer.parseInt(sum);

                    main_category_sum.setText(String.valueOf(current_sum));
                    setHeightListView(sub_categories_list);
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        init(view);

        btn_close.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    if (connection != null) {
                        preparedStatement = connection.prepareStatement(Queries.updateSumOfSubCategory());
                        preparedStatement.setInt(1, current_sum);
                        preparedStatement.setInt(2, id_category);
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    Log.d("MyLog", e.getMessage());
                    e.printStackTrace();
                }
                finally {
                    try {
                        if (connection != null) dataBaseHandler.closeConnect(connection);
                        if (preparedStatement != null) preparedStatement.close();
                        if (resultSet != null) resultSet.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

//            getActivity().findViewById(R.id.piechart_layout).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.main_fragment).setVisibility(View.VISIBLE);
//            getActivity().findViewById(R.id.total_sum).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.shadow_layout).setVisibility(View.VISIBLE);
//            getActivity().findViewById(R.id.buttons_linear).setVisibility(View.VISIBLE);

            getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();

        });

        sub_add_btn.setOnClickListener(v -> {
            ArrayList<String> names = new ArrayList<>();

            for (SubCategory subCategory: sub_categories) names.add(subCategory.getName());

            DialogAddSubCategory dialogAddSubCategory = new DialogAddSubCategory();
            dialogAddSubCategory.setCancelable(false);
            dialogAddSubCategory.show(getParentFragmentManager(), "add_sub_category");

            bundle.putStringArrayList("sub_category_names", names);
            bundle.putInt("pos", pos);
            bundle.putInt("current_sum", sum_category);

            dialogAddSubCategory.setArguments(bundle);
        });

        fragment_sub_categories.setMinHeight(1000);
        return view;
    }

    private void init(View view) {
        sub_add_btn = view.findViewById(R.id.sub_add_btn);
        fragment_sub_categories = view.findViewById(R.id.fragment_sub_categories);
        btn_close = view.findViewById(R.id.btn_close_sub_category);

        sub_categories = new ArrayList<>();
        names = new ArrayList<>();
        sum = new ArrayList<>();
        date_last_entry = new ArrayList<>();

        executorService = Executors.newSingleThreadExecutor();
        handler = new android.os.Handler(Looper.getMainLooper());

        bundle = this.getArguments();

        main_category = view.findViewById(R.id.main_category_sub_category);
        main_category_name = view.findViewById(R.id.main_sub_category_name);
        main_category_sum = view.findViewById(R.id.main_sub_category_sum);
        main_sub_category_icon = view.findViewById(R.id.main_sub_category_icon);

        sub_categories_list = view.findViewById(R.id.sub_category_listview);

        setMainCategory(bundle);

        if (sub_categories_list.getCount() == 0) {
            setSubListCategories(bundle);
        }

        else {
            for (int i = 0; i < sub_categories.size(); i++) {
                Log.d("MyLog", sub_categories.get(i).getName());
            }
        }
    }

    /**
     * Метод, устанавливающий выбранную категорию
     * @param bundle
     */
    private void setMainCategory(Bundle bundle) {
        int color = Color.parseColor((String) bundle.get("color_sub"));
        sum_category = (int)bundle.get("sum_sub");

        current_sum = sum_category;

        String name = (String) bundle.get("name_sub");
        Drawable id_icon = icons.get((int)bundle.get("id_icon_sub"));

        main_category.setBackgroundTintList(ColorStateList.valueOf(color));
        main_category.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.shape_item_bg));
        main_category_name.setText(name);
        main_category_sum.setText(String.valueOf(sum_category));
        main_sub_category_icon.setBackground(id_icon);
    }


    /**
     * Метод, выгружающий подкатегории из БД
     * @param bundle
     */
    private void setSubListCategories(Bundle bundle) {
        executorService.execute(() -> {
            try {
                boolean isFill = false;
                id_category = (int)bundle.get("id_category");
                connection = dataBaseHandler.connect(connection);

                preparedStatement = connection.prepareStatement(Queries.getSubCategories());
                preparedStatement.setInt(1, id_category);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    names.add(resultSet.getString(1));
                    sum.add(resultSet.getInt(2));
                    date_last_entry.add(String.valueOf(resultSet.getDate(3)));
                    isFill = true;
                }

                if (isFill) {
                    for (int i = 0; i < names.size(); i++) {
                        sub_categories.add(new SubCategory(names.get(i), date_last_entry.get(i), String.valueOf(sum.get(i)), id_category));
                    }

                    handler.post(() -> {
                        adapter = new SubAdapter(getContext(), R.layout.list_sub_item, sub_categories);
                        sub_categories_list.setAdapter(adapter);
                        setHeightListView(sub_categories_list);
                    });
                }
            }
            catch (SQLException | ClassNotFoundException e) {
                Log.d("MyLog", e.getMessage());
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    /**
     * Метод, устанавливающий высоту для списка подкатегорий
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



}