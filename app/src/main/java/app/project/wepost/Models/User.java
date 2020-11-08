package app.project.wepost.Models;

import java.util.Date;
import java.util.HashMap;

public class User {
    private String id;
    private String username;
    private String fullName;
    private String email;
    private String profilePicture;
    private String createdAt;

    public User(String uId, String username, String fullName, String email){
        this.id = uId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = new Date().toString();
    }

    public HashMap toMap() {
        HashMap userMap = new HashMap();

        userMap.put("uId", id);
        userMap.put("username", username);
        userMap.put("fullname", fullName);
        userMap.put("email", email);
        userMap.put("createdAt", createdAt);

        return userMap;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPassword(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
