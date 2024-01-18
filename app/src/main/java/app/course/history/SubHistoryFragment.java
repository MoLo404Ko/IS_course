package app.course.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import app.course.R;
import app.course.category.CategoryPrepare;
import app.course.sub_category.SubCategory;

public class SubHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageButton backBtn;
    private ArrayList<CategoryPrepare> categories_income;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_history, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.history_sub_category_recycler);
        backBtn = view.findViewById(R.id.back_btn_sub_history);
        setRecyclerView();
        setClickOnBackBtn();
    }


    /**
     * Наполняем категории по датам
     * @return
     */
    private HashMap<LocalDate, ArrayList<SubCategory>> filter_map() {
        Bundle args = this.getArguments();
        int id_category = args.getInt("id_category");

        FragmentHistory fragmentHistory = FragmentHistory.getFragmentHistory();
        HashMap<LocalDate, ArrayList<SubCategory>> map = fragmentHistory.getMap_of_history();
        HashMap<LocalDate, ArrayList<SubCategory>> filter_map = new HashMap<>();

        for (LocalDate key: map.keySet()) {
            for (int i = 0; i < map.get(key).size(); i++) {
                if (map.get(key).get(i).getId_category() == id_category) {
                    SubCategory object = map.get(key).get(i);
                    if (!filter_map.containsKey(key)) {
                        filter_map.put(key, new ArrayList<>());
                        filter_map.get(key).add(object);
                    }
                    else {
                        filter_map.get(key).add(object);
                    }
                }
            }
        }

        return filter_map;
    }

    /**
     * Метод установки подкатегорий
     */
    private void setRecyclerView() {
        HashMap<LocalDate, ArrayList<SubCategory>> filter_map = filter_map();

        ArrayList<LocalDate> key_set = new ArrayList<>(filter_map.keySet());
        ArrayList<ArrayList<SubCategory>> big_list = new ArrayList<>(filter_map.values());
        int general_size = 0;

        for (LocalDate key: key_set) {
            general_size += filter_map.get(key).size();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);



        SubHistoryAdapter subHistoryAdapter = new SubHistoryAdapter(getContext(),
                 key_set, big_list, general_size);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(subHistoryAdapter);
    }

    /**
     * Закрытие
     */
    private void setClickOnBackBtn() {
        backBtn.setOnClickListener(view -> {
            categories_income = (ArrayList<CategoryPrepare>)this.getArguments().getSerializable("categories_income");

            FragmentHistory fragmentHistory = new FragmentHistory();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

            Bundle args = new Bundle();

            args.putSerializable("categories_income", categories_income);
            fragmentHistory.setArguments(args);

            fragmentTransaction.replace(R.id.fragment_layout, fragmentHistory);
            fragmentTransaction.detach(this);
            fragmentTransaction.commit();
        });
    }
}