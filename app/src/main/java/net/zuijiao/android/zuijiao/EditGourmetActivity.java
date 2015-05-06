package net.zuijiao.android.zuijiao;


import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.MyTextWatcher;
import com.zuijiao.utils.UpyunUploadTask;

import java.io.File;
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

    public static final int EDIT_LABEL_REQ = 2001;
    public static final int EDIT_LOCATION_REQ = 2002;
    public static final int EDIT_POSITION_REQ = 2003;

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
    private String mEditName = null;
    private ArrayList<String> mEditLabels = null;
    private String mEditDescription = null;
    private String mEditAddress = null;
    private String mEditPrice = null;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mImagePath = null;
    private int mProvinceId = -1;
    private int mCityId = -1;

    @Override
    protected void findViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_gourmet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit_gourmet) {
            uploadImageContinuously(mImagePath.get(0), () -> {
                Router.getGourmetModule().addGourmet(mEditName, mEditAddress,
                        mEditPrice, mEditDescription, mImageUrls,
                        mEditLabels, mProvinceId, mCityId,
                        mType == TYPE_CREATE_PERSONAL_GOURMET, () -> {
                            finallizeDialog();
                        }, () -> {
                            finallizeDialog();
                        });
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadImageContinuously(String imagePath, LambdaExpression lambdaExpression) {
        String imageUrl = UpyunUploadTask.gourmetPath(
                Router.getInstance().getCurrentUser().get().getIdentifier(), mEditName, ".jpg");
        createDialog();
        new UpyunUploadTask(imagePath, imageUrl, null,
                (boolean isComplete, String result, String error) -> {
                    if (isComplete) {
                        mImageUrls.add(imageUrl);
                        String nextImagePath = getNextImagePath(imagePath);
                        if (nextImagePath != null && new File(nextImagePath).exists()) {
                            uploadImageContinuously(nextImagePath, lambdaExpression);
                        } else {
                            lambdaExpression.action();
                        }
                    } else {
                        mImageUrls.clear();
                        finallizeDialog();
                    }
                }).execute();
    }

    private String getNextImagePath(String currentPath) {
        try {
            return mImagePath.get(mImagePath.indexOf(currentPath) + 1);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            mType = mTendIntent.getIntExtra("edit_gourmet_type", TYPE_UNDEFINE);
            if (mType == TYPE_UNDEFINE) {
                finish();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            finish();
        }
        switch (mType) {
            case TYPE_EDIT_PERSONAL_GOURMET:
                getSupportActionBar().setTitle(getString(R.string.edit_personal_gourmet));
                mPriceLayout.setVisibility(View.GONE);
                mPositionLayout.setVisibility(View.GONE);
                break;
            case TYPE_CREATE_PERSONAL_GOURMET:
                getSupportActionBar().setTitle(getString(R.string.personal_gourmet));
                mPriceLayout.setVisibility(View.GONE);
                mPositionLayout.setVisibility(View.GONE);
                break;
            case TYPE_CREATE_STORE_GOURMET:
                getSupportActionBar().setTitle(getString(R.string.store_gourmet));
                break;
            case TYPE_EDIT_STORE_GOURMET:
                getSupportActionBar().setTitle(getString(R.string.edit_store_gourmet));
                break;
        }
        mTvGourmetNameListener.setText(String.format(getString(R.string.nick_name_watcher), 0, 15));
        mTvDescriptionListener.setText(String.format(getString(R.string.nick_name_watcher), 0, 100));
        mEtGourmetName.addTextChangedListener(new MyTextWatcher(mTvGourmetNameListener, 15, mContext));
        mEtGourmetDescription.addTextChangedListener(new MyTextWatcher(mTvDescriptionListener, 100, mContext));
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
                startActivityForResult(intent, EDIT_LABEL_REQ);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_LABEL_REQ:
                if (resultCode == RESULT_OK) {
                    mEditLabels = data.getStringArrayListExtra("labels");
                }
                break;
        }

    }
}
