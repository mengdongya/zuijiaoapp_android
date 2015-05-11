package net.zuijiao.android.zuijiao;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.entity.SimpleImage;
import com.zuijiao.utils.MyTextWatcher;
import com.zuijiao.utils.UpyunUploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    public static final int EDIT_IMAGE_REQ = 2004;
    public static final int PREVIEW_IMAGE_REQ = 2005;
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
    @ViewInject(R.id.edit_gourmet_label_title)
    private TextView mLabelTitle = null;
    @ViewInject(R.id.edit_gourmet_label_count)
    private TextView mLabelCount = null;
    @ViewInject(R.id.edit_gourmet_location_title)
    private TextView mLocationTitle = null;
    @ViewInject(R.id.edit_gourmet_position_title)
    private TextView mPositionTitle = null;
    @ViewInject(R.id.edit_gourmet_et_price)
    private EditText mEtPrice = null;
    @ViewInject(R.id.edit_gourmet_position)
    private LinearLayout mPositionLayout = null;
    @ViewInject(R.id.edit_gourmet_price)
    private LinearLayout mPriceLayout = null;
    @ViewInject(R.id.edit_gourmet_labels)
    private LinearLayout mLabelLayout = null;
    @ViewInject(R.id.edit_gourmet_location)
    private LinearLayout mLocationLayout = null;
    private BaseAdapter mSelectedImageListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mImages.size() >= 5 ? 5 : mImages.size() + 1;
//            return mSelectedImageList.size() + 1;
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
            AbsListView.LayoutParams alp = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(alp);
            ImageView contentView = new ImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int) getResources().getDimension(R.dimen.edit_gourmet_image_size), (int) getResources().getDimension(R.dimen.edit_gourmet_image_size));
//            contentView.setImageResource(R.drawable.shanghai);
            contentView.setLayoutParams(lp);
            layout.addView(contentView);
            contentView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (position == mImages.size()) {
                contentView.setImageResource(R.drawable.edit_gourmet_picture);
            } else {
                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        mContext.getContentResolver(),
                        Integer.parseInt(mImages.get(position).id),
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
                contentView.setImageBitmap(bitmap);
            }
            layout.setFocusable(false);
            return layout;
        }
    };
    private String mEditName = null;
    private ArrayList<String> mEditLabels = new ArrayList<>();
    private String mEditDescription = null;
    //gourmet address ;
    private String mEditAddress = null;
    private String mEditPrice = null;
    private List<String> mImageUrls = null;
    private ArrayList<SimpleImage> mImages = new ArrayList<>();
    //    private ArrayList<String> mImagePath = new ArrayList<>();
