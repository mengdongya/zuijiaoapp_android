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
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.utils.UpyunUploadTask;

import java.io.File;
import java.util.Date;

import static android.widget.AdapterView.OnItemClickListener;

/**
 * Created by xiaqibo on 2015/4/22.
 */
@ContentView(R.layout.activity_edit_info)
public class EditUserInfoActivity extends BaseActivity {
    public static final int CHOOSE_HEAD_IMAGE_REQ = 1001;

    public static final int VERIFY_PHONE_NUMBER_REQ = 1002;
    public static final int LANGUAGE_CHOOSE_REQ = 1003;
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
    private BaseAdapter mFavorAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 4;
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
            return LayoutInflater.from(mContext).inflate(R.layout.share_item, null);
        }
    };
    private OnItemClickListener mUserInfoItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    createGeneralEditTextDialog(getString(R.string.nick_name), getString(R.string.nick_name_hint), 1, 15, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doChangeNickName("");
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
                    startActivityForResult(intent, 2000);
                    break;
                case 4:
                    createGeneralEditTextDialog(getString(R.string.personal_introduction), getString(R.string.introduction_hint), 5, 100, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            doChangeNickName("");
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
                    createGeneralEditTextDialog(getString(R.string.email), getString(R.string.input_email), 1, 0, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                    createGeneralEditTextDialog(getString(R.string.industry), getString(R.string.industry_hint), 4, 100, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    break;
                case 1:
                    createGeneralEditTextDialog(getString(R.string.interest_hobby), getString(R.string.interest_hobby_hint), 4, 100, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    break;
                case 2:
                    createGeneralEditTextDialog(getString(R.string.education), getString(R.string.education_hint), 4, 100, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    break;
                case 3:
                    Intent intent = new Intent();
                    intent.setClass(mContext, LanguagesChooseActivity.class);
                    startActivityForResult(intent, LANGUAGE_CHOOSE_REQ);
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnClickListener mHeadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, ImageChooseActivity.class);
            startActivityForResult(intent, CHOOSE_HEAD_IMAGE_REQ);
        }
    };


    private void createGeneralEditTextDialog(String title, String etHint, int lineNum, int maxText, DialogInterface.OnClickListener finishListener) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.nick_name_input_dialog, null);
        TextView textView = (TextView) contentView.findViewById(R.id.tv_et_watcher);
        EditText editText = (EditText) contentView.findViewById(R.id.et_nick_name_input);
        if (maxText == 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(String.format(getString(R.string.nick_name_watcher), 0, maxText));
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxText)});
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textView.setText(String.format(getString(R.string.nick_name_watcher), s.length(), maxText));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            if (lineNum > 1) {
                editText.setHorizontallyScrolling(false);
            }
        }
//        editText.setLines(lineNum);
        editText.setHint(etHint);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        AlertDialog alertDialog = new AlertDialog.Builder(EditUserInfoActivity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save), finishListener).create();
        alertDialog.show();
        new Handler().postDelayed(() -> {
            InputMethodManager inputManager =
                    (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }, 200);
    }

    private void doChangeEmail(String newEmail) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBaseInfoList.setAdapter(new GeneralUserInfoAdapter(getResources().getStringArray(R.array.user_base_info_title), null));
        setListViewHeightBasedOnChildren(mBaseInfoList);
        mFavorGridView.setAdapter(mFavorAdapter);
//        setListViewHeightBasedOnChildren(mFavorGridView);
        mContactInfoList.setAdapter(new GeneralUserInfoAdapter(getResources().getStringArray(R.array.user_contact_info_title), null));
        mContactInfoList.setOnItemClickListener(mContactListener);
        setListViewHeightBasedOnChildren(mContactInfoList);
        mDetailInfoList.setAdapter(new GeneralUserInfoAdapter(getResources().getStringArray(R.array.user_detail_info_title), null));
        mDetailInfoList.setOnItemClickListener(mDetailListener);
        setListViewHeightBasedOnChildren(mDetailInfoList);
        mBaseInfoList.setItemsCanFocus(true);
        mBaseInfoList.setOnItemClickListener(mUserInfoItemListener);
        mUserHead.setOnClickListener(mHeadListener);
    }

    private void createBirthdayDialog() {
        View birthdayChooseView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.birthday_picker_dialog, null);
        DatePicker datePicker = (DatePicker) birthdayChooseView.findViewById(R.id.birthday_picker);
        new AlertDialog.Builder(EditUserInfoActivity.this).setView(birthdayChooseView).setTitle(getString(R.string.birthday)).setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                doChangeBirthday() ;
            }
        }).create().show();
    }

    private void createGenderDialog() {
        final String[] array = getResources().getStringArray(R.array.genders);
        AlertDialog alertDialog = new AlertDialog.Builder(EditUserInfoActivity.this).setSingleChoiceItems(array, 0, (DialogInterface dialog, int which) -> {
            dialog.dismiss();
            doChangeGender(array[which]);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_HEAD_IMAGE_REQ && resultCode == RESULT_OK) {
            doChangeUserAvatar();
        }
    }

    private void doChangeUserAvatar() {
        createDialog();
        String avatarUri = UpyunUploadTask.avatarPath(Router.getInstance().getCurrentUser().get().getIdentifier(), "jpg");
        new UpyunUploadTask(getCacheDir().getPath() + File.separator + "head.jpg"
                , avatarUri
                , (long transferedBytes, long totalBytes) -> {
        }
                , (boolean isComplete, String result, String error) -> {
            if (isComplete) {
                Router.getInstance().getCurrentUser().get().setAvatarURL(avatarUri);
                mPreferMng.saveAvatarPath(UpyunUploadTask.avatarPath(avatarUri));
                createDialog();
                Router.getAccountModule().updateAvatar(avatarUri, () -> {
                    Picasso.with(mContext)
                            .load(avatarUri)
                            .placeholder(R.drawable.default_user_head)
                            .into(mUserHead);
                    Intent intent = new Intent();
                    intent.setAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
                    sendBroadcast(intent);
                    finallizeDialog();
                }, () -> {
                    Toast.makeText(mContext, getString(R.string.error_upload), Toast.LENGTH_LONG).show();
                    finallizeDialog();
                });
            } else {
                Toast.makeText(mContext, getString(R.string.error_upload), Toast.LENGTH_LONG).show();
            }
            finallizeDialog();
        }
        ).execute();
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
                + (gdView.getVerticalSpacing() * (listAdapter.getCount()));

    }

    private class GeneralUserInfoAdapter extends BaseAdapter {
        private String[] titles = null;
        private String[] contents = null;

        public GeneralUserInfoAdapter(String[] titles, String[] contents) {
            super();
            this.titles = titles;
            this.contents = contents;
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
            valueText.setText(getString(R.string.un_setting));
            return convertView;
        }
    }


}
