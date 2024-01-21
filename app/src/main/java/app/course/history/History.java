package app.course.history;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class History implements Parcelable {
    private String name_category;
    private String bg_color;

    private int icon;
    private int id_category;
    private int sum;

    public History(String name_category, String bg_color, int icon, int id_category, int sum) {
        this.name_category = name_category;
        this.bg_color = bg_color;
        this.icon = icon;
        this.id_category = id_category;
        this.sum = sum;
    }

    protected History(Parcel in) {
        name_category = in.readString();
        bg_color = in.readString();
        icon = in.readInt();
        id_category = in.readInt();
        sum = in.readInt();
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public String getName_category() {
        return name_category;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBg_color() {
        return bg_color;
    }

    public int getId_category() {
        return id_category;
    }

    public int getSum() { return sum; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name_category);
        parcel.writeString(bg_color);
        parcel.writeInt(icon);
        parcel.writeInt(id_category);
        parcel.writeInt(sum);
    }
}
