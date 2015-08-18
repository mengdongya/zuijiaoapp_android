package com.zuijiao.android.zuijiao.model.Banquent;

/**
 * Created by user on 6/18/15.
 */
public enum OrderStatus {
     Finished, unComment ,unFinished ,Canceled, Unpaid, Waiting,All;

    public static OrderStatus fromString(String s) {
        switch (s) {
            case "finished":
                return Finished;
            case "canceled":
                return Canceled;
            case "waiting":
                return Waiting;
            case "unfinished":
                return unFinished ;
            case "uncomment":
                return unComment ;
            default:
                return Unpaid;
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
            case Unpaid:
                rawValue = "unpaid";
                break;
            case unComment:
                rawValue = "uncomment" ;
                break ;
            case unFinished:
                rawValue = "unfinished" ;
                break ;

        }
        return rawValue;
    }
}
