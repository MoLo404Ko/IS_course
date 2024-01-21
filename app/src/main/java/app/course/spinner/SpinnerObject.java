package app.course.spinner;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SpinnerObject implements Parcelable {
    private String name;
    private String sum;

    public SpinnerObject(String name, String sum) {
        this.name = name;
        this.sum = sum;
    }

    protected SpinnerObject(Parcel in) {
        name = in.readString();
        sum = in.readString();
    }

    public static final Creator<SpinnerObject> CREATOR = new Creator<SpinnerObject>() {
        @Override
        public SpinnerObject createFromParcel(Parcel in) {
            return new SpinnerObject(in);
        }

        @Override
        public SpinnerObject[] newArray(int size) {
            return new SpinnerObject[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(sum);
    }
}
