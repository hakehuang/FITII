package com.FSL.mcuTracker;


import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
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
	private SearchView mSearchView;
	private Boolean Online;
	private String addr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    addr= prefs.getString("ip","http://10.192.244.114:8080/");
		Intent intent = this.getIntent();
		Online = intent.getBooleanExtra("Online", false);
		mTextView = (TextView) findViewById(R.id.tv_i_user);
		user = (User)getApplication();
		if(Online)
			mTextView.setText(user.getId());
		else
			mTextView.setText(user.getId()+"  Offline Mode");
		mSearchView = (SearchView) findViewById(R.id.SearchView);
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent intent = new Intent();
				intent.putExtra("Online", Online);
				intent.setClass(IndexActivity.this, ListActivity.class);
				callOtherActivity(intent,query);
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
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
				intent.putExtra("Online", Online);
				intent.putExtra("Local", false);
				intent.setClass(IndexActivity.this, ListActivity.class);
				startActivity(intent);
			}
		});
		Button mBtnAdd = (Button) findViewById(R.id.btn_add_unit);
		mBtnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("Online", Online);
				intent.setClass(IndexActivity.this, EditInfo.class);
				startActivity(intent);
			}
		});
		Button mBtnLocal =  (Button) findViewById(R.id.btn_local_list);
		mBtnLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("Online", Online);
				intent.putExtra("Local", true);
				intent.setClass(IndexActivity.this, ListActivity.class);
				startActivity(intent);
			}
		});
		TextView mTvHelp = (TextView) findViewById(R.id.tv_help);
		mTvHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse(addr+"help.html");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});
		
	}

	public void callOtherActivity(Intent intent,String uid){
		Log.e(TAG,Online.toString());
		Bundle bundle = new Bundle();
		intent.putExtra("Online", Online);
		bundle.putString("UID", uid);
		intent.putExtras(bundle);
		startActivity(intent);
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
				if(Online){
					Intent intent = new Intent();
					intent.setClass(IndexActivity.this, UnitManager.class);
					callOtherActivity(intent,uid);
				}else{
					Intent intent = new Intent();
					intent.putExtra("Online", Online);
					intent.putExtra("board_number",uid);
					intent.setClass(IndexActivity.this, EditInfo.class);
					startActivity(intent);
				}
			}
			break;
		}
    }	
}
