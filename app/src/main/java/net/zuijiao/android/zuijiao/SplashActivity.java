package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.PreferenceManager.PreferenceInfo;
import com.zuijiao.db.DBOpenHelper;

import java.util.List;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    @ViewInject(R.id.splash_progressbar)
    private ProgressBar pb = null;
    private PreferenceManager mPreferMng = null;
    private PreferenceInfo mPreferInfo = null;
    private boolean loadResult = false;
    //    if true ,do not jump to main
    private boolean onBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPreferInfo.isAppFirstLaunch()) {
            goToGuide();
        } else {
            tryLoginFirst(() -> {
            }, e -> {
            });
            new Handler().postDelayed(() -> {
                try {
                    List<Gourmet> list = dbMng.initGourmets();
                    if (list != null) {
                        FileManager.mainGourmet = Optional.ofNullable(list);
                    } else {
                        FileManager.mainGourmet = Optional.empty();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                DBOpenHelper.copyLocationDb(SplashActivity.this.getApplicationContext());
                goToMain();
            }, 800);
        }
    }

    private void goToMain() {
        if (onBackPressed) {
            return;
        }
        Intent mainIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressed = true;
    }

    private void goToGuide() {
        Intent mainIntent = new Intent(getApplicationContext(),
                GuideActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void initPreferenceInfo() {
        mPreferMng = PreferenceManager.getInstance(getApplicationContext());
        mPreferInfo = mPreferMng.initPreferenceInfo();
        FileManager.createRootFolder();
    }


    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        initPreferenceInfo();
        if (mPreferInfo.isAppFirstLaunch()) {
            pb.setVisibility(View.GONE);
        }
    }

}
