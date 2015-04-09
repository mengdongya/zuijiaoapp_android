package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_concerning)
public class ConcerningActivity extends BaseActivity implements OnClickListener {
    @ViewInject(R.id.concerning_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.user_protocol)
    private TextView mTextUserProtocol = null;
    @ViewInject(R.id.zuijiao_team)
    private TextView mTextOurTeam = null;
    @ViewInject(R.id.concerning_tv_version_code)
    private TextView mTextVersionCode = null;
    @ViewInject(R.id.zuijiao_welcome)
    private TextView mTextGuide = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registeViews() {
        try {
            String versionCode = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
            mTextVersionCode.setText(String.format(getString(R.string.version_code), versionCode));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            mTextVersionCode.setText(String.format(getString(R.string.version_code), "1.0"));
        }
        mTextUserProtocol.setOnClickListener(this);
        mTextOurTeam.setOnClickListener(this);
        mTextGuide.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent  = new Intent () ;
        String strTitle = null;
        String url = null;
        switch (v.getId()) {
            case R.id.user_protocol:
                intent.setClass(ConcerningActivity.this,CommonWebViewActivity.class) ;
                strTitle = getResources().getString(R.string.user_protocol);
                url = getString(R.string.protocol_url);
                break ;
            case R.id.zuijiao_team:
                intent.setClass(ConcerningActivity.this,CommonWebViewActivity.class) ;
                strTitle = getResources().getString(R.string.zuijiao_team);
                url = getString(R.string.team_url);
                break ;
            case R.id.zuijiao_welcome:
                intent.setClass(ConcerningActivity.this,GuideActivity.class) ;
                break ;
        }
        if(strTitle != null && url !=null){
            intent.putExtra("title", strTitle);
            intent.putExtra("content_url", url);
        }
        ConcerningActivity.this.startActivity(intent);
    }
}
