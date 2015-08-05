package net.zuijiao.android.zuijiao;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by mengdongya on 2015/7/20.
 */
@ContentView(R.layout.activity_baidumap)
public class BaiDuMapActivity extends BaseActivity implements OnGetGeoCoderResultListener{
    @ViewInject(R.id.bmapView)
    private MapView mMapView;
    @ViewInject(R.id.map_view_tool_bar)
    private Toolbar mToolBar;
    @ViewInject(R.id.baidu_map_address)
    private TextView mHostAddress;
    @ViewInject(R.id.baidu_map_distance)
    private TextView mDistence;
    @ViewInject(R.id.baidu_map_navibtn)
    private Button naviBtn;
    private int distance;
    private GeoCoder mSearch = null;
    private BaiduMap mBaiduMap = null;
    private String address=null;
    private Context mContext = null ;
    private BMapManager mBMapMan;
    private boolean isFirstLoc = true;
    private LatLng start;
    private LatLng end;
    private DrivingRouteLine drivingRouteLine = null;
    private LocationClient mLocClient;
    private DrivingRoutePlanOption drivingRoutePlanOption;
    private  RoutePlanSearch routePlanSearch =null;
    private MyLocationListenner myLocationListenner  ;
    @Override
    protected void registerViews() {
        try{
        long onstart = System.currentTimeMillis() ;
        mContext = getApplicationContext() ;
        if (mTendIntent != null) {
            address = mTendIntent.getStringExtra("address");
        }
        if (address == null) {
            finish();
            return;
        }
        mLocClient = new LocationClient(mContext);
        myLocationListenner = new MyLocationListenner() ;
        mLocClient.registerLocationListener(myLocationListenner);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.map);
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

        drivingRoutePlanOption = new DrivingRoutePlanOption();
        drivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_FEE_FIRST);

        naviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start == null || end == null) {
                    Toast.makeText(mContext, getString(R.string.on_location), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String url = "geo:" ;
                    url = url + "0,0?q=" + address ;
//                    Uri uri = Uri.parse("geo:" + end.latitude + "," + end.longitude);
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(it);
                } catch (Throwable e) {
                    Toast.makeText(mContext, getString(R.string.no_map_app), Toast.LENGTH_SHORT).show();
                }
            }
        });
        long endtime= System.currentTimeMillis() - onstart ;
        }catch (Throwable t){
            finish();
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        try{
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
            mDistence.setText(getString(R.string.unknown_distance));
            return;
        }
        if(mBaiduMap == null){
            mBaiduMap = mMapView.getMap();
        }
        if(mBaiduMap == null)
            return;
        mBaiduMap.clear();
        end = geoCodeResult.getLocation();
        mBaiduMap.addOverlay(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_on_map)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(end));

//        drivingRoutePlanOption.from(PlanNode.withLocation(start)).to(PlanNode.withCityNameAndPlaceName(getString(R.string.shanghai), address));
        drivingRoutePlanOption.from(PlanNode.withLocation(start)).to(PlanNode.withLocation(end));
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.drivingSearch(drivingRoutePlanOption);
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                System.out.println("onGetWalkingRouteResult");
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
                System.out.println("onGetTransitRouteResult");
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                try{

                    if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        Toast.makeText(mContext,getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                        DrivingRouteOverlay routeOverlay = new DrivingRouteOverlay(mBaiduMap);
                        drivingRouteLine = drivingRouteResult.getRouteLines().get(0);
                        routeOverlay.setData(drivingRouteLine);
                        routeOverlay.addToMap();
                        distance = drivingRouteLine.getDistance();
                        mDistence.setText(String.format(getString(R.string.distance),distance/1000));
                    }

                }catch (Throwable t){
                    finish();
                }
            }
        });
        }catch (Throwable t){
            finish();
        }
        }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
    }

    public class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            try{
                if (location == null || mMapView == null)
                    return;

                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .direction(0).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                if(mBaiduMap != null)
                    mBaiduMap.setMyLocationData(locData);
                if (isFirstLoc) {
                    isFirstLoc = false;
                    start = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(start);
                    if(mBaiduMap != null)
                        mBaiduMap.animateMapStatus(u);
                }
            }catch (Throwable t){
                finish();
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
        if(mLocClient !=null){
            mLocClient.unRegisterLocationListener(myLocationListenner);
            mLocClient.stop();
            mLocClient = null ;
        }
        if(mBaiduMap!= null){
            mBaiduMap.setMyLocationEnabled(false);
            mBaiduMap.clear();
            mBaiduMap = null ;
        }
        if(mMapView!= null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        if(routePlanSearch!=null){
            routePlanSearch.destroy();
            routePlanSearch = null ;
        }
        }catch (Throwable t){
            finish();
        }
    }
}
