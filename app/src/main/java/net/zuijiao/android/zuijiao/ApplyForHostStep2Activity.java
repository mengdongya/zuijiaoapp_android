package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.upyun.block.api.listener.CompleteListener;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.user.ContactInfo;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.utils.UpyunUploadTask;

import java.io.File;

/**
 * Created by xiaqibo on 2015/8/10.
 * fill host information ;
 */
@ContentView(R.layout.activity_apply_host2)
public class ApplyForHostStep2Activity extends BaseActivity {

    private static final String TAG = "ApplyForHostStep2Activity";
    private static final int CHOOSE_AVATAR_REQ = 10001;
    private static final int CHOOSE_LOCATION_REQ = 10002;
    private static final int VERIFY_PHONE_REQ = 10003;
    private final static String EMAIL_REGEX = "^[\\w_\\.+-]*[\\w_\\.-]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    @ViewInject(R.id.apply2_tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.apply2_text)
    private TextView mTextView;
    @ViewInject(R.id.apply2_list_view)
    private ListView mListView;
    private int[] titles = {R.string.avatar, R.string.nick_name, R.string
            .gender, R.string.current_location, R.string.email, R.string.mobile_phone};
//    private ArrayList<String> values = new ArrayList<>(5);
    private String values[]= { "", "" , "" ,"" ,"" ,""};
    private String mAvatar = null;
    private User mFullUser, mTmpFullUser;
    private String mCurrentEdit  ;
    private int mGenderCheckItem = 0;
    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemListener);
        fetchContent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_AVATAR_REQ:
                    changeUserAvatar();
                    break;
                case CHOOSE_LOCATION_REQ:
                    Bundle bundle = data.getBundleExtra("location");
                    int cityId = bundle.getInt("city_id", 45055);
                    int provinceId = bundle.getInt("province_id", 9);
                    if (mFullUser.getProfile() != null
                            && mFullUser.getProfile().getCityId() != null
                            && mFullUser.getProfile().getCityId().equals(cityId)
                            && mFullUser.getProfile().getProvinceId() != null
                            && mFullUser.getProfile().getProvinceId().equals(provinceId)) {
                        return;
                    }
                    mTmpFullUser.getProfile().setProvinceAndCity(provinceId, cityId);
                    updateUserInfo();
                    break;
                case VERIFY_PHONE_REQ:
                    if (!mTmpFullUser.getContactInfo().isPresent()) {
                        mTmpFullUser.setContactInfo(new ContactInfo());
                    }
                    mTmpFullUser.getContactInfo().get().setPhoneNumber(data.getStringExtra("verified_phone_num"));
                    mFullUser = mTmpFullUser.clone();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    protected void fetchContent() {
        fetchMyInfo(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apply_host ,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_apply_host) {
            Intent intent = new Intent(mContext , CommonWebViewActivity.class) ;
            intent.putExtra("content_url", "http://db.zuijiaodev.com:3000/");
            intent.putExtra("apply_host" , true );
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mFullUser ==null){
                fetchMyInfo(new LambdaExpression() {
                    @Override
                    public void action() {
                        onItemClick(parent, view, position, id);
                    }
                });
                return;
            }
            switch (position) {
                case 0:
                    Intent intent = new Intent();
                    intent.setClass(mContext, ImageChooseActivity.class);
                    startActivityForResult(intent, CHOOSE_AVATAR_REQ);
                    break;
                case 1:
                    createGeneralEditTextDialog(mFullUser.getNickname().get(), getString(R.string.nick_name), getString(R.string.nick_name_hint), 1, 15, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mCurrentEdit != null && !mCurrentEdit.equals(mFullUser.getNickname())) {
                                mTmpFullUser.setNickname(mCurrentEdit);
                                updateUserInfo();
                            }
                            mCurrentEdit = null;
                            dialog.dismiss();
                        }
                    });
                    break;
                case 2:
                    createGenderDialog();
                    break;
                case 3:
                    Intent locationIntent = new Intent();
                    locationIntent.setClass(ApplyForHostStep2Activity.this, LocationActivity.class);
                    startActivityForResult(locationIntent, CHOOSE_LOCATION_REQ);
                    break;
                case 4:
                    String email = null;
                    if (mFullUser.getContactInfo().isPresent())
                        email = mFullUser.getContactInfo().get().getEmail();
                    createGeneralEditTextDialog(email, getString(R.string.email), getString(R.string.input_email), 1, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mCurrentEdit == null || mCurrentEdit.equals("")) {
                                return;
                            }
                            if (!mCurrentEdit.matches(EMAIL_REGEX)) {
                                Toast.makeText(mContext, getString(R.string.register_error_email_format), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (mFullUser.getContactInfo().isPresent() && mCurrentEdit.equals(mFullUser.getContactInfo().get().getEmail())) {
                                mCurrentEdit = null;
                                return;
                            }
                            if (!mTmpFullUser.getContactInfo().isPresent()) {
                                mTmpFullUser.setContactInfo(new ContactInfo());
                            }
                            mTmpFullUser.getContactInfo().get().setEmail(mCurrentEdit);
                            updateUserInfo();
                            dialog.dismiss();
                        }
                    });

                    break;
                case 5:
                    Intent verifyIntent = new Intent();
                    verifyIntent.setClass(mContext, VerifyPhoneNumActivity.class);
                    startActivityForResult(verifyIntent, VERIFY_PHONE_REQ);
                    break;
            }
        }
    };

    private void updateUserInfo() {
        createDialog();
        Router.getAccountModule().update(mTmpFullUser, new LambdaExpression() {
            @Override
            public void action() {
                if (!mFullUser.getNickname().equals(mTmpFullUser.getNickname())) {
                    mPreferMng.saveNickname(mTmpFullUser.getNickname().get());
                    Intent intent = new Intent();
                    intent.setAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
                    sendBroadcast(intent);
                }
                mFullUser = mTmpFullUser.clone();
                mAdapter.notifyDataSetChanged();
                finalizeDialog();
            }
        }, new LambdaExpression() {
            @Override
            public void action() {
                mTmpFullUser = mFullUser.clone();
                Toast.makeText(mContext, getString(R.string.save_error_check_network), Toast.LENGTH_SHORT).show();
                finalizeDialog();
            }
        });
        mCurrentEdit = null;
    }

    private void createGenderDialog() {
        final String[] array = getResources().getStringArray(R.array.genders);
        if (mFullUser.getProfile() != null && mFullUser.getProfile().getGender().equals("male")) {
            mGenderCheckItem = 0;
        } else if (mFullUser.getProfile() != null && mFullUser.getProfile().getGender().equals("female")) {
            mGenderCheckItem = 1;
        } else {
            mGenderCheckItem = 2;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(ApplyForHostStep2Activity.this).setSingleChoiceItems(array, mGenderCheckItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == mGenderCheckItem) {
                            return;
                        }
                        String gender = "keepSecret";
                        switch (which) {
                            case 0:
                                gender = "male";
                                break;
                            case 1:
                                gender = "female";
                                break;
                            case 2:
                                gender = "keepSecret";
                                break;
                        }
                        mTmpFullUser.getProfile().setGender(gender);
                        updateUserInfo();
                        dialog.dismiss();
                    }
                }).setTitle(getString(R.string.gender)).create();
        Window window = alertDialog.getWindow() ;
        window.setWindowAnimations(R.style.dialogWindowAnim);
        alertDialog.show();
    }

    private void createGeneralEditTextDialog(String message, String title, String etHint, int lineNum, int maxText, DialogInterface.OnClickListener finishListener) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.nick_name_input_dialog, null);
        TextView textView = (TextView) contentView.findViewById(R.id.tv_et_watcher);
        EditText editText = (EditText) contentView.findViewById(R.id.et_nick_name_input);
        if (maxText == 0) {
            textView.setVisibility(View.GONE);
        } else {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxText)});
        }
        textView.setText(String.format(getString(R.string.nick_name_watcher), 0, maxText));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCurrentEdit = s.toString().trim();
                textView.setText(String.format(getString(R.string.nick_name_watcher), s.length(), maxText));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (lineNum > 1) {
            editText.setHorizontallyScrolling(false);
        }
