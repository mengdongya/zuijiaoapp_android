package net.zuijiao.android.zuijiao;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by xiaqibo on 2015/4/30.
 */
@ContentView(R.layout.activity_edit_location)
public class EditPositionActivity extends BaseActivity {
    @ViewInject(R.id.edit_location_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.edit_location)
    private EditText mLocationEditor = null;
    @ViewInject(R.id.edit_location_editor_shadow_text)
    private TextView mEditorShadow = null;
    @ViewInject(R.id.edit_location_create_btn)
    private Button mCreateNewBtn = null;
    @ViewInject(R.id.edit_location_for_you_text)
    private TextView mFindListTitle = null;
    @ViewInject(R.id.edit_location_list)
    private ListView mSearchResultList = null;
    private ArrayAdapter<String> mAdapter = null;
    private ArrayList<String> mAutoSearchList = new ArrayList<>();

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mAutoSearchList);
        mSearchResultList.setAdapter(mAdapter);
        mLocationEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditorShadow.setText(s.toString());
                mAutoSearchList.add("result" + count);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


//    private class SimpleListAdapter extends ArrayAdapter{
//
//    }
}
