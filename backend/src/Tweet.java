import com.google.gson.Gson;

public class Tweet {
    String id_str;
    String text;
    Coordinates coordinates;
    Coordinates bounding_box;
    String timestamp_ms;
    String title;
    boolean truncated;
    String created_at;
    String source;

    public static Tweet parseTweet(String tweet) {
        return new Gson().fromJson(tweet, Tweet.class);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("id_str: " + id_str + "\n");
        s.append("text: " + text + "\n");
        s.append("title: " + title + "\n");
        s.append("truncated: " + truncated + "\n");
        s.append("created_at: " + created_at + "\n");
        s.append("source: " + source + "\n");
        s.append("coordinates: " + coordinates + "\n");
        s.append("bounding_box: " + bounding_box + "\n");
        return s.toString();
    }
}
