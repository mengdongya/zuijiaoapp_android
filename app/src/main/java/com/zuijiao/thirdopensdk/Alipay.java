package com.zuijiao.thirdopensdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

import net.zuijiao.android.zuijiao.BanquetOrderCallbackActivity;


/**
 * Created by mengdongya on 2015/6/29.
 */
public class Alipay {
    private static final int SDK_PAY_FLAG = 1;

    //    private static final int SDK_CHECK_FLAG = 2;
    private Activity mActivity;

    public Alipay(Activity act) {
        this.mActivity = act;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();
                    Intent intent = new Intent(mActivity, BanquetOrderCallbackActivity.class);
                    if (TextUtils.equals(resultStatus, "9000")) {
                        intent.putExtra("b_suceess", true);
                        mActivity.startActivity(intent);
//                        Toast.makeText(act, "successed",
//                                Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra("b_suceess", false);
                        mActivity.startActivity(intent);
//                        if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(act, "waiting for the result",
//                                    Toast.LENGTH_SHORT).show();
//
//                        } else {
//
//                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    public void pay(String queryString) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                String result = alipay.pay(queryString);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
