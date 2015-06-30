package com.zuijiao.thirdopensdk;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;


/**
 * Created by mengdongya on 2015/6/29.
 */
public class Alipay {
    private static final int SDK_PAY_FLAG = 1;

//    private static final int SDK_CHECK_FLAG = 2;
    Activity act ;

    public Alipay(Activity act){
     this.act = act ;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(act, "successed",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(act, "waiting for the result",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(act, "failed",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                /*case SDK_CHECK_FLAG: {
                    Toast.makeText(act, "the result is" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }*/
                default:
                    break;
            }
        };
    };

    public void pay(String queryString) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(act);
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
