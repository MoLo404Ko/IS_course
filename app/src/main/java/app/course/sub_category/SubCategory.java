package app.course.sub_category;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SubCategory implements Parcelable {
    private String name;
    private String date_last_entry;
    private String sum;
    private int id_category;

    public SubCategory(String name, String date_last_entry, String sum, int id_category) {
        this.name = name;
        this.sum = sum;
        this.date_last_entry = date_last_entry;
        this.id_category = id_category;
    }

    protected SubCategory(Parcel in) {
        name = in.readString();
        date_last_entry = in.readString();
        sum = in.readString();
        id_category = in.readInt();
    }

    public static final Creator<SubCategory> CREATOR = new Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel in) {
            return new SubCategory(in);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_last_entry() {
        return date_last_entry;
    }

    public String getSum() {
        return sum;
    }

    public int getId_category() {return id_category;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(date_last_entry);
        parcel.writeString(sum);
        parcel.writeInt(id_category);
    }
}
