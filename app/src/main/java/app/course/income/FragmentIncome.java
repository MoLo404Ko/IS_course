package app.course.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import app.course.R;
import app.course.category.CategoryAdapter;

public class FragmentIncome extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        setRecyclerView(view);
    }

    /**
     * Установка RecyclerView
     */
    private void setRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.category_income_recycler);
        Bundle args = this.getArguments();
    }
}
