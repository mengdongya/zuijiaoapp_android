package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.AuthorInfo;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_guide)
public class GuideActivity extends BaseActivity {
    @ViewInject(R.id.guide_viewpager)
    private ViewPager mPager;
    @ViewInject(R.id.guide_dots)
    private LinearLayout mDotsLayout;
    @ViewInject(R.id.guide_btn)
    private Button mBtn;
    @ViewInject(R.id.guide_progressbar)
    private ProgressBar mPb = null;
    private List<View> viewList;
    private boolean onBackPressed = false;
    private boolean mBCallByUser = false;
    private OnPageChangeListener mPageListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                if (i == arg0) {
                    mDotsLayout.getChildAt(i).setBackgroundResource(
                            R.drawable.wizard_index_unselected);
                } else {
                    mDotsLayout.getChildAt(i).setBackgroundResource(
                            R.drawable.wizard_index_selected);
                }
            }
            if (arg0 == mDotsLayout.getChildCount() - 1) {
                mBtn.setVisibility(View.VISIBLE);
            } else {
                mBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private boolean bKilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initPager() {
        viewList = new ArrayList<View>();
        int[] images = new int[]{R.drawable.welcome1, R.drawable.welcome2,
                R.drawable.welcome3};
//        int [] texts = new int[]{R.drawable.welcome_text1 ,R.drawable.welcome_text2 ,R.drawable.welcome_text3} ;
        String[] textHead = getResources().getStringArray(R.array.welcome_text1);
        String[] textNotes = getResources().getStringArray(R.array.welcome_text2);
        for (int i = 0; i < images.length; i++) {
            viewList.add(initView(images[i], textHead[i], textNotes[i]));
        }
        View view = LayoutInflater.from(this).inflate(R.layout.guide_last_item, null);
        viewList.add(view);
        //initDots(images.length);
    }

    private void initDots(int count) {
        for (int j = 0; j < count; j++) {
            mDotsLayout.addView(initDot(), new ViewGroup.LayoutParams(10, 10));
        }
        mDotsLayout.getChildAt(0).setSelected(true);
        mDotsLayout.getChildAt(0).setBackgroundResource(R.drawable.wizard_index_selected);
        mDotsLayout.getChildAt(1).setBackgroundResource(R.drawable.wizard_index_unselected);
        mDotsLayout.getChildAt(2).setBackgroundResource(R.drawable.wizard_index_unselected);
    }

    private View initDot() {

        ImageView dot = (ImageView) LayoutInflater.from(
                getApplicationContext()).inflate(R.layout.layout_dot, null);
        return dot;
    }

    private View initView(int imageRes, String title, String note) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.item_guide, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.guide_image);
        TextView textView1 = (TextView) view.findViewById(R.id.wizard_text1);
        TextView textView2 = (TextView) view.findViewById(R.id.wizard_text2);
        imageView.setImageResource(imageRes);
        textView1.setText(title);
        textView2.setText(note);
//        TextView headText = (TextView) view.findViewById(R.id.wizard_text1);
//        headText.setText(head);
//        TextView noteText = (TextView) view.findViewById(R.id.wizard_text2);
//        noteText.setText(note);
        return view;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registeViews() {
        if (mTendIntent != null) {
            mBCallByUser = mTendIntent.getBooleanExtra("b_user_call", false);
        }
        initPager();
        mPager.setAdapter(new ViewPagerAdapter(viewList));
        mPager.setOnPageChangeListener(mPageListener);

        mBtn.setOnClickListener(l -> {
            mPb.setVisibility(View.VISIBLE);
            mBtn.setVisibility(View.GONE);
//                openHome();
            mPreferenceInfo.setAppFirstLaunch(false);
            mPreferMng.saveFirstLaunch();
            if (!mBCallByUser) {
                firstInit();
//                Intent intent = new Intent(GuideActivity.this, SplashActivity.class);
//                startActivity(intent);
//                finish();
//                DBOpenHelper.copyLocationDb(GuideActivity.this.getApplicationContext()) ;
//                AuthorInfo auth = PreferenceManager.getInstance(getApplicationContext()).getThirdPartyLoginMsg();
//                networkSetup(auth);
            } else {
                goToMain();
            }
        });
    }

    private void firstInit() {
        try {
            FileManager.mainGourmet = Optional.of(dbMng.initGourmets());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        DBOpenHelper.copyLocationDb(GuideActivity.this.getApplicationContext());
        AuthorInfo auth = PreferenceManager.getInstance(getApplicationContext()).getThirdPartyLoginMsg();
        networkSetup(auth);
    }

    private void networkSetup(AuthorInfo auth) {

        if (ThirdPartySDKManager.getInstance(getApplicationContext()).isThirdParty(auth.getPlatform())) {
            Router.getOAuthModule().login(auth.getUid(), auth.getPlatform(), Optional.<String>empty(), Optional.of(auth.getToken()), () -> {
                        //  TinyUser user = Optional.of()Router.INSTANCE.getCurrentUser() ;
                        goToMain();
                    },
                    errorMessage -> {
                        System.out.println("failure " + errorMessage);
                        Toast.makeText(GuideActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        goToMain();
                    });
        } else if ((auth.getEmail() != null) && (!auth.getEmail().equals(""))) {
            //2@2.2
            //c81e728d9d4c2f636f067f89cc14862c
            RouterOAuth.INSTANCE.loginEmailRoutine(auth.getEmail(),
                    auth.getPassword(),
                    Optional.empty(),
                    Optional.empty(),
                    () -> {
                        goToMain();
                    },
                    errorMessage -> {
                        System.out.println("failure " + errorMessage);
                        Toast.makeText(GuideActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        goToMain();
                    }
            );

        } else {
            Router.getOAuthModule().visitor(() -> {
                        goToMain();
                    },
                    errorMessage -> {
                        System.out.println("failure " + errorMessage);
                        Toast.makeText(GuideActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        goToMain();
                    });
        }
        Cache.INSTANCE.setup();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressed = true;
    }

    private void goToMain() {
        if (onBackPressed) {
            return;
        }
        Intent mainIntent = new Intent(getApplicationContext(),
                MainActivity.class);
//        mainIntent.putExtra("login_result" , loadResult) ;
        startActivity(mainIntent);
        finish();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (bKilled) {
            finish();
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> data;

        public ViewPagerAdapter(List<View> data) {
            super();
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(data.get(position));
            return data.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }

    }
    //    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.guide_btn:
//            default:
//        }
//    }

}
