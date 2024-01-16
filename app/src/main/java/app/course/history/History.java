package app.course.history;

import android.graphics.drawable.Drawable;

public class History {
    private String name_sub_category;
    private String bg_color;
    private int icon;
    private int id_category;

    public History(String name_sub_category, String bg_color, int icon, int id_category) {
        this.name_sub_category = name_sub_category;
        this.bg_color = bg_color;
        this.icon = icon;
        this.id_category = id_category;
    }

    public String getName_sub_category() {
        return name_sub_category;
    }

    public void setName_sub_category(String name_sub_category) {
        this.name_sub_category = name_sub_category;
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

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public int getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }
}
