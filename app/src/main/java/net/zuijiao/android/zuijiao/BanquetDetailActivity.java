package net.zuijiao.android.zuijiao;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.bean.HandlerRequestCode;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.zuijiao.adapter.ImageViewPagerAdapter;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentCapacity;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentStatus;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentMenu;
import com.zuijiao.android.zuijiao.model.Banquent.Review;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.thirdopensdk.QQApi;
import com.zuijiao.thirdopensdk.WeiboApi;
import com.zuijiao.thirdopensdk.WeixinApi;
import com.zuijiao.utils.AdapterViewHeightCalculator;
import com.zuijiao.view.BanquetDetailScrollView;
import com.zuijiao.view.ReviewRatingBar;
import com.zuijiao.view.RoundImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaqibo on 2015/6/10.
 * display the detail information of the banquet ;
 */
@ContentView(R.layout.activity_banquet_detail)
public class BanquetDetailActivity extends BaseActivity implements BanquetDetailScrollView.ScrollStateChangeListener, View.OnClickListener {
    @ViewInject(R.id.banquet_detail_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.banquet_detail_bottom)
    private View mBottomView = null;
    @ViewInject(R.id.banquet_detail_division)
    private View mBottomDivisionView;
    @ViewInject(R.id.banquet_detail_scroll_view)
    private BanquetDetailScrollView mScrollView = null;
    @ViewInject(R.id.banquet_detail_images)
    private ViewPager mImagePages = null;
    @ViewInject(R.id.banquet_detail_images_index)
    private TextView mImagesIndex = null;
    @ViewInject(R.id.banquet_detail_banquet_title)
    private TextView mBanquetTitle;
    @ViewInject(R.id.banquet_detail_banquet_description)
    private TextView mBanquetDescription;
    @ViewInject(R.id.banquet_detail_host_head)
    private ImageView mHostHead;
    @ViewInject(R.id.banquet_detail_host_name)
    private TextView mHostName;
    @ViewInject(R.id.banquet_detail_star_container)
    private LinearLayout mStarContainer;
    @ViewInject(R.id.banquet_detail_host_score)
    private TextView mHostScore;
    @ViewInject(R.id.banquet_detail_about_host)
    private Button mAboutHostBtn = null;
    @ViewInject(R.id.banquet_detail_menu_title)
    private TextView mMenuTitle;
    /*@ViewInject(R.id.banquet_detail_menu_content)
    private TextView mMenuContent;*/
    @ViewInject(R.id.banquet_detail_chara_title)
    private TextView mCharaTitle;
    @ViewInject(R.id.banquet_detail_chara_content)
    private TextView mCharaContent;
    @ViewInject(R.id.banquet_detail_instruction_title)
    private TextView mInstructTitle;
    @ViewInject(R.id.banquet_detail_location_text)
    private TextView mInstructPosition;
    @ViewInject(R.id.banquet_detail_date_text)
    private TextView mInstructDate;
    @ViewInject(R.id.banquet_detail_price_text)
    private TextView mInstructPrice;
    @ViewInject(R.id.banquet_detail_request_text)
    private TextView mInstructRequirement;
    @ViewInject(R.id.banquet_detail_instruct_status)
    private TextView mInstructStatus;
    @ViewInject(R.id.banquet_detail_ordered_person)
    private GridView mOrderedPersonShow;
    @ViewInject(R.id.banquet_detail_review_container)
    private LinearLayout mReviewContainer;
    @ViewInject(R.id.banquet_detail_lastest_comment)
    private View mLastComment;
    @ViewInject(R.id.banquet_detail_comment_btn)
    private Button mAllCommentBtn;
    @ViewInject(R.id.banquet_detail_bottom_price)
    private TextView mBottomPrice;
    @ViewInject(R.id.banquet_detail_bottom_date)
    private TextView mBottomDate;
    @ViewInject(R.id.banquet_detail_bottom_order)
    private Button mOrderBtn;
    @ViewInject(R.id.banquet_detail_menu_container)
    private View mMenuContainer;
    @ViewInject(R.id.banquet_detail_char_container)
    private View mCharactContainer;
    @ViewInject(R.id.banquet_detail_finished)
    private TextView mFinishText;
    @ViewInject(R.id.banquet_detail_bottom_can_order)
    private View mBottomOrderView;
    @ViewInject(R.id.banquet_comment_rtatingbar)
    private ReviewRatingBar mCommentRatingbar;
    @ViewInject(R.id.banquet_datail_root)
    private View rootView = null;

