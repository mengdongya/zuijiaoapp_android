package com.zuijiao.android.zuijiao.network;

import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okio.Buffer;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Chen Hao on 3/16/15.
 * <p>
 * 总路由
 */
public class Router {

    private static Router instance = null;

    public static final String PicBaseUrl = "http://pic.zuijiao.net";

    RestAdapter restAdapter;
    Optional<String> accessToken = Optional.empty();
    Optional<TinyUser> currentUser = Optional.empty();

    public static Router getInstance() {
        return instance;
    }

    public static void setup(String baseUrl, File cacheDirectory, Interceptor networkInterceptor) {
        if (instance == null) {
            instance = new Router(baseUrl, cacheDirectory, networkInterceptor);
        }
    }

    private Router(String baseUrl, File cacheDirectory, Interceptor networkInterceptor) {

        RequestInterceptor requestInterceptor = request -> {
            if (accessToken.isPresent())
                request.addHeader("Authorization", "Bearer " + accessToken.get());
        };

        OkHttpClient client = new OkHttpClient();


        client.interceptors().add(chain -> {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();

            RequestBody requestBody = request.body();
            String checksum = "default value";
            if ("POST".equals(request.method()) && requestBody != null) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                checksum = buffer.readUtf8();
            }
            builder.addHeader("X-Request-Checksum", checksum);

            return chain.proceed(builder.build());
        });

        if (cacheDirectory != null) {
            try {
                com.squareup.okhttp.Cache responseCache = new com.squareup.okhttp.Cache(cacheDirectory, 1024 * 1024 * 16);
                client.setCache(responseCache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (networkInterceptor != null) {
            client.networkInterceptors().add(networkInterceptor);
        }

        restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create()))
                .setEndpoint(baseUrl)
                .setRequestInterceptor(requestInterceptor)
                .setClient(new OkClient(client))
                .build();
    }

    public Optional<String> getAccessToken() {
        return accessToken;
    }

    public Optional<TinyUser> getCurrentUser() {
        return currentUser;
    }

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

    private String generateChecksum(String requestBody, String privateKey) {
        return null;
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
