package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentForTheme;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.model.Banquent.OrderCreateErrorMessage;
import com.zuijiao.android.zuijiao.model.Banquent.OrderStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.model.OrderAuth;
import com.zuijiao.android.zuijiao.model.OrderAuthV3;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

/**
 * Created by user on 6/18/15.
 */
public enum RouterBanquent {
    INSTANCE;

    private IRouterBanquent service = Router.getInstance().restAdapter.create(IRouterBanquent.class);

//    public void createOrder(Integer themeId,
//                            String phoneNumber,
//                            String code,
//                            String remark,
//                            String payMethod,
//                            Integer quantity,
//                            final OneParameterExpression<OrderAuth> successCallback,
//                            final OneParameterExpression<String> failureCallback) {
//        service.createOrder(themeId, phoneNumber, code, remark, payMethod, quantity, CallbackFactory.getInstance().callback(successCallback, failureCallback));
//    }

    public void createOrder(Integer themeId,
                            String remark,
                            Integer quantity,
                            String phoneNumber,
                            String code,
                            final OneParameterExpression<Orders> successCallback,
                            final OneParameterExpression<ErrorType> failureCallback) {

        final Optional<OneParameterExpression<Orders>> finalSuccessCallback = Optional.ofNullable(successCallback);
        final Optional<OneParameterExpression<ErrorType>> finalFailureCallback = Optional.ofNullable(failureCallback);

        Callback<Orders> callback = new Callback<Orders>() {
            @Override
            public void success(Orders t, retrofit.client.Response response) {
                if (finalSuccessCallback.isPresent())
                    finalSuccessCallback.get().action(t);
            }

            @Override
            public void failure(RetrofitError error) {
                assert (error != null);
                assert (finalFailureCallback != null);
                assert (finalFailureCallback.get() != null);

                error.printStackTrace();

                if (finalFailureCallback.isPresent()) {
                    ErrorType errorType = new ErrorType(error.getResponse().getStatus()
                            , new String(((TypedByteArray) error.getResponse().getBody()).getBytes()));
                    finalFailureCallback.get().action(errorType);
                }
            }
        };

        service.createOrder(themeId, remark, quantity, phoneNumber, code, callback);
    }

    public void createOrder(Integer themeId,
                            String remark,
                            String payMethod,
                            final OneParameterExpression<OrderAuth> successCallback,
                            final OneParameterExpression<String> failureCallback) {
        service.createOrder(themeId, remark, payMethod, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public  void order(Integer orderID , final  OneParameterExpression<Orders> successCallback ,
                       final OneParameterExpression<String> failureCallback){
        service.order(orderID ,  CallbackFactory.getInstance().callback(successCallback, failureCallback)) ;
    }

    public void orders(OrderStatus status
            , Integer maxId
            , Integer count
            , final OneParameterExpression<Orders> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.orders(status.toString(), maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void themesOfPublic(Integer maxId
            , Integer count
            , final OneParameterExpression<Banquents> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.themesOfPublic(maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void theme(Integer identifier
            , final OneParameterExpression<Banquent> successCallback
            , final OneParameterExpression<String> failureCallback) {
        final OneParameterExpression<BanquentForTheme> helper = banquentForTheme -> successCallback.action(banquentForTheme.getHelper());
        service.theme(identifier, CallbackFactory.getInstance().callback(helper, failureCallback));
    }

    public void themesOfMaster(
            Integer identifier
            , Integer maxId
            , Integer count
            , final OneParameterExpression<Banquents> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.themesOfMaster(identifier, maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void themesOfParticipator(
            Integer identifier
            , Integer maxId
            , Integer count
            , final OneParameterExpression<Banquents> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.themesOfParticipator(identifier, maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void commentsOfBanquent(
            Integer sellerID
            , Integer maxID
            , Integer count
            , final OneParameterExpression<Reviews> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.commentsOfBanquent(sellerID, maxID, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void createComment(
            Integer orderId
            , String content
            , Integer score
            , LambdaExpression successCallback
            , LambdaExpression failureCallback) {
        service.createComment(orderId, content, score, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void cancelOrder(
            Integer orderId
            , LambdaExpression successCallback
            , LambdaExpression failureCallback) {
        service.cancelOrder(orderId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void payOrder(
            Integer orderId
            , String payMethod
            , String payPlatform
            , final OneParameterExpression<OrderAuthV3> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.payOrder(orderId, payMethod, payPlatform, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }


    public void sellerOrders(String status
            , Integer maxID
            , Integer count
            , final OneParameterExpression<Orders> successCallback
            , final OneParameterExpression<String> failureCallback) {
            service.sellerOrder(status , maxID , count, CallbackFactory.getInstance().callback(successCallback , failureCallback));
    }
}