    @ViewInject(R.id.lv_menu_dishes_item)
    private ListView menuList;

    private Reviews mReviews;
    private Banquent mBanquent;
    private String[] weekDays;
    private ImageViewPagerAdapter mViewPagerAdapter = null;

//    private static final int SHARE_TO_WEIBO = 0;
//    private static final int SHARE_TO_WECHAT = 1;
//    private static final int SHARE_TO_FRIEND_CIRCLE = 2;
//    private static final int SHARE_TO_QQ = 3;
//    private static final int SHARE_TO_QQ_SPACE = 4;
//    private static final int SHARE_TO_SMS = 5;
        private static final int SHARE_TO_WECHAT = 0;
    private static final int SHARE_TO_FRIEND_CIRCLE = 1;
    private static final int SHARE_TO_QQ = 2;
    private static final int SHARE_TO_QQ_SPACE = 3;
    private static final int SHARE_TO_SMS = 4;
    private String mShareUrl = "/feast/";
    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
//    private int mShareImageRes[] = {R.drawable.share_weibo, R.drawable.share_weixin, R.drawable.share_friend_circle, R.drawable.share_qq, R.drawable.share_qq_space,R.drawable.share_sms};
    private int mShareImageRes[] = {R.drawable.share_weixin, R.drawable.share_friend_circle, R.drawable.share_qq, R.drawable.share_qq_space,R.drawable.share_sms};

    private int mUnShareImageRes[] = { R.drawable.unshare_weixin, R.drawable.unshare_friend_circle, R.drawable.unshare_qq, R.drawable.unshare_qq_space,R.drawable.unshare_sms};
//    private int mUnShareImageRes[] = {R.drawable.unshare_weibo, R.drawable.unshare_weixin, R.drawable.unshare_friend_circle, R.drawable.unshare_qq, R.drawable.unshare_qq_space,R.drawable.unshare_sms};

    private int mShareTextRes[] = { R.string.weixin_friend, R.string.weixin_friend_circle, R.string.qq_friend, R.string.qq_space,R.string.sms};
    private int rootBottom = Integer.MIN_VALUE;
    private boolean withImage = true;
    private SsoHandler mSsoHandler;
    boolean qqInstalled = false;
    boolean qoneInstalled = false;
    boolean wxInstalled = false;
    boolean wxCircleInstalled = false;
    boolean smsInstalled = false;

