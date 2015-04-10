package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.PreferenceManager.PreferenceInfo;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.AuthorInfo;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    //    @ViewInject(R.id.splash_text1)
//    private TextView text1 = null;
//    @ViewInject(R.id.splash_text2)
//    private TextView text2 = null;
    @ViewInject(R.id.splash_progressbar)
    private ProgressBar pb = null;
    private PreferenceManager mPreferMng = null;
    private PreferenceInfo mPreferInfo = null;
    private boolean loadResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPreferInfo.isAppFirstLaunch()) {
            goToGuide();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    try {
                        FileManager.mainGourmet = Optional.of(dbMng.initGourmets());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    DBOpenHelper.copyLocationDb(SplashActivity.this.getApplicationContext());
                    AuthorInfo auth = PreferenceManager.getInstance(getApplicationContext()).getThirdPartyLoginMsg();
                    networkSetup(auth);
                }
            }, 800);
//            DBOpenHelper.copyLocationDb(SplashActivity.this.getApplicationContext()) ;
//            AuthorInfo auth = PreferenceManager.getInstance(getApplicationContext()).getThirdPartyLoginMsg();
//            networkSetup(auth);

        }
//        try {
//            FileManager.mainGourmet = Optional.of(dbMng.initGourmets());
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                if (mPreferInfo.isAppFirstLaunch()) {
//                    goToGuide();
//                } else {
//                    DBOpenHelper.copyLocationDb(SplashActivity.this.getApplicationContext()) ;
//                    AuthorInfo auth = PreferenceManager.getInstance(getApplicationContext()).getThirdPartyLoginMsg();
//                    networkSetup(auth);
//
//                }
//            }
//        }, 800);

    }

    private void goToMain() {
        Intent mainIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        mainIntent.putExtra("login_result", loadResult);
        startActivity(mainIntent);
        finish();
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

    private void networkSetup(AuthorInfo auth) {
        if (ThirdPartySDKManager.getInstance(getApplicationContext()).isThirdParty(auth.getPlatform())) {
            Router.getOAuthModule().login(auth.getUid(), auth.getPlatform(), Optional.<String>empty(), Optional.of(auth.getToken()), () -> {
                        //  TinyUser user = Optional.of()Router.INSTANCE.getCurrentUser() ;
                        goToMain();
                    },
                    errorMessage -> {
                        System.out.println(errorMessage);
                        Toast.makeText(SplashActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        goToMain();
                    });
        } else if ((auth.getEmail() != null) && (!auth.getEmail().equals(""))) {
            //2@2.2
            //c81e728d9d4c2f636f067f89cc14862c
            RouterOAuth.INSTANCE.loginEmailRoutine(auth.getEmail(),
                    auth.getPassword(),
                    Optional.empty(),
                    Optional.empty(),
                    () -> {
                        goToMain();
                    },
                    errorMessage -> {
                        System.out.println(errorMessage);
                        Toast.makeText(SplashActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        goToMain();
                    }
            );

        } else {
            Router.getOAuthModule().visitor(() -> {
                        goToMain();
                    },
                    errorMessage -> {
                        System.out.println(errorMessage);
                        Toast.makeText(SplashActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        goToMain();
                    });
        }
        Cache.INSTANCE.setup();
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registeViews() {
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initPreferenceInfo();
        if (mPreferInfo.isAppFirstLaunch()) {
            pb.setVisibility(View.GONE);
        }
    }

}
