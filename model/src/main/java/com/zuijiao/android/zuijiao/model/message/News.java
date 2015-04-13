package com.zuijiao.android.zuijiao.model.message;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 4/3/15.
 */
public class News {
    @SerializedName("showGroupID")
    private Integer type;  // of showGroupID
    @SerializedName("count")
    private Integer unreadCount;
    @SerializedName("IDs")
    private List<Integer> ids;

    public NotificationType getType() {
        return NotificationType.fromIndex(type);
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public enum NotificationType {
        Empty,
        Notice,
        Comment;

        public Integer index() {
            switch (this) {
                case Empty:
                    return 0;
                case Notice:
                    return 1;
                case Comment:
                    return 2;
            }
            return 0;
        }

        public static NotificationType fromIndex(Integer index) {
            switch (index) {
                case 1:
                    return Notice;
                case 2:
                    return Comment;
                default:
                    return Empty;
            }
        }
    }
}
