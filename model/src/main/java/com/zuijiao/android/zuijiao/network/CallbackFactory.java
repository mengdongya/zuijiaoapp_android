package com.zuijiao.android.zuijiao.network;

import com.squareup.okhttp.Response;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Chen Hao on 3/23/15.
 * <p>
 */
class CallbackFactory<T> {
    private static CallbackFactory instance = new CallbackFactory();

    public static CallbackFactory getInstance() {
        return instance;
    }

    private CallbackFactory() {
    }

    Callback<Response> callback(LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        final Optional<LambdaExpression> finalSuccessCallback = Optional.ofNullable(successCallback);
        final Optional<LambdaExpression> finalFailureCallback = Optional.ofNullable(failureCallback);

        return new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                if (finalSuccessCallback.isPresent())
                    finalSuccessCallback.get().action();
            }

            @Override
            public void failure(RetrofitError error) {

                error.printStackTrace();

                if (finalFailureCallback.isPresent()) finalFailureCallback.get().action();
            }

        };
    }

    public Callback<T> callback(OneParameterExpression<T> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        final Optional<OneParameterExpression<T>> finalSuccessCallback = Optional.ofNullable(successCallback);
        final Optional<OneParameterExpression<String>> finalFailureCallback = Optional.ofNullable(failureCallback);

        return new Callback<T>() {
            @Override
            public void success(T t, retrofit.client.Response response) {
                if (finalSuccessCallback.isPresent())
                    finalSuccessCallback.get().action(t);
            }

            @Override
            public void failure(RetrofitError error) {
                assert (error != null);
                assert (finalFailureCallback != null);
                assert (finalFailureCallback.get() != null);

                error.printStackTrace();

                if (finalFailureCallback.isPresent())
                    finalFailureCallback.get().action(error.toString());
            }
        };
    }

    public Callback<T> callback(LambdaExpression successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        final Optional<LambdaExpression> finalSuccessCallback = Optional.ofNullable(successCallback);
        final Optional<OneParameterExpression<String>> finalFailureCallback = Optional.ofNullable(failureCallback);

        return new Callback<T>() {
            @Override
            public void success(T t, retrofit.client.Response response) {
                if (finalSuccessCallback.isPresent())
                    finalSuccessCallback.get().action();
            }

            @Override
            public void failure(RetrofitError error) {
                assert (error != null);
                assert (finalFailureCallback != null);
                assert (finalFailureCallback.get() != null);

                error.printStackTrace();

                if (finalFailureCallback.isPresent())
                    finalFailureCallback.get().action(error.toString());
            }
        };
    }



}
