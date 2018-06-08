package com.birdy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tweet {
    String id_str;
    String text;
    String title;
    Coordinates location;
    String geoJson;
    boolean truncated;
    String timestamp_ms;
    long timestamp;
    String created_at;
    String source;
    String quoted_status_id_str;
    User user;
    Entities entities;
    List<String> hashtags;


    public static Tweet parseTweet(String tweet) {
        Tweet t = new Gson().fromJson(tweet, Tweet.class);
        t.timestamp = Long.parseLong(t.timestamp_ms);

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

    public static Tweet fromDocument(Document tweet) {
        Tweet t = new Tweet();

        t.id_str = tweet.get("id");
        t.text = tweet.get("text");
        t.user = new User(tweet.get("user"));
        t.title = tweet.get("title");
        t.timestamp = Long.parseLong(tweet.get("timestamp"));
        t.location = new Coordinates(Double.parseDouble(tweet.get("lat")), Double.parseDouble(tweet.get("lng")));

        IndexableField[] hashtags = tweet.getFields("hashtags");
        t.hashtags = Arrays.stream(hashtags)
            .map(field -> field.stringValue())
            .collect(Collectors.toList());

        return t;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("id_str: " + id_str + "\n");
        s.append("text: " + text + "\n");
        s.append("title: " + title + "\n");
        s.append("truncated: " + truncated + "\n");
        s.append("timestamp: " + timestamp + "\n");
        s.append("source: " + source + "\n");
        s.append("location: " + location + "\n");
        s.append("hashtags: ");

        if(entities != null && entities.hashtags != null) {
            s.append(entities.hashtags.stream()
                    .map(h -> "#" + h.text)
                    .collect(Collectors.joining(", ")));
        }
        s.append("\n");

        return s.toString();
    }
}
