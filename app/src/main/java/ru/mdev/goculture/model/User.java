package ru.mdev.goculture.model;

public class User {

    private static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/goculture-ca6c6.appspot.com/o/avatars%2Fdefault_avatar.png?alt=media&token=078c68c0-08dd-428a-b462-897a85e5e7e2";

    private String username;
    private String email;
    private String avatarUrl;
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
        this.avatarUrl = DEFAULT_AVATAR_URL;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
