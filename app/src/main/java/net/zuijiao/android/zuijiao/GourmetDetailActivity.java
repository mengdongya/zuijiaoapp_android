package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.SocializeUtils;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.zuijiao.adapter.GourmetCommentAdapter;
import com.zuijiao.adapter.ImageViewPagerAdapter;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Comments;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.WouldLikeToEatUser;
import com.zuijiao.android.zuijiao.model.user.WouldLikeToEatUsers;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.thirdopensdk.QQApi;
import com.zuijiao.thirdopensdk.WeiboApi;
import com.zuijiao.thirdopensdk.WeixinApi;
import com.zuijiao.utils.AdapterViewHeightCalculator;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.GourmetDetailScrollView;
import com.zuijiao.view.GourmetDetailScrollView.OnScrollListener;
import com.zuijiao.view.WordWrapView;

import java.util.ArrayList;

/**
 * show the detail information of a gourmet ;
 */
@SuppressLint("ShowToast")
@ContentView(R.layout.activity_gourmet_detail)
public class GourmetDetailActivity extends BaseActivity implements
        OnScrollListener {

    public static final int EDIT_GOURMET_REQ = 4001;
    public static final int MORE_COMMENT_REQ = 4002;
    public static final String SCOPE = "all";
    private static final int SHARE_TO_WEIBO = 0;
    private static final int SHARE_TO_WECHAT = 1;
    private static final int SHARE_TO_FRIEND_CIRCLE = 2;
    private static final int SHARE_TO_QQ = 3;
    private static final int SHARE_TO_QQ_SPACE = 4;
    private static final int SHARE_TO_SMS = 5;
    //share by umengSDK
    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    @ViewInject(R.id.food_detail_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.food_detail_container)
    private GourmetDetailScrollView mScrollView = null;
    @ViewInject(R.id.food_detail_layout)
    private LinearLayout mFloatView;
    @ViewInject(R.id.food_detail_layout_top)
    private LinearLayout mTopFloatView;
    @ViewInject(R.id.lv_food_detail_comment)
    private ListView mCommentList = null;
    private LayoutInflater mInflater = null;
    @ViewInject(R.id.food_detail_parent)
    private View rootView = null;
    @ViewInject(R.id.view_wordwrap)
    private WordWrapView mLabelContainer = null;
    @ViewInject(R.id.food_detail_images)
    private ViewPager mImagePager = null;
    @ViewInject(R.id.image_container)
    private FrameLayout mImageContainer = null;
    @ViewInject(R.id.gv_favor_people)
    private GridView mGdView = null;
    @ViewInject(R.id.food_detail_layout_top)
    private View mLayoutTop = null;
    @ViewInject(R.id.food_detail_layout)
    private View mLayoutTopFloat = null;
    @ViewInject(R.id.food_detail_image_dots)
    private LinearLayout mImageIndex = null;
    private TopViewHolder floatHolder = null;
    private TopViewHolder topHolder = null;
    @ViewInject(R.id.food_detail_food_msg_title)
    private TextView mGourmetMsgTitle = null;
    @ViewInject(R.id.food_detail_food_msg_splite)
    private View mGourmetMsgSpliteView = null;
    @ViewInject(R.id.food_detail_food_msg_price)
    private TextView mFoodPrice = null;
    @ViewInject(R.id.food_detail_food_msg_location)
    private TextView mFoodLocation = null;
    @ViewInject(R.id.none_person_like)
    private TextView mNonePerson = null;
    @ViewInject(R.id.content_item_comment)
    private TextView mFoodDescription = null;
    @ViewInject(R.id.food_detail_food_like_title)
    private TextView mWouldLikeTitle = null;
    @ViewInject(R.id.food_detail_food_comment_title)
    private TextView mCommentTitle = null;
    @ViewInject(R.id.none_comment_text)
    private TextView mNoneComment = null;
    @ViewInject(R.id.et_comment)
    private EditText mEtComment = null;
    @ViewInject(R.id.comment_commit)
    private ImageButton mCommentCommit = null;
    @ViewInject(R.id.food_detail_content)
    private LinearLayout mContentLayout = null;
    @ViewInject(R.id.food_detail_more_comment)
    private Button mMoreCommentBtn = null;
    private Gourmet gourmet = null;
    //private WouldLikeToEatUsers mUsers = null;
    private Comments mComments = null;
    private ArrayList<ImageView> mImageList = null;
    //if comment false ,reply true ;
    private Integer mReplyId = null;
    private GourmetCommentAdapter mCommentAdapter = null;
    private ImageViewPagerAdapter mViewPagerAdapter = null;

    private Optional<WouldLikeToEatUsers> mWouldLikeToEatList = Optional.empty();
    private String mShareUrl = "/zuijiao/share/cuisine?id=";

    private int mShareImageRes[] = {R.drawable.share_weibo, R.drawable.share_weixin, R.drawable.share_friend_circle, R.drawable.share_qq, R.drawable.share_qq_space,R.drawable.share_sms};
    private int mShareTextRes[] = {R.string.weibo, R.string.weixin_friend, R.string.weixin_friend_circle, R.string.qq_friend, R.string.qq_space,R.string.sms};
    private AuthInfo mAuthInfo = null;
    private SsoHandler mSsoHandler;
    private int rootBottom = Integer.MIN_VALUE;
    private boolean withImage = true;

    private int mToolbarHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NewApi")
    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarHeight = (int) getResources().getDimension(R.dimen.toolbar_height);
        try {
            gourmet = (Gourmet) mTendIntent.getSerializableExtra("selected_gourmet");
            System.out.println("gourmet.id:" + gourmet.getIdentifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gourmet == null) {
            this.finish();
            return;
        }
        mResource = getResources();
        mInflater = LayoutInflater.from(this);
        mScrollView.setOnScrollListener(this);
        //listen to the root view layout changes
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rootView.getGlobalVisibleRect(r);
                        if (rootBottom == Integer.MIN_VALUE) {
                            rootBottom = r.bottom;
                        } else {
                            if (withImage)
                                mScrollView.layout(mScrollView.getLeft(), mScrollView.getCurrentY(), mScrollView.getRight(), mScrollView.getBottom());
                            else {
                                mScrollView.layout(mScrollView.getLeft(), (int) mScrollView.getTopY(), mScrollView.getRight(), mScrollView.getBottom());
                            }
                        }
                        onScroll(mScrollView.getScrollY());
                        onTopChange(mScrollView.getTop());
                    }
                });
        mImagePager.setOnPageChangeListener(mPageListener);
        mEtComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mReplyId = null;
                mEtComment.setHint(getString(R.string.comment_hint));
            }
        });
        mGdView.setOnItemClickListener(mGvListener);
        mCommentList.setOnItemClickListener(mCommentListListener);
        mCommentCommit.setOnClickListener(mCommentCommitListener);
        mMoreCommentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GourmetCommentActivity.class);
                intent.putExtra("gourmet_identify", gourmet.getIdentifier());
                startActivityForResult(intent, MORE_COMMENT_REQ);
            }
        });
        initViewByGourmet();
        fetchWouldLikeList();
        fetchCommentList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == EDIT_GOURMET_REQ && resultCode == RESULT_OK) {
            gourmet = (Gourmet) data.getSerializableExtra("gourmet");
            initViewByGourmet();
        } else if (requestCode == MORE_COMMENT_REQ && resultCode == RESULT_OK) {
//            mComments = (Comments) data.getSerializableExtra("gourmet_comment");
//            if (mCommentAdapter == null) {
//                mCommentAdapter = new GourmetCommentAdapter(FoodDetailActivity.this , mComments) ;
//                mCommentAdapter.setShowAll(false);
//            }
            if (data.getBooleanExtra("comment_modified", false)) {
                fetchCommentList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViewByGourmet() {
        if (gourmet == null) {
            this.finish();
            return;
        }
        if (gourmet.getTags().size() == 0) {
            mLabelContainer.setVisibility(View.GONE);
        } else {
            mLabelContainer.setVisibility(View.VISIBLE);
            mLabelContainer.removeAllViews();
            for (int i = 0; i < gourmet.getTags().size(); i++) {
                TextView textview = new TextView(this);
                textview.setBackgroundResource(R.drawable.bg_label);
                textview.setTextColor(mResource.getColor(R.color.white));
                textview.setTextSize(14);
                textview.setText(gourmet.getTags().get(i));
                mLabelContainer.addView(textview);
            }
        }
        if (gourmet.getImageURLs().size() == 0 || gourmet.getImageURLs().size() == 1 && gourmet.getImageURLs().get(0).equals("http://")) {
            mScrollView.setImageHeight(getResources().getDimension(R.dimen.toolbar_height));
            withImage = false;
        } else {
            mScrollView.setImageHeight(getResources().getDimension(R.dimen.food_detail_image_height));
            mScrollView.setCurrentY((int) getResources().getDimension(R.dimen.scroll_view_bottom));
            withImage = true;
//            ArrayList<View> data = new ArrayList<View>();
            mImageList = new ArrayList<>();
            final ArrayList<String> imageUrls = (ArrayList) gourmet.getImageURLs();
            //5:image number
            for (int i = 0; i < imageUrls.size(); i++) {
                ImageView image = new ImageView(this);
                image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (gourmet.getImageURLs().isEmpty()) {
                            return;
                        }
                        Intent intent = new Intent(GourmetDetailActivity.this, BigImageActivity.class);
                        int currentImageIndex = mImagePager.getCurrentItem();
                        intent.putExtra("current_image_index", currentImageIndex);
                        intent.putStringArrayListExtra("cloud_images", imageUrls);
                        startActivity(intent);
                    }
                });
                Picasso.with(getApplicationContext())
                        .load(imageUrls.get(i))
                        .placeholder(R.drawable.empty_view_greeting)
                        .error(R.drawable.empty_view_greeting)
                        .fit()
                        .centerCrop()
                        .into(image);
                mImageList.add(image);
            }
            initDots(imageUrls.size());
            mViewPagerAdapter = new ImageViewPagerAdapter(mImageList);
            mImagePager.setAdapter(mViewPagerAdapter);
        }