    private TranslateAnimation hideToolbarAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
    private TranslateAnimation showToolbarAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
    private TranslateAnimation hideBottomViewAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
    private TranslateAnimation showBottomViewAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            mImagesIndex.setText((arg0 + 1) + "/" + mViewPagerAdapter.getCount());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Router.getInstance().getCurrentUser().isPresent() && Router.getInstance().getCurrentUser().get().getIdentifier().equals(mBanquent.getIdentifier())) {
            getMenuInflater().inflate(R.menu.gourmet_detail, menu);
        } else {
            getMenuInflater().inflate(R.menu.gourmet_detail_simple, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gourmet_detail_share:
                createShareWindow();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * call the list of the share ways , including qq friend ,qq space ,weibo ,wechat space ,wechat friend ,msg
     */
    private void createShareWindow() {

        View view = null;
        GridView shareList = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.share_dialog, null);
        shareList = (GridView) view.findViewById(R.id.gv_share_dialog);
        shareList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 5;
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
                View contentView = LayoutInflater.from(mContext).inflate(R.layout.share_item, null);
                TextView text = (TextView) contentView.findViewById(R.id.share_item_text);
                ImageView image = (ImageView) contentView.findViewById(R.id.share_item_image);
                text.setText(getString(mShareTextRes[position]));
                switch (position) {
//                    case 0:
//                        mController.getConfig().setSsoHandler(new SinaSsoHandler());
//                        mController.getConfig().setSinaCallbackUrl(WeiboApi.REDIRECT_URL);
////                        sineInstalled = mController.getConfig().getSsoHandler(HandlerRequestCode.SINA_REQUEST_CODE).isClientInstalled();
////                        if (sineInstalled) {
//                               image.setImageResource(mShareImageRes[0]);
////                        } else {
////                            image.setImageResource(mUnShareImageRes[0]);
////                        }
//                        break;
                    case 0:
                        UMWXHandler wxHandler = new UMWXHandler(mContext, WeixinApi.WEIXIN_ID, WeixinApi.WEIXIN_PWD);
                        wxHandler.addToSocialSDK();
                        wxInstalled = mController.getConfig().getSsoHandler(HandlerRequestCode.WX_REQUEST_CODE).isClientInstalled();
                        if (wxInstalled) {
                            image.setImageResource(mShareImageRes[position]);
                        } else {
                            image.setImageResource(mUnShareImageRes[position]);
                        }
                        break;
                    case 1:
                        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, WeixinApi.WEIXIN_ID, WeixinApi.WEIXIN_PWD);
                        wxCircleHandler.setToCircle(true);
                        wxCircleHandler.addToSocialSDK();
                        wxCircleInstalled = mController.getConfig().getSsoHandler(HandlerRequestCode.WX_CIRCLE_REQUEST_CODE).isClientInstalled();
                        if (wxCircleInstalled) {
                            image.setImageResource(mShareImageRes[position]);
                        } else {
                            image.setImageResource(mUnShareImageRes[position]);
                        }
                        break;
                    case 2:
                        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(BanquetDetailActivity.this,QQApi.QQ_ID, QQApi.QQ_PWD);
                        qqSsoHandler.addToSocialSDK();
                        qqInstalled = mController.getConfig().getSsoHandler(HandlerRequestCode.QQ_REQUEST_CODE).isClientInstalled();
                        if (qqInstalled) {
                            image.setImageResource(mShareImageRes[position]);
                        } else {
                            image.setImageResource(mUnShareImageRes[position]);
                        }
                        break;
                    case 3:
                        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(BanquetDetailActivity.this,
                                QQApi.QQ_ID, QQApi.QQ_PWD);
                        qZoneSsoHandler.addToSocialSDK();
                        qoneInstalled = mController.getConfig().getSsoHandler(HandlerRequestCode.QZONE_REQUEST_CODE).isClientInstalled();
                        if (qoneInstalled) {
                            image.setImageResource(mShareImageRes[position]);
                        } else {
                            image.setImageResource(mUnShareImageRes[position]);
                        }
                        break;
                    case 4:
                        smsInstalled = ((TelephonyManager)mContext.getSystemService(TELEPHONY_SERVICE)).getSimState()==1?false:true;
                        if (smsInstalled) {
                            image.setImageResource(mShareImageRes[position]);
                        } else {
                            image.setImageResource(mUnShareImageRes[position]);
                        }
                        break;
                }
                return contentView;
            }
        });

        final PopupWindow sharePopupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        shareList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                sharePopupWindow.dismiss();
                distributeShareAction(position);
            }
        });
        sharePopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        sharePopupWindow.setTouchable(true);
        sharePopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        sharePopupWindow.setOnDismissListener(new poponDismissListener());
        sharePopupWindow.setOutsideTouchable(true);
        sharePopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
    }

    /**
     * share to different way
     *
     * @param action
     */
    private void distributeShareAction(int action) {
//        mController.setShareImage(new UMImage(mContext, mBanquent.getSurfaceImageUrl()));
//        mController.setShareContent(String.format(getString(R.string.share_content), mBanquent.getTitle()) + "\n" + BuildConfig.Banquet_Web_Url + mShareUrl + mBanquent.getIdentifier());
        mController.setShareContent(String.format(getString(R.string.banquet_share_content ) , mBanquent.getTitle()));
        mController.setShareMedia(new UMImage(mContext,
                mBanquent.getSurfaceImageUrl()));
//        mController.setAppWebSite(SHARE_MEDIA.WEIXIN, mBanquent.getSurfaceImageUrl());
        switch (action) {
//            case SHARE_TO_WEIBO:
//                if (sineInstalled) {
//                    mController.getConfig().setSsoHandler(new SinaSsoHandler());
//                    mController.getConfig().setSinaCallbackUrl(WeiboApi.REDIRECT_URL);
//                    SinaSsoHandler sinaSsoHandler = new SinaSsoHandler(mContext) ;
//                    sinaSsoHandler.setTargetUrl(BuildConfig.Banquet_Web_Url + mShareUrl + mBanquent.getIdentifier());
//                    sinaSsoHandler.addToSocialSDK();
//                    performShare(SHARE_MEDIA.SINA);
//                }else{
//                    Toast.makeText(mContext,"您尚未安装此应用...",Toast.LENGTH_SHORT).show();
//                }
//                break;
            case SHARE_TO_WECHAT:
                if (wxInstalled) {
                    UMWXHandler wxHandler = new UMWXHandler(mContext, WeixinApi.WEIXIN_ID, WeixinApi.WEIXIN_PWD);
                    wxHandler.setTargetUrl(BuildConfig.Banquet_Web_Url + mShareUrl + mBanquent.getIdentifier());
                    wxHandler.addToSocialSDK();
                    performShare(SHARE_MEDIA.WEIXIN);
                }else {
                    Toast.makeText(mContext,"您尚未安装此应用...",Toast.LENGTH_SHORT).show();
                }
                break;
            case SHARE_TO_FRIEND_CIRCLE:
                if (wxCircleInstalled) {
                    UMWXHandler wxCircleHandler = new UMWXHandler(mContext, WeixinApi.WEIXIN_ID, WeixinApi.WEIXIN_PWD);
                    wxCircleHandler.setToCircle(true);
                    wxCircleHandler.addToSocialSDK();
                    wxCircleHandler.setRefreshTokenAvailable(true);
                    wxCircleHandler.setTitle(getString(R.string.wx_circle_share_title));
                    wxCircleHandler.setTargetUrl(BuildConfig.Banquet_Web_Url + mShareUrl + mBanquent.getIdentifier());
                    performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                }else {
                    Toast.makeText(mContext,"您尚未安装此应用...",Toast.LENGTH_SHORT).show();
                }
                break;
            case SHARE_TO_QQ:
                if (qqInstalled) {
                    UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                            QQApi.QQ_ID, QQApi.QQ_PWD);
                    qqSsoHandler.addToSocialSDK();
                    qqSsoHandler.setTitle(getString(R.string.wx_circle_share_title));
                    qqSsoHandler.setTargetUrl(BuildConfig.Banquet_Web_Url + mShareUrl + mBanquent.getIdentifier());
                    performShare(SHARE_MEDIA.QQ);
                }else {
                    Toast.makeText(mContext,"您尚未安装此应用...",Toast.LENGTH_SHORT).show();
                }
                break;
            case SHARE_TO_QQ_SPACE:
                if (qoneInstalled) {
                    QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
                            QQApi.QQ_ID, QQApi.QQ_PWD);
                    qZoneSsoHandler.addToSocialSDK();
                    qZoneSsoHandler.setTargetUrl(BuildConfig.Banquet_Web_Url + mShareUrl + mBanquent.getIdentifier());
                    performShare(SHARE_MEDIA.QZONE);
                }else {
                    Toast.makeText(mContext,"您尚未安装此应用...",Toast.LENGTH_SHORT).show();
                }
                break;
            case SHARE_TO_SMS:
                if (smsInstalled) {
                    SmsHandler smsHandler = new SmsHandler();
                    smsHandler.addToSocialSDK();
                    mController.setShareContent(String.format(getString(R.string.banquet_share_content), mBanquent.getTitle())
                            + "\n" + BuildConfig.Banquet_Web_Url + mShareUrl + mBanquent.getIdentifier());
                    performShare(SHARE_MEDIA.SMS);
                }else {
                    Toast.makeText(mContext,"您尚未安装SMS卡...",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void performShare(SHARE_MEDIA platform) {
        mController.postShare(this, platform, new SocializeListeners.SnsPostListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
            }
        });
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }
    private AdapterView.OnItemClickListener mGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            createDialog();
            Router.getAccountModule().banquetUserInfo(mBanquent.getAttendees().get(position).getIdentifier(), new OneParameterExpression<Attendee>() {
                @Override
                public void action(Attendee attendee) {
                    finalizeDialog();
                    Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("attendee_info", attendee);
                    mContext.startActivity(intent);
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    finalizeDialog();
                }
            });
        }
    };
    /**
     * display ordered user's avatar
     */
    private BaseAdapter mGridAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (mBanquent.getAttendees() != null)
                return mBanquent.getAttendees().size();
            return 0;
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
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.user_info_favor_item, null);
            TinyUser user = mBanquent.getAttendees().get(position);
            ImageView userHead = (ImageView) contentView.findViewById(R.id.user_info_favor_item_image);
            TextView userName = (TextView) contentView.findViewById(R.id.user_info_favor_item_text);
            if (user.getAvatarURLSmall().isPresent() && !user.getAvatarURLSmall().get().equals("http://pic.zuijiao.net")) {
                String url = user.getAvatarURLSmall().get();
                Picasso.with(mContext)
                        .load(user.getAvatarURLSmall().get())
                        .placeholder(R.drawable.default_user_head)
                        .fit()
                        .centerCrop()
                        .into(userHead);
            }
