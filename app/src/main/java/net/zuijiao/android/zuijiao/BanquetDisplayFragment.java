package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.zuijiao.adapter.BanquetAdapter;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.view.RefreshAndInitListView;

import java.lang.reflect.Field;

/**
 * Created by xiaqibo on 2015/6/8.
 * display the main list of banquet module
 */
public class BanquetDisplayFragment extends Fragment implements RefreshAndInitListView.MyListViewListener {
    private RefreshAndInitListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private View mContentView;
    private ImageView closeBanner;
    private WebViewClient mWvClient = null;
    private BanquetAdapter mAdapter;
    private Integer lastedId = null;
    private MainActivity mParentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null)
            mContentView = inflater.inflate(R.layout.fragment_banquet, null);
        mParentActivity = (MainActivity) getActivity();
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.banquet_list_view);
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
        netWorkStep(true);
        return mContentView;
    }

    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private void netWorkStep(boolean bRefresh) {
        if (bRefresh) {
            mRefreshLayout.setRefreshing(true);
            lastedId = null;
        }
        Router.getBanquentModule().themesOfPublic(lastedId, 20, new OneParameterExpression<Banquents>() {
            @Override
            public void action(Banquents banquents) {
                if (bRefresh) {
                    mAdapter.setData(banquents);
                } else {
                    mAdapter.addData(banquents);
                }
                lastedId = banquents.getBanquentList().get(banquents.getBanquentList().size() - 1).getIdentifier();
                if (banquents.getBanquentList().size() < 20)
                    mListView.setPullLoadEnable(false);
                else
                mListView.setPullLoadEnable(true);
                mRefreshLayout.setRefreshing(false);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Log.d("banquetDisplayFragment", "failed!");
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
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
