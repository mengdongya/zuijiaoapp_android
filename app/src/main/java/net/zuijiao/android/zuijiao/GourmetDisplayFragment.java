package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.zuijiao.adapter.GourmetMainAdapter;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.OnLongClickListener;

/**
 * display gourmet list ,used common gourmet list , favor gourmet list , recommendation gourmet list ;
 */
public class GourmetDisplayFragment extends Fragment
        implements MyListViewListener {
    public static final int UNDEFINE_PAGE = 0;
    public static final int MAIN_PAGE = 1;
    public static final int FAVOR_PAGE = 2;
    public static final int RECOMMEND_PAGE = 3;
    private int mContentType = UNDEFINE_PAGE;
    private View mContentView = null;
    //refreshable view
    private RefreshAndInitListView mListView = null;
    private FloatingActionMenu mAddMenu = null;
    private FloatingActionButton mFloatButtonA = null;
    private FloatingActionButton mFloatButtonB = null;
    private LayoutInflater mInflater = null;
    private GourmetMainAdapter mAdapter = null;
    private Context mContext = null;
    //display when the list is empty
    private LinearLayout mPlaceHolder = null;
    //display count of current page ;
    private TextView mFavorCount = null;
    //related activity
    private BaseActivity mActivity = null;
    private boolean bFirstInit = true;
    public Optional<TinyUser> mDisplayUser = Optional.empty();
    private Date tmpDate = null;
    private boolean bSelf = false;
    private int mPreviousVisibleItem;
    private TextView mAddTextView = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
    private TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f);
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), GourmetDetailActivity.class);
            intent.putExtra("selected_gourmet", mAdapter.getItem(position - 1));
            startActivity(intent);
        }
    };
    public GourmetDisplayFragment() {
        super();
    }

//    @SuppressLint("ValidFragment")
//    public GourmetDisplayFragment(int Type, Context context) {
//        super();
//        this.mContentType = Type;
//        this.mContext = context;
//        mDisplayUser = Router.getInstance().getCurrentUser();
//    }
    public static GourmetDisplayFragment newInstance(int type ){
        GourmetDisplayFragment fragment = new GourmetDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type" ,type );
//        bundle.putString( ARG, arg);
        fragment.setArguments(bundle);
        return fragment;
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
            mAdapter = new GourmetMainAdapter(mContentType, mContext);
//            if(mContentType == MAIN_PAGE && mAdapter.getData() == null ){
//                mAdapter.setData(FileManager.mainGourmet);
//            }
            mListView.setAdapter(mAdapter);
        }
        if ((mContentType == FAVOR_PAGE || mContentType == RECOMMEND_PAGE) && mAdapter.getCount() != 0) {
            mFavorCount.setVisibility(View.VISIBLE);
            if (mContentType == RECOMMEND_PAGE)
                mFavorCount.setText(String.format(getString(R.string.recommend_count_title), mAdapter.getCount()));
            else
                mFavorCount.setText(String.format(getString(R.string.favor_count), mAdapter.getCount()));
        } else {
            mFavorCount.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mInflater = inflater;
       Bundle bundle = getArguments() ;
        if(bundle != null)
            mContentType = bundle.getInt("type") ;
        mContentView = inflater.inflate(R.layout.fragment_gourmet, null);
        mActivity = (BaseActivity) getActivity();
        mContext = mActivity.getApplicationContext();
        findViews();
        if (mContentType == MAIN_PAGE) {
            initFloatButton();
        }
        if (bFirstInit)
            firstInit();
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

    private void findViews() {
        mPlaceHolder = (LinearLayout) mContentView.findViewById(R.id.main_empty_content);
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.content_items);
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setListViewListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.main_fragment_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GourmetDisplayFragment.this.onRefresh();
            }
        });
        mListView.setOnItemClickListener(mItemClickListener);
        mFavorCount = (TextView) mContentView.findViewById(R.id.tv_show_favor_count);
    }

    private void initFloatButton() {
        mAddTextView = (TextView) mContentView.findViewById(R.id.add_gourmet_indicator);
        mAddMenu = (FloatingActionMenu) mContentView.findViewById(R.id.multiple_actions);
        mAddMenu.setVisibility(View.VISIBLE);
        mAddMenu.setClosedOnTouchOutside(true);
        mFloatButtonA = (FloatingActionButton) mContentView.findViewById(R.id.action_a);
        mFloatButtonB = (FloatingActionButton) mContentView.findViewById(R.id.action_b);
        mFloatButtonA.setOnClickListener(new FloatButtonListener());
        mFloatButtonB.setOnClickListener(new FloatButtonListener());
        mFloatButtonA.setOnLongClickListener(new FloatButtonIndicator(getString(R.string.personal_gourmet)));
        mFloatButtonB.setOnLongClickListener(new FloatButtonIndicator(getString(R.string.store_gourmet)));
        mAddMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                if (b) {
                    mShowAction.setDuration(300);
                    mAddTextView.startAnimation(mShowAction);
                    mAddTextView.setVisibility(View.VISIBLE);
                } else {
                    mAddTextView.setVisibility(View.GONE);
                }
            }
        });
    }


    public void firstInit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 450);
    }


    public void onRefresh() {
        if (mContentType == MAIN_PAGE) {
            fetchCommonData(true);
        } else if (mContentType == FAVOR_PAGE) {
            fetchFavorData(true);
        } else if (mContentType == RECOMMEND_PAGE) {
            fetchRecommendData(true);
        }
    }

    public void onLoadMore() {
        if (mContentType == MAIN_PAGE) {
            fetchCommonData(false);
        } else if (mContentType == FAVOR_PAGE) {
            fetchFavorData(false);
        } else if (mContentType == RECOMMEND_PAGE) {
            fetchRecommendData(false);
        }
    }


