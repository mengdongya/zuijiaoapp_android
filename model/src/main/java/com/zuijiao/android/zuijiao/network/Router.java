package com.zuijiao.android.zuijiao.network;

import com.google.gson.GsonBuilder;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.util.List;

/**
 * Created by Chen Hao on 3/16/15.
 * <p>
 * 总路由
 */
public enum Router {
    INSTANCE;

    public static final String BaseURL = "http://xielingyu2.zuijiaodev.com";

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            if (accessToken.isPresent())
                request.addHeader("Authorization", "Bearer " + accessToken.get());
        }
    };

    RestAdapter restAdapter = new RestAdapter.Builder()
            .setConverter(new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create()))
            .setEndpoint(BaseURL)
            .setRequestInterceptor(requestInterceptor)
            .build();

    Optional<String> accessToken = Optional.empty();
    Optional<TinyUser> currentUser = Optional.empty();

    public static String convertJsonFromList(List l) {
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
}