//    private ArrayList<String> mSelectedImageId = new ArrayList<>();
    private AdapterView.OnItemClickListener mGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            if (mImages.size() < 5 && position == mSelectedImageListAdapter.getCount() - 1) {
                intent.setClass(mContext, MultiImageChooseActivity.class);
                intent.putParcelableArrayListExtra("edit_images", mImages);
//                intent.putStringArrayListExtra("selected_image_path", mImagePath);
//                intent.putStringArrayListExtra("selected_image_id", mSelectedImageId);
                startActivityForResult(intent, EDIT_IMAGE_REQ);
            } else {
                intent.setClass(mContext, BigImageActivity.class);
                intent.putParcelableArrayListExtra("edit_images", mImages);
                intent.putExtra("current_image_index", position);
                intent.putExtra("can_delete", true);
                startActivityForResult(intent, PREVIEW_IMAGE_REQ);
            }
        }
    };
    //where i edit ;
    private String mEditPosition = null;
    private int mProvinceId = 9;
    private int mCityId = 45055;

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
            mEditName = mEtGourmetName.getText().toString().trim();
            if (mEditName == null || mEditName.equals("")) {
                Toast.makeText(mContext, getString(R.string.notify_gourmet_name_empty), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            mEditDescription = mEtGourmetDescription.getText().toString().trim();
            if (mEditDescription == null || mEditDescription.equals("")) {
                Toast.makeText(mContext, getString(R.string.notify_gourmet_description_empty), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            mEditPrice = mEtPrice.getText().toString().trim();
            if (mImages != null && mImages.size() != 0) {
                mImageUrls = UpyunUploadTask.gourmetImagePaths(
                        Router.getInstance().getCurrentUser().get().getIdentifier(), mEditName, mImages.size(), ".jpg");
                uploadImageContinuously(mImages.get(0), () -> {
                    addGourmet();
                });
            } else {
                addGourmet();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGourmet() {
        createDialog();
        Router.getGourmetModule().addGourmet(mEditName, mEditAddress,
                mEditPrice, mEditDescription, mImageUrls,
                mEditLabels, mProvinceId, mCityId,
                mType == TYPE_CREATE_PERSONAL_GOURMET, () -> {
                    Toast.makeText(mContext, getString(R.string.notify_add_gourmet_success), Toast.LENGTH_SHORT).show();
                    finallizeDialog();
                    finish();
                }, () -> {
                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    finallizeDialog();
                });
    }

    private void uploadImageContinuously(SimpleImage image, LambdaExpression lambdaExpression) {
        String imageUrl = mImageUrls.get(mImages.indexOf(image));
        createDialog();
        new UpyunUploadTask(image.data, imageUrl, null,
                (boolean isComplete, String result, String error) -> {
                    if (isComplete) {
//                        mImageUrls.add(imageUrl);
                        SimpleImage nextImage = getNextImage(image);
                        if (nextImage != null && new File(nextImage.data).exists()) {
                            uploadImageContinuously(nextImage, lambdaExpression);
                        } else {
                            lambdaExpression.action();
                        }
                    } else {
                        mImageUrls.clear();
                        finallizeDialog();
                    }
                }).execute();
    }

    private SimpleImage getNextImage(SimpleImage currentImage) {
        try {
            return mImages.get(mImages.indexOf(currentImage) + 1);
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
        mEtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditPrice = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_gourmet_labels:
                Intent intent = new Intent();
                intent.setClass(mContext, LabelActivity.class);
                intent.putStringArrayListExtra("edit_label", mEditLabels);
                startActivityForResult(intent, EDIT_LABEL_REQ);
                break;
            case R.id.edit_gourmet_location:
                Intent intent3 = new Intent();
                intent3.setClass(mContext, LocationActivity.class);
                startActivityForResult(intent3, EDIT_LOCATION_REQ);
                break;
            case R.id.edit_gourmet_position:
                Intent intent2 = new Intent();
                intent2.setClass(mContext, EditPositionActivity.class);
                startActivityForResult(intent2, EDIT_POSITION_REQ);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case EDIT_LABEL_REQ:
                mEditLabels = data.getStringArrayListExtra("labels");
                mLabelCount.setVisibility(View.VISIBLE);
                mLabelCount.setText(String.format(getString(R.string.label_count), mEditLabels.size()));
                break;
            case EDIT_POSITION_REQ:
                mEditAddress = data.getStringExtra("position");
                mPositionTitle.setText(mEditAddress);
                break;
            case EDIT_LOCATION_REQ:
                Bundle bundle = data.getBundleExtra("location");
                mCityId = bundle.getInt("city_id", 45055);
                mProvinceId = bundle.getInt("province_id", 9);
                if (mCityId == mProvinceId) {
                    mEditPosition = bundle.getString("province");
                } else {
                    mEditPosition = bundle.getString("province") + bundle.getString("city");
                }
                mLocationTitle.setText(mEditPosition);
                break;
            case EDIT_IMAGE_REQ:
                mImages = data.getParcelableArrayListExtra("edit_images");
//                mImagePath = data.getStringArrayListExtra("selected_image_path");
//                mSelectedImageId = data.getStringArrayListExtra("selected_image_id");
                mSelectedImageListAdapter.notifyDataSetChanged();
                break;
            case PREVIEW_IMAGE_REQ:
                mImages = data.getParcelableArrayListExtra("edit_images");
                mSelectedImageListAdapter.notifyDataSetChanged();
                break;
        }

    }
}
