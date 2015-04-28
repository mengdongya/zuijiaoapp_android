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

    private IRouterAccount service = Router.getInstance().restAdapter.create(IRouterAccount.class);

    public void update(User user
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        String gender = user.getProfile().getGender();
        Integer provinceId = user.getProfile().getProvinceId();
        Integer cityId = user.getProfile().getCityId();
        String story = user.getProfile().getStory().orElse("");
        Integer year = null, month = null, day = null;
        if (user.getProfile().getBirthday().isPresent()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(user.getProfile().getBirthday().get());
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DATE);
        }
        String tags = Router.convertJsonFromList(user.getProfile().getTasteTags().orElse(new ArrayList<>()));

        service.update(gender, provinceId, cityId, story, year, month, day, tags, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void updateAvatar(String avatarUrl
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.updateAvatar(avatarUrl, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }
}
