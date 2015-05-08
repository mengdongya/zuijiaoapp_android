package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.model.common.Restaurant;
import com.zuijiao.android.zuijiao.network.Router;

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
    private Handler mHandler = new Handler();
    private String mRestaurantName = null;
    int i = 1;
    private Runnable mRun = () -> {
        Router.getCommonModule().restaurantSearch(mRestaurantName, 20, restaurants -> {
//            mAutoSearchList.clear();
            if (restaurants == null || restaurants.getRestaurants() == null) {
                mAutoSearchList.add("result" + i++);
                mAdapter.notifyDataSetChanged();
                return;
            }
            for (Restaurant restaurant : restaurants.getRestaurants()) {
//                         mAutoSearchList.add(restaurant.get) ;
            }
        }, errorMsg -> {

        });
    };

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
                mHandler.removeCallbacks(mRun);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                mEditorShadow.setText(text);
//                mAutoSearchList.add("result" + count);
//                mAdapter.notifyDataSetChanged();
                mRestaurantName = s.toString();
                mHandler.postDelayed(mRun, 500);
                if (text == null || text.equals("")) {
                    mCreateNewBtn.setVisibility(View.INVISIBLE);
                } else {
                    mCreateNewBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCreateNewBtn.setOnClickListener(mCreateListener);
        mSearchResultList.setOnItemClickListener(onItemClickListener);
        mCreateNewBtn.setVisibility(View.INVISIBLE);
    }

    private AdapterView.OnItemClickListener onItemClickListener = (AdapterView<?> parent, View view, int position, long id) -> {
        setResultOk();
    };
    private View.OnClickListener mCreateListener = (View view) -> {
        setResultOk();
    };

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRun);
    }

    private void setResultOk() {
        Intent intent = new Intent();
        intent.putExtra("position", mRestaurantName);
        setResult(RESULT_OK, intent);
        finish();
    }
    //    private class SimpleListAdapter extends ArrayAdapter{
//
//    }
}
