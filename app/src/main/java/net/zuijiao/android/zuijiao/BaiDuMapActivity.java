package net.zuijiao.android.zuijiao;


import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;

/**
 * Created by mengdongya on 2015/7/20.
 */
@ContentView(R.layout.activity_baidumap)
public class BaiDuMapActivity extends BaseActivity implements OnGetGeoCoderResultListener{

    @ViewInject(R.id.baidu_map_tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.bmapView)
    private MapView mMapView;
    GeoCoder mSearch = null;
    private BaiduMap mBaiduMap;
    private String mHostAddress=null;
    BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.location_on_map);

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mTendIntent != null) {
            mHostAddress = mTendIntent.getStringExtra("address");

        }
        if (mHostAddress == null) {
            finish();
            return;
        }

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
//        mBaiduMap = mMapView.getMap();
        initOverlay(mHostAddress);
    }

    public void initOverlay(String address){
//        LatLng ll =new LatLng();
        mSearch.geocode(new GeoCodeOption().city(getString(R.string.shanghai)).address(address));

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
    }
}
