package app.course.menu_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import app.course.R;

public class AmountsFragment extends Fragment {
    public AmountsFragment() {
    }

    public static AmountsFragment newInstance() {
        return new AmountsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_amounts, container, false);
    }
}
