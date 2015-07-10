package com.zuijiao.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * entity of images from sd-card
 * Created by xiaqibo on 2015/5/4.
 */

public class SimpleImage implements Parcelable {

    public String name;
    public String id;
    public String data;

    public SimpleImage(String name, String id, String data) {
        this.name = name;
        this.id = id;
        this.data = data;
    }

    public SimpleImage() {

    }

    public SimpleImage(Parcel in) {
        name = in.readString();
        id = in.readString();
        data = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(data);
    }


    public static final Parcelable.Creator<SimpleImage> CREATOR = new Creator<SimpleImage>() {
        @Override
        public SimpleImage[] newArray(int size) {
            return new SimpleImage[size];
        }

        @Override
        public SimpleImage createFromParcel(Parcel in) {
            return new SimpleImage(in);
        }
    };

}
