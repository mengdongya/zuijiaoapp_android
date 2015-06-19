package com.zuijiao.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.zuijiao.android.zuijiao.HostAndGuestActivity;
import net.zuijiao.android.zuijiao.R;

/**
 * Created by xiaqibo on 2015/6/9.
 */
public class BanquetAdapter extends BaseAdapter {
    private Context mContext;
    String urls[] = {
            "http://d.hiphotos.baidu.com/image/pic/item/b64543a98226cffc2d1e40b8ba014a90f603ea80.jpg",
            "http://t10.baidu.com/it/u=4286244909,263883255&fm=32&s=5A35894461A38EA4FAE4159603008089&w=533&h=800&img.JPEG",
            "http://t12.baidu.com/it/u=3975863465,10899649&fm=32&s=5160317707314388464C68EB0300E032&w=533&h=800&img.JPEG"};

    public BanquetAdapter(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 20;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.banquet_list_item, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.banquet_item_image);
            holder.title = (TextView) convertView.findViewById(R.id.banquet_item_title);
            holder.head = (ImageView) convertView.findViewById(R.id.banquet_item_user_head);
            holder.detail = (TextView) convertView.findViewById(R.id.banquet_item_detail);
            holder.description = (TextView) convertView.findViewById(R.id.banquet_item_description);
            holder.status = (TextView) convertView.findViewById(R.id.banquet_item_status);
            holder.price = (TextView) convertView.findViewById(R.id.banquet_item_price);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        Picasso.with(mContext).load(urls[position % 3]).into(holder.image);
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }


    class ViewHolder {
        ImageView image;
        TextView title;
        ImageView head;
        TextView detail;
        TextView description;
        TextView price;
        TextView status;
    }
}
