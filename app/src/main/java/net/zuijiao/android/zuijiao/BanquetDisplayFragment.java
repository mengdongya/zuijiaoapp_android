package net.zuijiao.android.zuijiao;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zuijiao.adapter.BanquetAdapter;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.CacheUtils;
import com.zuijiao.view.RefreshAndInitListView;

/**
 * Created by xiaqibo on 2015/6/8.
 * display the main list of banquet module
 */
public class BanquetDisplayFragment extends Fragment implements RefreshAndInitListView.MyListViewListener {

    public static final String TAG = "BanquetDisplayFragment" ;
    private RefreshAndInitListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private View mContentView;
    private ImageView closeBanner;
    private WebViewClient mWvClient = null;
    private BanquetAdapter mAdapter;
    private Integer lastedId = null;
    private LinearLayout mEmptyContentView ;
    private TextView mEmptyTextView ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null)
            mContentView = inflater.inflate(R.layout.fragment_banquet, null);
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.banquet_list_view);
        mEmptyContentView = (LinearLayout) mContentView.findViewById(R.id.no_banquet_container);
        mEmptyTextView = (TextView) mContentView.findViewById(R.id.no_banquet_tv);
        mListView.setPullRefreshEnable(false);
        mAdapter = new BanquetAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
//        mListView.setOnItemClickListener(mListItemListener);
        mRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.banquet_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                netWorkStep(true);
            }
        });
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setListViewListener(this);
        return mContentView;
    }
    public static BanquetDisplayFragment newInstance (){
        BanquetDisplayFragment fragment = new BanquetDisplayFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                netWorkStep(true);
            }
        } , 100) ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mAdapter != null && mListView != null )
            mListView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void netWorkStep(boolean bRefresh) {
        if (bRefresh) {
            mRefreshLayout.setRefreshing(true);
            lastedId = null;
        }
        Router.getBanquentModule().themesOfPublic(lastedId, 20, new OneParameterExpression<Banquents>() {
            @Override
            public void action(Banquents banquents) {
                mEmptyContentView.setVisibility(View.GONE);
                lastedId = banquents.getNextCursor();
                if (bRefresh) {
                    mRefreshLayout.setRefreshing(false);
                    mAdapter.setData(banquents);
                    Gson gson = new Gson();
                    CacheUtils.saveBanquets(gson.toJson(banquents), getActivity());
                } else {
                    mAdapter.addData(banquents);
                }
                if (banquents.getBanquentList().size() < 20 && !bRefresh)
                    mListView.setPullLoadEnable(false);
                else
                    mListView.setPullLoadEnable(true);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                if(bRefresh)
                    mRefreshLayout.setRefreshing(false);
                else
                    mListView.stopLoadMore();
                if(mAdapter.getCount()==0){
                    Gson gson = new Gson();
                    Banquents banquents = gson.fromJson(CacheUtils.getBanquets(getActivity()), Banquents.class);
                    if(banquents != null)
                        mAdapter.setData(banquents);
                }
                if(bRefresh && mAdapter.getCount() == 0){
                    mEmptyContentView.setVisibility(View.VISIBLE);
                    mEmptyTextView.setText(R.string.banquet_fetch_failed);
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        netWorkStep(true);
    }

    @Override
    public void onLoadMore() {
        netWorkStep(false);
    }
}
