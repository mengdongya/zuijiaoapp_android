package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.Language;
import com.zuijiao.android.zuijiao.model.common.Languages;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaqibo on 2015/5/5.
 */
@ContentView(R.layout.activity_language_choose)
public class LanguagesChooseActivity extends BaseActivity {

    @ViewInject(R.id.language_choose_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.language_choose_list)
    private ListView mList = null;
    private List<Language> mLanguages = null;
    private ArrayList<String> selectedCode = null;
    protected BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (mLanguages == null)
                return 0;
            return mLanguages.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.language_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.language_lv_item_text);
            ImageView image = (ImageView) convertView.findViewById(R.id.language_lv_item_image);
            if (selectedCode.contains(mLanguages.get(position).getCode())) {
                image.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.GONE);
            }
            textView.setText(mLanguages.get(position).getName());
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
//        mLanguages = Cache.INSTANCE.
        selectedCode = mTendIntent.getStringArrayListExtra("selected_language");
        if (selectedCode == null) {
            selectedCode = new ArrayList<>();
        }
        mLanguages = Cache.INSTANCE.languages;
        if (mLanguages == null) {
            Router.getCommonModule().languages(new OneParameterExpression<Languages>() {
                @Override
                public void action(Languages languages) {
                    mLanguages = languages.getLanguages();
                    mList.setAdapter(mAdapter);
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                }
            });
        }
//        mLanguages = getResources().getStringArray(R.array.languages);
        mList.setAdapter(mAdapter);
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedCode.contains(mLanguages.get(position).getCode())) {
                    selectedCode.remove(mLanguages.get(position).getCode());
                } else {
                    selectedCode.add(mLanguages.get(position).getCode());
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.language_choose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.language_choose_menu) {
            Intent intent = new Intent();
            intent.putExtra("selected_language", selectedCode);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
