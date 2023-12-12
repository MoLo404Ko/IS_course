package app.course.category;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Category {
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

}
