package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
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

/**
 * Created by xiaqibo on 2015/4/22.
 */
@ContentView(R.layout.activity_edit_info)
public class EditUserInfoActivity extends BaseActivity {
    public static final int CHOOSE_HEAD_IMAGE_REQ = 1001;
    private View.OnClickListener mHeadListener = (View v) -> {
        Intent intent = new Intent();
        intent.setClass(mContext, ImageChooseActivity.class);
        startActivityForResult(intent, CHOOSE_HEAD_IMAGE_REQ);
    };
    @ViewInject(R.id.edit_info_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.edit_info_user_head)
    private ImageView mUserHead = null;
    @ViewInject(R.id.edit_info_base_info_lv)
    private ListView mBaseInfoList = null;
    @ViewInject(R.id.edit_info_crazy_experience_lv)
    private ListView mExperienceList = null;
    @ViewInject(R.id.edit_info_favor_gv)
    private GridView mFavorGridView = null;
    private BaseAdapter mFavorAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    };
    private BaseAdapter mExperienceAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    };
    private BaseAdapter mBaseInfoAdapter = new BaseAdapter() {
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

            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_base_user_info_item, null);
            TextView keyText = (TextView) convertView.findViewById(R.id.edit_base_info_item_key);
            TextView valueText = (TextView) convertView.findViewById(R.id.edit_base_info_item_value);
            switch (position) {
                case 0:
                    keyText.setText(getString(R.string.nick_name));
                    break;
                case 1:
                    keyText.setText(getString(R.string.gender));
                    break;
                case 2:
                    keyText.setText(getString(R.string.birthday));
                    break;
                case 3:
                    keyText.setText(getString(R.string.residence));
                    break;
            }
//            valueText.setOnClickListener(new BaseInfoListener(position));
            keyText.setFocusable(false);
            valueText.setFocusable(false);
            return convertView;
        }
    };
    private AdapterView.OnItemClickListener mUserInfoItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    createHoneyNameDialog();
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
                default:
                    break;
            }
        }
    };

    private static void setTextColorBlack(ViewGroup v) {
        int count = v.getChildCount();
        for (int i = 0; i < count; i++) {
            View c = v.getChildAt(i);
            if (c instanceof ViewGroup) {
                setTextColorBlack((ViewGroup) c);
            } else if (c instanceof TextView) {
                ((TextView) c).setTextColor(Color.BLACK);
            }
        }
    }

    private Date mSelectedDate = null;

    private void createBirthdayDialog() {
        View birthdayChooseView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.birthday_picker_dialog, null);
        DatePicker datePicker = (DatePicker) birthdayChooseView.findViewById(R.id.birthday_picker);
        Date date = new Date();
        DatePickerDialog dia = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }
        }, date.getYear() + 1900, date.getMonth(), date.getDate());
        dia.show();
        dia.getDatePicker().setSpinnersShown(true);
        dia.getDatePicker().setCalendarViewShown(false);


//        datePicker.init(date.getYear()+1900,date.getMonth(),date.getDate(),new DatePicker.OnDateChangedListener() {
//            @Override
//            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//               // setTextColorBlack(datePicker) ;
//                mSelectedDate = new Date() ;
//                mSelectedDate.setYear(year);
//                mSelectedDate.setMonth(monthOfYear);
//                mSelectedDate.setDate(dayOfMonth);
//            }
//        });
//       // setTextColorBlack(datePicker) ;
//        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserInfoActivity.this) ;
//        builder.setView(birthdayChooseView).setPositiveButton("queding",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(mContext,"year ="+mSelectedDate.getYear() + "month=" +mSelectedDate.getMonth() + "date="+mSelectedDate.getDate(),Toast.LENGTH_SHORT).show();
//            }
//        }).create().show();
    }

    private void createGenderDialog() {
//        View genderChooseView = LayoutInflater.from(getApplicationContext()).inflate(
//                R.layout.gender_choose_dialog, null);
//        ListView genderList =(ListView) genderChooseView.findViewById(R.id.lv_gender) ;
//        genderList.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return 3;
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return position;
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = LayoutInflater.from(mContext).inflate(R.layout.gender_item, null) ;
//                CheckBox checkBox =(CheckBox) view.findViewById(R.id.gender_item_box) ;
//                return view;
//            }
//        });
//       new AlertDialog.Builder(EditUserInfoActivity.this).setView(genderChooseView).create().show();
        final String[] array = getResources().getStringArray(R.array.genders);
        AlertDialog alertDialog = new AlertDialog.Builder(EditUserInfoActivity.this).setSingleChoiceItems(array, 0, (DialogInterface dialog, int which) -> {
            dialog.dismiss();
            doChangeGender(array[which]);
        }).setTitle(getString(R.string.gender)).create();
        alertDialog.show();
    }


    private void createHoneyNameDialog() {
        forwardTo(NickNameInputActivity.class);
    }

    private void doChangeNickName(String newName) {
    }

    private void doChangeGender(String gender) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBaseInfoList.setAdapter(mBaseInfoAdapter);
        mExperienceList.setAdapter(mExperienceAdapter);
        mFavorGridView.setAdapter(mFavorAdapter);
        mBaseInfoList.setItemsCanFocus(true);
        mBaseInfoList.setOnItemClickListener(mUserInfoItemListener);
        mUserHead.setOnClickListener(mHeadListener);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_HEAD_IMAGE_REQ && resultCode == RESULT_OK) {
            doChangeUserAvatar();
//            String headPath = getCacheDir().getPath() + File.separator + "head.jpg" ;
//            Bitmap bm = BitmapFactory.decodeFile(headPath);
//            mUserHead.setImageBitmap(bm);
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

    private class BaseInfoListener implements View.OnClickListener {
        private int position;

        public BaseInfoListener(int position) {
            this.position = position;
        }

        public void onClick(View view) {
            switch (position) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;

            }
            Toast.makeText(getApplicationContext(), "item" + position, Toast.LENGTH_SHORT).show();
        }
    }

}
