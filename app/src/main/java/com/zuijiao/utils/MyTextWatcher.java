package com.zuijiao.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import net.zuijiao.android.zuijiao.R;

/**
 * Created by xiaqibo on 2015/5/5.
 */
public class MyTextWatcher implements TextWatcher {
    private TextView textView;
    private int maxSize = 0;
    private String format;

    public MyTextWatcher(TextView textView, int maxSize, Context context) {
        this.textView = textView;
        this.maxSize = maxSize;
        this.format = context.getString(R.string.nick_name_watcher);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textView.setText(String.format(format, s.length(), maxSize));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
