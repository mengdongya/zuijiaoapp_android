package com.zuijiao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentCapacity;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.view.MyViewPager;
import com.zuijiao.listener.AttendeeAvatarListener;

import net.zuijiao.android.zuijiao.BanquetDetailActivity;
import net.zuijiao.android.zuijiao.BaseActivity;
import net.zuijiao.android.zuijiao.CommonWebViewActivity;
import net.zuijiao.android.zuijiao.HostAndGuestActivity;
import net.zuijiao.android.zuijiao.MainActivity;
import net.zuijiao.android.zuijiao.MultiImageChooseActivity;
import net.zuijiao.android.zuijiao.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * banquet display adapter and listener, for banquet list in main-fragment
 * Created by xiaqibo on 2015/6/9.
 */
public class BanquetAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Context mContext;
    private Banquents mBanquents;
    private List<Banquent> mBanquentList;
    private String[] weekDays;
    private int count = 0;
    private LayoutInflater mInflater;
    private View mBannerContainer;
    private AttendeeAvatarListener mAvatarListener ;
    private static final String TAG = "BanquetAdapter" ;
    private int currentItem = 0;
    private MyViewPager bannerViewPager;
    private LinearLayout dotContainer;
    private ScheduledExecutorService scheduledExecutorService;
    private BannerPagerAdapter mBannerAdapter ;

    private ArrayList<Banquents.Banner> mBanners ;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (bannerViewPager != null ) {
                try{
                    bannerViewPager.setCurrentItem(currentItem);
                }catch (Throwable t){
                    currentItem = 0 ;
                    bannerViewPager.setCurrentItem(0);
                }
            }
        }
    };

    public BanquetAdapter(Context context) {
        super();
        this.mContext = context;
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        mInflater = LayoutInflater.from(mContext);
        mAvatarListener = new AttendeeAvatarListener(mContext);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= 1;
//        if (position == 0 && showBanner()) {
//            Intent intent = new Intent(mContext, CommonWebViewActivity.class);
//            intent.putExtra("title", "activity");
//            intent.putExtra("content_url", mBanquents.getBannerLinkUrl());
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
//        } else {
        if (showBanner())
            position = position - 1;
        Intent intent = new Intent(mContext, BanquetDetailActivity.class);
        intent.putExtra("banquet", mBanquentList.get(position));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ImageView imageView = (ImageView) view.findViewWithTag("image");
        mContext.startActivity(intent);
        //  }
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mBanquentList != null) {
            count = mBanquentList.size();
        }
        if (showBanner())
            count += 1;
        return count;
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
        ViewHolder holder;
        /**
         * judge if banner view exist
         */
        if (position == 0 && showBanner()) {
            View view = null;
            if (mBannerContainer == null) {
                mBannerContainer = mInflater.inflate(R.layout.banquet_banner, null);
                //ImageView bannerView = (ImageView) mBannerContainer.findViewById(R.id.banquet_banner);
                bannerViewPager = (MyViewPager) mBannerContainer.findViewById(R.id.banquet_banner_viewpager);
            }
            dotContainer = (LinearLayout) mBannerContainer.findViewById(R.id.banquet_dot_container);
            mBannerAdapter = new BannerPagerAdapter() ;
            bannerViewPager.setAdapter(mBannerAdapter);
            dotContainer.removeAllViews();
            int dimen = (int) mContext.getResources().getDimension(R.dimen.food_detail_image_index_height);
            LinearLayout.LayoutParams Lp = new LinearLayout.LayoutParams(dimen, dimen);
            Lp.rightMargin = 20;
            Lp.leftMargin = 20;
            if(mBanners.size() > 1){
                for (int j = 0; j < mBanners.size(); j++) {
                    View dot = new View(mContext);
//                    dot.setPadding(5, 5, 5, 5);
                    dot.setBackgroundResource(R.drawable.dot_normal);
                    dotContainer.addView(dot, Lp);
                }
                dotContainer.getChildAt(0).setBackgroundResource(R.drawable.dot_focused);
                bannerViewPager.setOnPageChangeListener(new BannerPagerChangeListener());
                startBanner();
            }
            return mBannerContainer;
        } else {
            /**
             * show banquet list
             */
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.banquet_list_item, null);
                Log.i("banquet_adapter", "inflater" + (count++));
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.banquet_item_image);
                holder.title = (TextView) convertView.findViewById(R.id.banquet_item_title);
                holder.head = (ImageView) convertView.findViewById(R.id.banquet_item_user_head);
                holder.detail = (TextView) convertView.findViewById(R.id.banquet_item_detail);
                holder.description = (TextView) convertView.findViewById(R.id.banquet_item_description);
                holder.status = (TextView) convertView.findViewById(R.id.banquet_item_status);
                holder.price = (TextView) convertView.findViewById(R.id.banquet_item_price);
                holder.finish = (TextView) convertView.findViewById(R.id.banquet_item_finished);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                if (holder == null) {
                    return getView(position, null, parent);
                }
            }
            if (showBanner())
                position -= 1;
            Banquent banquent = mBanquentList.get(position);
            Picasso.with(mContext).load(banquent.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).fit().centerCrop().into(holder.image);
            holder.image.setTag("image");
            if (banquent.getMaster().getAvatarURLSmall().isPresent())
                Picasso.with(mContext).load(banquent.getMaster().getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).fit().centerCrop().into(holder.head);
            holder.title.setText(banquent.getTitle());
            String dateInfo = formatDate(banquent.getTime());
            holder.detail.setText(dateInfo + mContext.getString(R.string.center_dot) + banquent.getAddress());
            holder.description.setText(banquent.getDesc());
            holder.price.setText(String.valueOf(banquent.getPrice().intValue()));
            BanquentCapacity banquentCapacity = banquent.getBanquentCapacity();
            if (banquentCapacity.getMin() == banquentCapacity.getMax()) {
                holder.status.setText(String.format(mContext.getString(R.string.banquent_capacity_simple), banquentCapacity.getMax(), banquentCapacity.getCount()));
            } else {
                holder.status.setText(String.format(mContext.getString(R.string.banquent_capacity_muilt), banquentCapacity.getMin(), banquentCapacity.getMax(), banquentCapacity.getCount()));
            }
            holder.head.setTag(banquent.getMaster());
            holder.head.setOnClickListener(mAvatarListener);
            switch (BanquentStatus.fromString(banquent.getStatus())) {
                case Selling:
                    holder.finish.setVisibility(View.GONE);
                    break;
                case SoldOut:
                    holder.finish.setText(R.string.banquet_status_sold_out);
                    holder.finish.setVisibility(View.VISIBLE);
                    break;
                case OverTime:
                    holder.finish.setText(R.string.banquet_status_over_time);
                    holder.finish.setVisibility(View.VISIBLE);
                    break;
                case End:
                    holder.finish.setText(R.string.banquet_status_end);
                    holder.finish.setVisibility(View.VISIBLE);
                    break;
            }
            return convertView;
        }
    }

    private boolean showBanner() {
//        return mBanquents != null
//                && mBanquents.getBannerLinkUrl() != null
//                && !mBanquents.getBannerLinkUrl().equals("");
        return mBanners != null &&
                mBanners.size() != 0 ;
    }
