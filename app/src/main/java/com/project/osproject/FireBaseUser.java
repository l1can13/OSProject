package com.project.osproject;

import java.util.ArrayList;

public class FireBaseUser {
    private String username, email, phoneNumber, id;

    public FireBaseUser(){}

    public FireBaseUser(String name,String mail, String phone, String ID){
        username = name;
        email = mail;
        phoneNumber = phone;
        id = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

