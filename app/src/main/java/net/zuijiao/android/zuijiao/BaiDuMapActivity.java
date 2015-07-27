package net.zuijiao.android.zuijiao;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.lbsapi.auth.LBSAuthManager;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.CommonParams;
import com.baidu.navisdk.comapi.base.BNObserver;
import com.baidu.navisdk.comapi.base.BNSubject;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.baidu.nplatform.comapi.map.MapController;
import com.baidu.pano.platform.b.b;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.common.message.Log;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by mengdongya on 2015/7/20.
 */
@ContentView(R.layout.activity_baidumap)
public class BaiDuMapActivity extends BaseActivity implements OnGetGeoCoderResultListener{
    private static final String LTAG = BaiDuMapActivity.class.getSimpleName();
    @ViewInject(R.id.bmapView)
    private MapView mMapView;
    @ViewInject(R.id.map_view_tool_bar)
    private Toolbar mToolBar;
    @ViewInject(R.id.baidu_map_address)
    private TextView mHostAddress;
    @ViewInject(R.id.baidu_map_distance)
    private TextView mDistence;
    private Boolean isFirstLoc =true;
    private GeoCoder mSearch = null;
    private BaiduMap mBaiduMap = null;
    private String address=null;
    private Context mContext = null ;
    private BMapManager mBMapMan;
    private LatLng start;
    private LatLng end;
    private LocationClient mLocClient;
    private RoutePlanModel mRoutePlanModel;
//    private MyLocationData locationData;
    public MyLocationListenner locationListenner= new MyLocationListenner();
    @Override
    protected void registerViews() {
        mContext = getApplicationContext() ;
        mBMapMan=new BMapManager(mContext);
        mBMapMan.init(new MKGeneralListener() {
            @Override
            public void onGetPermissionState(int i) {
                if (i == 300) {
                    Toast.makeText(mContext, R.string.key_error, Toast.LENGTH_LONG).show();
                }
            }
        });
        if (mTendIntent != null) {
            address = mTendIntent.getStringExtra("address");
        }
        if (address == null) {
            finish();
            return;
        }
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setTitle(getString(R.string.map));
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMaxAndMinZoomLevel(8, 19);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(end));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));

        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        zoom.setVisibility(View.GONE);

        mMapView.onResume();
        mBaiduMap.getUiSettings().setAllGesturesEnabled(true);
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        mSearch.geocode(new GeoCodeOption().city(getString(R.string.shanghai)).address(address));
        mHostAddress.setText(address);



        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(locationListenner);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        mBaiduMap.clear();
        end = geoCodeResult.getLocation();
        mBaiduMap.addOverlay(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_on_map)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(end));
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
    }


    public class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                start = new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(start);
                mBaiduMap.animateMapStatus(u);
            }

            Double distence = DistanceUtil.getDistance(start,end);
            DecimalFormat df = new DecimalFormat("#.##");
            mDistence.setText("距您" + df.format(distence / 1000) + "km");
        }

    }

    public void navi(View view) {
        mLocClient.stop();
        NaviParaOption para = new NaviParaOption();
        para.startPoint(start);
        para.startName("开始位置");
        para.endPoint(end);
        para.endName("结束位置");
        try {
            BaiduMapNavigation.openBaiduMapNavi(para, mContext);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "您尚未安装百度地图app或app版本过低，请原谅无法为你导航！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        super.onDestroy();
    }
}
