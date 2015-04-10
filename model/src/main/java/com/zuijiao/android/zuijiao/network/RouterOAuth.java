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
 * 用户授权部分具体的工作类, 因为是单例所以用了 enum
 * 具体接口详细流程参见 API 文档
 */
public enum RouterOAuth {
    INSTANCE;

    // OAuth 公共参数
    private static final Map<String, String> OAuthParam;
    private IRouterOAuth service = Router.INSTANCE.restAdapter.create(IRouterOAuth.class);

    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("client_id", "1419219041");
        aMap.put("client_secret", "6676a26412ce3755ffd337d4bc51c7a7");
        aMap.put("grant_type", "client_credentials");
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

    /**
     * 用户注册, 邮箱方式
     *
     * @param name             用户名
     * @param avatarURL        用户头像URL
     * @param email            在对应第三方平台上的 OpenID
     * @param password         对应平台名称
     * @param deviceToken      设备 Token( 用于接收推送) 可以为空Optional.Empty()
     * @param openIDOAuthToken 第三方 OAuthToken, 用于验证用户, 现在留空Optional.Empty()
     */
    public void registerEmailRoutine(String name
            , String avatarURL
            , String email
            , String password
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
            , LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);
        service.registerEmailRoutine(name
                , avatarURL
                , email
                , password
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
     * 用户注册
     *
     * @param name             用户名
     * @param avatarURL        用户头像URL
     * @param openID           在对应第三方平台上的 OpenID
     * @param platform         对应平台名称
     * @param deviceToken      设备 Token( 用于接收推送) 可以为空Optional.Empty()
     * @param openIDOAuthToken 第三方 OAuthToken, 用于验证用户, 现在留空Optional.Empty()
     */
    public void register(String name
            , String avatarURL
            , String openID
            , String platform
            , Optional<String> deviceToken
            , Optional<String> openIDOAuthToken
            , LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {
        assert (deviceToken != null);
        assert (openIDOAuthToken != null);
        service.register(name
                , avatarURL
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

    /**
     * 用户登录, 社交软件方式
     *
     * @param openID           在对应第三方平台上的 OpenID
     * @param platform         对应平台名称
     * @param deviceToken      设备 Token( 用于接收推送) 可以为空Optional.Empty()
     * @param openIDOAuthToken 第三方 OAuthToken, 用于验证用户, 现在留空Optional.Empty()
     * @param successCallback  登陆成功后的回调函数
     * @param failureCallback  登陆失败后的回调函数
     */
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

    /**
     * 用户登录, 邮箱方式
     *
     * @param email            用户注册邮箱
     * @param password         密码
     * @param deviceToken      设备 Token( 用于接收推送) 可以为空Optional.Empty()
     * @param openIDOAuthToken 第三方 OAuthToken, 用于验证用户, 现在留空Optional.Empty()
     * @param successCallback  登陆成功后的回调函数
     * @param failureCallback  登陆失败后的回调函数
     */
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

    /**
     * 统一处理填充 AccessToken 以及回调函数
     *
     * @param successCallback 成功回调, 可以为空
     * @param failureCallback 失败回调, 可以为空
     * @return 包含 OAuthModel 的回调类
     */
    private Callback<OAuthModel> fillOAuthToken(
            LambdaExpression successCallback
            , OneParameterExpression<Integer> failureCallback
    ) {

        final Optional<LambdaExpression> finalSuccessCallback = Optional.ofNullable(successCallback);
        final Optional<OneParameterExpression<Integer>> finalFailureCallback = Optional.ofNullable(failureCallback);

        return new Callback<OAuthModel>() {
            @Override
            public void success(OAuthModel oAuthModel, Response response) {
                Router.INSTANCE.accessToken = Optional.ofNullable(oAuthModel.getAccessToken());
                Router.INSTANCE.currentUser = oAuthModel.getUser();

                System.out.println("token: " + oAuthModel.getAccessToken() + " has callback: " + finalSuccessCallback.isPresent());

                if (finalSuccessCallback.isPresent())
                    finalSuccessCallback.get().action();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Failure: " + error);
                if (finalFailureCallback.isPresent())
                    finalFailureCallback.get().action(error.getResponse().getStatus());
            }
        };
    }

    private void fillOAuthToken(OAuthModel oAuthModel) {
        Router.INSTANCE.accessToken = Optional.ofNullable(oAuthModel.getAccessToken());
        Router.INSTANCE.currentUser = oAuthModel.getUser();
    }


}