//    public void onTouchDown() {
//        if (mAddMenu != null && mAddMenu.isOpened()) {
//            mAddMenu.close(true);
//        }
//    }

    public void clearPersonalData() {
        try {
            mAdapter.getData().clear();
            mAdapter.notifyDataSetChanged();
            mFavorCount.setVisibility(View.GONE);
        } catch (Throwable t) {
            System.err.println("no favor");
        }
    }

    private void fetchRecommendData(boolean isRefresh) {
        if (mDisplayUser == null || !mDisplayUser.isPresent()) {
            mDisplayUser = Router.getInstance().getCurrentUser();
            if (!mDisplayUser.isPresent()) {
                ((BaseActivity) getActivity()).tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (!Router.getInstance().getCurrentUser().isPresent()) {
                            if (mAdapter.getData() != null)
                                mAdapter.getData().clear();
                            mAdapter.notifyDataSetChanged();
                            mFavorCount.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            ((BaseActivity) getActivity()).notifyLogin(new LambdaExpression() {
                                @Override
                                public void action() {
                                    if (Router.getInstance().getCurrentUser().isPresent()) {
                                        fetchRecommendData(isRefresh);
                                    }
                                }
                            });
                        } else {
                            fetchRecommendData(isRefresh);
                        }
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                fetchRecommendData(isRefresh);
            }
            return;
        }
        List<Gourmet> tmpGourmets = mAdapter.getData() == null ? new ArrayList<>() : mAdapter.getData();
        Integer theLastOneIdentifier = null;
        if (!isRefresh) {
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        Router.getGourmetModule().fetchRecommendationListByUserId(mDisplayUser.get().getIdentifier(), theLastOneIdentifier, null, 20
                , new OneParameterExpression<Gourmets>() {
            @Override
            public void action(Gourmets gourmets) {
                if (isRefresh) {
                    tmpGourmets.clear();
                    if (gourmets.getTotalCount() == 0) {
                        mFavorCount.setVisibility(View.GONE);
                        mPlaceHolder.setVisibility(View.VISIBLE);
                        ((TextView) mContentView.findViewById(R.id.fragment_main_no_content_text_view)).setText(getString(R.string.no_recommendation));
                        mListView.setPullLoadEnable(false);
                    } else {
                        if (gourmets.getGourmets().size() < 20) {
                            mListView.setPullLoadEnable(false);
                        } else
                            mListView.setPullLoadEnable(true);
                        mFavorCount.setVisibility(View.VISIBLE);
                        mFavorCount.setText(String.format(getString(R.string.recommend_count_title), gourmets.getTotalCount()));
                        mPlaceHolder.setVisibility(View.GONE);
                    }
                } else {
                    if (gourmets.getGourmets().size() == 0) {
                        Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                        mListView.setPullLoadEnable(false);
                    } else {
                        if (gourmets.getGourmets().size() < 20) {
                            mListView.setPullLoadEnable(false);
                        } else {
                            mListView.setPullLoadEnable(true);
                        }
                        mFavorCount.setText(String.format(getString(R.string.recommend_count_title), tmpGourmets.size() + gourmets.getTotalCount()));
                    }
                }
                tmpGourmets.addAll(gourmets.getGourmets());
                mAdapter.setData(Optional.of(tmpGourmets));
                mAdapter.notifyDataSetChanged();
//            DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
                if (bSelf) {
                    FileManager.setGourmets(mContentType, Optional.of(tmpGourmets));
                    PreferenceManager.getInstance(mContext).saveFavorLastRefreshTime(new Date().getTime());
                } else {
                    tmpDate = new Date();
                }
                bFirstInit = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
                , new OneParameterExpression<String>() {
            @Override
            public void action(String errorMessage) {
                if (errorMessage.contains("401")) {
                    ((BaseActivity) getActivity()).tryLoginFirst(new LambdaExpression() {
                        @Override
                        public void action() {
                            fetchCommonData(isRefresh);
                        }
                    }, new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer integer) {
                            Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchFavorData(Boolean isRefresh) {
        if (mDisplayUser == null ||!mDisplayUser.isPresent()) {
//            if (Router.getInstance().getCurrentUser().isPresent()) {
            mDisplayUser = Router.getInstance().getCurrentUser();
//            }
            if (!mDisplayUser.isPresent()) {
                ((BaseActivity) getActivity()).tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (!Router.getInstance().getCurrentUser().isPresent()) {
                            if (mAdapter.getData() != null)
                                mAdapter.getData().clear();
                            mAdapter.notifyDataSetChanged();
                            mFavorCount.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            ((BaseActivity) getActivity()).notifyLogin(new LambdaExpression() {
                                @Override
                                public void action() {
                                    if (Router.getInstance().getCurrentUser().isPresent()) {
                                        fetchFavorData(isRefresh);
                                    }
                                }
                            });
                        } else {
                            fetchFavorData(isRefresh);
                        }
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                fetchFavorData(isRefresh);
            }
            return;
        }
        // TinyUser user = Router.getInstance().getCurrentUser().get();
        List<Gourmet> tmpGourmets = mAdapter.getData() == null ? new ArrayList<>() : mAdapter.getData();
        Integer theLastOneIdentifier = null;
        if (!isRefresh) {
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        Router.getGourmetModule().fetchFavoritesByUserId(mDisplayUser.get().getIdentifier(), theLastOneIdentifier, null, 20
                , new OneParameterExpression<Gourmets>() {
            @Override
            public void action(Gourmets gourmets) {
                if (isRefresh) {
                    tmpGourmets.clear();
                    if (gourmets.getTotalCount() == 0) {
                        mFavorCount.setVisibility(View.GONE);
                        mPlaceHolder.setVisibility(View.VISIBLE);
                        mListView.setPullLoadEnable(false);
                    } else {
                        if (gourmets.getGourmets().size() < 20) {
                            mListView.setPullLoadEnable(false);
                        } else {
                            mListView.setPullLoadEnable(true);
                        }
                        mFavorCount.setVisibility(View.VISIBLE);
                        int count = gourmets.getTotalCount();
                        mFavorCount.setText(String.format(getString(R.string.favor_count), gourmets.getTotalCount()));
                        mPlaceHolder.setVisibility(View.GONE);
                    }
                } else {
                    if (gourmets.getGourmets().size() == 0) {
                        Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                        mListView.setPullLoadEnable(false);
                    } else {
                        mFavorCount.setText(String.format(getString(R.string.favor_count), gourmets.getTotalCount()));
                        mListView.setPullLoadEnable(true);
                    }
                }
                tmpGourmets.addAll(gourmets.getGourmets());

                mAdapter.setData(Optional.of(tmpGourmets));
                mAdapter.notifyDataSetChanged();
//            DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
                if (bSelf) {
                    FileManager.setGourmets(mContentType, Optional.of(tmpGourmets));
                    PreferenceManager.getInstance(mContext).saveFavorLastRefreshTime(new Date().getTime());
                } else {
                    tmpDate = new Date();
                }
                bFirstInit = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
                , new OneParameterExpression<String>() {
            @Override
            public void action(String errorMessage) {
                if (errorMessage.contains("401")) {
                    ((BaseActivity) getActivity()).tryLoginFirst(new LambdaExpression() {
                        @Override
                        public void action() {
                            fetchFavorData(isRefresh);
                        }
                    }, new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer integer) {
                            Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchCommonData(Boolean isRefresh) {
        List<Gourmet> tmpGourmets = mAdapter.getData() == null ? new ArrayList<>() : mAdapter.getData();
        Integer theLastOneIdentifier = null;
        if (!isRefresh) {
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        Router.getGourmetModule().fetchOurChoice(theLastOneIdentifier
                , null
                , 20
                , new OneParameterExpression<Gourmets>() {
            @Override
            public void action(Gourmets gourmets) {
                if (isRefresh) {
                    tmpGourmets.clear();
                } else {
                    if (gourmets.getGourmets().size() == 0) {
                        Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                    }
                }
                if (gourmets.getGourmets().size() < 20) {
                    mListView.setPullLoadEnable(false);
                } else {
                    mListView.setPullLoadEnable(true);
                }
                tmpGourmets.addAll(gourmets.getGourmets());
                mAdapter.setData(Optional.of(tmpGourmets));
                FileManager.setGourmets(mContentType, Optional.of(tmpGourmets));
                mAdapter.notifyDataSetChanged();
                DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
                PreferenceManager.getInstance(mContext).saveMainLastRefreshTime(new Date().getTime());
                bFirstInit = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String errorMessage) {
                if (errorMessage.contains("401")) {
                    ((BaseActivity) getActivity()).tryLoginFirst(new LambdaExpression() {
                        @Override
                        public void action() {
                            fetchCommonData(isRefresh);
                        }
                    }, new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer integer) {
                            Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void goToEditGourmet(View selectedView) {
        Intent intent = new Intent();
        intent.setClass(mContext, EditGourmetActivity.class);
        switch (selectedView.getId()) {
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

    public void setType(int type) {
        this.mContentType = type;
    }

    private class FloatButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Router.getInstance().getCurrentUser().isPresent()) {
                goToEditGourmet(v);
            } else {
                mActivity.tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (Router.getInstance().getCurrentUser().isPresent()) {
                            goToEditGourmet(v);
                        } else {
                            mActivity.notifyLogin(null);
                        }
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(getActivity(), getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            mAddMenu.close(true);
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
        if (mDisplayUser != null && mDisplayUser.isPresent())
            return mDisplayUser.get().getIdentifier().
                    equals(PreferenceManager.getInstance(mContext).getStoredUserId());
        return false;
    }


    public boolean holdBackEvent() {
        return mAddMenu != null && mAddMenu.isOpened();
    }

    public void closeFloatMenu() {
        if (mAddMenu != null)
            mAddMenu.close(true);
    }


}
