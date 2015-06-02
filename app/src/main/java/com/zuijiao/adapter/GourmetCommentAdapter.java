package com.zuijiao.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.zuijiao.model.Comment;
import com.zuijiao.android.zuijiao.model.Comments;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.MeasuredTextView;

import net.zuijiao.android.zuijiao.R;
import net.zuijiao.android.zuijiao.UserInfoActivity;

/**
 * Created by xiaqibo on 2015/5/29.
 */
public class GourmetCommentAdapter extends BaseAdapter {
    private Comments mComments = null;
    private Context mContext;
    private LayoutInflater mInflater = null;
    private boolean showAll = true;

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public GourmetCommentAdapter() {
        super();
    }

    public GourmetCommentAdapter(Context context, Comments comments) {
        super();
        this.mContext = context;
        this.mComments = comments;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(Comments comments) {
        this.mComments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder holder;
        Comment comment = mComments.getCommentList().get(position);
        if (convertView == null) {
            holder = new CommentViewHolder();
            convertView = mInflater.inflate(R.layout.comment_item, null);
            //holder.commentContent = (TextView) convertView.findViewById(R.id.comment_content);
            TextView commentContent = new MeasuredTextView(mContext);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.BELOW, R.id.comment_user_name);
            commentContent.setLayoutParams(lp);
//                commentContent.setTextSize(getResources().getDimension(R.dimen.comment_content_text_size)) ;
//                commentContent.setTextColor(getResources().getColor(R.color.comment_content_color )) ;
            ((RelativeLayout) convertView.findViewById(R.id.comment_text_container)).addView(commentContent);
            holder.commentContent = commentContent;
            holder.head = (ImageView) convertView.findViewById(R.id.comment_user_head);
            holder.time = (TextView) convertView.findViewById(R.id.comment_time);
            holder.userName = (TextView) convertView.findViewById(R.id.comment_user_name);
            convertView.setTag(holder);
        } else {
            holder = (CommentViewHolder) convertView.getTag();
        }
        holder.time.setText(StrUtil.formatTime(comment.getPostDate(), mContext));
        holder.userName.setText(position + comment.getUser().getNickName());
        if (comment.getUser().getAvatarURL().isPresent())
            Picasso.with(mContext)
                    .load(comment.getUser().getAvatarURL().get())
                    .placeholder(R.drawable.default_user_head)
//                    .error(R.drawable.empty_view_greeting)
                    .into(holder.head);
        holder.head.setOnClickListener((View view) -> {
            Intent intent = new Intent();
            intent.setClass(mContext, UserInfoActivity.class);
            intent.putExtra("tiny_user", comment.getUser());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
        if (comment.getReplyTo().isPresent()) {
            String replyToUserName = comment.getReplyTo().get().getNickName();
            holder.commentContent.setText(String.format(mContext.getString(R.string.reply_content), replyToUserName + " " + comment.getDetail()));
            initReplyTextView(holder.commentContent, replyToUserName.length());
        } else {
            holder.commentContent.setText(comment.getDetail());
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (showAll) {
            return mComments.getCommentList().size();
        }
        return mComments.getCommentList().size() > 10 ? 10 : mComments.getCommentList().size();
    }

    private void initReplyTextView(TextView tv, int userNameLength) {
        String str = tv.getText().toString();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.GRAY), 2,
                2 + userNameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv.setText(style);
    }

    private class CommentViewHolder {
        public ImageView head;
        public TextView userName;
        public TextView commentContent;
        public TextView time;
    }

}
