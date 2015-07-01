package com.zuijiao.android.zuijiao.model.Banquent;

/**
 * Created by user on 6/18/15.
 */
public enum OrderStatus {
    Waiting, Finished, Canceled, All;

    public static OrderStatus fromString(String s) {
        switch (s) {
            case "waiting":
                return Waiting;

            case "finished":
                return Finished;

            case "canceled":
                return Canceled;

            default:
                return All;
        }
    }

    @Override
    public String toString() {
        String rawValue = null;
        switch (this) {
            case Waiting:
                rawValue = "waiting";
                break;
            case Finished:
                rawValue = "finished";
                break;
            case Canceled:
                rawValue = "canceled";
            case All:
                rawValue = "";
        }
        return rawValue;
    }
}
