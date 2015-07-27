package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.ViewSwitcher;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.SimpleLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * choose location ,called from edit-user-info-activity ,use baidu location service
 */
@ContentView(R.layout.activity_location)
public class LocationActivity extends BaseActivity {
    private static final int SELECT_CITY_REQ = 3001;
    @ViewInject(R.id.location_tv)
    public static TextView mCurrentLocationTv = null;
    @ViewInject(R.id.location_image_switcher)
    public static ViewSwitcher mSwitcher = null;
    public static String autoLocationCity = null;
    public static String autoLocationProvince = null;
    LocationManager locationManager;
    @ViewInject(R.id.fix_location_container)
    private LinearLayout mFixLocation = null;
    @ViewInject(R.id.location_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.location_progressbar)
    private ProgressBar mLoadingCurrentLocationPb = null;
    @ViewInject(R.id.location_iv_current_location)
    private ImageView mCurrentLocationIv = null;
    @ViewInject(R.id.location_lv)
    private ListView mListView = null;
    private String[] mDirectCities = null;
    private ArrayList<SimpleLocation> locations = null;
    private int mProvinceId = -1;
    private String mProvinceName = null;
    //location list adapter
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
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            if (mProvinceId != -1) {//city list case
                bundle.putInt("city_id", location.getId());
                bundle.putString("city_name", location.getName());
                intent.putExtra("location", bundle);
                setResult(RESULT_OK, intent);
                finish();
            } else if (isDirectCity(location.getName())) {//direct city case
                bundle.putInt("province_id", location.getId());
                bundle.putString("province", location.getName());
                bundle.putInt("city_id", location.getId());
                bundle.putString("city", location.getName());
                intent.putExtra("location", bundle);
                setResult(RESULT_OK, intent);
                finish();
            } else {//province case
                intent.setClass(LocationActivity.this, LocationActivity.class);
                intent.putExtra("province_id", location.getId());
                intent.putExtra("province_name", location.getName());
                mProvinceId = location.getId();
                mProvinceName = location.getName();
                startActivityForResult(intent, SELECT_CITY_REQ);
            }
        }
    };

    /**
     * return from city choose ,use the same activity model but not the same instance
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CITY_REQ && resultCode == RESULT_OK) {
            Bundle bundle = data.getBundleExtra("location");
            int cityId = bundle.getInt("city_id");
            String cityName = bundle.getString("city_name");
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putInt("city_id", cityId);
            b.putString("city", cityName);
            b.putString("province", mProvinceName);
            b.putInt("province_id", mProvinceId);
            intent.putExtra("location", b);
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private LocationClient mLocationClient;
    private SimpleLocation mCurrentLocation = null;

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
        if(mTendIntent != null){
            mProvinceId = mTendIntent.getIntExtra("province_id", -1);
            if (mProvinceId == -1)
                getProvinceList();
            else {
                getSupportActionBar().setTitle(mTendIntent.getStringExtra("province_name"));
                mFixLocation.setVisibility(View.GONE);
                locations = DBOpenHelper.getmInstance(mContext).getCitiesByProvinceId(mProvinceId);
            }
        }
        InitLocation();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemListener);
        mFixLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoLocationCity == null) {
                    return;
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    int cityId = DBOpenHelper.getmInstance(mContext).getLocationIdByName(autoLocationCity);
                    int provinceId = DBOpenHelper.getmInstance(mContext).getLocationIdByName(autoLocationProvince);
                    bundle.putString("city", autoLocationCity);
                    bundle.putString("province", autoLocationProvince);
                    bundle.putInt("city_id", cityId);
                    bundle.putInt("province_id", provinceId);
                    intent.putExtra("location", bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * judge if the city is direct city like shanghai ,beijing ,chongqing and so on .
     *
     * @param str
     * @return
     */
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

    /**
     * get provinces list ,including direct city from location.db
     */
    private void getProvinceList() {
        locations = DBOpenHelper.getmInstance(mContext).getProvinceList();
        Collections.sort(locations, new Comparator<SimpleLocation>() {
            @Override
            public int compare(SimpleLocation l1, SimpleLocation l2) {
                if (isDirectCity(l1.getName()) == isDirectCity(l2.getName())) {
                    return 0;
                } else if (isDirectCity(l1.getName()) && !isDirectCity(l2.getName())) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    /**
     * baidu location service ;
     */
    private void InitLocation() {
        mLocationClient = ((ActivityTask) getApplication()).mLocationClient;
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setProdName("zuijiao");
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private class ViewHolder {
        TextView tvName;
        ImageView image;
    }
}
