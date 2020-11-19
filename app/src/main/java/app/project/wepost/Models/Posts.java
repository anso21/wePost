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
    private String authorName;
    private String authorProfile;

    public Posts() {
    }

    public Posts(String postid,String userId, String authorName, String authorProfile, String body, String postImage, String createdDate, String createdTime) {
        this.postid = postid;
        this.userId = userId;
        this.authorName = authorName;
        this.authorProfile = authorProfile;
        this.body = body;
        this.postImage = postImage;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }

    public HashMap textPostsMap(){
        HashMap textPostsMap = new HashMap();
        textPostsMap.put("userId",userId);
        textPostsMap.put("body",body);
        textPostsMap.put("authorName",authorName);
        textPostsMap.put("authorProfile",authorProfile);
        textPostsMap.put("createdDate",createdDate);
        textPostsMap.put("createdTime",createdTime);
        return textPostsMap;
    }

    public HashMap imagePostsMap(){
        HashMap imagePostsMap = new HashMap();
        imagePostsMap.put("userId",userId);
        imagePostsMap.put("postImage",postImage);
        imagePostsMap.put("authorName",authorName);
        imagePostsMap.put("authorProfile",authorProfile);
        imagePostsMap.put("createdDate",createdDate);
        imagePostsMap.put("createdTime",createdTime);
        return imagePostsMap;
    }
    public HashMap fullPostsMap(){
        HashMap fullPostsMap = new HashMap();
        fullPostsMap.put("userId",userId);
        fullPostsMap.put("body",body);
        fullPostsMap.put("postImage",postImage);
        fullPostsMap.put("authorName",authorName);
        fullPostsMap.put("authorProfile",authorProfile);
        fullPostsMap.put("createdDate",createdDate);
        fullPostsMap.put("createdTime",createdTime);
        return fullPostsMap;
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

    public String getauthorName() {
        return authorName;
    }

    public void setauthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfile() {
        return authorProfile;
    }

    public void setAuthorProfile(String authorProfile) {
        this.authorProfile = authorProfile;
    }
}