//            userName.setText(user.getNickName());
            userName.setVisibility(View.GONE);
            return contentView;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(((ActivityTask) getApplication()).getDefaultDisplayImageOptions()).memoryCacheExtraOptions(50, 50)
                .threadPoolSize(1).build();
        ImageLoader.getInstance().init(config);
        if (mTendIntent != null) {
            mBanquent = (Banquent) mTendIntent.getSerializableExtra("banquet");
        }
        if (mBanquent == null) {
            finish();
            return;
        }
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        mScrollView.setScrollStateListener(this);
        mViewPagerAdapter = new ImageViewPagerAdapter(initImages());
        mImagePages.setAdapter(mViewPagerAdapter);
        mImagePages.setOnPageChangeListener(mPageListener);
        mImagesIndex.setText(1 + "/" + mViewPagerAdapter.getCount());
        initViewsByBanquet();
        menuList.setAdapter(MyAdapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                return;
            }
        });
        AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(menuList);
        mInstructPosition.setOnClickListener(this);
        mAllCommentBtn.setOnClickListener(this);
        mAboutHostBtn.setOnClickListener(this);
        mOrderBtn.setOnClickListener(this);
        mHostHead.setOnClickListener(this);
        Router.getBanquentModule().commentsOfBanquent(mBanquent.getMaster().getIdentifier(), null, 1, new OneParameterExpression<Reviews>() {
            @Override
            public void action(Reviews reviews) {
                mReviews = reviews;
                registerCommentView();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.get_history_list_failed), Toast.LENGTH_SHORT).show();
            }
        });
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBottomView.getLayoutParams();
        mBottomPrice.measure(0 , 0 );
        int priceHeight = mBottomPrice.getMeasuredHeight();
        mBottomDate.measure(0 , 0);
        int dateHeight = mBottomDate.getMeasuredHeight() ;
        int margin = (int) (3* getResources().getDimension(R.dimen.end_z));
        params.height = priceHeight + dateHeight + margin ;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
        layoutParams.bottomMargin = params.height ;
    }

    private BaseAdapter MyAdapter = new BaseAdapter(){
        @Override
        public int getCount() {
            if (mBanquent.getMenus() != null){
                return mBanquent.getMenus().size();
            }else {
                return 0;
            }
        }


        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.menu_dishes_item, null);
                holder = new ViewHolder();
                holder.menu = (TextView)(view .findViewById(R.id.menu_dishes_item_menu));
                holder.dishes = (TextView)(view .findViewById(R.id.menu_dishes_item_dishes));
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            BanquentMenu menu = mBanquent.getMenus().get(i);
            String categoryName = menu.getCategoryName() ;
            if(categoryName == null || categoryName.equals(""))
                holder.menu.setVisibility(View.GONE);
            else
            holder.menu.setText(categoryName);
            ArrayList<String> dishes =  mBanquent.getMenus().get(i).getDishes();
            StringBuilder strBuilder = new StringBuilder();
            for(int j = 0;j < dishes.size();j++ ){
                if (j != dishes.size()-1) {
                    strBuilder.append(dishes.get(j) + "\n");
                }else{
                    strBuilder.append(dishes.get(j));
                }
            }
            String dishList = strBuilder.toString();
            holder.dishes.setText(dishList);
            return view;
        }
    };
    class ViewHolder{
        TextView menu;
        TextView dishes;
    }
    private void registerCommentView() {
        List<Review> reviewList = mReviews.getReviewList();
        if (reviewList != null && reviewList.size() != 0) {
            mReviewContainer.setVisibility(View.VISIBLE);
            mStarContainer.setVisibility(View.VISIBLE);
            if (mReviews.getTotalCount() > 1) {
                mAllCommentBtn.setVisibility(View.VISIBLE);
                mAllCommentBtn.setText(String.format(getString(R.string.all_comments), mReviews.getTotalCount()));
            } else {
                mAllCommentBtn.setVisibility(View.GONE);
            }
            Review review = reviewList.get(0);
//            mCommentRatingbar.setRating(4.5f);
            mCommentRatingbar.setRating(mBanquent.getMaster().getScore());
            mHostScore.setText("(" + mReviews.getTotalCount() + ")");
            ImageView head = (RoundImageView) mLastComment.findViewById(R.id.banquet_comment_item_head);
            ImageLoader.getInstance().displayImage(review.getReviewer().getAvatarURLSmall().get(), head);
            head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog();
                    Router.getAccountModule().banquetUserInfo(review.getReviewer().getIdentifier(), new OneParameterExpression<Attendee>() {
                        @Override
                        public void action(Attendee attendee) {
                            finalizeDialog();
                            Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("attendee_info", attendee);
                            mContext.startActivity(intent);
                        }
                    }, new OneParameterExpression<String>() {
                        @Override
                        public void action(String s) {
                            finalizeDialog();
                        }
                    });
                }
            });
            ((TextView) mLastComment.findViewById(R.id.banquet_comment_item_user_name)).setText(review.getReviewer().getNickName());
            ((TextView) mLastComment.findViewById(R.id.banquet_comment_item_issue)).setText(review.getEvent().getTitle() + " · " + formatDate(review.getEvent().getTime()));
            ((ReviewRatingBar) mLastComment.findViewById(R.id.banquet_comment_item_stars)).setRating(review.getScore());
            ((TextView) mLastComment.findViewById(R.id.banquet_comment_item_comment)).setText(review.getContent());
        } else {
            mReviewContainer.setVisibility(View.GONE);
            mStarContainer.setVisibility(View.GONE);
        }
    }

    private void initViewsByBanquet() {
        mBanquetTitle.setText(mBanquent.getTitle());
        mBanquetDescription.setText(mBanquent.getDesc());
        if (mBanquent.getMaster().getAvatarURLSmall().isPresent())
            Picasso.with(mContext)
                    .load(mBanquent.getMaster().getAvatarURLSmall().get())
                    .placeholder(R.drawable.default_user_head)
                    .fit()
                    .centerCrop()
                    .into(mHostHead);
        mHostName.setText(mBanquent.getMaster().getNickName());

        String characteristic = mBanquent.getCharacteristic();
        if (characteristic == null || characteristic.equals("")) {
            mCharactContainer.setVisibility(View.GONE);
        } else
            mCharaContent.setText(characteristic);
        String address = mBanquent.getAddress();
        if (address == null || address.equals("")) {
            findViewById(R.id.banquet_detail_instruction_content_position).setVisibility(View.GONE);
        } else
            mInstructPosition.setText(address);
        if (mBanquent.getTime() == null) {
            findViewById(R.id.banquet_detail_instruction_content_date).setVisibility(View.GONE);
        } else {
            String date = formatDate(mBanquent.getTime());
            String endDate = formatDate(mBanquent.getEndTime());
            if (mBanquent.getTime().getDay() == mBanquent.getEndTime().getDay()) {
                date = date + " ~ " + endDate.substring(endDate.length() - 8, endDate.length());
            } else {
                date = date + " ~ " + endDate;
            }
            mInstructDate.setText(date);
        }

        if (mBanquent.getPrice() == null) {
            findViewById(R.id.banquet_detail_instruction_content_price).setVisibility(View.GONE);
        } else
            mInstructPrice.setText(String.format(getString(R.string.price_per_one), mBanquent.getPrice()));
        String requirement = mBanquent.getRequirement();
        if (requirement == null || requirement.equals("")) {
            findViewById(R.id.banquet_detail_instruction_content_requirement).setVisibility(View.GONE);
        }
        mInstructRequirement.setText(mBanquent.getRequirement());
        BanquentCapacity banquentCapacity = mBanquent.getBanquentCapacity();
        if (banquentCapacity.getMax() == banquentCapacity.getMin()) {
            mInstructStatus.setText(String.format(getString(R.string.banquent_capacity_simple),
                    banquentCapacity.getMin(),
                    banquentCapacity.getCount()));
        } else {
            mInstructStatus.setText(String.format(getString(R.string.banquent_capacity_muilt),
                    banquentCapacity.getMin(),
                    banquentCapacity.getMax(),
                    banquentCapacity.getCount()));
        }
        mOrderedPersonShow.setAdapter(mGridAdapter);
        mOrderedPersonShow.setOnItemClickListener(mGridListener);
        AdapterViewHeightCalculator.setGridViewHeightBasedOnChildren(mOrderedPersonShow);
        switch (BanquentStatus.fromString(mBanquent.getStatus())) {
            case Selling:

                break;
            case SoldOut:
                mOrderBtn.setText(getString(R.string.banquet_status_sold_out));
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                mOrderBtn.setEnabled(false);
                break;
            case OverTime:
                mOrderBtn.setText(getString(R.string.banquet_status_over_time));
                mOrderBtn.setEnabled(false);
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                break;
            case End:
                mOrderBtn.setText(getString(R.string.banquet_status_end));
                mOrderBtn.setEnabled(false);
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                break;
        }
        mBottomPrice.setText(String.valueOf(mBanquent.getPrice()));
        mBottomDate.setText(formatDate(mBanquent.getTime()));
    }


    /**
     * init the banquet display images ,
     * which shown on top of the activity in a viewpager;
     *
     * @return image-view list
     */
    private ArrayList<ImageView> initImages() {
        ArrayList<ImageView> mImageList = new ArrayList<>();
        if (mBanquent.getImageUrls() == null)
            return null;
        for (String imageUrl : mBanquent.getImageUrls()) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BanquetDetailActivity.this, BigImageActivity.class);
                    int currentImageIndex = mImagePages.getCurrentItem();
                    intent.putExtra("current_image_index", currentImageIndex);
                    intent.putStringArrayListExtra("cloud_images", mBanquent.getImageUrls());
                    startActivity(intent);
                }
            });
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.empty_view_greeting)
                    .error(R.drawable.empty_view_greeting)
                    .fit()
                    .centerCrop()
                    .into(image);
            mImageList.add(image);
        }
        return mImageList;
    }

    /**
     * listen to the scroll of the scroller view ;
     *
     * @param gapScrollY
     */
    @Override
    public void onScroll(int gapScrollY) {
        Log.i("gapScrollY", "gapScrollY == " + gapScrollY);
        if (gapScrollY > 0) {
            showToolbar();
        } else {
            hideToolbar();
        }
    }

    /**
     * form a list to a string ,split each item by \n
     *
     * @return formatted string
     */
    /*private String formatMenuContent() {
        if (mBanquent.getMenus() == null || mBanquent.getMenus().size() == 0) {
            return "";
        }
        StringBuilder strBuilder = new StringBuilder();
        for (String menuItem : mBanquent.getMenus()) {
            strBuilder.append(menuItem);
            if (mBanquent.getMenus().indexOf(menuItem) != mBanquent.getMenus().size() - 1)
                strBuilder.append("\n");
        }
        return strBuilder.toString();
    }*/

    private void hideToolbar() {
        if (mToolbar.getVisibility() == View.GONE)
            return;
        hideToolbarAnim.setDuration(500);
        mToolbar.startAnimation(hideToolbarAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.GONE);
            }
        }, 500);
    }

    private void showToolbar() {
        if (mToolbar.getVisibility() == View.VISIBLE)
            return;
        showToolbarAnim.setDuration(500);
        mToolbar.startAnimation(showToolbarAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.banquet_detail_comment_btn:
                intent.setClass(mContext, BanquetCommentActivity.class);
                intent.putExtra("host_id", mBanquent.getMaster().getIdentifier());
                intent.putExtra("totalCount", mReviews.getTotalCount());
                startActivity(intent);
                break;
            case R.id.banquet_detail_host_head:
            case R.id.banquet_detail_about_host:

                createDialog();
                Router.getAccountModule().banquetUserInfo(mBanquent.getMaster().getUserId(), new OneParameterExpression<Attendee>() {
                    @Override
                    public void action(Attendee attendee) {
                        finalizeDialog();
                        Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("attendee_info", attendee);
//                            intent.putExtra("b_host", true);
//                            intent.putExtra("attendee_id", banquent.getMaster().getUserId());
                        mContext.startActivity(intent);
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        finalizeDialog();
                    }
                });
//                intent.putExtra("b_host", true);
//                intent.putExtra("attendee_id", mBanquent.getMaster().getUserId());
//                intent.setClass(mContext, HostAndGuestActivity.class);
//                startActivity(intent);
                break;
            case R.id.banquet_detail_bottom_order:
                if (!Router.getInstance().getCurrentUser().isPresent()) {
                    tryLoginFirst(new LambdaExpression() {
                        @Override
                        public void action() {
                            if (Router.getInstance().getCurrentUser().isPresent()) {
                                goToOrder();
                            } else {
                                notifyLogin(null);
                            }
                        }
                    }, new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer integer) {
                            Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    goToOrder();
                }
                break;
            case R.id.banquet_detail_location_text:
            case R.id.banquet_detail_location_icon:
            case R.id.banquet_detail_location_img:
                intent = new Intent();
                intent.setClass(mContext,BaiDuMapActivity.class);
                intent.putExtra("address",mBanquent.getAddress());
                startActivity(intent);
                break;
        }
    }


    /**
     * before go to the order activity ,
     * fetch user info and check if telephone num is filled in ,
     * and used as default order connect way ;
     */
    private void goToOrder() {
        Intent intent = new Intent();
        createDialog();
        Router.getAccountModule().fetchMyInfo(new OneParameterExpression<User>() {
            @Override
            public void action(User user) {
                String phoneNum = "";
                try {
                    phoneNum = user.getContactInfo().get().getPhoneNumber();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                intent.setClass(mContext, BanquetOrderCreateActivity.class);
                intent.putExtra("banquet", mBanquent);
                if (phoneNum != null && !phoneNum.equals(""))
                    intent.putExtra("contact_phone_num", phoneNum);
                startActivity(intent);
                finalizeDialog();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                finalizeDialog();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * show the date in the specified format
     *
     * @param date
     * @return formatted string
     */
    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(mContext.getString(R.string.month_day), date.getMonth() + 1, date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(String.format(mContext.getString(R.string.banquet_format_time), date.getHours(), date.getMinutes()));
        strBuilder.append(" ");
        return strBuilder.toString();
    }


}
