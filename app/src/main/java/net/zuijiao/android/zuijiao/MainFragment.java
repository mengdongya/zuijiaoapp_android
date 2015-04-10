package net.zuijiao.android.zuijiao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.AuthorInfo;
import com.zuijiao.utils.UpyunUploadTask;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;
import com.zuijiao.view.WordWrapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFragment extends Fragment implements FragmentDataListener,
        MyListViewListener {
    public static final int UNDEFINE_PAGE = 0;
    public static final int MAIN_PAGE = 1;
    public static final int FAVOR_PAGE = 2;
    private View mContentView = null;
    private RefreshAndInitListView mListView = null;
    private int test_count = 2;
    private TextView mTextView = null;
    private LayoutInflater mInflater = null;
    private MainAdapter mAdapter = null;
    private Context mContext = null;
    //load url
//    private String url = null;
    //personal favor or general main
    private int type = UNDEFINE_PAGE;

    public MainFragment() {
        super();
    }

    public MainFragment(int Type, Context context) {
        super();
        this.type = Type;
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mInflater = inflater;
        mContentView = inflater.inflate(R.layout.fragment_main, null);
        mListView = (RefreshAndInitListView) mContentView
                .findViewById(R.id.content_items);
        mTextView = (TextView) mContentView.findViewById(R.id.tv_main_fm_blank);
        mAdapter = new MainAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setPullLoadEnable(true);
        mListView.setListViewListener(this);
        //mListView.getmHeaderView().setState(RefreshAndInitListView.XListViewHeader.STATE_REFRESHING);
        firstInit();

        new UpyunUploadTask(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1.jpg"
                , UpyunUploadTask.avatarPath(1, "png")
                , (long transferedBytes, long totalBytes) -> {
            System.out.println("trans:" + transferedBytes + "; total:" + totalBytes);
        }
                , (boolean isComplete, String result, String error) -> {
            System.out.println("isComplete:" + isComplete + ";result:" + result + ";error:" + error);
        }
        ).execute();

        return mContentView;
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
            intent.putExtra("click_item_index", position - 1);
            intent.putExtra("b_favor", type == FAVOR_PAGE);
            Toast.makeText(mContext, position + "", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    };

    //	private BaseAdapter mContentListAdapter = new BaseAdapter() {
//
//	};
    private class MainAdapter extends BaseAdapter {
        Optional<List<Gourmet>> gourmets = FileManager.mainGourmet;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                Typeface boldFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/NotoSansHans-Regular.otf");

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
//                holder.text1_food_name.setTypeface(boldFont);
                holder.text2_personal = (TextView) convertView
                        .findViewById(R.id.content_item_personal_tip);
                holder.text4_user_name = (TextView) convertView
                        .findViewById(R.id.content_item_user_name);
                holder.text_intro = (TextView) convertView
                        .findViewById(R.id.content_item_comment);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (gourmets.isPresent()) {
                Gourmet gourmet = gourmets.get().get(position);
                holder.text1_food_name.setText(gourmet.getName());
                holder.text4_user_name.setText(gourmet.getUser().getNickName());
                holder.text_intro.setText(gourmet.getDescription());
                //holder.text2_personal.setText(gourmet.getIsPrivate() ? "私房" : "");
                holder.text2_personal.setVisibility(gourmet.getIsPrivate() ? View.VISIBLE : View.GONE);
                if (gourmet.getImageURLs().size() > 0) {
                    holder.image_food.setVisibility(View.VISIBLE);
                    Picasso.with(parent.getContext())
                            .load(gourmet.getImageURLs().get(0) + "!Thumbnails")
                            .placeholder(R.drawable.empty_view_greeting)
                            .into(holder.image_food);
                    // Picasso.with(parent.getContext()).load(gourmet.getImageURLs().get(0)).into(holder.image_food);
                } else if (gourmet.getImageURLs().size() == 0) {
                    holder.image_food.setVisibility(View.GONE);
                }
                if (gourmet.getUser().getAvatarURL().isPresent() && gourmet.getUser().getAvatarURL().get().length() > 5) {
                    Picasso.with(parent.getContext())
                            .load(gourmet.getUser().getAvatarURL().get())
                            .placeholder(R.drawable.default_user_head)
                            .into(holder.image_user_head);
                }

                holder.label.removeAllViews();
                for (String tag : gourmet.getTags()) {
                    TextView textview = new TextView(getActivity());
                    textview.setBackgroundResource(R.drawable.bg_label);
                    textview.setTextColor(getResources().getColor(R.color.main_label));
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
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
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

    private void firstInit() {
        mListView.autoResetHeadView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (type == MAIN_PAGE) {
                    fetchCommmonData(true);
                } else if (type == FAVOR_PAGE) {
                    fetchFavorData(true);
                }
            }
        },450);
    }

    @Override
    public void onRefresh() {
        if (type == MAIN_PAGE) {
            fetchCommmonData(true);
        } else if (type == FAVOR_PAGE) {
            fetchFavorData(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (type == MAIN_PAGE) {
            fetchCommmonData(false);
        } else if (type == FAVOR_PAGE) {
            fetchFavorData(false);
        }
    }

    private boolean bLogin = false;

    private void fetchFavorData(Boolean isRefresh) {
        if (Router.INSTANCE.getCurrentUser().equals(Optional.empty())) {
            mAdapter.gourmets.get().clear();
            mAdapter.notifyDataSetChanged();
            if (isRefresh) {
                mListView.stopRefresh();
            } else {
                mListView.stopLoadMore();
            }
            Toast.makeText(mContext, getString(R.string.notify_unlogin), Toast.LENGTH_LONG).show();
            return;
        }
        TinyUser user = Router.INSTANCE.getCurrentUser().get();
        List<Gourmet> tmpGourmets = mAdapter.gourmets.orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;

        if (!isRefresh) { // fetch more
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        if (type == MAIN_PAGE) {
            mListView.setRefreshTime(new Date(PreferenceManager.getInstance(getActivity()).getMainLastRefreshTime()).toLocaleString());
        } else if (type == FAVOR_PAGE) {
            mListView.setRefreshTime(new Date(PreferenceManager.getInstance(getActivity()).getFavorLastRefreshTime()).toLocaleString());
        }
        Router.getGourmetModule().fetchFavoritesByUserId(user.getIdentifier(), theLastOneIdentifier, null, 20
                , gourmets ->
        {
            if (isRefresh) {
                tmpGourmets.clear();
                //mAdapter.gourmets = Optional.of(gourmets.getGourmets());
            } else {
                //mAdapter.gourmets = Optional.of(tmpGourmets);
            }
            tmpGourmets.addAll(gourmets.getGourmets());
            FileManager.setGourmets(type, Optional.of(tmpGourmets));
            mAdapter.gourmets = Optional.of(tmpGourmets);
            mAdapter.notifyDataSetChanged();
            DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
            if (type == MAIN_PAGE) {
                PreferenceManager.getInstance(mContext).saveMainLastRefreshTime(new Date().getTime());
            } else if (type == FAVOR_PAGE) {
                PreferenceManager.getInstance(mContext).saveFavorLastRefreshTime(new Date().getTime());
            }
            if (isRefresh) {
                mListView.stopRefresh();
            } else {
                mListView.stopLoadMore();
            }
        }
                //
                , errorMessage ->
        {
            Toast.makeText(getActivity(), getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
            if (isRefresh) {
                mListView.stopRefresh();
            } else {
                mListView.stopLoadMore();
            }
        });
    }

    private void fetchCommmonData(Boolean isRefresh) {

        //boolean :is login status now
        if (Router.INSTANCE.getCurrentUser().equals(Optional.empty()) && !bLogin) {
            tryLoginFirst();
            return;
        }
        List<Gourmet> tmpGourmets = mAdapter.gourmets.orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;

        if (!isRefresh) { // fetch more
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        if (type == MAIN_PAGE) {
            mListView.setRefreshTime(new Date(PreferenceManager.getInstance(getActivity()).getMainLastRefreshTime()).toLocaleString());
        } else if (type == FAVOR_PAGE) {
            mListView.setRefreshTime(new Date(PreferenceManager.getInstance(getActivity()).getFavorLastRefreshTime()).toLocaleString());
        }
        Router.getGourmetModule().fetchOurChoice(theLastOneIdentifier
                , null
                , 20
                , gourmets ->
        {
            if (isRefresh) {
                tmpGourmets.clear();
                //mAdapter.gourmets = Optional.of(gourmets.getGourmets());
            } else {
                if (gourmets.getGourmets().size() == 0) {
                    Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                }
                //  mAdapter.gourmets = Optional.of(tmpGourmets);
            }
            tmpGourmets.addAll(gourmets.getGourmets());
            mAdapter.gourmets = Optional.of(tmpGourmets);
            FileManager.setGourmets(type, Optional.of(tmpGourmets));
            mAdapter.notifyDataSetChanged();
            DBOpenHelper.getmInstance(mContext).insertGourmets(gourmets);
            if (type == MAIN_PAGE) {
                PreferenceManager.getInstance(mContext).saveMainLastRefreshTime(new Date().getTime());
            } else if (type == FAVOR_PAGE) {
                PreferenceManager.getInstance(mContext).saveFavorLastRefreshTime(new Date().getTime());
            }
            if (isRefresh) {
                mListView.stopRefresh();
            } else {
                mListView.stopLoadMore();
            }
        }
                //
                , errorMessage ->
        {
            Toast.makeText(getActivity(), getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
            if (isRefresh) {
                mListView.stopRefresh();
            } else {
                mListView.stopLoadMore();
            }
        });
    }

    private void tryLoginFirst() {
        AuthorInfo auth = PreferenceManager.getInstance(mContext).getThirdPartyLoginMsg();
        if (ThirdPartySDKManager.getInstance(mContext).isThirdParty(auth.getPlatform())) {
            Router.getOAuthModule().login(auth.getUid(), auth.getPlatform(), Optional.<String>empty(), Optional.of(auth.getToken()), () -> {
                        fetchCommmonData(true);
                    },
                    errorMessage -> {
                        System.out.println("failure " + errorMessage);
                        Toast.makeText(getActivity(), getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    });
        } else if ((auth.getEmail() != null) && (!auth.getEmail().equals(""))) {
            //2@2.2
            //c81e728d9d4c2f636f067f89cc14862c
            RouterOAuth.INSTANCE.loginEmailRoutine(auth.getEmail(),
                    auth.getPassword(),
                    Optional.empty(),
                    Optional.empty(),
                    () -> {
                        fetchCommmonData(true);
                    },
                    errorMessage -> {
                        System.out.println("failure " + errorMessage);
                        Toast.makeText(getActivity(), getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    }
            );

        } else {
            Router.getOAuthModule().visitor(() -> {
                        bLogin = true;
                        fetchCommmonData(true);
                    },
                    errorMessage -> {
                        System.out.println("failure " + errorMessage);
                        Toast.makeText(getActivity(), getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    });
        }
        Cache.INSTANCE.setup();
    }

}
