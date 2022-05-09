package ru.mdev.goculture.model;

public class User {

    private String username;
    private String email;
    private int score;

    public User() {

    }

    public User(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.score = 0;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
