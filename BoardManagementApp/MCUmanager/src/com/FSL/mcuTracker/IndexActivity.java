package com.FSL.mcuTracker;


import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * 
 * @author B53505
 *
 */
public class IndexActivity extends ActionBarActivity {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private final String TAG = "IndexActivity";
	User user;
	private TextView mTextView;
	private Button mBtnScan,mBtnList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		mTextView = (TextView) findViewById(R.id.tv_i_user);
		user = (User)getApplication();
		mTextView.setText(user.getId());
		mBtnScan = (Button) findViewById(R.id.btn_scan);
		mBtnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(IndexActivity.this, CaptureActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
		mBtnList = (Button) findViewById(R.id.btn_list_units);
		mBtnList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(IndexActivity.this, ListActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				Log.d(TAG,bundle.getString("result"));
				String uid = bundle.getString("result");
				Intent intent = new Intent();
				intent.setClass(IndexActivity.this, UnitManager.class);
				bundle.clear();
				bundle.putString("UID", uid);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		}
    }	
}
