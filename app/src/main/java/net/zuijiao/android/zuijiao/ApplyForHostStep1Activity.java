package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.network.Router;

/**
 * Created by xiaqibo on 2015/8/10.
 */
@ContentView(R.layout.activity_apply_host1)
public class ApplyForHostStep1Activity extends BaseActivity {

    @ViewInject(R.id.apply1_tool_bar)
    private Toolbar mToolbar ;
    @ViewInject(R.id.apply1_image_view)
    private ImageView mImageView ;
    @ViewInject(R.id.apply1_text_view)
    private TextView mTextView ;
    @ViewInject(R.id.apply1_button)
    private Button mButton ;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageView.setOnClickListener(viewsListener);
        mTextView.setOnClickListener(viewsListener);
        mButton.setOnClickListener(viewsListener);
    }


    private View.OnClickListener viewsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.apply1_image_view:
                    break ;
                case R.id.apply1_text_view:
                    break ;
                case R.id.apply1_button:
                    if(!Router.getInstance().getCurrentUser().isPresent())
                        tryLoginFirst(new LambdaExpression() {
                            @Override
                            public void action() {
                                if(!Router.getInstance().getCurrentUser().isPresent())
                                    notifyLogin(null);
                            }
                        }, new OneParameterExpression<Integer>() {
                            @Override
                            public void action(Integer integer) {
                                Toast.makeText(mContext, R.string.notify_net2 , Toast.LENGTH_SHORT).show();
                            }
                        });
                    else{
                        Intent intent = new Intent(mContext ,ApplyForHostStep2Activity.class) ;
                        startActivity(intent);
                    }
                    break ;
            }
        }
    };
}
