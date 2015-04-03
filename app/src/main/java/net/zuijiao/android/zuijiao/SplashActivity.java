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
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.PreferenceManager.PreferenceInfo;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
//    @ViewInject(R.id.splash_text1)
//    private TextView text1 = null;
//    @ViewInject(R.id.splash_text2)
//    private TextView text2 = null;
    @ViewInject(R.id.splash_progressbar)
    private ProgressBar pb = null ;
    private PreferenceManager mPreferMng = null;
    private PreferenceInfo mPreferInfo = null;
    private boolean loadResult = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileManager.mainGourmet = Optional.of(dbMng.initGourmets()) ;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (mPreferInfo.isAppFirstLaunch()) {
                    goToGuide();
                } else {
                    networkSetup();
                }
            }
        }, 800);

    }

    private void goToMain() {
        Intent mainIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        mainIntent.putExtra("login_result" , loadResult) ;
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

    private void networkSetup() {
        RouterOAuth.INSTANCE.loginEmailRoutine("2@2.2",
                "c81e728d9d4c2f636f067f89cc14862c",
                Optional.empty(),
                Optional.empty(),
                () -> {
                    goToMain();
//                    RouterGourmet.INSTANCE.fetchOurChoice(null
//                            , null
//                            , 20
//                            , (Gourmets mainGourmet) -> {
//                        for (Gourmet gourmet : mainGourmet.getGourmets()) {
//                            System.out.println(gourmet.getName());
//                        }
//                    }
//                            , (String errorString) -> {
//                        System.out.println(errorString);
//                    });
                },
                () -> {
                    System.out.println("failure");
                    Toast.makeText(SplashActivity.this, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    goToMain();
                }
        );
//        Router.getOAuthModule().visitor(() -> System.err.println("Visitor Success"), null);
//        Cache.INSTANCE.setup();
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
        if(mPreferInfo.isAppFirstLaunch()){
            pb.setVisibility(View.GONE);
        }
    }

}
