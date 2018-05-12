public class User {
    public String id;
    public String name;
    public String screen_name;
    public String location;
    public String url;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", location='" + location + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
