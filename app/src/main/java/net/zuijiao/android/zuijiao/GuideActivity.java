package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.FileManager;
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
        int[] images = new int[]{R.drawable.guide_image1, R.drawable.guide_image2,
                R.drawable.guide_image3, R.drawable.guide_image4};
        int[] texts = new int[]{R.drawable.guide_text1, R.drawable.guide_text2, R.drawable.guide_text3, R.drawable.guide_text4};
        for (int i = 0; i < images.length; i++) {
            viewList.add(initView(images[i], texts[i]));
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
        mDotsLayout.getChildAt(3).setBackgroundResource(R.drawable.wizard_index_unselected);
    }

    private View initDot() {
        ImageView dot = (ImageView) LayoutInflater.from(
                getApplicationContext()).inflate(R.layout.layout_dot, null);
        return dot;
    }

    private View initView(int imageRes, int textRes) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.item_guide, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.guide_image);
        ImageView textImage = (ImageView) view.findViewById(R.id.guide_text);
        imageView.setImageResource(imageRes);
        textImage.setImageResource(textRes);
        return view;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        if (mTendIntent != null) {
            mBCallByUser = mTendIntent.getBooleanExtra("b_user_call", false);
        }
        initPager();
        mPager.setAdapter(new ViewPagerAdapter(viewList));
        mPager.setOnPageChangeListener(mPageListener);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPb.setVisibility(View.VISIBLE);
                mBtn.setVisibility(View.GONE);
//                openHome();
                mPreferMng.getPreferInfo().setAppFirstLaunch(false);
                mPreferMng.saveFirstLaunch();
                if (!mBCallByUser) {
                    firstInit();
                } else {
                    goToMain();
                }
            }
        });
    }

    private void firstInit() {
        tryLoginFirst(null, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Gourmet> list = dbMng.initGourmets();
                    if (list != null) {
                        FileManager.mainGourmet = Optional.ofNullable(list);
                    } else {
                        FileManager.mainGourmet = Optional.empty();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                DBOpenHelper.copyLocationDb(mContext);
                goToMain();
            }
        }, 800);
    }

    private void networkSetup(AuthorInfo auth) {
        if (ThirdPartySDKManager.getInstance(getApplicationContext()).isThirdParty(auth.getPlatform())) {
            Router.getOAuthModule().login(auth.getUid(), auth.getPlatform(), Optional.<String>empty(), Optional.of(auth.getToken()), new LambdaExpression() {
                        @Override
                        public void action() {
                            goToMain();
                        }
                    },
                    new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer errorMessage) {
                            System.out.println("failure " + errorMessage);
                            Toast.makeText(GuideActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                            goToMain();
                        }
                    });
        } else if ((auth.getEmail() != null) && (!auth.getEmail().equals(""))) {
            //2@2.2
            //c81e728d9d4c2f636f067f89cc14862c
            RouterOAuth.INSTANCE.loginEmailRoutine(auth.getEmail(),
                    auth.getPassword(),
                    Optional.empty(),
                    Optional.empty(),
                    new LambdaExpression() {
                        @Override
                        public void action() {
                            goToMain();
                        }
                    },
                    new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer errorMessage) {
                            System.out.println("failure " + errorMessage);
                            Toast.makeText(GuideActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                            goToMain();
                        }
                    }
            );

        } else {
            Router.getOAuthModule().visitor(new LambdaExpression() {
                                                @Override
                                                public void action() {
                                                    goToMain();
                                                }
                                            },
                    new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer errorMessage) {
                            System.out.println("failure " + errorMessage);
                            Toast.makeText(GuideActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                            goToMain();
                        }
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
