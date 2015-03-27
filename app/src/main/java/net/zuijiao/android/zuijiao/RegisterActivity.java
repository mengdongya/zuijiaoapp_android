package net.zuijiao.android.zuijiao;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {
	@ViewInject(R.id.register_toolbar)
	private Toolbar mToolbar = null ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void findViews() {

	}

	@Override
	protected void registeViews() {

	}

}
