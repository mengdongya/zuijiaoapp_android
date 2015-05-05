package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.view.WordWrapView;

import java.util.ArrayList;

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
    private WordWrapView mHotLabels = null;
    @ViewInject(R.id.label_editor)
    private EditText mLabelEditor = null;
    @ViewInject(R.id.label_editor_indicator)
    private TextView mEditorIndicator = null;
    private ArrayList<String> mAddedText = new ArrayList<>();
    private MenuItem mMenuBtn = null;

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
        mLabelEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("mLabelEditor", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditorIndicator.setText(String.format(getString(R.string.nick_name_watcher), s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("mLabelEditor", "afterTextChanged");
            }
        });
        mLabelEditor.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = v.getText().toString().trim();
                v.setText("");
                if (mAddedText.size() == 5) {
                    Toast.makeText(mContext, getString(R.string.label_count_upper_limit), Toast.LENGTH_SHORT).show();
                    return false;
                }
                TextView textview = new TextView(mContext);
                textview.setBackgroundResource(R.drawable.bg_label);
                textview.setTextColor(getResources().getColor(R.color.white));
                textview.setTextSize(14);
                textview.setText(text);
                textview.setOnClickListener(mLabelListener);
                mAddedLabels.addView(textview);
                mAddedText.add(text);
                mMenuBtn.setTitle(String.format(getString(R.string.sure_with_num), mAddedText.size(), 5));
            }
            return false;
        });
        new Handler().postDelayed(() -> {
            InputMethodManager inputManager =
                    (InputMethodManager) mLabelEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mLabelEditor, 0);
        }, 200);
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
        return super.onOptionsItemSelected(item);
    }

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
}
