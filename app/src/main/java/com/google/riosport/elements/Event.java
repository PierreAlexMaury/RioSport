package com.google.riosport.elements;

import com.google.riosport.webservice.WebServiceException;

import java.util.Calendar;

/**
 * Created by Rémi Prévost on 11/11/2014.
 */
public class Event {
    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;

    private int id_event = -1;
    private String sport = "";
    private int owner = -1;
    private String visibility = "";
    private String date_time = "0000-00-00 00:00:00";
    private int duration = -1;
    private String location = "";
    private double longitude = 0.0;
    private double latitude = 0.0;
    private String description = "";

    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int hour = -1;
    private int minute= -1;

    public Event() {}

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getDateTime() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setVisibility(int visibility) throws WebServiceException {
        if (visibility == Event.PUBLIC)
            this.visibility = "public";
        else if(visibility == Event.PRIVATE)
            this.visibility = "private";
        else
            throw new WebServiceException("Wrong visibility parameter");
    }

    public String getVisibility() {
        return visibility;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        setDateTime();
    }

    public void setHour(int hour) {
        this.hour = hour;
        setDateTime();
    }

    public void setDay(int day) {
        this.day = day;
        setDateTime();
    }

    public void setMonth(int month) {
        this.month = month;
        setDateTime();
    }

    public void setYear(int year) {
        this.year = year;
        setDateTime();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getMinutes() {
        int calendar_month;
        if (month == 1)
            calendar_month = Calendar.JANUARY;
        else if (month == 2)
            calendar_month = Calendar.FEBRUARY;
        else if (month == 3)
            calendar_month = Calendar.MARCH;
        else if (month == 4)
            calendar_month = Calendar.APRIL;
        else if (month == 5)
            calendar_month = Calendar.MAY;
        else if (month == 6)
            calendar_month = Calendar.JUNE;
        else if (month == 7)
            calendar_month = Calendar.JULY;
        else if (month == 8)
            calendar_month = Calendar.AUGUST;
        else if (month == 9)
            calendar_month = Calendar.SEPTEMBER;
        else if (month == 10)
            calendar_month = Calendar.OCTOBER;
        else if (month ==11)
            calendar_month = Calendar.NOVEMBER;
        else if (month == 12)
            calendar_month = Calendar.DECEMBER;

        Calendar cal = Calendar.getInstance();
        //cal.set(Calendar.MONTH, );
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return year*365;
    }

    public int getDuration() {
        return duration;
    }

    public void setDurationDay(int days) {
        if (this.duration == -1)
            duration = days*24*60;
        else
            duration+=days*24*60;
    }

    public void setDurationHour(int hours) {
        if (this.duration == -1)
            duration = hours*60;
        else
            duration+=hours*60;
    }

    public void setDurationMinute(int minutes) {
        if (this.duration == -1)
            duration = minutes;
        else
            duration+=minutes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void setDateTime() {
        String year_string;
        if (this.year == -1)
            year_string = "00000";
        else
            year_string = ""+this.year;

        String month_string;
        if (this.month == -1)
            month_string = "00";
        else if (this.month < 10)
            month_string = "0"+this.month;
        else
            month_string = ""+this.month;

        String day_string;
        if (this.day == -1)
            day_string = "00";
        else if (this.day < 10)
            day_string = "0"+this.day;
        else
            day_string = ""+this.day;

        String hour_string;
        if (this.hour == -1)
            hour_string = "00";
        else if (this.hour < 10)
            hour_string = "0"+this.hour;
        else
            hour_string = ""+this.hour;

        String minute_string;
        if (this.minute == -1)
            minute_string = "00";
        else if (this.minute < 10)
            minute_string = "0"+this.minute;
        else
            minute_string = ""+this.minute;

        this.date_time = year_string+"-"+month_string+"-"+day_string+" "+hour_string+":"+minute_string+":"+"00";
    }

    @Override
    public String toString() {
        return "Event{" +
                "id_event=" + id_event +
                ", sport='" + sport + '\'' +
                ", owner=" + owner +
                ", visibility='" + visibility + '\'' +
                ", date_time='" + date_time + '\'' +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", description='" + description + '\'' +
                '}';
    }
}