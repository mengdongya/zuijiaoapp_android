package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

@ContentView(R.layout.activity_concerning)
public class ConcerningActivity extends BaseActivity {
    @ViewInject(R.id.concerning_toolbar)
    private Toolbar mToolbar = null;
    //    @ViewInject(R.id.user_protocol)
//    private TextView mTextUserProtocol = null;
//    @ViewInject(R.id.zuijiao_team)
//    private TextView mTextOurTeam = null;
    @ViewInject(R.id.concerning_tv_version_code)
    private TextView mTextVersionCode = null;
    //    @ViewInject(R.id.zuijiao_welcome)
//    private TextView mTextGuide = null;
//    @ViewInject(R.id.check_new_version)
//    private TextView mCheckVersion = null ;
    @ViewInject(R.id.concerning_item_list_view)
    private ListView mListView = null;
    private int[] mItemRes = {R.string.user_protocol, R.string.zuijiao_team, R.string.wizard_page, R.string.check_new_version};

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
    protected void registerViews() {
        try {
            String versionCode = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
            mTextVersionCode.setText(String.format(getString(R.string.version_code), versionCode));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            mTextVersionCode.setText(String.format(getString(R.string.version_code), "1.0"));
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mListener);
    }

    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            String strTitle = null;
            String url = null;
            switch (position) {
                case 0:
                    intent.setClass(ConcerningActivity.this, CommonWebViewActivity.class);
                    strTitle = getResources().getString(R.string.user_protocol);
                    url = getString(R.string.protocol_url);
                    break;
                case 1:
                    intent.setClass(ConcerningActivity.this, CommonWebViewActivity.class);
                    strTitle = getResources().getString(R.string.zuijiao_team);
                    url = getString(R.string.team_url);
                    break;
                case 2:
                    intent.putExtra("b_user_call", true);
                    intent.setClass(ConcerningActivity.this, GuideActivity.class);
                    break;
                case 3:
                    checkVersion();
                    return;
            }
            if (strTitle != null && url != null) {
                intent.putExtra("title", strTitle);
                intent.putExtra("content_url", url);
            }
            startActivity(intent);
        }
    };
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setTextSize(20);
            textView.setTextColor(Color.GRAY);
            textView.setPadding(50, 30, 50, 30);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setText(getString(mItemRes[position]));
            return textView;
        }
    };

    private void checkVersion() {
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus,
                                         UpdateResponse updateInfo) {
                if (updateStatus == 0 && updateInfo != null) {
                    showUpdateDialog(updateInfo.path, updateInfo.updateLog);
                }
            }
        });
        UmengUpdateAgent.update(this);
    }

}
