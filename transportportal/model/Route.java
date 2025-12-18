package com.transportportal.model;

public class Route {

    private int id;
    private String origin;
    private String destination;
    private double distanceKm;
    private double baseFare;

    public Route() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    @Override
    public String toString() {
        return "Route{id=" + id + ", origin='" + origin + "', destination='" +
                destination + "', distanceKm=" + distanceKm + ", baseFare=" + baseFare + "}";
    }
}
