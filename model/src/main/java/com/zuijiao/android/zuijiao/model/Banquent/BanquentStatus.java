package com.zuijiao.android.zuijiao.model.Banquent;

/**
 * Created by user on 6/19/15.
 */
public enum BanquentStatus {
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
            case SoldOut:
                rawValue = "outOfStock";
            case OverTime:
                rawValue = "overtime";
            case End:
                rawValue = "end";
        }
        return rawValue;
    }
}
