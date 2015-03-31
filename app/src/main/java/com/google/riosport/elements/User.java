package com.google.riosport.elements;

/**
 * Created by Rémi Prévost on 30/10/2014.
 */
public class User {
    private int id_user = -1;
    private String full_name = new String("");
    private String url_avatar = new String("");
    private Gender gender = Gender.undefined;
    private String date_of_birth = new String("0000-00-00");

    public User(int id_user, String full_name, String url_avatar, Gender gender, String date_of_birth) {
        this.id_user = id_user;
        this.full_name = full_name;
        this.url_avatar = url_avatar;
        this.gender = gender;
        if (!date_of_birth.isEmpty())
            this.date_of_birth = date_of_birth;
    }

    public User(String full_name, String url_avatar, Gender gender, String date_of_birth) {
        this.full_name = full_name;
        this.url_avatar = url_avatar;
        this.gender = gender;
        if (!date_of_birth.isEmpty())
            this.date_of_birth = date_of_birth;
    }

    public User() {}

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getUrl_avatar() {
        return url_avatar;
    }

    public void setUrl_avatar(String url_avatar) {
        this.url_avatar = url_avatar;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", full_name='" + full_name + '\'' +
                ", url_avatar='" + url_avatar + '\'' +
                ", gender=" + gender +
                ", date_of_birth='" + date_of_birth + '\'' +
                '}';
    }
}
