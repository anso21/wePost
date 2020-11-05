package app.project.wepost.Models;

public class Posts {

    private int id;
    private int userId;
    private String body;
    private String picture;
    private String video;
    private String addAt;
    private String updateAt;
    private Boolean viewed;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getVideo() {
        return this.video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAddAt() {
        return this.addAt;
    }

    public void setAddAt(String addAt) {
        this.addAt = addAt;
    }

    public String getUpdateAt() {
        return this.updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public Boolean isViewed() {
        return this.viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

}
