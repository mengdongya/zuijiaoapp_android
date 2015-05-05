package net.zuijiao.android.zuijiao;


import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by xiaqibo on 2015/4/29.
 */
@ContentView(R.layout.activity_edit_gourmet)
public class EditGourmetActivity extends BaseActivity implements View.OnClickListener {

    public static final int TYPE_UNDEFINE = 0;
    private int mType = TYPE_UNDEFINE;
    public static final int TYPE_CREATE_STORE_GOURMET = 1;
    public static final int TYPE_CREATE_PERSONAL_GOURMET = 2;
    public static final int TYPE_EDIT_STORE_GOURMET = 3;
    public static final int TYPE_EDIT_PERSONAL_GOURMET = 4;
    @ViewInject(R.id.edit_gourmet_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.edit_gourmet_pictures)
    private GridView mGdView = null;
    @ViewInject(R.id.edit_gourmet_name)
    private EditText mEtGourmetName = null;
    @ViewInject(R.id.edit_gourmet_description)
    private EditText mEtGourmetDescription = null;
    @ViewInject(R.id.edit_gourmet_name_listener)
    private TextView mTvGourmetNameListener = null;
    @ViewInject(R.id.edit_gourmet_description_listener)
    private TextView mTvDescriptionListener = null;
    private ArrayList<String> mSelectedImageList = new ArrayList<>();
    @ViewInject(R.id.edit_gourmet_position)
    private LinearLayout mPositionLayout = null;
    @ViewInject(R.id.edit_gourmet_price)
    private LinearLayout mPriceLayout = null;
    @ViewInject(R.id.edit_gourmet_labels)
    private LinearLayout mLabelLayout = null;
    @ViewInject(R.id.edit_gourmet_location)
    private LinearLayout mLocationLayout = null;
    private AdapterView.OnItemClickListener mGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (position == mSelectedImageListAdapter.getCount() - 1) {
                Intent it = new Intent();
                it.setClass(mContext, MultiImageChooseActivity.class);
                startActivity(it);
            }
        }
    };
    private BaseAdapter mSelectedImageListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
//            return mSelectedImageList.size() + 1 > 5 ? 5 : mSelectedImageList.size() + 1;
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout = new LinearLayout(mContext);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(llp);
            ImageView contentView = new ImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int) getResources().getDimension(R.dimen.edit_gourmet_image_size), (int) getResources().getDimension(R.dimen.edit_gourmet_image_size));
            contentView.setImageResource(R.drawable.shanghai);
            contentView.setLayoutParams(lp);
            layout.addView(contentView);
            contentView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (position == mSelectedImageList.size()) {
                contentView.setImageResource(R.drawable.edit_gourmet_picture);
            }
            layout.setFocusable(false);
            return layout;
        }
    };

    @Override
    protected void findViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_gourmet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            mType = mTendIntent.getIntExtra("edit_gourmet_type", 0);
            if (mType == TYPE_UNDEFINE) {
                finish();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            finish();
        }
        switch (mType) {
            case TYPE_CREATE_PERSONAL_GOURMET:
                getSupportActionBar().setTitle(getString(R.string.personal_gourmet));
                mPriceLayout.setVisibility(View.GONE);
                mPositionLayout.setVisibility(View.GONE);
                break;
            case TYPE_CREATE_STORE_GOURMET:
                getSupportActionBar().setTitle(getString(R.string.store_gourmet));
                break;
            case TYPE_EDIT_PERSONAL_GOURMET:
                break;
            case TYPE_EDIT_STORE_GOURMET:
                break;
        }
        mGdView.setAdapter(mSelectedImageListAdapter);
        mGdView.setOnItemClickListener(mGridListener);
        mLabelLayout.setOnClickListener(this);
        mLocationLayout.setOnClickListener(this);
        mPositionLayout.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_gourmet_labels:
                Intent intent = new Intent();
                intent.setClass(mContext, LabelActivity.class);
                startActivity(intent);
                break;
            case R.id.edit_gourmet_price:
                break;
            case R.id.edit_gourmet_location:
                Intent intent3 = new Intent();
                intent3.setClass(mContext, LocationActivity.class);
                startActivity(intent3);
                break;
            case R.id.edit_gourmet_position:
                Intent intent2 = new Intent();
                intent2.setClass(mContext, EditPositionActivity.class);
                startActivity(intent2);
                break;
        }
    }

}
