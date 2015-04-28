package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Comment;
import com.zuijiao.android.zuijiao.model.Comments;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.WouldLikeToEatUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.FileManager;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.MeasuredTextView;
import com.zuijiao.view.MyScrollView;
import com.zuijiao.view.MyScrollView.OnScrollListener;
import com.zuijiao.view.WordWrapView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ShowToast")
@ContentView(R.layout.activity_food_detail)
public class FoodDetailActivity extends BaseActivity implements
        OnScrollListener {
    @ViewInject(R.id.food_detail_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.food_detail_container)
    private MyScrollView mScrollView = null;
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
    ;
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
    private boolean openEdit = false;
    private Gourmet gourmet = null;

    //private WouldLikeToEatUsers mUsers = null;
    private Comments mComments = null;
    //if comment false ,reply true ;
    private Integer mReplyId = null;

    private OnClickListener mCommentCommitListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!Router.getInstance().getCurrentUser().isPresent()) {
                View contentView = mInflater.inflate(R.layout.alert_login_dialog, null);
                TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
                final AlertDialog dialog = new AlertDialog.Builder(FoodDetailActivity.this).setView(contentView).create();
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finallizeDialog();
                    }
                });
                dialog.show();
                return;
            }
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
                Router.getGourmetModule().replyCommentTo(mReplyId, comment, () -> {
                    fetchCommentList();
                    Toast.makeText(getApplicationContext(), mResource.getString(R.string.reply_success), Toast.LENGTH_SHORT).show();
                    finallizeDialog();
                }, () -> {
                    Toast.makeText(getApplicationContext(), mResource.getString(R.string.reply_failed), Toast.LENGTH_SHORT).show();
                    finallizeDialog();
                });
            } else {
                Router.getGourmetModule().postComment(gourmet.getIdentifier(), comment, () -> {
                    fetchCommentList();
                    Toast.makeText(getApplicationContext(), mResource.getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
                    finallizeDialog();
                }, () -> {
                    Toast.makeText(getApplicationContext(), mResource.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                    finallizeDialog();
                });
            }
        }
    };
    private AdapterView.OnItemClickListener mCommentListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!Router.getInstance().getCurrentUser().isPresent()) {
                View contentView = mInflater.inflate(R.layout.alert_login_dialog, null);
                TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
                final AlertDialog dialog = new AlertDialog.Builder(FoodDetailActivity.this).setView(contentView).create();
                tv.setOnClickListener((View v) -> {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    finallizeDialog();
                });
                dialog.show();
            } else {
                if (Router.getInstance().getCurrentUser().get().getIdentifier().equals(mComments.getCommentList().get(position).getUser().getIdentifier())) {
                    View deleteView = LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.alert_delete_comment, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            FoodDetailActivity.this);
                    AlertDialog dialog = builder.setView(deleteView).create();
                    dialog.show();
                    deleteView.findViewById(R.id.alert_delete_comment).setOnClickListener((View v) -> {
                        dialog.dismiss();
                        Router.getGourmetModule().removeComment(mComments.getCommentList().get(position).getIdentifier(), () ->
                        {
                            fetchCommentList();
                        }, () -> {
                            Toast.makeText(getApplicationContext(), getString(R.string.fail_delete_comment), Toast.LENGTH_SHORT).show();
                        });
                    });
                } else {
                    mEtComment.setFocusable(true);
                    mEtComment.setFocusableInTouchMode(true);
                    mEtComment.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) mEtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mEtComment, 0);
                    mEtComment.setHint(String.format(mResource.getString(R.string.reply_to), mComments.getCommentList().get(position).getUser().getNickName()));
                    openEdit = true;
                    mReplyId = mComments.getCommentList().get(position).getIdentifier();
                }
            }
        }
    };
    private OnClickListener favorListener = new OnClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            Log.i("my_scrollview", "start");
            if (!Router.getInstance().getCurrentUser().isPresent()) {
                View contentView = mInflater.inflate(R.layout.alert_login_dialog, null);
                TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
                final AlertDialog dialog = new AlertDialog.Builder(FoodDetailActivity.this).setView(contentView).create();
                tv.setOnClickListener((View view) -> {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    finallizeDialog();
                });
                dialog.show();
            } else {
                if (!gourmet.getWasMarked()) {
                    createDialog();
                    Router.getGourmetModule().favoriteAdd(gourmet.getIdentifier(), () -> {
                        gourmet.setWasMarked(true);
                        fetchWouldLikeList();
//                        View view = mInflater.inflate(R.layout.favor_feedback, null);
//                        Toast toast = new Toast(getApplicationContext());
//                        toast.setDuration(Toast.LENGTH_SHORT);
//                        toast.setView(view);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
                        Toast.makeText(getApplicationContext(), getString(R.string.favor_feedback), Toast.LENGTH_SHORT).show();
                        topHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor_marked));
                        topHolder.mFavorBtn2.setImageResource(R.drawable.faviro_clicked);
                        floatHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor_marked));
                        floatHolder.mFavorBtn2.setImageResource(R.drawable.faviro_clicked);
                        finallizeDialog();
                    }, () -> {
                        Toast.makeText(getApplicationContext(), mResource.getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        finallizeDialog();
                    });
                } else {
                    createDialog();
                    Router.getGourmetModule().removeFavorite(gourmet.getIdentifier(), () -> {
                        gourmet.setWasMarked(false);
                        fetchWouldLikeList();
                        Toast.makeText(getApplicationContext(), mResource.getString(R.string.remove_favor), Toast.LENGTH_SHORT).show();
                        topHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor));
                        topHolder.mFavorBtn2.setImageResource(R.drawable.faviro_unclick);
                        floatHolder.mFavorBtn2.setBackground(mResource.getDrawable(R.drawable.bg_favor));
                        floatHolder.mFavorBtn2.setImageResource(R.drawable.faviro_unclick);
                        finallizeDialog();
                    }, () -> {
                        Toast.makeText(getApplicationContext(), mResource.getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        finallizeDialog();
                    });
                }

            }
        }
    };
    private Resources mResource = null;
    private BaseAdapter mCommentAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommentViewHolder holder;
            Comment comment = mComments.getCommentList().get(position);
            if (convertView == null) {
                holder = new CommentViewHolder();
                convertView = mInflater.inflate(R.layout.comment_item, null);
                //holder.commentContent = (TextView) convertView.findViewById(R.id.comment_content);
                TextView commentContent = new MeasuredTextView(FoodDetailActivity.this);
                //commentContent.setText("这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容这是我回复的内容");
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, R.id.comment_user_name);
                commentContent.setLayoutParams(lp);
