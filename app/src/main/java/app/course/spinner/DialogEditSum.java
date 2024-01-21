package app.course.spinner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import app.course.Main.MainActivity;
import app.course.Queries;
import app.course.R;
import app.course.User;
import app.course.authorization.DataBaseHandler;

public class DialogEditSum extends DialogFragment {
    private SpinnerObject object;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_amount, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        clickOnCloseBtn(view);
        editSum(view);
    }

    private void editSum(View view) {
        EditText sum = view.findViewById(R.id.sum_edit_text);
        AppCompatButton btn = view.findViewById(R.id.add_btn);

        btn.setOnClickListener(v -> {
            if (sum.getText().toString().isEmpty()) Toast.makeText(getContext(), "Введите сумму!", Toast.LENGTH_SHORT).show();
            else {
                Bundle args = this.getArguments();

                object = args.getParcelable("object");
                object.setSum(sum.getText().toString());

                args.clear();
                args.putParcelable("amount_object", object);

                getParentFragmentManager().setFragmentResult("change_amount", args);
                Toast.makeText(getContext(), "Сумма на счете изменена", Toast.LENGTH_SHORT).show();

                new Thread(() -> {
                    Connection connection = null;
                    PreparedStatement preparedStatement = null;
                    DataBaseHandler db = DataBaseHandler.getDataBaseHadler();

                    try {
                       connection = db.connect(connection);

                       preparedStatement = connection.prepareStatement(Queries.updateSumAmount());
                       preparedStatement.setInt(1, Integer.parseInt(sum.getText().toString()));
                       preparedStatement.setInt(2, User.getUser().getID_user());
                       preparedStatement.setString(3, object.getName());

                       preparedStatement.executeUpdate();
                   }
                   catch (SQLException | ClassNotFoundException e) {
                       Log.d("MyLog", e.getMessage());
                       e.printStackTrace();
                   }
                   finally {
                        try {
                           if (connection != null) db.closeConnect(connection);
                           if (preparedStatement != null) preparedStatement.close();
                        }
                        catch (SQLException e) {
                            Log.d("MyLog", e.getMessage());
                            e.printStackTrace();
                        }
                   }
                }).start();
            }
            dismiss();
        });

    }

    private void clickOnCloseBtn(View view) {
        ImageButton btn = view.findViewById(R.id.btn_close_add_dialog);

        btn.setOnClickListener(v -> {
            dismiss();
        });
    }
}
