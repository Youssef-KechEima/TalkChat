package youssef.kecheima.topchat_v12.Model;

public class User {
    private String id, user_name, imageUrl, email,sexe, desc;

    public User() {
    }

    public User(String id, String user_name, String imageUrl, String email, String sexe, String desc) {
        this.id = id;
        this.user_name = user_name;
        this.imageUrl = imageUrl;
        this.email = email;
        this.sexe = sexe;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
