package com.birdy;

public class Coordinates {
    public double lat;
    public double lng;

    public Coordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String toString() {
        return "<Point: (" + lat + ", " + lng + ")>";
    }
}
