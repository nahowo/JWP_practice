package web_application_server.model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean login(String password) {
        return this.password.equals(password);
    }

    public boolean isSameUser(User user) {
        return userId.equals(user.userId);
    }

    public void update(User user) {
        this.userId = user.userId;
        this.password = user.password;
        this.name = user.name;
        this.email = user.email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
