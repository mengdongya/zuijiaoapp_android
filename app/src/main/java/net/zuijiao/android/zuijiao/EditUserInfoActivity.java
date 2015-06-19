package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.upyun.block.api.listener.CompleteListener;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.model.common.TasteTag;
import com.zuijiao.android.zuijiao.model.user.ContactInfo;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.utils.UpyunUploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.widget.AdapterView.OnItemClickListener;

/**
 * Created by xiaqibo on 2015/4/22.
 */
@ContentView(R.layout.activity_edit_info)
public class EditUserInfoActivity extends BaseActivity {
    public static final int CHOOSE_HEAD_IMAGE_REQ = 1001;
    private View.OnClickListener mHeadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, ImageChooseActivity.class);
            startActivityForResult(intent, CHOOSE_HEAD_IMAGE_REQ);
        }
    };
    public static final int VERIFY_PHONE_NUMBER_REQ = 1002;
    public static final int LANGUAGE_CHOOSE_REQ = 1003;
    public static final int LOCATION_CHOOSE_REQ = 1004;
    private static final int TASTE_TAG_REQ = 1005;
    private OnItemClickListener mTasteItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(mContext, TasteActivity.class);
            if (mTmpFullUser.getProfile().getTasteTags().isPresent())
                intent.putStringArrayListExtra("my_taste_tag", (ArrayList) mTmpFullUser.getProfile().getTasteTags().get());
            startActivityForResult(intent, TASTE_TAG_REQ);
        }
    };
    private static final int BASE_INFO_ADAPTER = 0;
    private static final int CONTACT_INFO_ADAPTER = 1;
    private static final int DETAIL_INFO_ADAPTER = 2;
    private final static String EMAIL_REGEX = "^[\\w_\\.+-]*[\\w_\\.-]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    @ViewInject(R.id.edit_info_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.edit_info_user_head)
    private ImageView mUserHead = null;
    @ViewInject(R.id.edit_info_base_info_lv)
    private ListView mBaseInfoList = null;
    @ViewInject(R.id.edit_info_contact_lv)
    private ListView mContactInfoList = null;
    @ViewInject(R.id.edit_info_detail_lv)
    private ListView mDetailInfoList = null;
    @ViewInject(R.id.edit_info_favor_gv)
    private GridView mFavorGridView = null;
    private Date mSelectedDate = null;
    private int[] mBaseInfoTitles = {R.string.nick_name, R.string.gender, R.string.birthday, R.string.residence, R.string.personal_introduction};
    private GeneralUserInfoAdapter mBaseInfoAdapter = null;
    private GeneralUserInfoAdapter mContactInfoAdapter = null;
    private GeneralUserInfoAdapter mDetailInfoAdapter = null;
    private User mFullUser;
    //copy of full user
    private User mTmpFullUser;
    private String etAvatar = null;
    private String etAvatarPath = null;
    private String etNickName = null;
    private String etGender = null;
    private Date etBirth = null;
    private int etProvinceId = -1, etCityId = -1;
    private String etSelfIntroduction = null;
    private ArrayList<String> etTasteTag = null;
    private String etEmail = null;
    private String etPhone = null;
    private String etCareer = null;
    private String etHobby = null;
    private String etEducation = null;
    private List<String> etLanguage = null;
    private String mCurrentEdit = null;
    private OnItemClickListener mUserInfoItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    createGeneralEditTextDialog(mFullUser.getNickname().get(), getString(R.string.nick_name), getString(R.string.nick_name_hint), 1, 15, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (mCurrentEdit != null && !mCurrentEdit.equals(mFullUser.getNickname())) {
                                mTmpFullUser.setNickname(mCurrentEdit);
                                updateUserInfo();
                            }
                            mCurrentEdit = null;
                        }
                    });
                    break;
                case 1:
                    createGenderDialog();
                    break;
                case 2:
                    createBirthdayDialog();
                    break;
                case 3:
                    Intent intent = new Intent();
                    intent.setClass(EditUserInfoActivity.this, LocationActivity.class);
                    startActivityForResult(intent, LOCATION_CHOOSE_REQ);
                    break;
                case 4:
                    String introduction = null;
                    if (mFullUser.getProfile().getSelfIntroduction().isPresent())
                        introduction = mFullUser.getProfile().getSelfIntroduction().get();
                    createGeneralEditTextDialog(introduction, getString(R.string.personal_introduction), getString(R.string.introduction_hint), 5, 100, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if ((mCurrentEdit == null || mCurrentEdit.equals(""))
                                    || mFullUser.getProfile().getSelfIntroduction().isPresent()
                                    && mFullUser.getProfile().getSelfIntroduction().get().equals(mCurrentEdit)) {
                                mCurrentEdit = null;
                                return;
                            }
                            mTmpFullUser.getProfile().setSelfIntroduction(mCurrentEdit);
                            updateUserInfo();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };
    private OnItemClickListener mContactListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
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
                            dialog.dismiss();
                            if (mFullUser.getContactInfo().isPresent() && mCurrentEdit.equals(mFullUser.getContactInfo().get().getEmail())) {
                                mCurrentEdit = null;
                                return;
                            }
                            if (!mTmpFullUser.getContactInfo().isPresent()) {
                                mTmpFullUser.setContactInfo(new ContactInfo());
                            }
                            mTmpFullUser.getContactInfo().get().setEmail(mCurrentEdit);
                            updateUserInfo();
                        }
                    });
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setClass(mContext, VerifyPhoneNumActivity.class);
                    startActivityForResult(intent, VERIFY_PHONE_NUMBER_REQ);
                    break;
            }
        }
    };
    private OnItemClickListener mDetailListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    String industry = null;
                    if (mFullUser.getProfile().getCareer().isPresent()) {
                        industry = mFullUser.getProfile().getCareer().get();
                    }
                    createGeneralEditTextDialog(industry, getString(R.string.industry), getString(R.string.industry_hint), 4, 100, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (mCurrentEdit == null || mCurrentEdit.equals("")) {
                                return;
                            }
                            if (mFullUser.getProfile() != null
                                    && mFullUser.getProfile().getCareer().isPresent()
                                    && mFullUser.getProfile().getCareer().get().equals(mCurrentEdit)) {
                                mCurrentEdit = null;
                                return;
                            }
                            mTmpFullUser.getProfile().setCareer(mCurrentEdit);
                            updateUserInfo();
                        }
                    });
                    break;
                case 1:
                    String hobby = null;
                    if (mFullUser.getProfile().getHobby().isPresent())
                        hobby = mFullUser.getProfile().getHobby().get();
                    createGeneralEditTextDialog(hobby, getString(R.string.interest_hobby), getString(R.string.interest_hobby_hint), 4, 100, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (mCurrentEdit == null || mCurrentEdit.equals("")) {
                                return;
                            }
                            if (mFullUser.getProfile() != null
                                    && mFullUser.getProfile().getHobby().isPresent()
                                    && mFullUser.getProfile().getHobby().get().equals(mCurrentEdit)) {
                                mCurrentEdit = null;
                                return;
                            }
                            mTmpFullUser.getProfile().setHobby(mCurrentEdit);
                            updateUserInfo();
                        }
                    });
                    break;
                case 2:
                    String education = null;
                    if (mFullUser.getProfile().getEducationBackground().isPresent()) {
                        education = mFullUser.getProfile().getEducationBackground().get();
                    }
                    createGeneralEditTextDialog(education, getString(R.string.education), getString(R.string.education_hint), 4, 100, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (mCurrentEdit == null || mCurrentEdit.equals("")) {
                                return;
                            }
                            if (mFullUser.getProfile() != null
                                    && mFullUser.getProfile().getEducationBackground().isPresent()
                                    && mFullUser.getProfile().getEducationBackground().get().equals(mCurrentEdit)) {
                                mCurrentEdit = null;
                                return;
                            }
                            mTmpFullUser.getProfile().setEducationBackground(mCurrentEdit);
                            updateUserInfo();
                        }
                    });
                    break;
                case 3:
                    Intent intent = new Intent();
                    intent.setClass(mContext, LanguagesChooseActivity.class);

                    ArrayList<String> languageCode = new ArrayList<>();
                    try {
                        for (String str : mTmpFullUser.getProfile().getLanguages().get()) {
                            languageCode.add(str);
                        }
                    } catch (Exception e) {

                    }
                    if (languageCode != null && languageCode.size() != 0) {
                        intent.putExtra("selected_language", languageCode);
                    }
                    startActivityForResult(intent, LANGUAGE_CHOOSE_REQ);
                    break;
                default:
                    break;
            }
        }
    };
    private boolean bAnyInfoChanged = false;
    private BaseAdapter mFavorAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (mFullUser.getProfile() != null && mFullUser.getProfile().getTasteTags().isPresent() && mFullUser.getProfile().getTasteTags().get().size() != 0) {
                return mFullUser.getProfile().getTasteTags().get().size() + 1;
            }
            return 1;
        }

        @Override
        public String getItem(int position) {
            try {
                return mFullUser.getProfile().getTasteTags().get().get(position);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.user_info_favor_item, null);
            TextView text = (TextView) contentView.findViewById(R.id.user_info_favor_item_text);
            ImageView image = (ImageView) contentView.findViewById(R.id.user_info_favor_item_image);
            String taste = getItem(position);
            if (taste != null) {
                for (TasteTag tag : Cache.INSTANCE.tasteTags) {
                    if (tag.getName().equals(taste)) {
                        Picasso.with(mContext).load(tag.getImageURL()).into(image);
                    }
                }
                text.setText(taste);
            } else {
                image.setImageResource(R.drawable.add_taste);
                image.setBackgroundColor(Color.TRANSPARENT);
                text.setText(getString(R.string.add));
            }
            return contentView;
        }
    };
    private int mGenderCheckItem = 0;

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
        AlertDialog alertDialog = new AlertDialog.Builder(EditUserInfoActivity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save), finishListener).create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 200);
    }

    private void doChangeEmail(String newEmail) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateUserInfo() {
        createDialog();
        Router.getAccountModule().update(mTmpFullUser, new LambdaExpression() {
            @Override
            public void action() {
                bAnyInfoChanged = true;
                if (!mFullUser.getNickname().equals(mTmpFullUser.getNickname())) {
                    mPreferMng.saveNickname(mTmpFullUser.getNickname().get());
                }
                mFullUser = mTmpFullUser.clone();
                mBaseInfoAdapter.notifyDataSetChanged();
                mContactInfoAdapter.notifyDataSetChanged();
                mDetailInfoAdapter.notifyDataSetChanged();
                mFavorAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(mFavorGridView);
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

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFullUser = mFileMng.getFullUser();
        if (mFullUser == null) {
            finish();
            return;
        }
        mTmpFullUser = mFullUser.clone();
        if (mTmpFullUser.getAvatarURL().isPresent())
            Picasso.with(mContext).load(mTmpFullUser.getAvatarURL().get()).placeholder(R.drawable.default_user_head).into(mUserHead);
        mBaseInfoAdapter = new GeneralUserInfoAdapter(getResources().getStringArray(R.array.user_base_info_title), BASE_INFO_ADAPTER);
        mBaseInfoList.setAdapter(mBaseInfoAdapter);
        setListViewHeightBasedOnChildren(mBaseInfoList);
        mFavorGridView.setAdapter(mFavorAdapter);
        mFavorGridView.setOnItemClickListener(mTasteItemListener);
        setListViewHeightBasedOnChildren(mFavorGridView);
        mContactInfoAdapter = new GeneralUserInfoAdapter(getResources().getStringArray(R.array.user_contact_info_title), CONTACT_INFO_ADAPTER);
        mContactInfoList.setAdapter(mContactInfoAdapter);
        mContactInfoList.setOnItemClickListener(mContactListener);
        setListViewHeightBasedOnChildren(mContactInfoList);
        mDetailInfoAdapter = new GeneralUserInfoAdapter(getResources().getStringArray(R.array.user_detail_info_title), DETAIL_INFO_ADAPTER);
        mDetailInfoList.setAdapter(mDetailInfoAdapter);
        mDetailInfoList.setOnItemClickListener(mDetailListener);
        setListViewHeightBasedOnChildren(mDetailInfoList);
        mBaseInfoList.setItemsCanFocus(true);
        mBaseInfoList.setOnItemClickListener(mUserInfoItemListener);
        mUserHead.setOnClickListener(mHeadListener);
    }

    private void createBirthdayDialog() {
//        Router.getAccountModule().update();
        View birthdayChooseView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.birthday_picker_dialog, null);
        DatePicker datePicker = (DatePicker) birthdayChooseView.findViewById(R.id.birthday_picker);
        Date birthDate = null;
        if (mFullUser.getProfile().getBirthday().isPresent()) {
            birthDate = mFullUser.getProfile().getBirthday().get();
        }
        int year;
        int month;
        int date;
        if (birthDate == null) {
            birthDate = new Date();
        }
        year = birthDate.getYear();
        month = birthDate.getMonth();
        date = birthDate.getDate();
        datePicker.setMinDate(new Date(50, 0, 0).getTime());
        datePicker.setMaxDate(new Date().getTime());
        datePicker.init(year + 1900, month, date, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                etBirth = new Date(year - 1900, monthOfYear, dayOfMonth);
//                etBirth = String.format(getString(R.string.year_month_day) , year , monthOfYear , dayOfMonth) ;
            }
        });
        new AlertDialog.Builder(EditUserInfoActivity.this).setView(birthdayChooseView).setTitle(getString(R.string.birthday)).setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                    String birthday = String.format(getString(R.string.year_month_day) , datePicker.getYear() , datePicker.getMonth() , datePicker.getMinDate())
                dialog.dismiss();
                if (etBirth == null
                        || mFullUser.getProfile().getBirthday().isPresent()
                        && mFullUser.getProfile().getBirthday().get().getYear() == etBirth.getYear()
                        && mFullUser.getProfile().getBirthday().get().getMonth() == etBirth.getMonth()
                        && mFullUser.getProfile().getBirthday().get().getDate() == etBirth.getDate()) {
                    return;
                }
                mTmpFullUser.getProfile().setBirthday(etBirth);
                updateUserInfo();
            }
        }).create().show();
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
        AlertDialog alertDialog = new AlertDialog.Builder(EditUserInfoActivity.this).setSingleChoiceItems(array, mGenderCheckItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
                    }
                }).setTitle(getString(R.string.gender)).create();
        alertDialog.show();
    }

    private void doChangeIntroduction(String introduction) {
    }

    private void doChangeNickName(String newName) {
    }

    private void doChangeGender(String gender) {
    }

    @Override
    protected void findViews() {

    }

    @Override
    public void onBackPressed() {
        if (bAnyInfoChanged) {
            mFileMng.setFullUser(mFullUser);
            setResult(RESULT_OK);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_HEAD_IMAGE_REQ && resultCode == RESULT_OK) {
            doChangeUserAvatar();
        } else if (requestCode == LOCATION_CHOOSE_REQ && resultCode == RESULT_OK) {
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
        } else if (requestCode == VERIFY_PHONE_NUMBER_REQ && resultCode == RESULT_OK) {
            if (!mTmpFullUser.getContactInfo().isPresent()) {
                mTmpFullUser.setContactInfo(new ContactInfo());
            }
            mTmpFullUser.getContactInfo().get().setPhoneNumber(data.getStringExtra("verified_phone_num"));
            mFullUser = mTmpFullUser.clone();
            mContactInfoAdapter.notifyDataSetChanged();
        } else if (requestCode == TASTE_TAG_REQ && resultCode == RESULT_OK) {
            etTasteTag = data.getStringArrayListExtra("my_taste_tag");
            mTmpFullUser.getProfile().setTasteTags(etTasteTag);
            updateUserInfo();
        } else if (requestCode == LANGUAGE_CHOOSE_REQ && resultCode == RESULT_OK) {
            etLanguage = data.getStringArrayListExtra("selected_language");
            mTmpFullUser.getProfile().setLanguages(etLanguage);
            updateUserInfo();
        }
    }

    private void doChangeUserAvatar() {
        createDialog();
//        String avatarUri = UpyunUploadTask.avatarPath(mPreferMng.getStoredUserId(), "jpg");
        etAvatar = UpyunUploadTask.avatarPath(mPreferMng.getStoredUserId(), "jpg");
        new UpyunUploadTask(getCacheDir().getPath() + File.separator + "head.jpg"
                , etAvatar
                , null
                , new CompleteListener() {
            @Override
            public void result(boolean isComplete, String s, String s2) {
                if (isComplete) {
                    createDialog();
                    Router.getAccountModule().updateAvatar(etAvatar, new LambdaExpression() {
                        @Override
                        public void action() {
                            bAnyInfoChanged = true;
                            if (Router.getInstance().getCurrentUser().isPresent()) {
                                Router.getInstance().getCurrentUser().get().setAvatarURL(etAvatar);
                                mPreferMng.saveAvatarPath(Router.getInstance().getCurrentUser().get().getAvatarURL().get());
                                mFullUser.setAvatarURL(Router.getInstance().getCurrentUser().get().getAvatarURL().get());
                                Picasso.with(mContext)
                                        .load(Router.getInstance().getCurrentUser().get().getAvatarURL().get())
                                        .placeholder(R.drawable.default_user_head)
                                        .into(mUserHead);
                            }
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()));

    }

    public void setListViewHeightBasedOnChildren(GridView gdView) {
        ListAdapter listAdapter = gdView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i += 5) {
            View listItem = listAdapter.getView(i, null, gdView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gdView.getLayoutParams();
        params.height = totalHeight
                + (gdView.getVerticalSpacing() * (listAdapter.getCount() / 5));

    }

    private String checkSet(String str) {
        if (str != null) {

        }
        return getString(R.string.un_setting);
    }

    private class GeneralUserInfoAdapter extends BaseAdapter {
        private String[] titles = null;
        //        private String[] contents = null;
        private int infoType = -1;

        public GeneralUserInfoAdapter(String[] titles, int type) {
            super();
            this.titles = titles;
            this.infoType = type;
        }

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
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_base_user_info_item, null);
            TextView keyText = (TextView) convertView.findViewById(R.id.edit_base_info_item_key);
            TextView valueText = (TextView) convertView.findViewById(R.id.edit_base_info_item_value);
            keyText.setText(titles[position]);
            keyText.setFocusable(false);
            switch (infoType) {
                case BASE_INFO_ADAPTER:
                    switch (position) {
                        case 0:
                            if (mFullUser.getNickname().isPresent()) {
                                valueText.setText(mFullUser.getNickname().get());
                            } else {
                                valueText.setText(getString(R.string.un_setting));
                            }
                            break;
                        case 1:
                            if (mFullUser.getProfile() != null) {
                                if (mFullUser.getProfile().getGender().equals("female")) {
                                    valueText.setText(getString(R.string.gender_female));
                                } else if (mFullUser.getProfile().getGender().equals("male")) {
                                    valueText.setText(getString(R.string.gender_male));
                                } else {
                                    valueText.setText(getString(R.string.gender_keep_secret));
                                }
                            } else {
                                valueText.setText(getString(R.string.gender_keep_secret));
                            }
                            break;
                        case 2:
                            if (mFullUser.getProfile() != null
                                    && mFullUser.getProfile().getBirthday().isPresent()) {
                                Date date = mFullUser.getProfile().getBirthday().get();
                                String formatBirth = String.format(getString(R.string.year_month_day), date.getYear() + 1900, date.getMonth() + 1, date.getDate());
                                valueText.setText(formatBirth);
                            } else {
                                valueText.setText(getString(R.string.un_setting));
                            }
                            break;
                        case 3:
                            if (mFullUser.getProfile() != null) {
                                int cityId = mFullUser.getProfile().getCityId();
                                int provinceId = mFullUser.getProfile().getProvinceId();
                                String location = dbMng.getLocationByIds(provinceId, cityId);
                                if (location != null && !location.equals(""))
                                    valueText.setText(location);
                                else
                                    valueText.setText(getString(R.string.un_setting));
                            } else
                                valueText.setText(getString(R.string.un_setting));
                            break;
                        case 4:
                            if (mFullUser.getProfile() != null) {
                                Optional<String> introduction = mFullUser.getProfile().getSelfIntroduction();
                                if (introduction.isPresent() && !introduction.get().equals("")) {
                                    valueText.setText(introduction.get());
                                } else {
                                    valueText.setText(getString(R.string.un_setting));
                                }
                            } else {
                                valueText.setText(getString(R.string.un_setting));
                            }
                            break;
                    }
//                    valueText.setText();
                    break;
                case CONTACT_INFO_ADAPTER:
                    if (mFullUser.getContactInfo().isPresent()) {
                        switch (position) {
                            case 0:
                                String email = mFullUser.getContactInfo().get().getEmail();
                                if (email != null && !email.equals("")) {
                                    valueText.setText(email);
                                } else {
                                    valueText.setText(getString(R.string.un_setting));
                                }
                                break;
                            case 1:
                                String phone = mFullUser.getContactInfo().get().getPhoneNumber();
                                if (phone != null && !phone.equals("")) {
                                    valueText.setText(phone);

                                } else {
                                    valueText.setText(getString(R.string.un_setting));
                                }
                                break;
                        }

                    } else
                        valueText.setText(getString(R.string.un_setting));
                    break;
                case DETAIL_INFO_ADAPTER:
                    if (mFullUser.getProfile() != null) {
                        switch (position) {
                            case 0:
                                Optional<String> career = mFullUser.getProfile().getCareer();
                                if (career.isPresent() && !career.get().equals("")) {
                                    valueText.setText(career.get());
                                } else {
                                    valueText.setText(getString(R.string.un_setting));
                                }
                                break;
                            case 1:
                                Optional<String> hobby = mFullUser.getProfile().getHobby();
                                if (hobby.isPresent() && !hobby.get().equals("")) {
                                    valueText.setText(hobby.get());
                                } else {
                                    valueText.setText(getString(R.string.un_setting));
                                }
                                break;

                            case 2:
                                Optional<String> education = mFullUser.getProfile().getEducationBackground();
                                if (education.isPresent() && !education.get().equals("")) {
                                    valueText.setText(education.get());
                                } else {
                                    valueText.setText(getString(R.string.un_setting));
                                }
                                break;
                            case 3:
                                Optional<List<String>> language = mFullUser.getProfile().getLanguages();
                                if (language.isPresent() && language.get().size() != 0) {
//                                    String languageStr = "";
//                                    for (String lan : language.get()) {
//                                        languageStr += lan;
//                                    }
                                    valueText.setText(String.format(getString(R.string.language_count), language.get().size()));
                                } else {
                                    valueText.setText(getString(R.string.un_setting));
                                }
                        }
                    } else {
                        valueText.setText(getString(R.string.un_setting));
                    }
                    break;

            }
            if (valueText.getText().toString().equals(getString(R.string.un_setting))) {
                valueText.setTextColor(getResources().getColor(R.color.bg_light_gray));
            }
            return convertView;
        }
    }
}
