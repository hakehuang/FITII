package com.FSL.mcuTracker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.FSL.local.database.DBOpenHelper;
import com.FSL.local.database.DataBaseManager;
import com.FSL.mcuTracker.UploadUtil.OnUploadProcessListener;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditInfo extends ActionBarActivity implements
		OnUploadProcessListener {
	private final String TAG = "EditInfo";
	TextView mTvBn;
	EditText mEtMcob, mEtBr, mEtSr, mEtDes, mEtPic, mEtBn;
	ImageView mImageView;
	Button mBtnConfirm, mBtnUpload;
	private Bundle bundle;
	private Intent intent;
	private String picPath;
	private Uri photoUri;
	private Boolean uploadPhoto = false;
	private Boolean Online;
	private String addr;
	EditTask eTask = new EditTask();
	String path = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    addr= prefs.getString("ip","");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);
		mEtBn = (EditText) findViewById(R.id.et_edit_bn);
		mEtMcob = (EditText) findViewById(R.id.et_edit_mcob);
		mEtBr = (EditText) findViewById(R.id.et_edit_br);
		mEtSr = (EditText) findViewById(R.id.et_edit_sr);
		mEtDes = (EditText) findViewById(R.id.et_edit_dcp);
		mImageView = (ImageView) findViewById(R.id.iv_edit_pic);
		intent = this.getIntent();
		Online = intent.getBooleanExtra("Online", false);
		Log.e(TAG,"On Create Online?"+Online);
		mEtBn.setText(intent.getStringExtra("board_number"));
		bundle = intent.getExtras();
		Log.d(TAG,"size: "+bundle.size());
		if (bundle.size()>4) {
			mEtBn.setFocusable(false);
			mEtBn.setFocusableInTouchMode(false);
			if(bundle.getString("BoardNumber").equals("no record"))
				mEtBn.setText(bundle.getString("ID"));
			else
				mEtBn.setText(bundle.getString("BoardNumber"));
			mEtMcob.setText(bundle.getString("Master chip on board"));
			mEtBr.setText(bundle.getString("BoardRev"));
			mEtSr.setText(bundle.getString("SchematicRev"));
			mEtDes.setText(bundle.getString("description"));
			path = bundle.getString("Pic");
		} 
		mBtnUpload = (Button) findViewById(R.id.btn_edit_upload);
		mBtnConfirm = (Button) findViewById(R.id.btn_edit_submit);
		if(!Online)
			mBtnUpload.setVisibility(View.GONE);
		this.getContentResolver();
		mBtnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG,"Online Mode:"+Online);
				if(Online){
					if (uploadPhoto) 
						toUploadFile();
					 else
						eTask.execute();
				}else
					localStore();
			}


		});

		mBtnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String SDState = Environment.getExternalStorageState();
				if (SDState.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
					ContentValues values = new ContentValues();
					photoUri = EditInfo.this.getContentResolver().insert(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							values);
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							photoUri);
					startActivityForResult(intent, 1);
					uploadPhoto = true;
				} else {
					Toast.makeText(EditInfo.this, "take photo and upload",
							Toast.LENGTH_LONG).show();
				}
			}

		});

	}
	private void localStore() {
		DBOpenHelper dbHelper = DataBaseManager.getInstance().getHelper();
		SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
		Boolean exist = dbHelper.isExist(db, mEtBn.getText().toString());
		if(!exist)
			dbHelper.insert(db,mEtBn.getText().toString(),mEtDes.getText().toString(),mEtMcob.getText().toString(),mEtBr.getText().toString(),mEtSr.getText().toString());
		DataBaseManager.getInstance().closeDatabase();
		intent = new Intent();
		intent.setClass(EditInfo.this, IndexActivity.class);
		intent.putExtra("Online", Online);
		intent.putExtra("Local", true);
		startActivity(intent);
		EditInfo.this.finish();
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String result = (String) msg.obj;
				eTask.execute(result);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private String toUploadFile() {

		String fileKey = "pic";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		;
		uploadUtil.setOnUploadProcessListener(this);

		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", "11111");
		String result = uploadUtil.uploadFile(picPath, fileKey,
				addr+"FSL_WebServer/Pic", params);
		return result;
	}

	private void ParsePhoto(int requestCode, Intent data) {

		String[] pojo = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			picPath = cursor.getString(columnIndex);
			cursor.close();
		}
		Log.i(TAG, "imagePath = " + picPath);
		if (picPath != null
				&& (picPath.endsWith(".png") || picPath.endsWith(".PNG")
						|| picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
			Bitmap bm = BitmapFactory.decodeFile(picPath);
			mImageView.setImageBitmap(bm);
		} else {
			Toast.makeText(this, "Wrong Path", Toast.LENGTH_LONG).show();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			ParsePhoto(requestCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class EditTask extends AsyncTask<String, Void, String> {
		private String address = addr+"FSL_WebServer/MCUs";

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient hc = new DefaultHttpClient();
			JSONObject jsonObj;
			try {
				HttpPost hp = new HttpPost(address);
				jsonObj = buildUploadJson();
				if (params.length == 1) {
					jsonObj.put("Pic", params[0]);
					Log.d(TAG, "PIC  " + params[0]);
				} else {
					jsonObj.put("Pic", path);
				}
				Log.e(TAG,jsonObj.toString());
				hp.setEntity(new StringEntity(jsonObj.toString()));
				HttpResponse response = hc.execute(hp);
				if (response.getStatusLine().getStatusCode() == 200) {
					String mStrResult = EntityUtils.toString(response
							.getEntity());
					Log.d(TAG, mStrResult);
					result = new JSONObject(mStrResult).getString("ID");

				} else {
					Log.e(TAG, "Connection Failed!"+response.getStatusLine().getStatusCode());
				}
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}

			return result;

		}

		private JSONObject buildUploadJson() throws JSONException {
			JSONObject jsonObj = new JSONObject();
			if (bundle.size()<4) {
				// add new by click button
				jsonObj.put("Mode", "add");
				jsonObj.put("BoardNumber", mEtBn.getText().toString());
			}else if (bundle.getString("BoardNumber").equals("no record")) {
				// add new by scan
				jsonObj.put("Mode", "add");
				jsonObj.put("BoardNumber", bundle.getString("ID"));
			} else {
				// modify info
				jsonObj.put("ID", bundle.getString("ID"));
				jsonObj.put("Mode", "modify");
				jsonObj.put("BoardNumber", mEtBn.getText().toString());
			}
			User user = (User) getApplication();
			String CoreID = user.getId();
			jsonObj.put("Editor", CoreID);
			jsonObj.put("Master chip on board", mEtMcob.getText().toString());
			jsonObj.put("BoardRev", mEtBr.getText().toString());
			jsonObj.put("SchematicRev", mEtSr.getText().toString());
			jsonObj.put("description", mEtDes.getText().toString());
			return jsonObj;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Bundle bundle = new Bundle();
			Intent intent = new Intent();
			intent.setClass(EditInfo.this, UnitManager.class);
			bundle.putString("UID", result);
			intent.putExtras(bundle);
			startActivity(intent);
			EditInfo.this.finish();
		}
	}

	@Override
	public void onUploadDone(int responseCode, String message) {
		Message msg = Message.obtain();
		msg.what = 1;
		msg.obj = message;
		handler.sendMessage(msg);
	}

}
