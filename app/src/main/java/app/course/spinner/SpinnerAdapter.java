package app.course.spinner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import app.course.R;

public class SpinnerAdapter extends ArrayAdapter<SpinnerObject> {

    private LayoutInflater layoutInflater;
    private FragmentManager fragmentManager;
    private SpinnerObject object;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull SpinnerObject[] spinnerObjects,
                          FragmentManager fragmentManager) {
        super(context, resource, spinnerObjects);
        layoutInflater = LayoutInflater.from(context);
        this.fragmentManager = fragmentManager;
        this.object = spinnerObjects[0];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.amount_adapter, null, true);
        SpinnerObject spinnerObject = getItem(position);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView sum = (TextView) view.findViewById(R.id.sum);

        name.setText(spinnerObject.getName());
        sum.setText(spinnerObject.getSum());

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.amount_adapter_with_edit, null,  true);

        SpinnerObject spinnerObject = getItem(position);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView sum = (TextView) convertView.findViewById(R.id.sum);

        ImageButton edit_btn = (ImageButton) convertView.findViewById(R.id.edit_btn);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEditSum dialogEditSum = new DialogEditSum();

                Bundle args = new Bundle();
                args.putParcelable("object", object);
                dialogEditSum.setArguments(args);

                dialogEditSum.show(fragmentManager, "TAG");
            }
        });

        name.setText(spinnerObject.getName());
        sum.setText(spinnerObject.getSum());
        return convertView;
    }
}