//        editText.setLines(lineNum);
        editText.setHint(etHint);
        if (message != null && !message.equals("")) {
            editText.setText(message);
            editText.setSelection(0, message.length());
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        AlertDialog alertDialog = new AlertDialog.Builder(ApplyForHostStep2Activity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save), finishListener).create();
        Window window = alertDialog.getWindow() ;
        window.setWindowAnimations(R.style.dialogWindowAnim);
        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mCurrentEdit = null ;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 200);
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return titles.length;
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
            if (position == 0) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.fill_host_info0, null);
                ImageView image = (ImageView) convertView.findViewById(R.id.fill_host_item_image);
                if (mFullUser != null && mFullUser.getAvatarURLSmall().isPresent())
                    Picasso.with(mContext).load(mFullUser.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).fit().centerCrop().into(image);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.fill_host_infor_item, null);
                TextView titleText = (TextView) convertView.findViewById(R.id.fill_host_item_title);
                TextView contentText = (TextView) convertView.findViewById(R.id.fill_host_item_content);
                titleText.setText(titles[position]);
                if(mFullUser != null)
                    switch (position){
                        case 0 :break ;
                        case 1 :
                            contentText.setText(mFullUser.getNickname().isPresent()? mFullUser.getNickname().get() : getString(R.string.un_setting));
                            break ;
                        case 2 :
                            if(mFullUser.getProfile()!= null ){
                                if (mFullUser.getProfile().getGender().equals("female")) {
                                    contentText.setText(getString(R.string.gender_female));
                                } else if (mFullUser.getProfile().getGender().equals("male")) {
                                    contentText.setText(getString(R.string.gender_male));
                                } else {
                                    contentText.setText(getString(R.string.gender_keep_secret));
                                }
                            }
                            break ;
                        case 3 :
                            if(mFullUser.getProfile()!= null)
                                contentText.setText(dbMng.getLocationByIds(mFullUser.getProfile().getProvinceId() , mFullUser.getProfile().getCityId()));
                            break ;
                        case 4 :
                            if(mFullUser .getContactInfo().isPresent()
                                && mFullUser.getContactInfo().get().getEmail()!=null
                                && !mFullUser.getContactInfo().get().getEmail().equals(""))
                                contentText.setText(mFullUser.getContactInfo().get().getEmail());
                            break ;
                        case 5 :
                            if(mFullUser.getContactInfo().isPresent()
                                &&mFullUser.getContactInfo().get().getPhoneNumber()!=null
                                &&!mFullUser.getContactInfo().get().getPhoneNumber().equals(""))
                                contentText.setText(mFullUser.getContactInfo().get().getPhoneNumber());
                            break ;
                    }
            }
            return convertView;
        }
    };



    private void fetchMyInfo(LambdaExpression fetchCallBack){
        Router.getAccountModule().fetchMyInfo(new OneParameterExpression<User>() {
            @Override
            public void action(User user) {
                mFullUser = user;
                mTmpFullUser = mFullUser.clone();
                mAdapter.notifyDataSetChanged();
                if (fetchCallBack != null)
                    fetchCallBack.action();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                if (s.contains("401")) {
                    tryLoginFirst(new LambdaExpression() {
                        @Override
                        public void action() {
                            fetchMyInfo(null);
                        }
                    }, null);
                }
                Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void changeUserAvatar() {
        mAvatar = UpyunUploadTask.avatarPath(mPreferMng.getStoredUserId(), "jpg");
        new UpyunUploadTask(getCacheDir().getPath() + File.separator + "head.jpg"
                , mAvatar, null, new CompleteListener() {
            @Override
            public void result(boolean isComplete, String s, String s2) {
                if (isComplete) {
                    Router.getAccountModule().updateAvatar(mAvatar, new LambdaExpression() {
                        @Override
                        public void action() {
                            if (Router.getInstance().getCurrentUser().isPresent()) {
                                Router.getInstance().getCurrentUser().get().setAvatarURL(mAvatar);
                                mPreferMng.saveAvatarPath(Router.getInstance().getCurrentUser().get().getAvatarURLSmall().get());
                                mFullUser.setAvatarURL(Router.getInstance().getCurrentUser().get().getAvatarURLSmall().get());
                                mTmpFullUser = mFullUser.clone();
//                                mAdapter.getView(0, null, mListView);
                            }
                            mAdapter.notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.setAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
                            sendBroadcast(intent);
                            finalizeDialog();
                        }
                    }, new LambdaExpression() {
                        @Override
                        public void action() {
                            Toast.makeText(mContext, getString(R.string.error_upload), Toast.LENGTH_LONG).show();
                            finalizeDialog();
                        }
                    });
                } else {
                    Toast.makeText(mContext, getString(R.string.error_upload), Toast.LENGTH_LONG).show();
                }
                finalizeDialog();
            }
        }).execute();
    }
}