//                commentContent.setTextSize(getResources().getDimension(R.dimen.comment_content_text_size)) ;
//                commentContent.setTextColor(getResources().getColor(R.color.comment_content_color )) ;
                ((RelativeLayout) convertView.findViewById(R.id.comment_text_container)).addView(commentContent);
                holder.commentContent = commentContent;
                holder.head = (ImageView) convertView.findViewById(R.id.comment_user_head);
                holder.time = (TextView) convertView.findViewById(R.id.comment_time);
                holder.userName = (TextView) convertView.findViewById(R.id.comment_user_name);
                convertView.setTag(holder);
            } else {
                holder = (CommentViewHolder) convertView.getTag();
            }
            holder.time.setText(StrUtil.formatTime(comment.getPostDate(), getApplicationContext()));
            holder.userName.setText(comment.getUser().getNickName());
            if (comment.getUser().getAvatarURL().isPresent())
                Picasso.with(getApplicationContext())
                        .load(comment.getUser().getAvatarURL().get())
                        .placeholder(R.drawable.default_user_head)
//                    .error(R.drawable.empty_view_greeting)
                        .into(holder.head);
            if (comment.getReplyTo().isPresent()) {
                String replyToUserName = comment.getReplyTo().get().getNickName();
                holder.commentContent.setText(String.format(mResource.getString(R.string.reply_content), replyToUserName + " " + comment.getDetail()));
                initReplyTextView(holder.commentContent, replyToUserName.length());
            } else {
                holder.commentContent.setText(comment.getDetail());
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return mComments.getCommentList().size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    };
    private AdapterView.OnItemClickListener mGvListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), FavorPersonListActivity.class);
            startActivity(intent);
        }
    };
    private BaseAdapter mGdAdapter = new BaseAdapter() {
        private WouldLikeToEatUser users = null;
        private int totalCount = 0;

//        public void setData(WouldLikeToEatUser users) {
//            this.users = users;
//            notifyDataSetChanged();
//        }

        @Override
        public int getCount() {
            totalCount = FileManager.tmpWouldLikeList.get().getCount();
            return totalCount > 5 ? 5 : totalCount;
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
            View contentView = null;
            WouldLikeToEatUser user = FileManager.tmpWouldLikeList.get().getUsers().get(position);
            if (position <= 3) {
                contentView = mInflater.inflate(R.layout.food_detail_favor_item, null);
                if (user.getAvatarURL().isPresent()) {
                    Picasso.with(parent.getContext())
                            .load(user.getAvatarURL().get())
                            .placeholder(R.drawable.default_user_head)
                            .into((ImageView) contentView.findViewById(R.id.would_like_eat_head));
                } else {
                    ImageView avatarView = (ImageView) contentView.findViewById(R.id.would_like_eat_head);
                    avatarView.setImageResource(R.drawable.default_user_head);
                }
            }
            if (position == 4) {
                contentView = mInflater.inflate(R.layout.food_detail_favor_item2, null);
                TextView tv = (TextView) contentView.findViewById(R.id.favor_people_count);
                tv.setText(totalCount + "");
            }
            return contentView;
        }
    };
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

    //    @ViewInject(R.id.test_focusable)
