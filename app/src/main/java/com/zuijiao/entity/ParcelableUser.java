package com.zuijiao.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zuijiao.android.zuijiao.model.user.User;

/**
 * Created by xiaqibo on 2015/4/22.
 */
public class ParcelableUser extends User implements Parcelable {


    public ParcelableUser(User user) {
        setAvatarURL(user.getAvatarURLSmall().get());
        setBirthday(user.getBirthday().get());
        setGender(user.getGender());
        setNickname(user.getNickname().get());
        setProvinceAndCity(user.getProvinceId(), user.getCityId());
        setStory(user.getStory().get());
        setTasteTags(user.getTasteTags().get());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }
}
