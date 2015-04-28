package net.zuijiao.android.zuijiao;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by xiaqibo on 2015/4/23.
 */
@ContentView(R.layout.activity_nick_name_input)
public class NickNameInputActivity extends BaseActivity {
    @ViewInject(R.id.btn_nick_name_commit)
    private Button commitBtn;
    @ViewInject(R.id.et_nick_name_input)
    private EditText editText;
    @ViewInject(R.id.tv_et_watcher)
    private TextView textView;

    @Deprecated
    protected void findViews() {

    }

    protected void registerViews() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.8
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.7
        getWindow().setAttributes(p);
//        View view = LayoutInflater.from(getApplicationContext()).inflate(
//                R.layout.activity_nick_name_input, null);
//        final AlertDialog dialog = new AlertDialog.Builder(NickNameInputActivity.this).setView(view).create();
//        EditText editText = (EditText) view.findViewById(R.id.et_nick_name_input);
//        Button commitBtn = (Button) view.findViewById(R.id.btn_nick_name_commit);
//        TextView textView = (TextView)view.findViewById(R.id.tv_et_watcher) ;
        textView.setText(String.format(getString(R.string.nick_name_watcher), 0));
        commitBtn.setOnClickListener((View v) -> {
            String newName = editText.getText().toString().trim();
            if (newName.equals("")) {
                Toast.makeText(mContext, getString(R.string.nick_name_null), Toast.LENGTH_SHORT).show();
            } else {
//                dialog.dismiss();
                finish();
            }
        });
//        Window dialogWindow = dialog.getWindow();
//        dialog.show();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText(String.format(getString(R.string.nick_name_watcher), s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        new Handler().postDelayed(() -> {
            InputMethodManager inputManager =
                    (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }, 200);
    }

    ;

}
