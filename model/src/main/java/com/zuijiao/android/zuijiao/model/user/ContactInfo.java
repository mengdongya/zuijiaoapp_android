package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 4/28/15.
 */
public class ContactInfo implements Cloneable {
    @SerializedName("mobile")
    private String phoneNumber;


    @SerializedName("email")
    private String email;
    @SerializedName("isEmailBind")
    private String isEmailBound;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsEmailBound() {
        return isEmailBound;
    }

    public void setIsEmailBound(String isEmailBound) {
        this.isEmailBound = isEmailBound;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
