package com.zuijiao.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.view.WordWrapView;

import net.zuijiao.android.zuijiao.R;
import net.zuijiao.android.zuijiao.UserInfoActivity;

import java.util.List;

/**
 * gourmet adapter for gourmet-display-fragment
 * Created by xiaqibo on 2015/5/26.
 */
public class GourmetMainAdapter extends BaseAdapter {

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private Optional<List<Gourmet>> gourmets = Optional.empty();
    private static Typeface demiLight = null;
    private static Typeface light = null;


    public GourmetMainAdapter() {
        super();
    }

    public GourmetMainAdapter(int contentType, Context context) {
        super();
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        if (demiLight == null)
            demiLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
        if (light == null)
            light = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Light.ttf");
    }

    public void setData(Optional<List<Gourmet>> gourmets) {
        this.gourmets = gourmets;
    }

    public List<Gourmet> getData() {
        if (gourmets.isPresent())
            return gourmets.get();
        return null;
    }

    int count = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_content_item, null);
            Log.i("gourmetADpater", "count == " + count++);
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
        Gourmet gourmet = gourmets.get().get(position);
        holder.text1_food_name.setText("\u200B" + gourmet.getName());
        holder.text1_food_name.setTypeface(demiLight);
        holder.text4_user_name.setText(gourmet.getUser().getNickName());
        holder.text4_user_name.setTypeface(light);
        holder.text_intro.setText(gourmet.getDescription());
        holder.text_intro.setTypeface(light);
        holder.text2_personal.setVisibility(gourmet.getIsPrivate() ? View.VISIBLE : View.GONE);
        holder.text2_personal.setTypeface(light);
        if (gourmet.getImageURLs().size() > 0) {
            holder.image_food.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(gourmet.getImageURLs().get(0) + "!Thumbnails").placeholder(R.drawable.empty_view_greeting).into(holder.image_food);
        } else if (gourmet.getImageURLs().size() == 0) {
            holder.image_food.setVisibility(View.GONE);
        }
        if (gourmet.getUser().getAvatarURLSmall().isPresent())
            Picasso.with(mContext).load(gourmet.getUser().getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(holder.image_user_head);
        holder.image_user_head.setOnClickListener(new UserHeadClickListener(gourmet.getUser()));
        if (gourmet.getTags() == null || gourmet.getTags().size() == 0) {
            holder.label.setVisibility(View.GONE);
        } else {
            holder.label.setVisibility(View.VISIBLE);
            holder.label.removeAllViews();
            for (String tag : gourmet.getTags())
                holder.label.addView(getDrawText(tag));
        }
        return convertView;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Gourmet getItem(int position) {
        if (gourmets.isPresent()) {
            return gourmets.get().get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return gourmets.isPresent() ? gourmets.get().size() : 0;
    }

    private TextView getDrawText(String textContent) {
        TextView textView = new TextView(mContext);
        textView.setBackgroundResource(R.drawable.bg_label);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(14);
        textView.setText(textContent);
        textView.setTypeface(light);
        return textView;
    }

    class UserHeadClickListener implements View.OnClickListener {
        TinyUser user;

        UserHeadClickListener(TinyUser user) {
            this.user = user;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, UserInfoActivity.class);
            intent.putExtra("tiny_user", user);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    class ViewHolder {
        public ImageView image_food;
        public TextView text1_food_name;
        public TextView text2_personal;
        public WordWrapView label;
        public TextView text_intro;
        public ImageView image_user_head;
        public TextView text4_user_name;
    }
}




