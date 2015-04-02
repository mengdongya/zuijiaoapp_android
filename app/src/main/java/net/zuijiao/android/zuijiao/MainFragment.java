package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
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
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.FileManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;
import com.zuijiao.view.WordWrapView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements FragmentDataListener,
        MyListViewListener {
    public static final int UNDEFINE_PAGE = 0 ;
    public static final int MAIN_PAGE = 1 ;
    public static final int FAVOR_PAGE = 2 ;
    private View mContentView = null;
    private RefreshAndInitListView mListView = null;
    private int test_count = 2;
    private TextView mTextView = null;
    private LayoutInflater mInflater = null;
    private MainAdapter mAdapter = null;
    private String url = null;
    private int type = 0 ;

    public MainFragment (){
        super();
    }
    public MainFragment(int Type , String url){
        super();
        this.type = Type ;
        this.url = url ;
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
        firstInit() ;
        return mContentView;
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
            Toast.makeText(getActivity().getApplicationContext() , position +"" ,1000).show();
            startActivity(intent);
        }
    };

    //	private BaseAdapter mContentListAdapter = new BaseAdapter() {
//
//	};
    private class MainAdapter extends BaseAdapter {
        Optional<List<Gourmet>> gourmets = FileManager.tmpGourmets == null ? Optional.empty() : Optional.of(
                FileManager.tmpGourmets
        );

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
                holder.text2_personal.setVisibility(gourmet.getIsPrivate() ? View.VISIBLE :View.GONE);
                if (gourmet.getImageURLs().size() > 0) {
                    holder.image_food.setVisibility(View.VISIBLE);
                    Picasso.with(parent.getContext())
                            .load(gourmet.getImageURLs().get(0) + "!Thumbnails")
                            .placeholder(R.drawable.empty_view_greeting)
                            .into(holder.image_food);
                   // Picasso.with(parent.getContext()).load(gourmet.getImageURLs().get(0)).into(holder.image_food);
                }
                else if(gourmet.getImageURLs().size()==0){
                    holder.image_food.setVisibility(View.GONE);
                }
                if (gourmet.getUser().getAvatarURL().isPresent() && gourmet.getUser().getAvatarURL().get().length() > 5){
                    Picasso.with(parent.getContext()).load(gourmet.getUser().getAvatarURL().get()).into(holder.image_user_head);
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
//
//	@Override
//	public ArrayList<Object> getContentFromNetWork(String Url) {
//		return null;
//	}

//	@Override
//	public void NotifyData() {
//
//	}

//	@Override
//	public void onRefresh() {
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				if (test_count != 0) {
//					mTextView.setVisibility(View.GONE);
//				} else {
//					mTextView.setVisibility(View.VISIBLE);
//				}
//				mAdapter.notifyDataSetChanged();
//				mListView.stopRefresh();
//			}
//		}, 2000);
//	}

    @Override
    public ArrayList<Object> getContentFromNetWork(String Url) {
        return null;
    }

    @Override
    public void NotifyData() {

    }
    private void firstInit(){
        mListView.autoResetHeadView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData(true) ;
            }
        } , 2000) ;
    }
    @Override
    public void onRefresh() {
        fetchData(true);
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (test_count != 0) {
//                    mTextView.setVisibility(View.GONE);
//                } else {
//                    mTextView.setVisibility(View.VISIBLE);
//                }
//
//            }
//        }, 2000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }
    //login result ;
    private boolean canFetch = true ;
    private void fetchData(Boolean isRefresh) {

        //boolean :is login status now
        if(false){
            RouterOAuth.INSTANCE.loginEmailRoutine("2@2.2",
                    "c81e728d9d4c2f636f067f89cc14862c",
                    Optional.empty(),
                    Optional.empty(),
                    () -> {
                        //login success !
                    },
                    () -> {
                        System.out.println("failure");
                        canFetch = false ;
                    }
            );
        }
        if(!canFetch){
            Toast.makeText(getActivity(), getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
            return ;
        }
        List<Gourmet> tmpGourmets = mAdapter.gourmets.orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;

        if (!isRefresh) { // fetch more
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        Router.getGourmetModule().fetchOurChoice(theLastOneIdentifier
                , null
                , 20
                , gourmets ->
        {
            if (isRefresh) {
                mAdapter.gourmets = Optional.of(gourmets.getGourmets());
            } else {
                tmpGourmets.addAll(gourmets.getGourmets());
                mAdapter.gourmets = Optional.of(tmpGourmets);
            }
            mAdapter.notifyDataSetChanged();
            DBOpenHelper.getmInstance(getActivity().getApplicationContext()).insertGourmets(gourmets);
            mListView.stopRefresh();
        }
                , errorMessage ->
        { Toast.makeText(getActivity(), getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
            mListView.stopRefresh();
        });
    }

}
