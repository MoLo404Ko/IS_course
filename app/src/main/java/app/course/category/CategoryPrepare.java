package app.course.category;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CategoryPrepare implements Parcelable {
    private String bg_color_category;
    private int icon_category;
    private int sum_category;
    private String category_procent;
    private String name_category;
    private int id_category;

    public CategoryPrepare(String bg_color_category, int icon_category, int sum_category, String category_procent,
                           String name_category, int id_category) {
        this.bg_color_category = bg_color_category;
        this.icon_category = icon_category;
        this.sum_category = sum_category;
        this.category_procent = category_procent;
        this.name_category = name_category;
        this.id_category = id_category;
    }

    protected CategoryPrepare(Parcel in) {
        bg_color_category = in.readString();
        icon_category = in.readInt();
        sum_category = in.readInt();
        category_procent = in.readString();
        name_category = in.readString();
        id_category = in.readInt();
    }

    public static final Creator<CategoryPrepare> CREATOR = new Creator<CategoryPrepare>() {
        @Override
        public CategoryPrepare createFromParcel(Parcel in) {
            return new CategoryPrepare(in);
        }

        @Override
        public CategoryPrepare[] newArray(int size) {
            return new CategoryPrepare[size];
        }
    };

    public String getBg_color_category() {
        return bg_color_category;
    }

    public int getIcon_category() {
        return icon_category;
    }

    public int getSum_category() {
        return sum_category;
    }

    public String getName_category() {
        return name_category;
    }

    public String getCategory_procent() {
        return category_procent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(bg_color_category);
        parcel.writeInt(icon_category);
        parcel.writeInt(sum_category);
        parcel.writeString(category_procent);
        parcel.writeString(name_category);
        parcel.writeInt(id_category);
    }


    public void setSum_category(int sum_category) {
        this.sum_category = sum_category;
    }

    public int getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    public void setCategory_procent(String category_procent) {
        this.category_procent = category_procent;
    }
}
