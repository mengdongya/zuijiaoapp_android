package com.zuijiao.utils;

import android.content.Context;

import net.zuijiao.android.zuijiao.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * get format string
 * Created by xiaqibo on 2015/4/1.
 */
public class StrUtil {

    /**
     * change tag list to string
     *
     * @param list
     * @return
     */
    public static String buildTags(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append(item);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * chang string too tag list
     *
     * @param tag
     * @return
     */
    public static List<String> retriveTags(String tag) {
        String[] tags = tag.split("\n");
        ArrayList<String> tagsArray = new ArrayList<String>();
        for (String item : tags) {
            tagsArray.add(item);
        }
        return tagsArray;
    }

    /**
     * format date to string ,used in gourmet detail activity
     *
     * @param date
     * @param context
     * @return
     */
    public static String formatTime(Date date, Context context) {
        String result = null;
        Date currentDate = new Date();
        result = String.format(context.getString(R.string.month_day), date.getMonth() + 1, date.getDate() + 1);
        if (currentDate.getYear() > date.getYear() + 1) {
            result = String.format(context.getString(R.string.year_month_day), date.getYear() + 1900, date.getMonth(), date.getDate());
        } else if (currentDate.getYear() == date.getYear() + 1) {
            if (date.getMonth() == 12 && currentDate.getMonth() == 1) {
                switch (currentDate.getDate() + 31 - date.getDate()) {
                    case 0:
                        result = context.getString(R.string.today);
                        break;
                    case 1:
                        result = context.getString(R.string.yesterday);
                        break;
                    case 2:
                        result = context.getString(R.string.beforeyesterday);
                        break;
                    default:
                        break;

                }
            } else {
                result = String.format(context.getString(R.string.year_month_day), date.getYear() + 1900, date.getMonth(), date.getDate());
            }
        } else {
            if (date.getMonth() + 1 == currentDate.getMonth()) {
                switch (currentDate.getDate() + getMonthLength(date.getMonth(), date.getYear() + 1900) - date.getDate()) {
                    case 0:
                        break;
                    case 1:
                        result = context.getString(R.string.yesterday);
                        break;
                    case 2:
                        result = context.getString(R.string.beforeyesterday);
                        break;
                    default:
                        break;
                }
            } else if (date.getMonth() == currentDate.getMonth()) {
                switch (currentDate.getDate() - date.getDate()) {
                    case 0:
                        result = context.getString(R.string.today);
                        break;
                    case 1:
                        result = context.getString(R.string.yesterday);
                        break;
                    case 2:
                        result = context.getString(R.string.beforeyesterday);
                    default:
                        break;
                }
            }
        }
        return result;
    }

    private static int getMonthLength(int month, int year) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                    return 29;
                else
                    return 28;
        }
        return 30;
    }
}
