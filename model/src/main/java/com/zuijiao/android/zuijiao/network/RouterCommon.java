package com.zuijiao.android.zuijiao.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.Configuration;
import com.zuijiao.android.zuijiao.model.common.ConfigurationType;
import com.zuijiao.android.zuijiao.model.common.GourmetTags;
import com.zuijiao.android.zuijiao.model.common.Languages;
import com.zuijiao.android.zuijiao.model.common.Restaurants;
import com.zuijiao.android.zuijiao.model.common.TasteTags;

import java.util.List;

/**
 * Created by Chen Hao on 3/30/15.
 */
public enum RouterCommon {
    INSTANCE;

    private IRouterCommon service = Router.getInstance().restAdapter.create(IRouterCommon.class);

    public void tasteTags(OneParameterExpression<TasteTags> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.tasteTags(CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void gourmetTags(OneParameterExpression<List<String>> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        OneParameterExpression<GourmetTags> callback = tags -> {
            successCallback.action(tags.getTags());
        };
        service.gourmetTags(CallbackFactory.getInstance().callback(callback, failureCallback));
    }

    public void restaurantSearch(String keyword, Integer count, OneParameterExpression<Restaurants> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.restaurantSearch(keyword, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void requestSecurityCode(final String phoneNumber
            , final LambdaExpression successCallback
            , final LambdaExpression failureCallback) {
        service.requestSecurityCode(phoneNumber, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void currentConfiguration(final OneParameterExpression<Configuration> successCallback
            , final OneParameterExpression<String> failureCallback) {

        OneParameterExpression<JsonElement> configParser = jsonElement -> {
            JsonObject rawDictionary = jsonElement.getAsJsonObject().get("items")
                    .getAsJsonObject().get("push").getAsJsonObject();
            boolean notifyComment = rawDictionary.get("3").getAsInt() != 0;
            boolean notifyLike = rawDictionary.get("2").getAsInt() != 0;
            boolean notifyFollowed = rawDictionary.get("1").getAsInt() != 0;
            Configuration configuration = new Configuration(notifyFollowed, notifyLike, notifyComment);
            successCallback.action(configuration);
        };

        service.currentSetting(CallbackFactory.getInstance().callback(configParser, failureCallback));
    }

    public void updateConfiguration(final ConfigurationType type
            , final Boolean v
            , final LambdaExpression successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.updateConfiguration(type.toString(), v ? 1 : 0, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void languages(final OneParameterExpression<Languages> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.languages(CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }


    public void verifyPhoneNumber(final String phoneNum, final String code, final OneParameterExpression<Integer> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.verifyPhoneNumber(phoneNum, code, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }
}
