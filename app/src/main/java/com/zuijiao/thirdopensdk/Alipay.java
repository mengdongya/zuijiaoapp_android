package com.zuijiao.thirdopensdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import net.zuijiao.android.zuijiao.BanquetOrderCallbackActivity;


/**
 * Created by mengdongya on 2015/6/29.
 */
public class Alipay {
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

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

                        intent.putExtra("b_success", true);
                        mActivity.startActivity(intent);
                    } else if (TextUtils.equals(resultStatus, "8000")) {
//                        intent.putExtra("b_success", "wait");
                        mActivity.startActivity(intent);
                    } else {
                        intent.putExtra("b_success", false);
                        mActivity.startActivity(intent);
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(mActivity, "" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                default:
                    break;
            }
        }


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

    public void check() {
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask payTask = new PayTask(mActivity);
                boolean isExist = payTask.checkAccountIfExist();
                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };
        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }

}
