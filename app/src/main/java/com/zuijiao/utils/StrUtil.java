package com.zuijiao.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaqibo on 2015/4/1.
 */
public class StrUtil {

    public static String buildTags (List<String> list){
        StringBuilder stringBuilder = new StringBuilder() ;
        for(String item:list){
            stringBuilder.append(item) ;
            stringBuilder.append("\n") ;
        }
        return  stringBuilder.toString() ;
    }

    public static  List<String> retriveTags(String tag){
        String[] tags = tag.split("\n") ;
        ArrayList<String> tagsArray = new ArrayList<String>() ;
        for(String item : tags){
            tagsArray.add(item);
        }
        return tagsArray ;
    }



    public static String formatTime(Date date,String res){
        Date currentDate = new Date() ;
        if(currentDate.getYear() > date.getYear() + 1){
            return String.format(res , )
        }
        return null ;
    }
}
