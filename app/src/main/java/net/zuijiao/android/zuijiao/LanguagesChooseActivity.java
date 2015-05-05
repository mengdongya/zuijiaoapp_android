package net.zuijiao.android.zuijiao;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by xiaqibo on 2015/5/5.
 */
@ContentView(R.layout.activity_language_choose)
public class LanguagesChooseActivity extends BaseActivity {

    @ViewInject(R.id.language_choose_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.language_choose_list)
    private ListView mList = null;
    private String[] mLanguages = null;
    protected BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mLanguages.length;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.location_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.location_lv_item_text);
            ImageView image = (ImageView) convertView.findViewById(R.id.location_lv_item_image);
            textView.setText(mLanguages[position]);
            return convertView;
        }
    };

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLanguages = getResources().getStringArray(R.array.languages);
        mList.setAdapter(mAdapter);
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
