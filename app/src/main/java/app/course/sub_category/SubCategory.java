package app.course.sub_category;

public class SubCategory {
    private String name;
    private String date_last_entry;
    private String sum;

    public SubCategory(String name, String date_last_entry, String sum) {
        this.name = name;
        this.date_last_entry = date_last_entry;
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_last_entry() {
        return date_last_entry;
    }

    public void setDate_last_entry(String date_last_entry) {
        this.date_last_entry = date_last_entry;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
