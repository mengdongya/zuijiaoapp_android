package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xiaqibo on 2015/8/12.
 */
public class Notifications {

    @SerializedName("nextCursor")
    private Integer nextCursor ;
    @SerializedName("itemCount")
    private Integer itemCount ;
    @SerializedName("itemTotalCount")
    private Integer totalCount ;
    @SerializedName("items")
    private ArrayList<Notification> items ;


    public ArrayList<Notification> getItems() {
        return items;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public Integer getNextCursor() {
        return nextCursor;
    }


    public class Notification{
        @SerializedName("ID")
        private Integer id ;
        @SerializedName("content")
        private String content ;
        @SerializedName("type")
        private String type ;
        @SerializedName("linkID")
        private Integer linkID ;
        @SerializedName("isRead")
        private Boolean isRead ;
        @SerializedName("createdAt")
        private Date createDate ;

        public Integer getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        public NotificationType getType() {
            return NotificationType.fromString(type) ;
        }

        public Boolean getIsRead() {
            return isRead;
        }

        public Integer getLinkID() {
            return linkID;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setIsRead(Boolean isRead) {
            this.isRead = isRead;
        }
    }

    public enum StatusType{
        read , unread ,all;
        public String toString(){
            switch (this){
                case read:
                    return "read" ;
                case unread:
                    return "unread" ;
                default:
                    return null ;
            }
        }

        public static StatusType fromString(String statusType){
            switch (statusType){
                case "read" :
                    return read ;
                case "unread" :
                    return unread ;
                default:
                    return  all ;
            }
        }
    }



    public enum NotificationType{
        order , sellerOrder , comment , profile ,application,others ;
        public String toString(){
            switch (this){
                case order:
                    return "order" ;
                case sellerOrder:
                    return "sellerOrder" ;
                case comment :
                    return "comment" ;
                case profile:
                    return "profile" ;
                case application:
                    return "application" ;
                default:
                    return null ;
            }
        }

        public static NotificationType fromString(String notificationType){
            switch (notificationType){
                case "order" :
                    return order ;
                case "sellerOrder" :
                    return sellerOrder ;
                case "comment" :
                    return comment;
                case "profile" :
                    return profile;
                case "application":
                    return application ;
                default:
                    return  others ;
            }
        }
    }
}