//    private void initDots(int count) {
//        if (count <= 1) {
//            return;
//        }
//        int dimen = (int) mContext.getResources().getDimension(R.dimen.food_detail_image_index_height);
//        LinearLayout.LayoutParams Lp = new LinearLayout.LayoutParams(dimen, dimen);
//        Lp.rightMargin = 5;
//        Lp.leftMargin = 5;
//        Lp.bottomMargin = 20;
//        for (int j = 0; j < count; j++) {
//            mImageIndex.addView(initDot(), Lp);
//        }
//        mImageIndex.getChildAt(0).setBackgroundResource(R.drawable.food_detail_index_selected);
//    }
    /**
     * refresh called
     *
     * @param banquents
     */
    public void setData(Banquents banquents) {
        this.mBanquents = banquents;
        this.mBanquentList = mBanquents.getBanquentList();
        mBanners = banquents.getBanners() ;
//        int randomCount = new Random().nextInt(5) ;
//        Toast.makeText(mContext , mBanners.size() + "" , Toast.LENGTH_LONG).show();
//        mBanners = new ArrayList<>() ;
//        for(int i= 0 ; i <= randomCount ; i++ ){
//            mBanners.add(new Banquents.Banner("www.baidu.com" , "http://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=JELLY_BEAN&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3480012295,1633097787&os=1223343871,129863328&pn=2&rn=1&di=55491551940&ln=1288&fr=&fmq=1439980024859_R&ic=undefined&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&gsm=0&objurl=http%3A%2F%2Farticles.csdn.net%2Fuploads%2Fallimg%2F120306%2F118_120306100059_1_lit.jpg")) ;
//        }
//        mBanners = banquents.getBanners() ;
        notifyDataSetChanged();
        if(mBannerAdapter != null)
             mBannerAdapter.notifyDataSetChanged();
    }

    /**
     * load more called
     *
     * @param banquents
     */
    public void addData(Banquents banquents) {
        if (mBanquentList == null)
            mBanquentList = new ArrayList<>();
        this.mBanquentList.addAll(banquents.getBanquentList());
        mBanners = banquents.getBanners() ;
        if(mBannerAdapter != null)
            mBannerAdapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }

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


    class ViewHolder {
        ImageView image;
        TextView title;
        ImageView head;
        TextView detail;
        TextView description;
        TextView price;
        TextView status;
        TextView finish;
    }

    private class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if(mBanners == null || mBanners.size() == 0 ){
                return 0 ;
            }
            return mBanners.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            Picasso.with(mContext).load(mBanners.get(position).getImageUrl()).placeholder(R.drawable.empty_view_greeting).fit().centerCrop().into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setFocusable(true);
            imageView.setFocusableInTouchMode(true);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showBanner()) {
                        Intent intent = new Intent(mContext, CommonWebViewActivity.class);
                        intent.putExtra("title", "activity");
                        intent.putExtra("content_url", mBanners.get(position).getLinkUrl());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class BannerPagerChangeListener implements ViewPager.OnPageChangeListener {

        private int oldPosition = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            try{
                currentItem = position;
                dotContainer.getChildAt(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                dotContainer.getChildAt(currentItem).setBackgroundResource(R.drawable.dot_focused);
                oldPosition = position;
            }catch (Throwable t){
                currentItem = 0 ;
                oldPosition = 0 ;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void startBanner() {
        if(scheduledExecutorService == null ){
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(mRun, 1, 5, TimeUnit.SECONDS);
        }
    }


    private Runnable mRun = new Runnable(){

        @Override
        public void run() {
            currentItem = (currentItem + 1) % mBanners.size();
            handler.obtainMessage().sendToTarget();
        }
    } ;
    private class BannerRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (bannerViewPager) {
                currentItem = (currentItem + 1) % mBanners.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }
}
