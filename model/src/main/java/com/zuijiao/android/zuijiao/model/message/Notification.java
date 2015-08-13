package com.zuijiao.android.zuijiao.model.message;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mengdongya on 2015/8/11.
 */
public class Notification implements Serializable{
    @SerializedName("ID")
    private Integer msgID;
    @SerializedName("content")
    private String msgContent;
    @SerializedName("type")
    private String msgType;
    @SerializedName("linkID")
    private Integer msgLinkID;
    @SerializedName("isRead")
    private boolean isRead;
    @SerializedName("createAt")
    private String createTime;

    public Integer getMsgID() {
        return msgID;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public Integer getMsgLinkID() {
        return msgLinkID;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCreateTime() {
        return createTime;
    }

  /*  public enum Type{
       empty, order, sellerOrder, comment, profile,application;

        public Integer index() {
            switch (this) {
                case order:
                    return 1;
                case sellerOrder:
                    return 2;
                case comment:
                    return 3;
                case profile:
                    return 4;
                case application:
                    return 5;
                default:
                    return 0;
            }
        }

        public static Type fromIndex(Integer index) {
            switch (index) {
                case 1:
                    return order;
                case 2:
                    return sellerOrder;

                case 3:
                    return comment;
                case 4:
                    return profile;
                case 5:
                    return application;
                default:
                    return empty;
            }
        }
    }*/
}
