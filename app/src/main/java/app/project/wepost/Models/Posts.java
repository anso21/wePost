package app.project.wepost.Models;

import java.util.HashMap;

public class Posts {

    private String postid;
    private String userId;
    private String body;
    private String postImage;
    private String updatedAt;
    private String createdDate;
    private String createdTime;
    private Boolean viewed;

    public Posts() {
    }

    public Posts(String postid,String userId, String body, String createdDate, String createdTime) {
        this.postid = postid;
        this.userId = userId;
        this.body = body;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }

    public HashMap postsMap(){
        HashMap postsMap = new HashMap();
        postsMap.put("userId",userId);
        postsMap.put("userId",userId);
        postsMap.put("body",body);
        postsMap.put("createdDate",createdDate);
        postsMap.put("createdTime",createdTime);
        return postsMap;
    }

    public String getcreatedDate() {
        return createdDate;
    }

    public void setcreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getcreatedTime() {
        return createdTime;
    }

    public void setcreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getupdatedAt() {
        return this.updatedAt;
    }

    public void setupdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean isViewed() {
        return this.viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
