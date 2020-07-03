package com.dupat.dupatchat.model;

public class UserInfo {
    public int id;
    public String name,email,password,phone,photo,status,uid,username,online_status,last_seen;

    public UserInfo() {
    }

    public UserInfo(int id, String name, String email, String password, String phone, String photo, String status, String uid, String username, String online_status, String last_seen) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.photo = photo;
        this.status = status;
        this.uid = uid;
        this.username = username;
        this.online_status = online_status;
        this.last_seen = last_seen;
    }

}
