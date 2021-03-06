package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chen Hao on 4/28/15.
 */
public class Profile implements Cloneable ,Serializable{
    @SerializedName("gender")
    private String gender;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("provinceID")
    private Integer provinceId = 0;
    @SerializedName("cityID")
    private Integer cityId = 0;
    @SerializedName("story")
    private String story;
    @SerializedName("tasteTags")
    private ArrayList<String> tasteTags;

    public void setEducationBackground(String educationBackground) {
        this.educationBackground = educationBackground;
    }

    @SerializedName("education")
    private String educationBackground;

    public void setCareer(String career) {
        this.career = career;
    }

   /* @SerializedName("cooking")
    private String cooking;
    @SerializedName("skilled")
    private ArrayList<String> skilled;
    @SerializedName("place")
    private String place;
    @SerializedName("address")
    private String address;*/


    @SerializedName("career")
    private String career;
    @SerializedName("introduce")
    private String selfIntroduction;

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    @SerializedName("interest")
    private String hobby;
    @SerializedName("languages")
    private List<String> languages;

    public String getGender() {
        return gender == null ? "keepSecret" : gender;
    }

    public Optional<Date> getBirthday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        try {
            birthday = sdf.parse(this.birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(birthday);
    }


   /* public Optional<String> getCooking() {
        return Optional.ofNullable(cooking);
    }

    public void setCooking(String cooking) {
        this.cooking = cooking;
    }

    public Optional<ArrayList<String>> getSkilled() {
        return Optional.ofNullable(skilled);
    }

    public void setSkilled(ArrayList<String> skilled) {
        this.skilled = skilled;
    }

    public Optional<String> getPlace() {
        return Optional.ofNullable(place);
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }*/

    public Integer getProvinceId() {
        return provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public Optional<String> getStory() {
        return Optional.ofNullable(story);
    }

    public Optional<ArrayList<String>> getTasteTags() {
        return Optional.ofNullable(tasteTags);
    }

    public Optional<String> getEducationBackground() {
        return Optional.ofNullable(educationBackground);
    }

    public Optional<String> getCareer() {
        return Optional.ofNullable(career);
    }

    public Optional<String> getSelfIntroduction() {
        return Optional.ofNullable(selfIntroduction);
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public Optional<String> getHobby() {
        return Optional.ofNullable(hobby);
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Optional<List<String>> getLanguages() {
        return Optional.ofNullable(languages);
    }

    public void setGender(String gender) {
        if ("male".equals(gender) || "female".equals(gender)) {
            this.gender = gender;
        } else {
            this.gender = "keepSecret";
        }
    }

    public void setBirthday(Date birthday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.birthday = sdf.format(birthday);
    }

    public void setProvinceAndCity(Integer provinceId, Integer cityId) {
        this.provinceId = provinceId;
        this.cityId = cityId;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setTasteTags(ArrayList<String> tasteTags) {
        this.tasteTags = tasteTags;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
