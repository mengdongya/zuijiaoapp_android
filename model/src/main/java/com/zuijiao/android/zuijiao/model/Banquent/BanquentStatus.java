package com.zuijiao.android.zuijiao.model.Banquent;

import java.io.Serializable;

/**
 * Created by user on 6/19/15.
 */
public enum BanquentStatus implements Serializable {
    Selling, SoldOut, OverTime, End;

    public static BanquentStatus fromString(String s) {
        switch (s) {
            case "selling":
                return Selling;
            case "outOfStock":
                return SoldOut;
            case "overtime":
                return OverTime;
            case "end":
                return End;
            default:
                return End;
        }
    }

    @Override
    public String toString() {
        String rawValue = null;
        switch (this) {
            case Selling:
                rawValue = "selling";
                break;
            case SoldOut:
                rawValue = "outOfStock";
                break;
            case OverTime:
                rawValue = "overtime";
                break;
            case End:
                rawValue = "end";
                break;
        }
        return rawValue;
    }
}
