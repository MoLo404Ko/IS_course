package app.course.Main;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.course.Queries;
import app.course.R;
import app.course.authorization.DataBaseHandler;
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
    private ArrayList<String> removed_items;
    private ArrayList<String> date_last_entry;
    private ArrayList<Integer> id_sub_categories;
    private ArrayList<Integer> sum;
    private RecyclerView sub_categories_recycler;

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
    private String name;

    public FragmentSubCategory(ArrayList<Drawable> icons, int pos) {
        this.icons = icons;
        this.pos = pos;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("setNewSumCategory", this, (requestKey, result) -> {
            int sum = result.getInt("sum");
            int pos = result.getInt("pos");

            current_sum = Integer.parseInt(main_category_sum.getText().toString()) + sum;
            sub_categories.get(pos).setSum(String.valueOf(current_sum));

            String new_sum = String.valueOf(Integer.parseInt(main_category_sum.getText().toString()) + sum);
            main_category_sum.setText(new_sum);

            adapter.notifyDataSetChanged();

            Bundle args = new Bundle();
            args.putInt("id_category", id_category);
            args.putInt("pos", pos);
            args.putParcelableArrayList("sub_categories", sub_categories);
        });

        getParentFragmentManager().setFragmentResultListener("add_sub_key", this,
                (requestKey, result) -> {
                    String name = result.getString("name");
                    String sum = result.getString("sum");
                    String date = result.getString("date");
                    int id_category = result.getInt("id_category");

                    sub_categories.add(new SubCategory(name, date, sum, id_category));
//                    if (adapter == null) {
                        adapter = new SubAdapter(getContext(), sub_categories, getParentFragmentManager());
                        sub_categories_recycler.setAdapter(adapter);
//                    }

                    current_sum += Integer.parseInt(sum);

                    main_category_sum.setText(String.valueOf(current_sum));
                    sub_categories_recycler.getLayoutParams().height = 1000;
//                    setHeightListView(sub_categories_list);
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

        getActivity().findViewById(R.id.main_fragment).setVisibility(View.GONE);
        getActivity().findViewById(R.id.shadow_layout).setVisibility(View.GONE);

        try {
            init(view);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        btn_close.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    if (connection != null) {
                        if (!removed_items.isEmpty()) {
                            preparedStatement = connection.prepareStatement(Queries.removeItemFromSubCategory());

                            for (int i = 0; i < removed_items.size(); i++) {
                                preparedStatement.setString(1, removed_items.get(i));
                                preparedStatement.setInt(2, id_category);
                                preparedStatement.executeUpdate();
                                preparedStatement.clearParameters();
                            }
                        }
                        preparedStatement = connection.prepareStatement(Queries.updateSumOfCategory());

                        preparedStatement.setInt(1, current_sum);
                        preparedStatement.setInt(2, id_category);
                        preparedStatement.executeUpdate();

                        Bundle result = new Bundle();

                        result.putSerializable("sub_categories", sub_categories);
                        result.putInt("id_category", id_category);
                        result.putInt("sum", current_sum);
                        result.putInt("pos", pos);

                        getParentFragmentManager().setFragmentResult("result_sum", result);

                        getParentFragmentManager().beginTransaction().detach(this);
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

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

            getActivity().findViewById(R.id.main_fragment).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.shadow_layout).setVisibility(View.VISIBLE);

            fragmentTransaction.detach(this).commit();

        });

        sub_add_btn.setOnClickListener(v -> {
            int id = bundle.getInt("id_category");

            ArrayList<String> names = new ArrayList<>();

            for (SubCategory subCategory: sub_categories) names.add(subCategory.getName());

            DialogAddSubCategory dialogAddSubCategory = new DialogAddSubCategory();
            dialogAddSubCategory.setCancelable(false);
            dialogAddSubCategory.show(getParentFragmentManager(), "add_sub_category");

            bundle.putStringArrayList("sub_category_names", names);
            bundle.putString("name_category", name);
            bundle.putIntegerArrayList("sub_category_id", id_sub_categories);
            bundle.putInt("pos", pos);
            bundle.putInt("current_sum", current_sum);
            bundle.putInt("id_category", id);

            dialogAddSubCategory.setArguments(bundle);
        });

        fragment_sub_categories.setMinHeight(1000);

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
                int position = viewHolder.getAdapterPosition();
                SubCategory object = sub_categories.get(position);
                current_sum -= Integer.parseInt(object.getSum());
                main_category_sum.setText(String.valueOf(current_sum));

                sub_categories.remove(position);
                removed_items.add(object.getName());
                adapter.notifyItemRemoved(position);

                Bundle result = new Bundle();

                result.putInt("key", id_category);
                result.putParcelable("object", object);
                result.putBoolean("remove", true);
                result.putBoolean("isSubCategory", true);
                result.putInt("pos", position);

                getParentFragmentManager().setFragmentResult("edit_map_of_sub_categories_by", result);

                result.clear();

                Snackbar.make(sub_categories_recycler, object.getName(), Snackbar.LENGTH_SHORT).
                        setAction("восстановить", v -> {
                            current_sum += Integer.parseInt(object.getSum());
                            sub_categories.add(position, object);
                            main_category_sum.setText(String.valueOf(current_sum));
                            adapter.notifyItemInserted(position);

                            removed_items.remove(object.getName());

                            result.putInt("key", id_category);
                            result.putParcelable("object", object);
                            result.putBoolean("remove", false);
                            result.putBoolean("isSubCategory", true);

                            getParentFragmentManager().setFragmentResult("edit_map_of_sub_categories_by", result);
                        }).show();



            }
        }).attachToRecyclerView(sub_categories_recycler);

        return view;
    }

    private void init(View view) throws SQLException, ClassNotFoundException {
        sub_add_btn = view.findViewById(R.id.sub_add_btn);
        fragment_sub_categories = view.findViewById(R.id.fragment_sub_categories);
        btn_close = view.findViewById(R.id.btn_close_sub_category);

        removed_items = new ArrayList<>();
        names = new ArrayList<>();
        sum = new ArrayList<>();
        date_last_entry = new ArrayList<>();
        id_sub_categories = new ArrayList<>();

        executorService = Executors.newSingleThreadExecutor();
        handler = new android.os.Handler(Looper.getMainLooper());

        bundle = this.getArguments();

        main_category = view.findViewById(R.id.main_category_sub_category);
        main_category_name = view.findViewById(R.id.main_sub_category_name);
        main_category_sum = view.findViewById(R.id.main_sub_category_sum);
        main_sub_category_icon = view.findViewById(R.id.main_sub_category_icon);

        sub_categories_recycler = view.findViewById(R.id.sub_category_recycler);

        setMainCategory(bundle);

//        if (sub_categories_list.getAdapter().getItemCount() == 0) {
            setSubListCategories(bundle);
//        }


//
//        else {
//            for (int i = 0; i < sub_categories.size(); i++) {
//                Log.d("MyLog", sub_categories.get(i).getName());
//            }
//        }
    }

    /**
     * Метод, устанавливающий выбранную категорию
     * @param bundle
     */
    private void setMainCategory(Bundle bundle) {
        int color = Color.parseColor((String) bundle.get("color_sub"));
        sum_category = (int)bundle.get("sum_sub");

        current_sum = sum_category;

        name = (String) bundle.get("name_sub");
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
    private void setSubListCategories(Bundle bundle)  {
//        sub_categories = bundle.getParcelableArrayList("sub_categories");
        MainActivity mainActivity = MainActivity.getMainActivity();
        id_category = (int)bundle.get("id_category");
        sub_categories = mainActivity.getMap_of_sub_categories().get(id_category);

        if (sub_categories != null) {
            Log.d("MyLog", "not null");
            adapter = new SubAdapter(getContext(), sub_categories, getParentFragmentManager());

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            sub_categories_recycler.setLayoutManager(linearLayoutManager);
            sub_categories_recycler.setHasFixedSize(false);
            sub_categories_recycler.setItemAnimator(new DefaultItemAnimator());
            sub_categories_recycler.setAdapter(adapter);

            new Thread(() -> {
                try {
                    connection = dataBaseHandler.connect(connection);
                }
                catch (SQLException | ClassNotFoundException e) {
                    Log.d("MyLog", e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }

        else {
            Log.d("MyLog", "is null");
            executorService.execute(() -> {
                try {
                    sub_categories = new ArrayList<>();
                    boolean isFill = false;
                    connection = dataBaseHandler.connect(connection);
                    preparedStatement = connection.prepareStatement(Queries.getSubCategories());
                    preparedStatement.setInt(1, id_category);
                    resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        String[] date_parse;
                        String ready_date;

                        date_parse = resultSet.getDate(3).toString().split("-");
                        ready_date = date_parse[2] + "-" + date_parse[1] + "-" + date_parse[0];

                        names.add(resultSet.getString(1));
                        sum.add(resultSet.getInt(2));
                        id_sub_categories.add(resultSet.getInt(4));
                        date_last_entry.add(ready_date);
                        isFill = true;
                    }

                    if (isFill) {
                        for (int i = 0; i < names.size(); i++) {
                            sub_categories.add(new SubCategory(names.get(i), date_last_entry.get(i), String.valueOf(sum.get(i)), id_category));
                        }
                    }

                    handler.post(() -> {
                        adapter = new SubAdapter(getContext(), sub_categories, getParentFragmentManager());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        sub_categories_recycler.setLayoutManager(linearLayoutManager);
                        sub_categories_recycler.setHasFixedSize(false);
                        sub_categories_recycler.setItemAnimator(new DefaultItemAnimator());
                        sub_categories_recycler.setAdapter(adapter);
    //                        setHeightListView(sub_categories_list);
                    });
                }
                catch (SQLException | ClassNotFoundException e) {
                    Log.d("MyLog", e.getMessage());
                    e.printStackTrace();
                }
            });
            executorService.shutdown();
        }
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