package com.caffeine.e_commercedemo.model;

public class UserDetails {

    public UserDetails() {
    }

    String uid, email, pass, acc;

    public UserDetails(String uid, String email, String pass, String acc) {
        this.uid = uid;
        this.email = email;
        this.pass = pass;
        this.acc = acc;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getAcc() {
        return acc;
    }
}
