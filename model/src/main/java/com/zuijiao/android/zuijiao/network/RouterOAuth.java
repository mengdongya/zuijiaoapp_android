package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.OAuthModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chen Hao on 3/16/15.
 * <p>
 */
public enum RouterOAuth {
    INSTANCE;

    private static final Map<String, String> OAuthParam;
    private IRouterOAuth service = Router.getInstance().restAdapter.create(IRouterOAuth.class);

    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("client_id", "1419219041");
        aMap.put("client_secret", "6676a26412ce3755ffd337d4bc51c7a7");
        aMap.put("grant_type", "client_credentials");
        aMap.put("origin" ,"android") ;
        OAuthParam = Collections.unmodifiableMap(aMap);
    }

    /**
     * 游客登录(Do *NOT* ask me why visitor need login)
     */
    public void visitor(LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        service.visitor(OAuthParam, fillOAuthToken(successCallback, failureCallback));
    }

    public void visitor() {
        OAuthModel model = service.visitor(OAuthParam);
        fillOAuthToken(model);
    }

    public void registerEmailRoutine(String name
            , String avatarURL
            , String email
            , String password
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
            , LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        registerEmailRoutine(name, avatarURL, email, password, "male", deviceToken, openIDOAuthToken, successCallback, failureCallback);
    }

    public void registerEmailRoutine(String name
            , String avatarURL
            , String email
            , String password
            , String gender
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
            , LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);
        assert (gender.equals("male") || gender.equals("female"));
        service.registerEmailRoutine(name
                , avatarURL
                , email
                , password
                , gender // male or female
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam
                , fillOAuthToken(successCallback, failureCallback));
    }

    public void registerEmailRoutine(String name
            , String avatarURL
            , String email
            , String password
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
    ) {
        OAuthModel model = service.registerEmailRoutine(name
                , avatarURL
                , email
                , password
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam
        );
        fillOAuthToken(model);
    }

    /**
     *
     */
    public void register(String name
            , String avatarURL
            , String gender
            , String openID
            , String platform
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
            , OneParameterExpression<Boolean> successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);
        service.register(name
                , avatarURL
                , gender // male or female
                , openID
                , platform
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam
                , fillOAuthToken(successCallback, failureCallback));
    }

    public void register(String name
            , String avatarURL
            , String openID
            , String platform
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
    ) {
        OAuthModel model = service.register(name
                , avatarURL
                , openID
                , platform
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam);
        fillOAuthToken(model);
    }

    public void login(String openID
            , String platform
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
            , LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);

        service.login(openID
                , platform
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam
                , fillOAuthToken(successCallback, failureCallback)
        );
    }

    public void login(String openID
            , String platform
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);

        OAuthModel model = service.login(openID
                , platform
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam
        );
        fillOAuthToken(model);
    }

    public void loginEmailRoutine(String email
            , String password
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
            , LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);

        service.loginEmailRoutine(email
                , password
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam
                , fillOAuthToken(successCallback, failureCallback)
        );
    }

    public void loginEmailRoutine(String email
            , String password
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);

        OAuthModel model = service.loginEmailRoutine(email
                , password
                , deviceToken.orElse(null)
                , openIDOAuthToken.orElse(null)
                , OAuthParam
        );
        fillOAuthToken(model);
    }

    private Callback<OAuthModel> fillOAuthToken(
            OneParameterExpression<Boolean> successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {

        final Optional<OneParameterExpression<Boolean>> finalSuccessCallback = Optional.ofNullable(successCallback);
        final Optional<OneParameterExpression<Integer>> finalFailureCallback = Optional.ofNullable(failureCallback);

        return new Callback<OAuthModel>() {
            @Override
            public void success(OAuthModel oAuthModel, Response response) {
                Router.getInstance().accessToken = Optional.ofNullable(oAuthModel.getAccessToken());
                Router.getInstance().currentUser = oAuthModel.getUser();

                if (finalSuccessCallback.isPresent())
                    finalSuccessCallback.get().action(oAuthModel.getIsNew());
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Failure: " + error);
                if (finalFailureCallback.isPresent())
                    finalFailureCallback.get().action(error.getResponse().getStatus());
            }
        };
    }

    private Callback<OAuthModel> fillOAuthToken(
            LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {

        final Optional<LambdaExpression> finalSuccessCallback = Optional.ofNullable(successCallback);
        final Optional<OneParameterExpression<Integer>> finalFailureCallback = Optional.ofNullable(failureCallback);

        return new Callback<OAuthModel>() {
            @Override
            public void success(OAuthModel oAuthModel, Response response) {
                Router.getInstance().accessToken = Optional.ofNullable(oAuthModel.getAccessToken());
                Router.getInstance().currentUser = oAuthModel.getUser();

                if (finalSuccessCallback.isPresent())
                    finalSuccessCallback.get().action();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Failure: " + error);
                if (finalFailureCallback.isPresent()) {
                    if (error.getResponse() == null)
                        finalFailureCallback.get().action(0);
                    else
                        finalFailureCallback.get().action(error.getResponse().getStatus());
                }
            }
        };
    }

    private void fillOAuthToken(OAuthModel oAuthModel) {
        Router.getInstance().accessToken = Optional.ofNullable(oAuthModel.getAccessToken());
        Router.getInstance().currentUser = oAuthModel.getUser();
    }


}
