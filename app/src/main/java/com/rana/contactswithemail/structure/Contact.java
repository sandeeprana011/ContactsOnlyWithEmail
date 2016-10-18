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
    private String photoUri;
    private String lastContactTime;

    private String dob;
    private String totalTimeFormatted;

    private int totalNoOfTimesCalled = 0;
    //    private long totalCallTime = 0;
//    private long compareParameter = 0;


    private boolean hasBirthday = false;
    private long totalTime;

    public Contact(String mobile, String email, String name, String photoUri, String lastContactTime) {
        this.mobile = mobile;
        this.email = email;
        this.name = name;
        this.photoUri = photoUri;
        this.lastContactTime = lastContactTime;
    }

    public long getComparatorParam() {
        int birthdayCount = 0;
        if (hasBirthday) {
            birthdayCount = 1;
        }

        return getTotalTime() * (1 + birthdayCount) * totalNoOfTimesCalled;
    }


    public String getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(String lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
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

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public boolean isHasBirthday() {
        return hasBirthday;
    }

    public void setHasBirthday(boolean hasBirthday) {
        this.hasBirthday = hasBirthday;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getTotalTimeFormatted() {
        return totalTimeFormatted;
    }

    public void setTotalTimeFormatted(String totalTimeFormatted) {
        this.totalTimeFormatted = totalTimeFormatted;
    }

    public int getTotalNoOfTimesCalled() {
        return totalNoOfTimesCalled;
    }

    public void setTotalNoOfTimesCalled(int totalNoOfTimesCalled) {
        this.totalNoOfTimesCalled = totalNoOfTimesCalled;
    }


}
