package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.upyun.block.api.listener.CompleteListener;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.entity.AuthorInfo;
import com.zuijiao.utils.MD5;
import com.zuijiao.utils.UpyunUploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * activity for registering zuijiao account ,called from login-activity ;
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final int CHOOSE_GALLERY_REQ = 7001;
    private static final int TAKE_PHOTO_REQ = 7002;
    private static final int PHOTO_ZOOM_REQ = 7003;
    //check if the email address isn't must wrong
    private final static String EMAIL_REGEX = "^[\\w_\\.+-]*[\\w_\\.-]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    @ViewInject(R.id.register_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.et_regist_name)
    private EditText mEtNickName = null;
    @ViewInject(R.id.et_regist_email)
    private EditText mEtEmail = null;
    @ViewInject(R.id.et_regist_password)
    private EditText mEtPwd = null;
    @ViewInject(R.id.et_regist_password_comfirm)
    private EditText mEtPwdConfirm = null;
    @ViewInject(R.id.register_add_head)
    private ImageView mHeadChooseImage = null;
    @ViewInject(R.id.register_male_btn)
    private Button mMaleBtn;
    @ViewInject(R.id.register_female_btn)
    private Button mFemaleBtn;
    private String mEmail = null;
    private String mNickName = null;
    private String mPwd = null;
    private String mPwdConfirm = null;
    private String mErrorCode = null;
    private ProgressDialog mDialog = null;
    private String photoFileName;
    private String mUserAvatar;
    private File photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    private boolean bUserAvatarSet = false;
    private String mSelectedGender = "male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void registerViews() {
        //configuration changed begin
        if(mSelectedGender.equals("male")){
            mMaleBtn.setActivated(true);
            mMaleBtn.setTextColor(Color.WHITE);
        }else{
            mFemaleBtn.setActivated(true);
            mFemaleBtn.setTextColor(Color.WHITE);
        }
        //configuration changed end
        mMaleBtn.setOnClickListener(this);
        mFemaleBtn.setOnClickListener(this);
        mHeadChooseImage.setOnClickListener(this);
        //configuration changed begin
        if(bUserAvatarSet){
            String path = (getCacheDir().getPath() + File.separator + "head.jpg");
            File file = new File(path);
            if (file.exists()) {
                mHeadChooseImage.setImageBitmap(BitmapFactory.decodeFile(path));
            }
        }
        //configuration changed end ;
    }

    /**
     * receive image user choose for avatar
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_GALLERY_REQ && RESULT_OK == resultCode) {
            String path = (getCacheDir().getPath() + File.separator + "head.jpg");
            File file = new File(path);
            if (file.exists()) {
                mHeadChooseImage.setImageBitmap(BitmapFactory.decodeFile(path));
                bUserAvatarSet = true;
            }
        } else if (requestCode == TAKE_PHOTO_REQ && resultCode == RESULT_OK) {
//            Bitmap photo = data.getParcelableExtra("data");
//            if(photo != null && !photo.isRecycled()){
            photoZoom();
//            }
        } else if (requestCode == PHOTO_ZOOM_REQ && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }
            createDialog();
            Bitmap photo = extras.getParcelable("data");
            File file = new File(getCacheDir().getPath() + File.separator + "head.jpg");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(mContext, getString(R.string.error_read_file), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    finalizeDialog();
                    return;
                }
            }
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mContext, getString(R.string.error_read_file), Toast.LENGTH_LONG).show();
                finalizeDialog();
                return;
            }
            if (photo.compress(Bitmap.CompressFormat.JPEG, 75, os)) {
                try {
                    os.flush();
                    os.close();
                    os = null;
                    photo.recycle();
                } catch (IOException e) {
                    Toast.makeText(mContext, getString(R.string.error_read_file), Toast.LENGTH_LONG).show();
                    finalizeDialog();
                    e.printStackTrace();
                    return;
                }
                mHeadChooseImage.setImageBitmap(BitmapFactory.decodeFile(getCacheDir().getPath() + File.separator + "head.jpg"));
                bUserAvatarSet = true;
                finalizeDialog();
            } else {
                finalizeDialog();
            }
        }
    }


    public void photoZoom() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(photoDir, photoFileName)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_ZOOM_REQ);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                if (checkInputState()) {
                    try {
                        mPwd = MD5.crypt(mPwd);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        return false;
                    }
                    createDialog();
                    mUserAvatar = UpyunUploadTask.avatarPath(new Date().getTime(), "jpg");
                    new UpyunUploadTask(getCacheDir().getPath() + File.separator + "head.jpg", mUserAvatar, null, new CompleteListener() {
                        @Override
                        public void result(boolean bComplete, String s, String s2) {
                            finalizeDialog();
                            if (bComplete)
                                doRegister();
                            else
                                Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                        }
                    }).execute();
                } else {
                    Toast.makeText(getApplicationContext(), mErrorCode, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doRegister() {
        createDialog();
        Router.getOAuthModule().registerEmailRoutine(mNickName, mUserAvatar, mEmail, mPwd, mSelectedGender,
                Optional.of(PreferenceManager.mDeviceToken), Optional.<String>empty(), new LambdaExpression() {
                    @Override
                    public void action() {
                        //register success !
                        Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        TinyUser user = Router.getInstance().getCurrentUser().get();
                        AuthorInfo authorInfo = new AuthorInfo();
                        authorInfo.setUserName(user.getNickName());
                        authorInfo.setHeadPath(user.getAvatarURLSmall().get());
                        authorInfo.setPassword(mPwd);
                        authorInfo.setPlatform("");
                        authorInfo.setEmail(mEmail);
                        authorInfo.setUserId(user.getIdentifier());
                        PreferenceManager.getInstance(getApplicationContext()).saveThirdPartyLoginMsg(authorInfo);
                        Intent intent = new Intent();
                        intent.setAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
                        sendBroadcast(intent);
                        intent.setClass(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finalizeDialog();
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer errorMessage) {
                        if (errorMessage != null && errorMessage == 401) {
                            Toast.makeText(getApplicationContext(), getString(R.string.repeat_email_address), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        }
                        finalizeDialog();
                    }
                });

    }

    private boolean checkInputState() {
        if (!bUserAvatarSet) {
            mErrorCode = getString(R.string.notify_choose_avatar);
            return false;
        }
        mNickName = mEtNickName.getText().toString().trim();
        if (mNickName == null || mNickName.equals("")) {
            mErrorCode = getString(R.string.register_error_name);
            return false;
        }
        mEmail = mEtEmail.getText().toString().trim();
        if (mEmail == null || mEmail.equals("")) {
            mErrorCode = getString(R.string.register_error_email);
            return false;
        }
        if (!mEmail.matches(EMAIL_REGEX)) {
            mErrorCode = getString(R.string.register_error_email_format);
            return false;
        }
        mPwd = mEtPwd.getText().toString().trim();
        if (mPwd == null || mPwd.equals("")) {
            mErrorCode = getString(R.string.register_error_pwd);
            return false;
        }
        mPwdConfirm = mEtPwdConfirm.getText().toString().trim();
        if (mPwdConfirm == null || !mPwdConfirm.equals(mPwd)) {
            mErrorCode = getString(R.string.register_error_pwd_imsame);
            return false;
        }
        if(mPwd.length() <6){
            mErrorCode = getString(R.string.pwd_too_short) ;
            return false ;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_add_head:
                String[] array = getResources().getStringArray(R.array.image_resources);
                AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this).setItems(R.array.image_resources, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        switch (which) {
                            case 0:
                                intent.setClass(mContext, ImageChooseActivity.class);
                                startActivityForResult(intent, CHOOSE_GALLERY_REQ);
                                break;
                            case 1:
                                intent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                photoFileName = "zuijiao_head" + new Date().getTime() + ".jpg";
                                File file = new File(photoDir, photoFileName);
                                intent.putExtra(
                                        MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(file));
                                startActivityForResult(intent, TAKE_PHOTO_REQ);
                                break;
                        }
                    }
                }).create();
                Window window = dialog.getWindow() ;
                window.setWindowAnimations(R.style.dialogWindowAnim);
                dialog.show();
                break;
            case R.id.register_male_btn:
                if (v.isActivated()) {
                    return;
                }
                mSelectedGender = "male";
                mMaleBtn.setActivated(true);
                mMaleBtn.setTextColor(Color.WHITE);
                mFemaleBtn.setActivated(false);
                mFemaleBtn.setTextColor(getResources().getColor(R.color.bg_light_gray));
                break;
            case R.id.register_female_btn:
                if (v.isActivated()) {
                    return;
                }
                mSelectedGender = "female";
                mFemaleBtn.setActivated(true);
                mFemaleBtn.setTextColor(Color.WHITE);
                mMaleBtn.setActivated(false);
                mMaleBtn.setTextColor(getResources().getColor(R.color.bg_light_gray));
                break;
        }
    }
}
