package com.project.osproject;

public class FireBaseUser {
    private String username, email, phoneNumber;

    public FireBaseUser(){}

    public FireBaseUser(String name,String mail, String phone){
        username = name;
        email = mail;
        phoneNumber = phone;
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
}
