package com.zuijiao.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Master;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.utils.CacheUtils;

import net.zuijiao.android.zuijiao.HostAndGuestActivity;

/**
 * Created by xiaqibo on 2015/8/7.
 */
public class AttendeeAvatarListener implements View.OnClickListener {
    private Integer attendeeId ;
    private TinyUser mTinyUser = null ;
    private static Context mCxt ;
    private TinyUser user ;
    public static AttendeeAvatarListener newInstance
            (Integer attendeeId , Context context){
        mCxt = context ;
        return new AttendeeAvatarListener(attendeeId) ;
    }

    private AttendeeAvatarListener(Integer id){
        this.attendeeId = id ;
    }

    public AttendeeAvatarListener(Context context){
        mCxt = context ;
    }

    @Override
    public void onClick(View v) {
//        attendeeId = (Integer) v.getTag();
//        mTinyUser = (TinyUser) v.getTag() ;

        user = (TinyUser) v.getTag();
        if(user instanceof Master)
            attendeeId = ((Master) user).getUserId()  ;
        else if (user instanceof TinyUser)
            attendeeId = user.getIdentifier() ;
        else
            attendeeId = -1 ;
        Gson gson = new Gson();
        Attendee attendee = gson.fromJson(CacheUtils.getAttendee(attendeeId, mCxt), Attendee.class);
        Intent intent = new Intent(mCxt , HostAndGuestActivity.class) ;
        intent.putExtra("attendee_info" , attendee) ;
        intent.putExtra("tiny_user" , user) ;
        intent.putExtra("attendee_id" , attendeeId );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
        mCxt.startActivity(intent);
    }
}
