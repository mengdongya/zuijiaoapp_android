package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.SellerStatus;
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
    @ViewInject(R.id.apply1_text_view2)
    private TextView mTextView2 ;
    @ViewInject(R.id.apply1_button)
    private Button mButton ;
    @ViewInject(R.id.apply1_text_view1)
    private TextView mTextView1 ;
    private SellerStatus mSellerStatus ;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(mTendIntent != null)
            mSellerStatus= (SellerStatus) mTendIntent.getSerializableExtra("seller_status");
        if(mSellerStatus == null){
            if( Router.getInstance(). getSellerStatus().isPresent())
                mSellerStatus = Router.getInstance(). getSellerStatus().get() ;
            if(mSellerStatus == null){
                finish();
                return;
            }
        }
        mImageView.setOnClickListener(viewsListener);
        mTextView.setOnClickListener(viewsListener);
        mButton.setOnClickListener(viewsListener);
        switch (mSellerStatus.getApplyStatus()){
            case editing:
//                Picasso.with(mContext).load(R.drawable.apply_host_place_holder).centerCrop().fit().into(mImageView);
                break ;
            case waiting:
            case reviewing:
                mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mImageView.setImageResource(R.drawable.host_apply_progressing);
                mTextView.setText(R.string.application_progressing_notice);
                mTextView1.setVisibility(View.GONE);
//                mTextView1.setText(R.string.application_progressing_notice2);
                mTextView2.setText(getString(R.string.application_progressing_notice2) + getString(R.string.our_qq_group));
                mTextView2.setAutoLinkMask(Linkify.ALL);
                mTextView2.setMovementMethod(LinkMovementMethod.getInstance());
                mButton.setText(R.string.confirm);
                break ;
            case passed:
                mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mImageView.setImageResource(R.drawable.host_apply_passed);
                mTextView.setText(R.string.application_success_notice1);
                mTextView1.setVisibility(View.INVISIBLE);
                mTextView2.setText(R.string.our_qq_group);
                mTextView2.setAutoLinkMask(Linkify.ALL);
                mTextView2.setMovementMethod(LinkMovementMethod.getInstance());
                if(mSellerStatus.getBankStatus() == SellerStatus.BankStatus.finished){
                    mButton.setText(R.string.confirm);
                }else{
                    mButton.setText(R.string.complete_bind_bank_account);
                }
                break ;
            case fail:
                mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mImageView.setImageResource(R.drawable.host_appply_failed);
                mTextView1.setText(String.format(getString(R.string.format_apply_fail_reason), mSellerStatus.getFaiReason()));
                mTextView.setText(R.string.application_failed_notice);
                mTextView2.setText(R.string.our_qq_group);
                mTextView2.setAutoLinkMask(Linkify.ALL);
                mTextView2.setMovementMethod(LinkMovementMethod.getInstance());
                mButton.setText(R.string.modify_apply_information);
                break ;
            default:
                finish();

        }
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
                    if(mSellerStatus == null || !Router.getInstance().getCurrentUser().isPresent()){
                        return ;
                    }
                    Intent intent = new Intent( ) ;
                    switch (mSellerStatus.getApplyStatus()){
                        case editing:
                            if(mSellerStatus.getProfileStatus() == SellerStatus.ProfileStatus.finished){
                                intent.setClass(mContext, CommonWebViewActivity.class) ;
                                String formatUrl = null ;
                                if(BuildConfig.DEBUG)
                                     formatUrl = "http://bugzilla.zuijiaodev.com/?token=" + Router.getInstance().getAccessToken().get() + "&d" ;
                                else
                                    formatUrl = "http://bugzilla.zuijiaodev.com/?token=" + Router.getInstance().getAccessToken().get() + "&r" ;
                                intent.putExtra("content_url", formatUrl) ;
                                intent.putExtra("apply_host", true) ;
                            }else{
                                intent.setClass(mContext, ApplyForHostStep2Activity.class) ;

                            }
                            startActivity(intent);
                            break ;
                        case fail:
                            intent.setClass(mContext , CommonWebViewActivity.class) ;
                            String formatUrl = null ;
                            if(BuildConfig.DEBUG)
                                formatUrl = "http://bugzilla.zuijiaodev.com/?token=" + Router.getInstance().getAccessToken().get() + "&d" ;
                            else
                                formatUrl = "http://bugzilla.zuijiaodev.com/?token=" + Router.getInstance().getAccessToken().get() + "&r" ;
                            intent.putExtra("content_url" ,formatUrl) ;
                            intent.putExtra("apply_host", true) ;
                            startActivity(intent);
                            break ;
                        case passed:
                            if(mSellerStatus.getBankStatus() == SellerStatus.BankStatus.finished){
                                finish();
                            }else {
                                intent.setClass(mContext, ReceivingAccountActivity.class) ;
                                intent.putExtra("b_edit", true) ;
                                startActivity(intent);
                            }
                            break ;
                        default:
                            finish();
                            break ;
                        }
                    break ;
            }
        }
    };
}
