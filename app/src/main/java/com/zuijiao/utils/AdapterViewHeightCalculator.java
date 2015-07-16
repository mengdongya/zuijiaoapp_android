package com.zuijiao.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by xiaqibo on 2015/7/8.
 * calculate the real height of the adapter-view ,
 * to adapt the scroller-view and adapter-view
 */
public class AdapterViewHeightCalculator {


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem == null)
                continue;
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()));
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setGridViewHeightBasedOnChildren(GridView gdView) {
        ListAdapter listAdapter = gdView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i += 5) {
            View listItem = listAdapter.getView(i, null, gdView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gdView.getLayoutParams();
        params.height = totalHeight
                + (gdView.getVerticalSpacing() * (listAdapter.getCount() / 5));
        int verticalSpacing = gdView.getVerticalSpacing() ;
        int adapterCount = listAdapter.getCount() ;
        int numColumns = gdView.getNumColumns() ;
        int space =gdView.getVerticalSpacing() * (listAdapter.getCount() / gdView.getNumColumns()) ;
        gdView.setLayoutParams(params);
    }
}