//        mImagePager.setOnPageChangeListener(mPageListener);
        registerTopView();
        if (gourmet.getPrice() == null || gourmet.getPrice().equals("") || gourmet.getPrice().equals("0")) {
            mFoodPrice.setVisibility(View.GONE);
        } else {
            mFoodPrice.setText(String.format(mResource.getString(R.string.format_price), gourmet.getPrice()));
        }
        if (gourmet.getAddress() == null || gourmet.getAddress().equals("")) {
            mFoodLocation.setVisibility(View.GONE);
        } else {
            mFoodLocation.setText(String.format(mResource.getString(R.string.format_location), gourmet.getAddress()));
        }
        if (mFoodLocation.getVisibility() == View.GONE && mFoodPrice.getVisibility() == View.GONE) {
            mGourmetMsgTitle.setVisibility(View.GONE);
            mGourmetMsgSpliteView.setVisibility(View.GONE);
        }
        mFoodDescription.setText(gourmet.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Router.getInstance().getCurrentUser().isPresent() && Router.getInstance().getCurrentUser().get().getIdentifier().equals(gourmet.getUser().getIdentifier())) {
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
//            case R.id.gourmet_detail_refresh:
//                refreshGourmet() ;
//                break;
            case R.id.gourmet_detail_edit:
                editGourmet();
                break;
            case R.id.gourmet_detail_delete:
                deleteGourmet();
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
                return 6;
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
                image.setImageResource(mShareImageRes[position]);
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
     * show half alpha of the background when share dialog is shown ;
     *
     * @param bgAlpha
     */
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

    /**
     * share to different way
     *
     * @param action
     */
    private void distributeShareAction(int action) {
        mController.setShareContent(String.format(getString(R.string.share_content), gourmet.getName())+BuildConfig.Web_View_Url + mShareUrl + gourmet.getIdentifier());
        mController.setShareMedia(new UMImage(mContext,
                BuildConfig.Web_View_Url + mShareUrl + gourmet.getIdentifier()));
        switch (action) {
            case SHARE_TO_WEIBO:
                mController.getConfig().setSsoHandler(new SinaSsoHandler());
                mController.getConfig().setSinaCallbackUrl(WeiboApi.REDIRECT_URL);
                performShare(SHARE_MEDIA.SINA);
                break;
            case SHARE_TO_WECHAT:
                UMWXHandler wxHandler = new UMWXHandler(this, WeixinApi.WEIXIN_ID, WeixinApi.WEIXIN_PWD);
                wxHandler.addToSocialSDK();
                performShare(SHARE_MEDIA.WEIXIN);
                break;
            case SHARE_TO_FRIEND_CIRCLE:
                UMWXHandler wxCircleHandler = new UMWXHandler(this, WeixinApi.WEIXIN_ID, WeixinApi.WEIXIN_PWD);
                wxCircleHandler.setToCircle(true);
                wxCircleHandler.addToSocialSDK();
                performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case SHARE_TO_QQ:
                UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                        QQApi.QQ_ID, QQApi.QQ_PWD);
                qqSsoHandler.addToSocialSDK();
                performShare(SHARE_MEDIA.QQ);
                break;
            case SHARE_TO_QQ_SPACE:
                QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
                        QQApi.QQ_ID, QQApi.QQ_PWD);
                qZoneSsoHandler.addToSocialSDK();
                performShare(SHARE_MEDIA.QZONE);
                break;
            case SHARE_TO_SMS:
                SmsHandler smsHandler = new SmsHandler();
                smsHandler.addToSocialSDK();
                performShare(SHARE_MEDIA.SMS);
                break;
        }
    }


    private void performShare(SHARE_MEDIA platform) {
        mController.postShare(this, platform, new SocializeListeners.SnsPostListener() {

            @Override
            public void onStart() {
                if (platform.equals(SHARE_MEDIA.SMS)){

                }
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
            }
        });
    }

    /**
     * re-fetch the gourmet information ;
     */
    @Deprecated
    private void refreshGourmet() {
        createDialog();
        Router.getGourmetModule().fetchGourmetInformation(gourmet.getIdentifier(), new OneParameterExpression<Gourmet>() {
            @Override
            public void action(Gourmet gourmet) {
                finalizeDialog();
                GourmetDetailActivity.this.gourmet = gourmet;
                initViewByGourmet();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * delete a gourmet
     */
    private void deleteGourmet() {
        View confirmView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.logout_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(GourmetDetailActivity.this).setView(confirmView).create();
        ((TextView) confirmView.findViewById(R.id.logout_content)).setText(String.format(getString(R.string.confirm_delete_gourmet), gourmet.getName()));
        confirmView.findViewById(R.id.logout_btn_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirmView.findViewById(R.id.logout_btn_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createDialog();
                Router.getGourmetModule().removeMyRecommendation(gourmet.getIdentifier(), new LambdaExpression() {
                    @Override
                    public void action() {
                        Toast.makeText(mContext, getString(R.string.success_delete_comment), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction(MessageDef.ACTION_REFRESH_RECOMMENDATION);
                        sendBroadcast(intent);
                        finalizeDialog();
                        finish();
                    }
                }, new LambdaExpression() {
                    @Override
                    public void action() {
                        finalizeDialog();
                        Toast.makeText(mContext, getString(R.string.fail_delete_comment) + "   " + getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     *
     */
    private void editGourmet() {
        Intent intent = new Intent();
        intent.setClass(mContext, EditGourmetActivity.class);
        intent.putExtra("gourmet", gourmet);
        int editType = gourmet.getIsPrivate() ? EditGourmetActivity.TYPE_EDIT_PERSONAL_GOURMET : EditGourmetActivity.TYPE_EDIT_STORE_GOURMET;
        intent.putExtra("edit_gourmet_type", editType);
        startActivityForResult(intent, EDIT_GOURMET_REQ);
    }

    /**
     * get the list of person who would like to eat the gourmet
     */
    private void fetchWouldLikeList() {
        createDialog();
        Router.getGourmetModule().fetchWouldLikeToListByGourmetId(gourmet.getIdentifier(), 500, new OneParameterExpression<WouldLikeToEatUsers>() {
            @Override
            public void action(WouldLikeToEatUsers wouldLikeUser) {
                mWouldLikeToEatList = Optional.of(wouldLikeUser);
                if (wouldLikeUser.getCount() == 0) {
                    mGdView.setVisibility(View.GONE);
                } else {
                    mGdView.setVisibility(View.VISIBLE);
                    if (mGdView.getAdapter() != null) {
                        mGdAdapter.notifyDataSetChanged();
                    } else {
                        mGdView.setAdapter(mGdAdapter);
                    }
                }
                mWouldLikeTitle.setText(String.format(mResource.getString(R.string.format_favor_person), wouldLikeUser.getCount()));
                finalizeDialog();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                mGdView.setVisibility(View.GONE);
                finalizeDialog();
            }
        });
    }


    private void fetchCommentList() {
        createDialog();
        Router.getGourmetModule().fetchComments(gourmet.getIdentifier(), null, null, 11, new OneParameterExpression<Comments>() {
            @Override
            public void action(Comments comments) {
                mComments = comments;
                mCommentTitle.setText(String.format(mResource.getString(R.string.format_comment), mComments.count() + ""));
                if (mComments.count() == 0) {
                    mCommentList.setVisibility(View.GONE);
                } else {
                    mCommentList.setVisibility(View.VISIBLE);
                    mNoneComment.setVisibility(View.GONE);
                    if (mCommentAdapter == null) {
                        mCommentAdapter = new GourmetCommentAdapter(GourmetDetailActivity.this, mComments);
                        mCommentAdapter.setShowAll(false);
                    } else {
                        mCommentAdapter.setData(mComments);
                    }
                    mCommentList.setAdapter(mCommentAdapter);
                    if (mComments.count() > 10) {
                        mMoreCommentBtn.setVisibility(View.VISIBLE);
                    } else {
                        mMoreCommentBtn.setVisibility(View.GONE);
                    }
                    AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mCommentList);
                }
                finalizeDialog();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                mCommentList.setVisibility(View.GONE);
                finalizeDialog();
            }
        });
    }

    /**
     * register the float black view group
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void registerTopView() {
        floatHolder = new TopViewHolder();
        topHolder = new TopViewHolder();
        floatHolder.mCreateTime1 = (TextView) mLayoutTopFloat.findViewById(R.id.food_detail_time);
        floatHolder.mFavorBtn2 = (ImageView) mLayoutTopFloat.findViewById(R.id.favor_btn);
        floatHolder.mFoodName1 = (TextView) mLayoutTopFloat.findViewById(R.id.food_detai_name);
        floatHolder.mPrivateText1 = (TextView) mLayoutTopFloat.findViewById(R.id.food_detai_isprivate);
        floatHolder.mUserHead1 = (ImageView) mLayoutTopFloat.findViewById(R.id.food_detail_head);
        floatHolder.mUserName1 = (TextView) mLayoutTopFloat.findViewById(R.id.food_detail_user_name);
        topHolder.mCreateTime1 = (TextView) mLayoutTop.findViewById(R.id.food_detail_time);
        topHolder.mFavorBtn2 = (ImageView) mLayoutTop.findViewById(R.id.favor_btn);
        topHolder.mFoodName1 = (TextView) mLayoutTop.findViewById(R.id.food_detai_name);
        topHolder.mPrivateText1 = (TextView) mLayoutTop.findViewById(R.id.food_detai_isprivate);
        topHolder.mUserHead1 = (ImageView) mLayoutTop.findViewById(R.id.food_detail_head);
        topHolder.mUserName1 = (TextView) mLayoutTop.findViewById(R.id.food_detail_user_name);
        floatHolder.mFavorBtn2.setOnClickListener(favorListener);
        topHolder.mFavorBtn2.setOnClickListener(favorListener);
        topHolder.mUserHead1.setOnClickListener(userHeadListener);
        floatHolder.mUserHead1.setOnClickListener(userHeadListener);
//        floatHolder.mCreateTime1.setText(gourmet.getDate().toLocaleString());
//        topHolder.mCreateTime1.setText(gourmet.getDate().toLocaleString());
        if (gourmet.getWasMarked()) {
            topHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor_marked));
            topHolder.mFavorBtn2.setImageResource(R.drawable.faviro_clicked);
            floatHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor_marked));
            floatHolder.mFavorBtn2.setImageResource(R.drawable.faviro_clicked);
        }
        floatHolder.mFoodName1.setText(gourmet.getName());
        floatHolder.mPrivateText1.setVisibility(gourmet.getIsPrivate() ? View.VISIBLE : View.GONE);
        if (gourmet.getUser().getAvatarURLSmall().isPresent())
            Picasso.with(getApplicationContext())
                    .load(gourmet.getUser().getAvatarURLSmall().get())
                    .placeholder(R.drawable.default_user_head)
                    .fit()
                    .centerCrop()
                    .into(floatHolder.mUserHead1);
        floatHolder.mUserName1.setText(gourmet.getUser().getNickName());
        topHolder.mCreateTime1.setText(String.format(getString(R.string.format_create_time), StrUtil.formatTime(gourmet.getDate(), getApplicationContext())));
        floatHolder.mCreateTime1.setText(String.format(getString(R.string.format_create_time), StrUtil.formatTime(gourmet.getDate(), getApplicationContext())));
        topHolder.mFoodName1.setText(gourmet.getName());
        topHolder.mPrivateText1.setVisibility(gourmet.getIsPrivate() ? View.VISIBLE : View.GONE);
        if (gourmet.getUser().getAvatarURLSmall().isPresent())
            Picasso.with(getApplicationContext())
                    .load(gourmet.getUser().getAvatarURLSmall().get())
                    .placeholder(R.drawable.default_user_head)
                    .fit()
                    .centerCrop()
                    .into(topHolder.mUserHead1);
        topHolder.mUserName1.setText(gourmet.getUser().getNickName());
    }

    /**
     * listen to the scroll-view scrollY ,and change to top view position;
     *
     * @param scrollY
     */
    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, mFloatView.getTop());
        mTopFloatView.layout(0, mBuyLayout2ParentTop, mTopFloatView.getWidth(),
                mBuyLayout2ParentTop + mTopFloatView.getHeight());
        Log.i("scrollerY", "scrollY == " + scrollY);
    }


    /**
     * judge if the scroll-view's position is on the top ,and change toolbar background ;
     *
     * @param top
     */
    @Override
    public void onTopChange(int top) {
        if (top <= mToolbarHeight) {
            mToolbar.setBackgroundColor(mResource.getColor(R.color.toolbar));
        } else {
            mToolbar.setBackgroundDrawable(mResource.getDrawable(R.drawable.transparent_gradient));
        }
    }

    //init viewpager index indicator
    private void initDots(int count) {
        mImageIndex.removeAllViews();
        if (count <= 1) {
            return;
        }
        int dimen = (int) mResource.getDimension(R.dimen.food_detail_image_index_height);
        LinearLayout.LayoutParams Lp = new LinearLayout.LayoutParams(dimen, dimen);
        Lp.rightMargin = 5;
        Lp.leftMargin = 5;
        Lp.bottomMargin = 20;
        for (int j = 0; j < count; j++) {
            mImageIndex.addView(initDot(), Lp);
        }
        mImageIndex.getChildAt(0).setBackgroundResource(R.drawable.food_detail_index_selected);
    }

    private View initDot() {
        View dot = new View(getApplicationContext());
        dot.setPadding(5, 5, 5, 5);
        dot.setBackgroundResource(R.drawable.food_detail_index_unselected);
        return dot;
    }

    /**
     * do when comment commit btn is pressed
     */
    private OnClickListener mCommentCommitListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //check is logged in ;
            if (!Router.getInstance().getCurrentUser().isPresent()) {
                tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (!Router.getInstance().getCurrentUser().isPresent()) {
                            notifyLogin(null);
                        } else {
                            onClick(v);
                        }
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            //do commit
            String comment = mEtComment.getText().toString().trim();
            if (comment.equals("")) {
                Toast.makeText(getApplicationContext(), mResource.getString(R.string.notify_empty_comment), Toast.LENGTH_SHORT);
                return;
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                        0);
            }
            mEtComment.setHint(mResource.getString(R.string.comment_hint));
            mEtComment.setText("");
            createDialog();
            if (mReplyId != null) {
                Router.getGourmetModule().replyCommentTo(mReplyId, comment, new LambdaExpression() {
                    @Override
                    public void action() {
                        fetchCommentList();
                        Toast.makeText(getApplicationContext(), mResource.getString(R.string.reply_success), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                }, new LambdaExpression() {
                    @Override
                    public void action() {
                        Toast.makeText(getApplicationContext(), mResource.getString(R.string.reply_failed), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                });
            } else {
                Router.getGourmetModule().postComment(gourmet.getIdentifier(), comment, new LambdaExpression() {
                    @Override
                    public void action() {
                        fetchCommentList();
                        Toast.makeText(getApplicationContext(), mResource.getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                }, new LambdaExpression() {
                    @Override
                    public void action() {
                        Toast.makeText(getApplicationContext(), mResource.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                });
            }
        }
    };

    /**
     * listen to the comment list click issue
     */
    private AdapterView.OnItemClickListener mCommentListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (!Router.getInstance().getCurrentUser().isPresent()) {
                tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (!Router.getInstance().getCurrentUser().isPresent())
                            notifyLogin(null);
                        else
                            onItemClick(parent, view, position, id);
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                //if the clicked item is created by myself ,delete it
                if (Router.getInstance().getCurrentUser().get().getIdentifier().equals(mComments.getCommentList().get(position).getUser().getIdentifier())) {
                    View deleteView = LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.alert_delete_comment, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GourmetDetailActivity.this);
                    AlertDialog dialog = builder.setView(deleteView).create();
                    Window window = dialog.getWindow() ;
                    window.setWindowAnimations(R.style.dialogWindowAnim);
                    dialog.show();
                    deleteView.findViewById(R.id.alert_delete_comment).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Router.getGourmetModule().removeComment(mComments.getCommentList().get(position).getIdentifier(), new LambdaExpression() {
                                @Override
                                public void action() {
                                    fetchCommentList();
                                }
                            }, new LambdaExpression() {
                                @Override
                                public void action() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.fail_delete_comment), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    mEtComment.setFocusable(true);
                    mEtComment.setFocusableInTouchMode(true);
                    mEtComment.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) mEtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mEtComment, 0);
                    mEtComment.setHint(String.format(mResource.getString(R.string.reply_to), mComments.getCommentList().get(position).getUser().getNickName()));
                    mReplyId = mComments.getCommentList().get(position).getIdentifier();
                }
            }
        }
    };


    private OnClickListener userHeadListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, UserInfoActivity.class);
            intent.putExtra("tiny_user", gourmet.getUser());
            startActivity(intent);
        }
    };

    /**
     * favor a gourmet or cancel favor
     */
    private OnClickListener favorListener = new OnClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            Log.i("my_scrollview", "start");
            if (!Router.getInstance().getCurrentUser().isPresent()) {
                tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (!Router.getInstance().getCurrentUser().isPresent()) {
                            notifyLogin(null);
                        } else {
                            onClick(v);
                        }
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
//            if (!Router.getInstance().getCurrentUser().isPresent()) {
//                View contentView = mInflater.inflate(R.layout.alert_login_dialog, null);
//                TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
//                final AlertDialog dialog = new AlertDialog.Builder(GourmetDetailActivity.this).setView(contentView).create();
//                tv.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(intent);
//                        dialog.dismiss();
//                        finalizeDialog();
//                    }
//                });
//                dialog.show();
//            }
            else {
                if (!gourmet.getWasMarked()) {
                    createDialog();
                    Router.getGourmetModule().favoriteAdd(gourmet.getIdentifier(), new LambdaExpression() {
                        @Override
                        public void action() {
                            gourmet.setWasMarked(true);
                            fetchWouldLikeList();
                            Toast.makeText(getApplicationContext(), getString(R.string.favor_feedback), Toast.LENGTH_SHORT).show();
                            topHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor_marked));
                            topHolder.mFavorBtn2.setImageResource(R.drawable.faviro_clicked);
                            floatHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor_marked));
                            floatHolder.mFavorBtn2.setImageResource(R.drawable.faviro_clicked);
                            finalizeDialog();
                        }
                    }, new LambdaExpression() {
                        @Override
                        public void action() {
                            Toast.makeText(getApplicationContext(), mResource.getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                            finalizeDialog();
                        }
                    });
                } else {
                    createDialog();
                    Router.getGourmetModule().removeFavorite(gourmet.getIdentifier(), new LambdaExpression() {
                        @Override
                        public void action() {
                            gourmet.setWasMarked(false);
                            fetchWouldLikeList();
                            Toast.makeText(getApplicationContext(), mResource.getString(R.string.remove_favor), Toast.LENGTH_SHORT).show();
                            topHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor));
                            topHolder.mFavorBtn2.setImageResource(R.drawable.faviro_unclick);
                            floatHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor));
                            floatHolder.mFavorBtn2.setImageResource(R.drawable.faviro_unclick);
                            finalizeDialog();
                        }
                    }, new LambdaExpression() {
                        @Override
                        public void action() {
                            Toast.makeText(getApplicationContext(), mResource.getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                            finalizeDialog();
                        }
                    });
                }

            }
        }
    };
    private Resources mResource = null;

    /**
     * would like to eat list listener ;
     */
    private AdapterView.OnItemClickListener mGvListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WouldLikeToEatUser boundUser = (WouldLikeToEatUser) mGdAdapter.getItem(position);
            if (boundUser == null) {
                Intent intent = new Intent(getApplicationContext(), FavorPersonListActivity.class);
                intent.putExtra("would_like_list", mWouldLikeToEatList.get());
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext, UserInfoActivity.class);
                intent.putExtra("tiny_user", boundUser);
                startActivity(intent);
            }
        }
    };
    /**
     * would like to list adapter
     */
    private BaseAdapter mGdAdapter = new BaseAdapter() {
        private WouldLikeToEatUser users = null;
        private int totalCount = 0;


        @Override
        public int getCount() {
            totalCount = mWouldLikeToEatList.get().getCount();
            return totalCount > 5 ? 5 : totalCount;
        }

        @Override
        public Object getItem(int position) {
            if (mWouldLikeToEatList.get().getCount() <= 5 || (mWouldLikeToEatList.get().getCount() > 5 && position <= 3)) {
                return mWouldLikeToEatList.get().getUsers().get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = null;
            WouldLikeToEatUser user = mWouldLikeToEatList.get().getUsers().get(position);
            if (mWouldLikeToEatList.get().getCount() <= 5 || (mWouldLikeToEatList.get().getCount() > 5 && position <= 3)) {
                contentView = mInflater.inflate(R.layout.food_detail_favor_item, null);
                if (user.getAvatarURLSmall().isPresent()) {
                    Picasso.with(parent.getContext())
                            .load(user.getAvatarURLSmall().get())
                            .placeholder(R.drawable.default_user_head)
                            .fit()
                            .centerCrop()
                            .into((ImageView) contentView.findViewById(R.id.would_like_eat_head));
                } else {
                    ImageView avatarView = (ImageView) contentView.findViewById(R.id.would_like_eat_head);
                    avatarView.setImageResource(R.drawable.default_user_head);
                }
            } else if (position == 4) {
                contentView = mInflater.inflate(R.layout.food_detail_favor_item2, null);
                TextView tv = (TextView) contentView.findViewById(R.id.favor_people_count);
                tv.setText(totalCount + "");
            }
            return contentView;
        }
    };

    /**
     * gourmet images' viewpager change listener ;
     */
    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mImageIndex.getChildCount(); i++) {
                if (i == arg0) {
                    mImageIndex.getChildAt(i).setBackgroundResource(
                            R.drawable.food_detail_index_selected);
                } else {
                    mImageIndex.getChildAt(i).setBackgroundResource(
                            R.drawable.food_detail_index_unselected);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private class CommentViewHolder {
        public ImageView head;
        public TextView userName;
        public TextView commentContent;
        public TextView time;
    }


    private class TopViewHolder {
        public ImageView mFavorBtn2 = null;
        public TextView mFoodName1 = null;
        public TextView mUserName1 = null;
        public TextView mPrivateText1 = null;
        public TextView mCreateTime1 = null;
        public ImageView mUserHead1 = null;
    }
}
