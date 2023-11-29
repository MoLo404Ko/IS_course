package app.course;

public class User {
    private int ID_user;
    private static User user = new User();

    public int getID_user() {
        return ID_user;
    }

    public void setID_user(int ID_user) {
        this.ID_user = ID_user;
    }

    public static User getUser() {
        return user;
    }
}
