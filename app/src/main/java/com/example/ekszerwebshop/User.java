package com.example.ekszerwebshop;

public class User {
    private String uname;
    private String email;
    private String pwd;

    public User(String uname, String email, String pwd) {
        this.uname = uname;
        this.email = email;
        this.pwd = pwd;
    }

    public String getUname() {
        return uname;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }
}
