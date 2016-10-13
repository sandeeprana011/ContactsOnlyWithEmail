package com.rana.contactswithemail.structure;

/**
 * Created by sandeeprana on 13/10/16.
 * License is only applicable to individuals and non-profits
 * and that any for-profit company must
 * purchase a different license, and create
 * a second commercial license of your
 * choosing for companies
 */

public class Contact {
    private String mobile;
    private String email;
    private String name;
    private String photoId;
    private String lastContactTime;

    public Contact(String mobile, String email, String name, String photoId, String lastContactTime) {
        this.mobile = mobile;
        this.email = email;
        this.name = name;
        this.photoId = photoId;
        this.lastContactTime = lastContactTime;
    }


    public String getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(String lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
