package app.course.category;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Category implements Parcelable {
    private Drawable bg_color_category;
    private Drawable icon_category;
    private int sum_category;
    private String category_procent;
    private String name_category;

    public Category(Drawable bg_color_category, Drawable icon_category, int sum_category, String category_procent,
                           String name_category) {
        this.bg_color_category = bg_color_category;
        this.icon_category = icon_category;
        this.sum_category = sum_category;
        this.category_procent = category_procent;
        this.name_category = name_category;
    }

    protected Category(Parcel in) {
        sum_category = in.readInt();
        category_procent = in.readString();
        name_category = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public Drawable getBg_color_category() {
        return bg_color_category;
    }

    public Drawable getIcon_category() {
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


    public void setSum_category(int sum_category) {
        this.sum_category = sum_category;
    }

    public void setCategory_procent(String category_procent) {
        this.category_procent = category_procent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(sum_category);
        parcel.writeString(category_procent);
        parcel.writeString(name_category);
    }
}