//    private LinearLayout mTestLayout = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void findViews() {

    }

    @SuppressLint("NewApi")
    @Override
    protected void registerViews() {
        try {
            int index = mTendIntent.getIntExtra("click_item_index", -1);
            boolean fromFavor = mTendIntent.getBooleanExtra("b_favor", false);
            if (index == -1 && fromFavor == false) {
                gourmet = FileManager.tmpMessageGourmet;
            } else {
                gourmet = mFileMng.getItem(fromFavor, index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gourmet == null) {
            this.finish();
            return;
        }
//        mTestLayout.requestFocus() ;
//        mTestLayout.requestFocusFromTouch() ;
        mResource = getResources();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInflater = LayoutInflater.from(this);
        for (int i = 0; i < gourmet.getTags().size(); i++) {
            TextView textview = new TextView(this);
            textview.setBackgroundResource(R.drawable.bg_label);
            textview.setTextColor(mResource.getColor(R.color.white));
            textview.setTextSize(14);
            textview.setText(gourmet.getTags().get(i));
            mLabelContainer.addView(textview);
        }
        mScrollView.setOnScrollListener(this);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    if (openEdit) {
                        mScrollView.moveToTop();
                        openEdit = false;
                    }
                    onScroll(mScrollView.getScrollY());
                    onTopChange(mScrollView.getTop());
                });
        ArrayList<View> data = new ArrayList<View>();
        final ArrayList<String> imageUrls = (ArrayList) gourmet.getImageURLs();
        //5:image number
        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setOnClickListener((View view) -> {
                if (gourmet.getImageURLs().isEmpty()) {
                    return;
                }
                Intent intent = new Intent(FoodDetailActivity.this, BigImageActivity.class);
                int currentImageIndex = mImagePager.getCurrentItem();
                intent.putExtra("current_image_index", currentImageIndex);
                intent.putStringArrayListExtra("image_url", imageUrls);
                startActivity(intent);
            });
            Picasso.with(getApplicationContext())
                    .load(imageUrls.get(i))
                    .placeholder(R.drawable.empty_view_greeting)
                    .error(R.drawable.empty_view_greeting)
                    .into(image);
            data.add(image);
        }
        initDots(imageUrls.size());
        mImagePager.setAdapter(new ViewPagerAdapter(data));
        mImagePager.setOnPageChangeListener(mPageListener);

        registerTopView();
        mEtComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("my_scrollview", "start");
                openEdit = true;
                mReplyId = null;
                mEtComment.setHint(getString(R.string.comment_hint));
            }
        });
        if (gourmet.getPrice() == null || gourmet.getPrice().equals("")) {
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
        mGdView.setOnItemClickListener(mGvListener);
        mCommentList.setOnItemClickListener(mCommentListListener);
        mCommentCommit.setOnClickListener(mCommentCommitListener);
        fetchWouldLikeList();
        fetchCommentList();
    }

    private void fetchWouldLikeList() {
        createDialog();
        Router.getGourmetModule().fetchWouldLikeToListByGourmetId(gourmet.getIdentifier(), 500, wouldLikeUser -> {
            FileManager.tmpWouldLikeList = Optional.of(wouldLikeUser);
            if (wouldLikeUser.getCount() == 0) {
                mGdView.setVisibility(View.GONE);
            } else {
                mGdView.setVisibility(View.VISIBLE);
                if (mGdView.getAdapter() != null) {
                    mScrollView.keepState = true;
                    mScrollView.tmpScrollY = (int) mScrollView.getY();
                    mGdAdapter.notifyDataSetChanged();
                } else {
                    mGdView.setAdapter(mGdAdapter);
                }
            }
            mWouldLikeTitle.setText(String.format(mResource.getString(R.string.format_favor_person), wouldLikeUser.getCount()));
            finallizeDialog();
        }, errorMsg -> {
            mGdView.setVisibility(View.GONE);
            finallizeDialog();
        });
    }


    private void fetchCommentList() {
        createDialog();
        Router.getGourmetModule().fetchComments(gourmet.getIdentifier(), null, null, 500, comments -> {
            mComments = comments;
            mCommentTitle.setText(String.format(mResource.getString(R.string.format_comment), mComments.count() + ""));
            mScrollView.keepState = true;
            mScrollView.tmpScrollY = (int) mScrollView.getY();
            if (mComments.count() == 0) {
                mCommentList.setVisibility(View.GONE);
            } else {
                mCommentList.setVisibility(View.VISIBLE);
                mNoneComment.setVisibility(View.GONE);
                if (mCommentList.getAdapter() != null) {
                    mCommentAdapter.notifyDataSetChanged();
                } else {
                    mCommentList.setAdapter(mCommentAdapter);
                }
                setListViewHeightBasedOnChildren(mCommentList);
            }
            finallizeDialog();
        }, errorMsg -> {
            mCommentList.setVisibility(View.GONE);
            finallizeDialog();
        });
    }


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
        Picasso.with(getApplicationContext())
                .load(gourmet.getUser().getAvatarURL().get())
                .placeholder(R.drawable.default_user_head)
                .into(floatHolder.mUserHead1);
        floatHolder.mUserName1.setText(gourmet.getUser().getNickName());
        topHolder.mCreateTime1.setText(String.format(getString(R.string.format_create_time), StrUtil.formatTime(gourmet.getDate(), getApplicationContext())));
        floatHolder.mCreateTime1.setText(String.format(getString(R.string.format_create_time), StrUtil.formatTime(gourmet.getDate(), getApplicationContext())));
        topHolder.mFoodName1.setText(gourmet.getName());
        topHolder.mPrivateText1.setVisibility(gourmet.getIsPrivate() ? View.VISIBLE : View.GONE);
        Picasso.with(getApplicationContext())
                .load(gourmet.getUser().getAvatarURL().get())
                .placeholder(R.drawable.default_user_head)
                .into(topHolder.mUserHead1);
        topHolder.mUserName1.setText(gourmet.getUser().getNickName());
    }

    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, mFloatView.getTop());
        mTopFloatView.layout(0, mBuyLayout2ParentTop, mTopFloatView.getWidth(),
                mBuyLayout2ParentTop + mTopFloatView.getHeight());
        Log.i("scrollerY", "scrollY == " + scrollY);
    }


    private void initReplyTextView(TextView tv, int userNameLength) {
        String str = tv.getText().toString();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.GRAY), 2,
                2 + userNameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv.setText(style);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
