package com.zuijiao.android.zuijiao.network;

import com.google.gson.GsonBuilder;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Chen Hao on 3/16/15.
 * <p>
 * 总路由
 */
public enum Router {
    INSTANCE;

//    public static final String BaseUrl = "http://api.zuijiao.net";
    public static final String BaseUrl = "http://xielingyu2.zuijiaodev.com";
    public static final String PicBaseUrl = "http://pic.zuijiao.net";

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            if (accessToken.isPresent())
                request.addHeader("Authorization", "Bearer " + accessToken.get());
        }
    };

    RestAdapter restAdapter = new RestAdapter.Builder()
            .setConverter(new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()))
            .setEndpoint(BaseUrl)
            .setRequestInterceptor(requestInterceptor)
            .build();

    public Optional<String> getAccessToken() {
        return accessToken;
    }

    Optional<String> accessToken = Optional.empty();

    public Optional<TinyUser> getCurrentUser() {
        return currentUser;
    }

    Optional<TinyUser> currentUser = Optional.empty();

    static String convertJsonFromList(List l) {
        StringBuilder stringBuilder = new StringBuilder("[");
        if (l.size() > 0) {
            for (Object o : l) {
                stringBuilder.append("\"");
                stringBuilder.append(o.toString());
                stringBuilder.append("\",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    // Modules
    public static RouterAccount getAccountModule() {
        return RouterAccount.INSTANCE;
    }

    public static RouterGourmet getGourmetModule() {
        return RouterGourmet.INSTANCE;
    }

    public static RouterOAuth getOAuthModule() {
        return RouterOAuth.INSTANCE;
    }

    public static RouterCommon getCommonModule() {
        return RouterCommon.INSTANCE;
    }

    public static RouterMessage getMessageModule() {
        return RouterMessage.INSTANCE;
    }

}
