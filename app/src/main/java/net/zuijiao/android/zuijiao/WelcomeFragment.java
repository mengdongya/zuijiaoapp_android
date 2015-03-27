package net.zuijiao.android.zuijiao;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment") public class WelcomeFragment extends Fragment {
	private int index = -1;
	private TextView text1;
	private TextView text2;
	private ImageView image;
	private ImageView image1;
	private ImageView image2;
	private ImageView image3;

	public WelcomeFragment(int index) {
		this.index = index;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_welcome, null);
		text1 = (TextView) contentView.findViewById(R.id.wizard_text1);
		text2 = (TextView) contentView.findViewById(R.id.wizard_text2);
		image = (ImageView) contentView.findViewById(R.id.wizard_image);
		image1 = (ImageView) contentView.findViewById(R.id.wizard_index1);
		image2 = (ImageView) contentView.findViewById(R.id.wizard_index2);
		image3 = (ImageView) contentView.findViewById(R.id.wizard_index3);
		setViewContent() ;
		return contentView;
	}

	private void setViewContent() {
		
	}
}
