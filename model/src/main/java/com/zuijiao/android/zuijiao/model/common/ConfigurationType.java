package com.zuijiao.android.zuijiao.model.common;

/**
 * Created by Chen Hao on 5/18/15.
 */
public enum ConfigurationType {
    Follow, Comment, Like;

    @Override
    public String toString() {
        switch (this) {
            case Follow:
                return "push.followers";
            case Comment:
                return "push.comments";
            case Like:
                return "push.collections";
        }
        return "Unexpected";
    }
}
