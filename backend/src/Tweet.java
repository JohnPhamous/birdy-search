import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Tweet {
    String id_str;
    String text;
    String timestamp_ms;
    String title;
    Coordinates location;
    String geoJson;
    boolean truncated;
    String created_at;
    String source;
    User user;

    public static Tweet parseTweet(String tweet) {
        Tweet t = new Gson().fromJson(tweet, Tweet.class);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(tweet).getAsJsonObject();

        if(!obj.get("coordinates").isJsonNull()) {
            // Get location from "coordinates" object
            JsonArray pair = obj
                    .get("coordinates").getAsJsonObject()
                    .get("coordinates").getAsJsonArray();
            t.location = new Coordinates(pair.get(1).getAsDouble(), pair.get(0).getAsDouble());
            t.geoJson = obj.get("coordinates").getAsJsonObject().toString();
        } else {
            try {
                JsonObject place = obj.get("place").getAsJsonObject();
                JsonArray coords = place
                        .get("bounding_box").getAsJsonObject()
                        .get("coordinates").getAsJsonArray()
                        .get(0).getAsJsonArray();

                double avgLng = 0, avgLat = 0;
                for(int i = 0; i < coords.size(); ++i) {
                    JsonArray pair = coords.get(i).getAsJsonArray();
                    avgLng += pair.get(0).getAsDouble();
                    avgLat += pair.get(1).getAsDouble();
                }
                avgLng /= coords.size();
                avgLat /= coords.size();
                t.location = new Coordinates(avgLat, avgLng);
                t.geoJson = place.get("bounding_box").getAsJsonObject().toString();
            } catch(IllegalStateException e) {
                System.out.println("Failed to parse location from tweet: " + e.getMessage());
                System.out.println(tweet);
            }
        }
        return t;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("id_str: " + id_str + "\n");
        s.append("text: " + text + "\n");
        s.append("title: " + title + "\n");
        s.append("truncated: " + truncated + "\n");
        s.append("created_at: " + created_at + "\n");
        s.append("source: " + source + "\n");
        s.append("location: " + location + "\n");

        return s.toString();
    }
}
