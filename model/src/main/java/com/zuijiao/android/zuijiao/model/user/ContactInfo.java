package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 4/28/15.
 */
public class ContactInfo {
    @SerializedName("mobile")
    private String phoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("isEmailBind")
    private String isEmailBound;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getIsEmailBound() {
        return isEmailBound;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