//        mScrollView.keepState = true ;
//        mScrollView.tmpScrollY = (int) mScrollView.getY() ;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()));

    }

    @Override
    public void onTopChange(int top) {
        if (top <= mToolbar.getHeight()) {
            mToolbar.setBackgroundColor(mResource.getColor(R.color.toolbar));
        } else {
            mToolbar.setBackgroundDrawable(mResource.getDrawable(R.drawable.transparent_gradient));
        }
    }

    private void initDots(int count) {
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
//        mImageIndex.getChildAt(0).setSelected(true);
        mImageIndex.getChildAt(0).setBackgroundResource(R.drawable.food_detail_index_selected);
//        mImageIndex.getChildAt(1).setBackgroundResource(R.drawable.wizard_index_unselected);
//        mImageIndex.getChildAt(2).setBackgroundResource(R.drawable.wizard_index_unselected);
    }

    private View initDot() {
        View dot = new View(getApplicationContext());
        dot.setPadding(5, 5, 5, 5);
        dot.setBackgroundResource(R.drawable.food_detail_index_unselected);
        return dot;
    }

    private class CommentViewHolder {
        public ImageView head;
        public TextView userName;
        public TextView commentContent;
        public TextView time;
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

    private class TopViewHolder {
        public ImageView mFavorBtn2 = null;
        public TextView mFoodName1 = null;
        public TextView mUserName1 = null;
        public TextView mPrivateText1 = null;
        public TextView mCreateTime1 = null;
        public ImageView mUserHead1 = null;
    }
}
