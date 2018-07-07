package com.mayanksharma.instagramclone.models;

public class User {
    private String username;
    private long phone_number;
    private String user_id;
    private String email;

    public User(String username, long phone_number, String user_id, String email) {
        this.username = username;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.email = email;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
