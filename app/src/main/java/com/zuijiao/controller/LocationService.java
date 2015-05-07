package com.zuijiao.controller;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;

/**
 * @author SunnyCoffee
 * @version 1.0
 * @date 2014-1-19
 * @desc 定位服务
 */
public class LocationService extends Service implements LocationListener {

    private static final String TAG = "LocationSvc";
    private LocationManager locationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) locationManager
                .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                        this);
        else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                        this);
        else Toast.makeText(this, "无法定位", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    BMapManager mapManager;

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Get the current position \n" + location);
        MKSearch search = new MKSearch();
        mapManager = new BMapManager(this);
        mapManager.init("EDB67AD764D300895C95ABA02A4DDC58D5485CCC",
                new MyMKGeneralListener());
        mapManager.getLocationManager().setNotifyInternal(20, 5);

        mapManager.getLocationManager().requestLocationUpdates(
                new MyLocationListener1());
        mapManager.start();
        search.init(mapManager, new MyMKSearchListener());
        search.reverseGeocode(new GeoPoint((int) location.getLongitude(), (int) location.getLatitude()));
        locationManager.removeUpdates(this);
//		stopSelf();
    }

    class MyLocationListener1 implements com.baidu.mapapi.LocationListener {

        @Override
        public void onLocationChanged(Location arg0) {
            int jindu = (int) (arg0.getLatitude() * 1000000);
            int weidu = (int) (arg0.getLongitude() * 1000000);
            MKSearch search = new MKSearch();
            search.init(mapManager, new MyMKSearchListener());
            search.reverseGeocode(new GeoPoint(jindu, weidu));
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    class MyMKSearchListener implements MKSearchListener {

        @Override
        public void onGetAddrResult(MKAddrInfo add, int arg1) {
            if (add == null) {

            } else {
                GeoPoint point = add.geoPt;
                String city = add.addressComponents.city;
                String province = add.addressComponents.province;
                Intent intent = new Intent();
                intent.setAction("locationAction");
                sendBroadcast(intent);
            }
        }

        @Override
        public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

        }

        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {

        }

        @Override
        public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {

        }

        @Override
        public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

        }

        @Override
        public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {

        }

        @Override
        public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {

        }

    }

    class MyMKGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int arg0) {

        }

        @Override
        public void onGetPermissionState(int arg0) {

        }

    }
}
