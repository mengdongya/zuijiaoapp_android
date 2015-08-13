package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by mengdongya on 2015/8/12.
 */
public class MyBanquentActiveFragment extends Fragment {
    private TextView mBanquentNotice;
    private View mContentView;

    public MyBanquentActiveFragment() {
        super();
    }

    public static MyBanquentActiveFragment newInstance() {
        MyBanquentActiveFragment fragment = new MyBanquentActiveFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_my_banquent_active,null);
        mBanquentNotice = (TextView)mContentView.findViewById(R.id.my_banquent_active_notice);
        mBanquentNotice.setAutoLinkMask(Linkify.PHONE_NUMBERS|Linkify.WEB_URLS);
        mBanquentNotice.setMovementMethod(LinkMovementMethod.getInstance());
        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
