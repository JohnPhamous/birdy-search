package com.birdy;

public class User {
    public String id;
    public String name;
    public String screen_name;
    public String location;
    public String url;

    public User(String screen_name) {
        this.screen_name = screen_name;
    }

    @Override
    public String toString() {
        return "birdy.User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", location='" + location + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
