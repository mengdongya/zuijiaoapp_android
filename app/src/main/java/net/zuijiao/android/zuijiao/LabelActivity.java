package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.MyTextWatcher;
import com.zuijiao.view.WordWrapView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaqibo on 2015/4/30.
 */
@ContentView(R.layout.activity_edit_label)
public class LabelActivity extends BaseActivity {
    @ViewInject(R.id.label_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.label_added_label)
    private WordWrapView mAddedLabels = null;
    @ViewInject(R.id.label_hot_label)
    private ListView mLvHotLabels = null;
    @ViewInject(R.id.label_editor)
    private EditText mLabelEditor = null;
    @ViewInject(R.id.label_editor_indicator)
    private TextView mEditorIndicator = null;
    private ArrayList<String> mAddedText = new ArrayList<>();
    private List<String> mHotLabels = null;
    private MenuItem mMenuBtn = null;
    private View.OnClickListener mLabelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View logoutView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.delete_label_dialog, null);
            AlertDialog dialog = new AlertDialog.Builder(LabelActivity.this).setView(logoutView).create();
            logoutView.findViewById(R.id.delete_label_btn_cancel).setOnClickListener((View v) -> {
                dialog.dismiss();
            });
            logoutView.findViewById(R.id.delete_label_btn_confirm).setOnClickListener((View v) -> {
                dialog.dismiss();
                try {
                    mAddedLabels.removeView(view);
                    mAddedText.remove(((TextView) view).getText());
                    mMenuBtn.setTitle(String.format(getString(R.string.sure_with_num), mAddedText.size(), 5));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
            dialog.show();
        }
    };

    @Override

    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLabelEditor.setFocusable(true);
        mLabelEditor.setFocusableInTouchMode(true);
        mLabelEditor.requestFocus();
        mEditorIndicator.setText(String.format(getString(R.string.nick_name_watcher), 0, 15));
        mLabelEditor.addTextChangedListener(new MyTextWatcher(mEditorIndicator, 15, mContext));
        mLabelEditor.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                String text = v.getText().toString().trim();
                v.setText("");
                addOneLabel(text);
                return true;
            }
            return false;
        });
        mLvHotLabels.setOnItemClickListener(mHotLabelListener);
        fetchHotLabels();
        new Handler().postDelayed(() -> {
            InputMethodManager inputManager =
                    (InputMethodManager) mLabelEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mLabelEditor, 0);
        }, 200);
    }

    private void fetchHotLabels() {
        Router.getCommonModule().gourmetTags(hotLabels -> {
            mHotLabels = hotLabels;
            mLvHotLabels.setAdapter(new HotLabelAdapter());
        }, errorMsg -> {
            mHotLabels = new ArrayList<>();
            mHotLabels.add("xihuanni");
            mHotLabels.add("nashuangyandongren");
            mHotLabels.add("xiaoshenggengmiren");
            mHotLabels.add("yuanzaike");
            mLvHotLabels.setAdapter(new HotLabelAdapter());
//            fetchHotLabels();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.label_edit, menu);
        mMenuBtn = menu.findItem(R.id.label_commit);
        mMenuBtn.setTitle(String.format(getString(R.string.sure_with_num), mAddedText.size(), 5));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.label_commit) {
            InputMethodManager inputManager =
                    (InputMethodManager) mLabelEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mLabelEditor.getWindowToken(), 0);
            Intent intent = new Intent();
            intent.putExtra("labels", mAddedText);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mAddedText.size() != 0) {
            View logoutView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.delete_label_dialog, null);
            AlertDialog dialog = new AlertDialog.Builder(LabelActivity.this).setView(logoutView).create();
            ((TextView) logoutView.findViewById(R.id.dialog_title)).setText(getString(R.string.notify_discard_edit_label));
            logoutView.findViewById(R.id.delete_label_btn_cancel).setOnClickListener((View v) -> {
                dialog.dismiss();
            });
            logoutView.findViewById(R.id.delete_label_btn_confirm).setOnClickListener((View v) -> {
                super.onBackPressed();
            });
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }


    private AdapterView.OnItemClickListener mHotLabelListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            addOneLabel(mHotLabels.get(position));
        }
    };

    private class HotLabelAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mHotLabels.size();
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
            TextView textView = new TextView(mContext);
            textView.setText(mHotLabels.get(position));
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(getResources().getColor(R.color.tv_deep_gray));
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.main_item_label_height));
            textView.setBackgroundResource(R.drawable.hot_label_bg);
            textView.setLayoutParams(lp);
            return textView;
        }
    }


    private boolean addOneLabel(String label) {
        if (mAddedText.size() == 5) {
            Toast.makeText(mContext, getString(R.string.label_count_upper_limit), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mAddedText.contains(label)) {
            Toast.makeText(mContext, getString(R.string.label_repeated), Toast.LENGTH_SHORT).show();
            return false;
        }
        TextView textview = new TextView(mContext);
        textview.setBackgroundResource(R.drawable.bg_label);
        textview.setTextColor(getResources().getColor(R.color.white));
        textview.setTextSize(14);
        textview.setText(label);
        textview.setOnClickListener(mLabelListener);
        mAddedLabels.addView(textview);
        mAddedText.add(label);
        mMenuBtn.setTitle(String.format(getString(R.string.sure_with_num), mAddedText.size(), 5));
        return true;
    }
}
