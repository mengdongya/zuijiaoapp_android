package com.zuijiao.android.zuijiao.model.Banquent;

/**
 * Created by user on 6/18/15.
 */
public enum OrderStatus {
    Waiting, Finished, Canceled, Uncomment, Unpaid, Unclosed, Unfinished , All;

    public static OrderStatus fromString(String s) {
        switch (s) {
            case "waiting":
                return Waiting;

            case "finished":
                return Finished;

            case "canceled":
                return Canceled;
            case "uncomment":
                return Uncomment;
            case "unpaid":
                return Unpaid;
            case "unclosed":
                return Unclosed;
            case "unfinished" :
                return  Unfinished ;
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
                break;
            case Uncomment:
                rawValue = "uncomment";
                break;
            case Unpaid:
                rawValue = "unpaid";
                break;
            case Unclosed:
                rawValue = "unclosed";
                break;
            case Unfinished:
                rawValue = "unfinished";
                break;
            case All:
                rawValue = "";
        }
        return rawValue;
    }
}
