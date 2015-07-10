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
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.Restaurant;
import com.zuijiao.android.zuijiao.model.common.Restaurants;
import com.zuijiao.android.zuijiao.network.Router;

import java.util.ArrayList;

/**
 * edit the location where the gourmet can eaten ;
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
    /**
     * auto fit locations by user input ,
     */
    private Runnable restaurantSearchTask = new Runnable() {
        @Override
        public void run() {
            Router.getCommonModule().restaurantSearch(mRestaurantName, 20, new OneParameterExpression<Restaurants>() {
                @Override
                public void action(Restaurants restaurants) {
                    mAutoSearchList.clear();
                    if (restaurants == null || restaurants.getRestaurants() == null) {
                        //do nothing
                    } else for (Restaurant restaurant : restaurants.getRestaurants()) {
                        mAutoSearchList.add(restaurant.getAddress());
                    }
                    mAdapter.notifyDataSetChanged();
                    return;
                }
            }, null);
        }
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
        //listen to the edited text and auto research restaurant
        mLocationEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mHandler.removeCallbacks(restaurantSearchTask);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                mEditorShadow.setText(text);
//                mAutoSearchList.add("result" + count);
//                mAdapter.notifyDataSetChanged();
                mRestaurantName = s.toString();
                mHandler.postDelayed(restaurantSearchTask, 500);
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setResultOk(mAutoSearchList.get(position));
        }
    };
    private View.OnClickListener mCreateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResultOk(mRestaurantName);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(restaurantSearchTask);
    }

    private void setResultOk(String name) {
        Intent intent = new Intent();
        intent.putExtra("position", name);
        setResult(RESULT_OK, intent);
        finish();
    }
    //    private class SimpleListAdapter extends ArrayAdapter{
//
//    }
}
