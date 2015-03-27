package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.model.user.User;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Chen Hao on 3/23/15.
 */
public enum RouterAccount {
    INSTANCE;

    private IRouterAccount service = Router.INSTANCE.restAdapter.create(IRouterAccount.class);

    public void update(User user
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        String gender = user.getGender();
        Integer provinceId = user.getProvinceId();
        Integer cityId = user.getCityId();
        String story = user.getStory().orElse("");
        Integer year = null, month = null, day = null;
        if (user.getBirthday().isPresent()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(user.getBirthday().get());
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DATE);
        }
        String tags = Router.convertJsonFromList(user.getTasteTags().orElse(new ArrayList<>()));

        service.update(gender, provinceId, cityId, story, year, month, day, tags, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }
}
