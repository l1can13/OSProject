package com.example.osproject;

public class FireBaseUser {
    private String username, email, password, phoneNumber;

    public FireBaseUser(){}

    public FireBaseUser(String name,String mail, String code, String phone){
        username = name;
        email = mail;
        password = code;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
