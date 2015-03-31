package com.google.riosport.elements;

/**
 * Created by Rémi Prévost on 08/11/2014.
 */
public class Practiced {
    private String sport = "";
    private String level = "";

    public Practiced(String sport, String level) {
        this.sport = sport;
        this.level = level;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Practiced{" +
                "sport='" + sport + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
