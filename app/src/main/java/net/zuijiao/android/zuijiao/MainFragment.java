package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;
import com.zuijiao.view.WordWrapView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.OnLongClickListener;

public class MainFragment extends Fragment implements FragmentDataListener,
        MyListViewListener {
    public static final int UNDEFINE_PAGE = 0;
    public static final int MAIN_PAGE = 1;
    public static final int FAVOR_PAGE = 2;
    public static final int RECOMMEND_PAGE = 3;
    private int mContentType = UNDEFINE_PAGE;
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
            intent.putExtra("selected_gourmet", mAdapter.getItem(position - 1));
//            intent.putExtra("click_item_index", position - 1);
//            intent.putExtra("b_favor", mContentType == FAVOR_PAGE);
            startActivity(intent);
        }
    };
    private View mContentView = null;
    private RefreshAndInitListView mListView = null;
    private FloatingActionsMenu mAddMenu = null;
    private FloatingActionButton mFloatButtonA = null;
    private FloatingActionButton mFloatButtonB = null;
    private LayoutInflater mInflater = null;
    private MainAdapter mAdapter = null;
    private Context mContext = null;
    private LinearLayout mLayout = null;
    private TextView mFavorCount = null;
    private boolean bLogin = false;
    private Activity mActivity = null;
    private boolean bFirstInit = true;
    private TinyUser mDisplayUser = null;
    private Date tmpDate = null;
    private boolean bSelf = false;
    private View.OnClickListener mFloatBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    public MainFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public MainFragment(int Type, Context context) {
        super();
        this.mContentType = Type;
        this.mContext = context;
        if (Router.getInstance().getCurrentUser().isPresent()) {
            mDisplayUser = Router.getInstance().getCurrentUser().get();
        }
    }

    public void setType(int type) {
        this.mContentType = type;
    }

    public void setUser(TinyUser user) {
        mDisplayUser = user;
        bSelf = displaySelfInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mContentType == UNDEFINE_PAGE) {
            try {
                mContentType = ((RecommendAndFavorActivity) mActivity).mContentType;
                mDisplayUser = ((RecommendAndFavorActivity) mActivity).mCurrentUser;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        bSelf = displaySelfInfo();
        if (mAdapter == null) {
            mAdapter = new MainAdapter();
            mListView.setAdapter(mAdapter);
        }
        if (mAdapter.getCount() < 20) {
            mListView.setPullLoadEnable(false);
        } else {
            mListView.setPullLoadEnable(true);
        }
        if (mContentType == FAVOR_PAGE || mContentType == RECOMMEND_PAGE && mAdapter.getCount() != 0) {
            mFavorCount.setVisibility(View.VISIBLE);
            mFavorCount.setText(String.format(getString(R.string.favor_count), mAdapter.getCount()));
        } else {
            mFavorCount.setVisibility(View.GONE);
        }
        if (bFirstInit)
            firstInit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mInflater = inflater;
        mActivity = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_main, null);
        mLayout = (LinearLayout) mContentView.findViewById(R.id.main_empty_content);
        mListView = (RefreshAndInitListView) mContentView
                .findViewById(R.id.content_items);
        mFavorCount = (TextView) mContentView.findViewById(R.id.tv_show_favor_count);
//        mFloatButton.attachToListView(mListView);
        if (mContentType == MAIN_PAGE) {
            mAddMenu = (FloatingActionsMenu) mContentView.findViewById(R.id.multiple_actions);
            mAddMenu.setVisibility(View.VISIBLE);
            mFloatButtonA = (FloatingActionButton) mContentView.findViewById(R.id.action_a);
            mFloatButtonB = (FloatingActionButton) mContentView.findViewById(R.id.action_b);
            mFloatButtonA.setOnClickListener(new FloatButtonListener());
            mFloatButtonB.setOnClickListener(new FloatButtonListener());
            mFloatButtonA.setOnLongClickListener(new FloatButtonIndicator(getString(R.string.personal_gourmet)));
            mFloatButtonB.setOnLongClickListener(new FloatButtonIndicator(getString(R.string.store_gourmet)));
        }
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setListViewListener(this);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public ArrayList<Object> initCache(int type) {
        return null;
    }

    @Override
    public ArrayList<Object> getContentFromNetWork(String Url) {
        return null;
    }

    @Override
    public void NotifyData() {

    }

    public void firstInit() {
        mListView.autoResetHeadView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 450);
    }


    @Override
    public void onRefresh() {
        if (mContentType == MAIN_PAGE) {
            fetchCommonData(true);
        } else if (mContentType == FAVOR_PAGE) {
            fetchFavorData(true);
        } else if (mContentType == RECOMMEND_PAGE) {
            fetchRecommendData(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (mContentType == MAIN_PAGE) {
            fetchCommonData(false);
        } else if (mContentType == FAVOR_PAGE) {
            fetchFavorData(false);
        } else if (mContentType == RECOMMEND_PAGE) {
            fetchRecommendData(false);
        }
    }

    public void onTouchDown() {
        if (mAddMenu != null && mAddMenu.isExpanded()) {
            mAddMenu.collapse();
        }
    }

    public void clearFavorData() {
        try {
            mAdapter.gourmets.get().clear();
            mAdapter.notifyDataSetChanged();
            mListView.setPullLoadEnable(false);
            mFavorCount.setVisibility(View.GONE);
        } catch (Throwable t) {
            System.err.println("no favor");
        }
    }

    private void fetchRecommendData(boolean isRefresh) {
        if (mDisplayUser == null) {
            if (Router.getInstance().getCurrentUser().isPresent()) {
                mDisplayUser = Router.getInstance().getCurrentUser().get();
            }
            if (mDisplayUser == null) {
                ((BaseActivity) getActivity()).tryLoginFirst(() -> {
                    if (!Router.getInstance().getCurrentUser().isPresent()) {
                        if (mAdapter.gourmets.isPresent())
                            mAdapter.gourmets.get().clear();
                        mAdapter.notifyDataSetChanged();
                        mListView.setPullLoadEnable(false);
                        mFavorCount.setVisibility(View.GONE);
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                        ((BaseActivity) getActivity()).notifyLogin(() -> {
                            if (Router.getInstance().getCurrentUser().isPresent()) {
                                fetchRecommendData(isRefresh);
                            } else {
                                //do nothing
                            }
                        });
                    } else {
                        fetchRecommendData(isRefresh);
                    }
                }, error -> {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                });
            } else {
                fetchRecommendData(isRefresh);
            }
            return;
        }
        List<Gourmet> tmpGourmets = mAdapter.gourmets.orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;
        if (!isRefresh) {
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        if (bSelf) {
            mListView.setRefreshTime(new Date(PreferenceManager.getInstance(mContext).getFavorLastRefreshTime()).toLocaleString());
        } else if (tmpDate != null) {
            mListView.setRefreshTime(tmpDate.toLocaleString());
        }
        Router.getGourmetModule().fetchRecommendationListByUserId(mDisplayUser.getIdentifier(), theLastOneIdentifier, null, 20
                , gourmets ->
        {
            if (isRefresh) {
                tmpGourmets.clear();
                if (gourmets.getTotalCount() == 0) {
                    mFavorCount.setVisibility(View.GONE);
                    mLayout.setVisibility(View.VISIBLE);
                    ((TextView) mContentView.findViewById(R.id.fragment_main_no_content_text_view)).setText(getString(R.string.no_recommendation));
                    mListView.setPullLoadEnable(false);
                } else {
                    mFavorCount.setVisibility(View.VISIBLE);
                    mFavorCount.setText(String.format(getString(R.string.recommend_count_title), gourmets.getTotalCount()));
                    if (gourmets.getGourmets().size() < 20) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                    mLayout.setVisibility(View.GONE);
                }
            } else {
                if (gourmets.getGourmets().size() == 0) {
                    mListView.setPullLoadEnable(false);
                    Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                } else {
                    mFavorCount.setText(String.format(getString(R.string.recommend_count_title), tmpGourmets.size() + gourmets.getTotalCount()));
                }
            }
            tmpGourmets.addAll(gourmets.getGourmets());
            mAdapter.gourmets = Optional.of(tmpGourmets);
            mAdapter.notifyDataSetChanged();
//            DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
            if (bSelf) {
                FileManager.setGourmets(mContentType, Optional.of(tmpGourmets));
                PreferenceManager.getInstance(mContext).saveFavorLastRefreshTime(new Date().getTime());
            } else {
                tmpDate = new Date();
            }
            mListView.stopRefresh();
            mListView.stopLoadMore();
            bFirstInit = false;
        }
                , errorMessage ->
        {
            if (errorMessage.contains("401")) {
                ((BaseActivity) getActivity()).tryLoginFirst(() -> {
                    fetchCommonData(isRefresh);
                }, error -> {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                });
            } else {
                Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                mListView.stopRefresh();
                mListView.stopLoadMore();
            }
        });
    }

    private void fetchFavorData(Boolean isRefresh) {
        if (mDisplayUser == null) {
            if (Router.getInstance().getCurrentUser().isPresent()) {
                mDisplayUser = Router.getInstance().getCurrentUser().get();
            }
            if (mDisplayUser == null) {
                ((BaseActivity) getActivity()).tryLoginFirst(() -> {
                    if (!Router.getInstance().getCurrentUser().isPresent()) {
                        if (mAdapter.gourmets.isPresent())
                            mAdapter.gourmets.get().clear();
                        mAdapter.notifyDataSetChanged();
                        mListView.setPullLoadEnable(false);
                        mFavorCount.setVisibility(View.GONE);
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                        ((BaseActivity) getActivity()).notifyLogin(() -> {
                            if (Router.getInstance().getCurrentUser().isPresent()) {
                                fetchFavorData(isRefresh);
                            } else {
                                //do nothing
                            }
                        });
                    } else {
                        fetchFavorData(isRefresh);
                    }
                }, error -> {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                });
            } else {
                fetchFavorData(isRefresh);
            }

            return;
        }
        // TinyUser user = Router.getInstance().getCurrentUser().get();
        List<Gourmet> tmpGourmets = mAdapter.gourmets.orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;
        if (!isRefresh) {
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        mListView.setRefreshTime(new Date(PreferenceManager.getInstance(mContext).getFavorLastRefreshTime()).toLocaleString());
        Router.getGourmetModule().fetchFavoritesByUserId(mDisplayUser.getIdentifier(), theLastOneIdentifier, null, 20
                , gourmets ->
        {
            if (isRefresh) {
                tmpGourmets.clear();
                if (gourmets.getTotalCount() == 0) {
                    mFavorCount.setVisibility(View.GONE);
                    mLayout.setVisibility(View.VISIBLE);
                    mListView.setPullLoadEnable(false);
                } else {
                    mFavorCount.setVisibility(View.VISIBLE);
                    int count = gourmets.getTotalCount();
                    mFavorCount.setText(String.format(getString(R.string.favor_count), gourmets.getTotalCount()));
                    if (gourmets.getGourmets().size() < 20) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                    mLayout.setVisibility(View.GONE);
                }
            } else {
                if (gourmets.getGourmets().size() == 0) {
                    mListView.setPullLoadEnable(false);
                    Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                } else {
                    mFavorCount.setText(String.format(getString(R.string.favor_count), gourmets.getTotalCount()));
                }
            }
            tmpGourmets.addAll(gourmets.getGourmets());

            mAdapter.gourmets = Optional.of(tmpGourmets);
            mAdapter.notifyDataSetChanged();
//            DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
            if (bSelf) {
                FileManager.setGourmets(mContentType, Optional.of(tmpGourmets));
                PreferenceManager.getInstance(mContext).saveFavorLastRefreshTime(new Date().getTime());
            } else {
                tmpDate = new Date();
            }
            mListView.stopRefresh();
            mListView.stopLoadMore();
            bFirstInit = false;
        }
                , errorMessage ->
        {
            if (errorMessage.contains("401")) {
                ((BaseActivity) getActivity()).tryLoginFirst(() -> {
                    fetchCommonData(isRefresh);
                }, error -> {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                });
            } else {
                Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                mListView.stopRefresh();
                mListView.stopLoadMore();
            }
        });
    }

    private void fetchCommonData(Boolean isRefresh) {
        List<Gourmet> tmpGourmets = mAdapter.gourmets.orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;
        if (!isRefresh) {
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        mListView.setRefreshTime(new Date(PreferenceManager.getInstance(getActivity()).getMainLastRefreshTime()).toLocaleString());
        Router.getGourmetModule().fetchOurChoice(theLastOneIdentifier
                , null
                , 20
                , gourmets ->
        {
            if (isRefresh) {
                tmpGourmets.clear();
                mListView.setPullLoadEnable(true);
            } else {
                if (gourmets.getGourmets().size() == 0) {
                    mListView.setPullLoadEnable(false);
                    Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                }
            }
            tmpGourmets.addAll(gourmets.getGourmets());
            mAdapter.gourmets = Optional.of(tmpGourmets);
            FileManager.setGourmets(mContentType, Optional.of(tmpGourmets));
            mAdapter.notifyDataSetChanged();
            DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
            PreferenceManager.getInstance(mContext).saveMainLastRefreshTime(new Date().getTime());
            mListView.stopRefresh();
            mListView.stopLoadMore();
            bFirstInit = false;
        }
                , errorMessage ->
        {
            if (errorMessage.contains("401")) {
                ((BaseActivity) getActivity()).tryLoginFirst(() -> {
                    fetchCommonData(isRefresh);
                }, error -> {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                });
            } else {
                Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                mListView.stopRefresh();
                mListView.stopLoadMore();
            }
        });
    }

    protected interface MainFragmentDataListener {
        public void onDataChanged();
    }

    private class FloatButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mAddMenu.collapse();
            Intent intent = new Intent();
            intent.setClass(mContext, EditGourmetActivity.class);
            switch (v.getId()) {
                case R.id.action_a:
                    intent.putExtra("edit_gourmet_type", EditGourmetActivity.TYPE_CREATE_PERSONAL_GOURMET);
                    break;
                case R.id.action_b:
                    intent.putExtra("edit_gourmet_type", EditGourmetActivity.TYPE_CREATE_STORE_GOURMET);
                    break;
                default:
                    break;
            }
            startActivity(intent);
        }
    }

    private class FloatButtonIndicator implements OnLongClickListener {
        private String indicator = null;

        public FloatButtonIndicator(String indicator) {
            this.indicator = indicator;
        }

        @Override
        public boolean onLongClick(View v) {
            DisplayMetrics metric = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;
            int height = metric.heightPixels;
            int[] location = {0, 0};
            v.getLocationInWindow(location);
            int h = v.getHeight();
            Toast toast = Toast.makeText(mContext, indicator, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.RIGHT | Gravity.BOTTOM, width - location[0], height - location[1] - v.getHeight() * 2 / 3);
            toast.show();
            return true;
        }
    }

    private boolean displaySelfInfo() {
        if (mDisplayUser == null) {
            return false;
        }
        return mDisplayUser.getIdentifier().equals(PreferenceManager.getInstance(mContext).getStoredUserId());
    }

    private class MainAdapter extends BaseAdapter {
        Optional<List<Gourmet>> gourmets;

        public MainAdapter() {
            super();
            if (mContentType == MAIN_PAGE) {
                gourmets = FileManager.mainGourmet;
            } else if (mContentType == FAVOR_PAGE && bSelf) {
                gourmets = FileManager.favorGourmets;
            } else if (mContentType == RECOMMEND_PAGE && bSelf) {
                gourmets = FileManager.recommendList;
            } else {
                gourmets = Optional.empty();
            }
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.main_content_item, null);
                holder = new ViewHolder();
                holder.image_food = (ImageView) convertView
                        .findViewById(R.id.content_item_image);
                holder.image_user_head = (ImageView) convertView
                        .findViewById(R.id.content_item_user_head);
                holder.label = (WordWrapView) convertView
                        .findViewById(R.id.view_wordwrap);
                holder.text1_food_name = (TextView) convertView
                        .findViewById(R.id.content_item_title);
                holder.text1_food_name.setTypeface(ActivityTask.getTypeFace(mContext));
                holder.text2_personal = (TextView) convertView
                        .findViewById(R.id.content_item_personal_tip);
                holder.text4_user_name = (TextView) convertView
                        .findViewById(R.id.content_item_user_name);
                holder.text_intro = (TextView) convertView
                        .findViewById(R.id.content_item_comment);
                holder.user = convertView.findViewById(R.id.content_item_user);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (gourmets.isPresent()) {
                Gourmet gourmet = gourmets.get().get(position);
                holder.text1_food_name.setText("\u200B" + gourmet.getName());
                holder.text4_user_name.setText(gourmet.getUser().getNickName());
                holder.text_intro.setText(gourmet.getDescription());
                holder.text2_personal.setVisibility(gourmet.getIsPrivate() ? View.VISIBLE : View.GONE);
                if (gourmet.getImageURLs().size() > 0) {
                    holder.image_food.setVisibility(View.VISIBLE);
                    Picasso.with(parent.getContext())
                            .load(gourmet.getImageURLs().get(0) + "!Thumbnails")
                            .placeholder(R.drawable.empty_view_greeting)
                            .into(holder.image_food);
                } else if (gourmet.getImageURLs().size() == 0) {
                    holder.image_food.setVisibility(View.GONE);
                }
                if (gourmet.getUser().getAvatarURL().isPresent() && gourmet.getUser().getAvatarURL().get().length() > 5) {
                    Picasso.with(parent.getContext())
                            .load(gourmet.getUser().getAvatarURL().get())
                            .placeholder(R.drawable.default_user_head)
                            .into(holder.image_user_head);
                }
                holder.user.setOnClickListener((View v) -> {
                    Intent intent = new Intent();
                    intent.setClass(mContext, UserInfoActivity.class);
                    intent.putExtra("tiny_user", gourmet.getUser());
                    startActivity(intent);
                });
                holder.label.removeAllViews();
                for (String tag : gourmet.getTags()) {
                    TextView textview = new TextView(getActivity());
                    textview.setBackgroundResource(R.drawable.bg_label);
                    textview.setTextColor(getResources().getColor(R.color.white));
                    textview.setTextSize(14);
                    textview.setText(tag);
                    holder.label.addView(textview);
                }
            } else {
            }

            return convertView;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Gourmet getItem(int position) {
//            return position;
            if (gourmets.isPresent()) {
                return gourmets.get().get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return gourmets.isPresent() ? gourmets.get().size() : 0;
        }
    }

    private class ViewHolder {
        public ImageView image_food;
        public TextView text1_food_name;
        public TextView text2_personal;
        public WordWrapView label;
        public TextView text_intro;
        public ImageView image_user_head;
        public TextView text4_user_name;
        private View user;
    }
}
