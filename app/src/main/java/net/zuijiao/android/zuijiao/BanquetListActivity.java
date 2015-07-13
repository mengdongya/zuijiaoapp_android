package net.zuijiao.android.zuijiao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.AdapterViewHeightCalculator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mengdongya on 2015/7/10.
 */
@ContentView(R.layout.activity_banquet_list)
public class BanquetListActivity extends BaseActivity{
    @ViewInject(R.id.banquet_list_tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.host_guest_history_list)
    private ListView mHistoryList;

    private List<Banquent> banquentList;
    private String[] weekDays;
    private Boolean isHold = false;
    private int mAttendeeId = -1;
    private Attendee mAttendee;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("gg");
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        if (mTendIntent != null) {
            mAttendeeId = mTendIntent.getIntExtra("attendee_id", -1);
        }
        if (mAttendeeId == -1) {
            finish();
            return;
        }

        mHistoryList.setOnItemClickListener(mItemListener);

        networkStep();
    }

    private void networkStep() {
        createDialog();

        if (isHold) {
            Router.getAccountModule().attendeeInfo(mAttendeeId, new OneParameterExpression<Attendee>() {
                @Override
                public void action(Attendee attendee) {
                    mAttendee = attendee;
                    finalizeDialog();
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    finalizeDialog();
                }
            });
            Router.getBanquentModule().themesOfParticipator(mAttendeeId, null, 500, new OneParameterExpression<Banquents>() {

                @Override
                public void action(Banquents banquents) {
                    /*banquentList = banquents.getBanquentList();
                    mHistoryList.setAdapter(mHistoryAdapter);
                    AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mHistoryList);*/
                    finalizeDialog();
                }
            }
                    , new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mContext, getString(R.string.get_history_list_failed), Toast.LENGTH_SHORT).show();
                    finalizeDialog();
                }
            });
        }else{
            Router.getAccountModule().attendeeInfo(mAttendeeId, new OneParameterExpression<Attendee>() {
                @Override
                public void action(Attendee attendee) {
                    mAttendee = attendee;
                    finalizeDialog();
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    finalizeDialog();
                }
            });
            Router.getBanquentModule().themesOfParticipator(mAttendeeId, null, 500, new OneParameterExpression<Banquents>() {
                @Override
                public void action(Banquents banquents) {
                    banquentList = banquents.getBanquentList();
                    mHistoryList.setAdapter(mHistoryAdapter);
                    AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mHistoryList);
                    finalizeDialog();
                }
            }
                    , new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mContext, getString(R.string.get_history_list_failed), Toast.LENGTH_SHORT).show();
                    finalizeDialog();
                }
            });
        }
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
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


    private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(mContext, BanquetDetailActivity.class);
            intent.putExtra("banquet", banquentList.get(position));
            startActivity(intent);
        }
    };
    private View.OnClickListener mHeadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAttendee != null && mAttendee.getAvatarURLSmall().isPresent()) {
                Intent intent = new Intent(mContext, BigImageActivity.class);
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(mAttendee.getAvatarURL().get());
                intent.putStringArrayListExtra("cloud_images", arrayList);
                startActivity(intent);
            }
        }
    };
    private BaseAdapter mHistoryAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (banquentList == null) {
                return 0;
            }
            return banquentList.size();
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
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.banquet_history_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            Banquent banquet = banquentList.get(position);
            Picasso.with(mContext).load(banquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into(holder.image);
            holder.title.setText(banquet.getTitle());
            holder.date.setText(formatDate(banquet.getTime()));
            holder.situation.setText(String.format(getString(R.string.total_attendee), banquet.getAttendees().size()));
            return convertView;
        }
    };

    protected void createDialog() {
        if (mDialog != null && mDialog.isShowing()) return;
        mDialog = ProgressDialog.show(this, "", getString(R.string.on_loading));
    }


    class ViewHolder {
        @ViewInject(R.id.banquet_history_item_image)
        ImageView image;
        @ViewInject(R.id.banquet_history_item_title)
        TextView title;
        @ViewInject(R.id.banquet_history_item_date)
        TextView date;
        @ViewInject(R.id.banquet_history_item_situation)
        TextView situation;

        ViewHolder(View convertView) {
            com.lidroid.xutils.ViewUtils.inject(this, convertView);
        }
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

}
