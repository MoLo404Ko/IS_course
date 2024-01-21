package app.course.history;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import app.course.Main.MainActivity;
import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;
import app.course.category.CategoryPrepare;
import app.course.sub_category.SubCategory;

public class FragmentHistory extends Fragment {
    private static FragmentHistory fragmentHistory = new FragmentHistory();
    private MainActivity mainActivity = MainActivity.getMainActivity();

    private HistoryAdapter historyAdapter;
    private ArrayList<History> list = new ArrayList<>();
    private ArrayList<CategoryPrepare> categories;

    private HashMap<Integer, ArrayList<SubCategory>> map;
    private HashMap<LocalDate, ArrayList<History>> map_of_history;

    private ImageButton backBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        try {
            init(view);
        } catch (ExecutionException | InterruptedException e) {
            Log.d("MyLog", "HistoryFragment/onCreateView/init - ошибка получения имен категорий");
        }

        return view;
    }

    private void init(View view) throws ExecutionException, InterruptedException {
        list = fillCategories();

        map_of_history = mainActivity.getMap_of_history();
        map = mainActivity.getMap_of_sub_categories();
        fragmentHistory.setMap(map);
        fragmentHistory.setMap_of_history(map_of_history);

        setRecyclerView(view);

        backBtn = view.findViewById(R.id.back_btn_history);
        setOnClickBackBtn();
    }


    /**
     * Заполнение списка категорий
     * @return
     */
    private ArrayList<History> fillCategories() {
        Bundle args = this.getArguments();
        categories = (ArrayList<CategoryPrepare>) args.getSerializable("categories_income");

        for (CategoryPrepare c: categories) {
            list.add(new History(c.getName_category(), c.getBg_color_category(), c.getIcon_category(), c.getId_category(), c.getSum_category()));
        }

        return list;
    }

    /**
     * Установка RECYCLER_VIEW
     * @param view
     */
    private void setRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.history_fr_categories_recycler);

        historyAdapter = new HistoryAdapter(getContext(), list, getParentFragmentManager(), this, categories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(historyAdapter);
    }
    // ---------------------------------------------------------------------------------------------

    /**
     * Закрытие
     * @return
     */
    private void setOnClickBackBtn() {
        backBtn.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

            fragmentTransaction.detach(this);
            fragmentTransaction.commit();
            getParentFragmentManager().setFragmentResult("backBtn", null);
        });
    }

    public HashMap<Integer, ArrayList<SubCategory>> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, ArrayList<SubCategory>> map) {
        this.map = map;
    }

    public HashMap<LocalDate, ArrayList<History>> getMap_of_history() {
        return map_of_history;
    }

    public void setMap_of_history(HashMap<LocalDate, ArrayList<History>> map_of_history) {
        this.map_of_history = map_of_history;
    }

    public static FragmentHistory getFragmentHistory() {
        return fragmentHistory;
    }
}
