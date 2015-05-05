package net.zuijiao.android.zuijiao;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.SimpleLocation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ContentView(R.layout.activity_location)
public class LocationActivity extends BaseActivity {
    LocationManager locationManager;
    @ViewInject(R.id.fix_location_container)
    private LinearLayout mFixLocation = null;
    @ViewInject(R.id.location_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.location_progressbar)
    private ProgressBar mLoadingCurrentLocationPb = null;
    @ViewInject(R.id.location_iv_current_location)
    private ImageView mCurrentLocationIv = null;
    @ViewInject(R.id.location_tv)
    private TextView mCurrentLocationTv = null;
    @ViewInject(R.id.location_lv)
    private ListView mListView = null;
    private String[] mDirectCities = null;
    //    private String mCurrentLocation = "";
    private ArrayList<SimpleLocation> locations = null;
    private int mProvinceId = -1;
    private static final String BAIDU_KEY = "4lE0F9aM8Q3o9bGREEk9eFHe";
    private LocationClient mLocationClient;
    private SimpleLocation mCurrentLocation = null;
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return locations.size();
        }

        @Override
        public Object getItem(int position) {
            return locations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.location_item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.location_lv_item_image);
                holder.tvName = (TextView) convertView.findViewById(R.id.location_lv_item_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SimpleLocation location = locations.get(position);
            holder.tvName.setText(location.getName());
            if (isDirectCity(location.getName()) || mProvinceId != -1) {
                holder.image.setVisibility(View.GONE);
//            } else if (mCurrentLocation != null && mCurrentLocation.equals(location.getName())) {
//                holder.image.setVisibility(View.VISIBLE);
            } else {
                holder.image.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    };
    private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SimpleLocation location = locations.get(position);
            if (isDirectCity(location.getName()) || mProvinceId != -1) {
                setResult(RESULT_OK);
                finish();
            } else {
                Intent intent = new Intent();
                intent.setClass(LocationActivity.this, LocationActivity.class);
                intent.putExtra("province_id", location.getId());
                intent.putExtra("province_name", location.getName());
                startActivityForResult(intent, 1001);
            }
        }
    };
    private LocationListener locationListener2 = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "onLocationChanged");
            Log.d("Location", "onLocationChanged Latitude" + location.getLatitude());
            Log.d("Location", "onLocationChanged location" + location.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Location", "onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Location", "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Location", "onStatusChanged");
        }
    };
    private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            GpsStatus gpsstatus = locationManager.getGpsStatus(null);
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Toast.makeText(mContext, "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
                    Iterable<GpsSatellite> allSatellites = gpsstatus.getSatellites();
                    Iterator<GpsSatellite> it = allSatellites.iterator();
                    int count = 0;
                    while (it.hasNext()) {
                        count++;
                    }
                    Toast.makeText(mContext, "Satellite Count:" + count, Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d("Location", "GPS_EVENT_STOPPED");
                    break;
            }
        }
    };
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "onLocationChanged");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Location", "onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Location", "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Location", "onStatusChanged");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProvinceId = mTendIntent.getIntExtra("province_id", -1);
        if (mProvinceId == -1)
            getProvinceList();
        else {
            getSupportActionBar().setTitle(mTendIntent.getStringExtra("province_name"));
            mFixLocation.setVisibility(View.GONE);
            locations = DBOpenHelper.getmInstance(mContext).getCitiesByProvinceId(mProvinceId);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemListener);
        mFixLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitLocation();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isDirectCity(String str) {
        if (mDirectCities == null) {
            mDirectCities = getResources().getStringArray(R.array.direct_city);
        }
        for (String s : mDirectCities) {
            if (str.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    private void getProvinceList() {
        locations = DBOpenHelper.getmInstance(mContext).getProvinceList();
        Collections.sort(locations, (SimpleLocation l1, SimpleLocation l2) -> {
            if (isDirectCity(l1.getName()) == isDirectCity(l2.getName())) {
                return 0;
            } else if (isDirectCity(l1.getName()) && !isDirectCity(l2.getName())) {
                return -1;
            } else {
                return 1;
            }
        });
    }

    private boolean getLocationByGPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //根据设置的Criteria对象，获取最符合此标准的provider对象
        String currentProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();

        Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
        if (currentLocation == null) {
            locationManager.requestLocationUpdates(currentProvider, 0, 0, locationListener2);
        }
        locationManager.addGpsStatusListener(gpsListener);

        currentLocation = locationManager.getLastKnownLocation(currentProvider);
        if (currentLocation != null) {
            Log.d("Location", "Latitude: " + currentLocation.getLatitude());
            Log.d("Location", "location: " + currentLocation.getLongitude());
            return true;
        } else {
            Log.d("Location", "Latitude: " + 0);
            Log.d("Location", "location: " + 0);
        }
        return false;
    }

    private boolean getLocationByNetWork() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setSpeedRequired(false);

        String currentProvider = locationManager.getBestProvider(criteria, true);
        Log.d("Location", "currentProvider: " + currentProvider);
        Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
        if (currentLocation == null) {
            locationManager.requestLocationUpdates(currentProvider, 0, 0, locationListener2);
        }
        currentLocation = locationManager.getLastKnownLocation(currentProvider);
        if (currentLocation != null) {
            Log.d("Location", "Latitude: " + currentLocation.getLatitude());
            Log.d("Location", "location: " + currentLocation.getLongitude());
            resolveLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
            return true;
        } else {
            Log.d("Location", "Latitude: " + 0);
            Log.d("Location", "location: " + 0);
            return false;
        }
    }

    //local mobile base station locate
    private void getLocationByBaseStation() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation gsmCell = (GsmCellLocation) tm.getCellLocation();
        int cid = gsmCell.getCid();
        int lac = gsmCell.getLac();
        String netOperator = tm.getNetworkOperator();
        int mcc = Integer.valueOf(netOperator.substring(0, 3));
        int mnc = Integer.valueOf(netOperator.substring(3, 5));
        JSONObject holder = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject data = new JSONObject();
        try {
            holder.put("version", "1.1.0");
            holder.put("host", "maps.google.com");
            holder.put("address_language", "zh_CN");
            holder.put("request_address", true);
            holder.put("radio_type", "gsm");
            holder.put("carrier", "HTC");
            data.put("cell_id", cid);
            data.put("location_area_code", lac);
            data.put("mobile_countyr_code", mcc);
            data.put("mobile_network_code", mnc);
            array.put(data);
            holder.put("cell_towers", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://www.google.com/loc/json");
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(holder.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(stringEntity);
        HttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(httpPost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = httpResponse.getEntity();
        InputStream is = null;
        try {
            is = httpEntity.getContent();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            String result = "";
            while ((result = reader.readLine()) != null) {
                stringBuffer.append(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(mContext, stringBuffer.toString(), Toast.LENGTH_LONG).show();
//        txtInfo.setText();setText
    }

    private void resolveLocation(double latitude, double longitude) {
        Geocoder geoCoder = new Geocoder(this);
        try {
//            int latitude = (int) currentLocation.getLatitude();
//            int longitude = (int) currentLocation.getLongitude();
            List<Address> list = geoCoder.getFromLocation(latitude, longitude, 2);
            for (int i = 0; i < list.size(); i++) {
                Address address = list.get(i);
                mCurrentLocation = new SimpleLocation();
//                mCurrentLocation.setName();
                //  Toast.makeText(mContext, address.getCountryName() + address.getAdminArea() + address.getFeatureName(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    private void InitLocation() {
        mLocationClient = ((ActivityTask) getApplication()).mLocationClient;
//        mLocationClient = ((LocationApplication)getApplication()).mLocationClient;
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");
        int span = 1000;
        option.setScanSpan(5000);
        option.setIsNeedAddress(false);
        option.setLocationNotify(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private class ViewHolder {
        TextView tvName;
        ImageView image;
    }
}
