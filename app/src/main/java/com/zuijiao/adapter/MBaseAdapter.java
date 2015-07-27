package com.zuijiao.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yitianhao on 2015/7/27.
 */
public class MBaseAdapter<T> extends BaseAdapter {

    protected Context context;
    protected ArrayList<T> models;

    public MBaseAdapter(Context context, ArrayList<T> models) {
        this.context = context;
        if (models == null)
            this.models = new ArrayList<T>();
        else
            this.models = models;
    }

    @Override
    public int getCount() {
        return models != null ? models.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    /**
     * update models
     *
     * @param models
     */
    public void update(List<T> models) {
        if (models == null) {
            return;
        }
        this.models.clear();
        for (T t : models) {
            this.models.add(t);
        }
        notifyDataSetChanged();
    }

    public ArrayList<T> getItems() {
        return models;
    }
}
