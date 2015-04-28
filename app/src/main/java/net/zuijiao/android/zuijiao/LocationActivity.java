package net.zuijiao.android.zuijiao;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
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
import java.util.List;

@ContentView(R.layout.activity_location)
public class LocationActivity extends BaseActivity {
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
    private String mCurrentLocation = "";
    private ArrayList<SimpleLocation> locations = null;
    private int mProvinceId = -1;
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
            } else if (mCurrentLocation != null && mCurrentLocation.equals(location.getName())) {
                holder.image.setVisibility(View.VISIBLE);
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
    //创建位置监听器
    private LocationListener locationListener = new LocationListener() {
        //位置发生改变时调用
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "onLocationChanged");
            Log.d("Location", "onLocationChanged Latitude" + location.getLatitude());
            Log.d("Location", "onLocationChanged location" + location.getLongitude());
        }

        //provider失效时调用
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Location", "onProviderDisabled");
        }

        //provider启用时调用
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Location", "onProviderEnabled");
        }

        //状态改变时调用
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
                ;
                getLocationByNetWork();
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

    private void getLocationByNetWork() {
        //获取到LocationManager对象
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //创建一个Criteria对象
        Criteria criteria = new Criteria();
        //设置粗略精确度
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //设置是否需要返回海拔信息
        criteria.setAltitudeRequired(false);
        //设置是否需要返回方位信息
        criteria.setBearingRequired(false);
        //设置是否允许付费服务
        criteria.setCostAllowed(true);
        //设置电量消耗等级
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        //设置是否需要返回速度信息
        criteria.setSpeedRequired(false);

        //根据设置的Criteria对象，获取最符合此标准的provider对象
        String currentProvider = locationManager.getBestProvider(criteria, true);
        Log.d("Location", "currentProvider: " + currentProvider);
        //根据当前provider对象获取最后一次位置信息
        Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
        //如果位置信息为null，则请求更新位置信息
        if (currentLocation == null) {
            locationManager.requestLocationUpdates(currentProvider, 0, 0, locationListener);
        }
        //直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度
        //每隔10秒获取一次位置信息
        while (true) {
            currentLocation = locationManager.getLastKnownLocation(currentProvider);
            if (currentLocation != null) {
                Log.d("Location", "Latitude: " + currentLocation.getLatitude());
                Log.d("Location", "location: " + currentLocation.getLongitude());
                break;
            } else {
                Log.d("Location", "Latitude: " + 0);
                Log.d("Location", "location: " + 0);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.e("Location", e.getMessage());
            }
        }

        //解析地址并显示
        Geocoder geoCoder = new Geocoder(this);
        try {
            int latitude = (int) currentLocation.getLatitude();
            int longitude = (int) currentLocation.getLongitude();
            List<Address> list = geoCoder.getFromLocation(latitude, longitude, 2);
            for (int i = 0; i < list.size(); i++) {
                Address address = list.get(i);
                Toast.makeText(mContext, address.getCountryName() + address.getAdminArea() + address.getFeatureName(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
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

    private class ViewHolder {
        TextView tvName;
        ImageView image;
    }
}
