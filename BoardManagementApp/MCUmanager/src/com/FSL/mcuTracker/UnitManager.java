package com.FSL.mcuTracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UnitManager extends ActionBarActivity {
	private final String TAG = "UnitManager";
	private String uid;
	private Bundle bundle;
	private Intent intent;
	private JSONObject jsonObj;
	private String addr;
	TextView mTvID, mTvMcob, mTvBr, mTvSr, mTvOwner, mTvRd, mTvLo, mTvDes,mTvUp,
			mTvBn;
	ImageView mIvPic;
	Button mBtnRegister, mBtnEdit;
	String picpath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    addr= prefs.getString("ip","");
		setContentView(R.layout.activity_unit_manager);
		mTvID = (TextView) findViewById(R.id.tv_unit_displayid);
		mTvMcob = (TextView) findViewById(R.id.tv_unit_displaymcob);
		mTvBr = (TextView) findViewById(R.id.tv_unit_displaybr);
		mTvSr = (TextView) findViewById(R.id.tv_unit_displaysr);
		mTvOwner = (TextView) findViewById(R.id.tv_unit_displayowner);
		mTvBn = (TextView) findViewById(R.id.tv_unit_displaybn);
		mTvOwner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bundle.clear();
				Intent intent = new Intent();
				intent.setClass(UnitManager.this, PersonalInfo.class);
				bundle.putString("ID", mTvOwner.getText().toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}

		});

		mTvRd = (TextView) findViewById(R.id.tv_unit_displayrd);
		mTvLo = (TextView) findViewById(R.id.tv_unit_displaylo);
		mTvDes = (TextView) findViewById(R.id.tv_unit_displaydcp);
		mTvUp = (TextView) findViewById(R.id.tv_unit_displayup);
		mIvPic = (ImageView) findViewById(R.id.iv_unit_displaypic);
		mBtnRegister = (Button) findViewById(R.id.btn_unit_rg);
		mBtnEdit = (Button) findViewById(R.id.btn_unit_Edit);

		intent = this.getIntent();
		bundle = intent.getExtras();
		uid = bundle.getString("UID");
		ManageTask mTask = new ManageTask();
		mTask.execute(uid);

		mBtnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ManageTask newTask = new ManageTask();
				User user = (User) getApplication();
				String CoreID = user.getId();
				Log.d(TAG, "Core ID:" + CoreID);
				if (!CoreID.equals(mTvOwner.getText().toString()))
					newTask.execute(uid, CoreID, mTvOwner.getText().toString());
			}
		});
		mBtnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bundle.clear();
				Intent intent = new Intent();
				intent.putExtra("Online",true);
				intent.setClass(UnitManager.this, EditInfo.class);
				try {
					bundle = parse2Bundle(jsonObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				intent.putExtras(bundle);
				startActivity(intent);
				UnitManager.this.finish();

			}
		});
		mIvPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse(picpath);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});
	
	}

	public Bundle parse2Bundle(JSONObject jsonObj) throws JSONException {
		Bundle bundle = new Bundle();
		Iterator<String> keys = jsonObj.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String value = jsonObj.get(key).toString();
			bundle.putString(key, value);

		}
		return bundle;
	}

	private class ManageTask extends AsyncTask<String, Void, JSONObject> {
		private String address = addr+"FSL_WebServer/MCUs";

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject result = new JSONObject();
			HttpClient hc = new DefaultHttpClient();
			jsonObj = new JSONObject();
			try {
				jsonObj.put("UID", params[0]);
				if (params.length == 3) {
					jsonObj.put("CoreID", params[1]);
					jsonObj.put("LastOwner", params[2]);

				}
				HttpPost hp = new HttpPost(address);
				hp.setEntity(new StringEntity(jsonObj.toString()));
				HttpResponse response = hc.execute(hp);
				if (response.getStatusLine().getStatusCode() == 200) {
					String mStrResult = EntityUtils.toString(response
							.getEntity());
					Log.d(TAG, mStrResult);
					result = new JSONObject(mStrResult);
					if (result.length() < 2) {
						result = new JSONObject();
						result.put("ID", params[0]);
						result.put("Master chip on board", "");
						result.put("BoardRev", "");
						result.put("SchematicRev", "");
						result.put("description", "");
						result.put("Pic", " ");
						result.put("BoardNumber", "no record");
						result.put("Last Update","no record");
					}
					picpath = result.getString("Pic");

				} else {
					Log.e(TAG, "Connection Failed!");
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;

		}

		/**
		 * Update text view to display the information of MCU
		 * 
		 * @param json
		 * @throws JSONException
		 */
		private void setView(JSONObject json) throws JSONException {
			Log.d(TAG, "Set View");
			mTvID.setText(json.getString("ID"));
			mTvMcob.setText(json.getString("Master chip on board"));
			mTvBr.setText(json.getString("BoardRev"));
			mTvSr.setText(json.getString("SchematicRev"));
			mTvOwner.setText(json.getString("OwnerID"));
			mTvRd.setText(json.getString("OwnerRegisterDate"));
			mTvBn.setText(json.getString("BoardNumber"));
			mTvUp.setText(json.getString("LastUpdate"));
			// List last 5 owners
			String LastOwner = "";
			String[] Owners = json.getString("LastOwner").split(";");
			for (int i = 0; i < Owners.length && i < 5; i++) {
				LastOwner += Owners[i] + ";";
			}
			mTvLo.setText(LastOwner.substring(0, LastOwner.length() - 1));
			mTvDes.setText(json.getString("description"));
			// Set image
			if (picpath.contains("http://"))
				new Thread(new picDownload()).start();
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			jsonObj = result;
			try {
				if (jsonObj.getString("BoardNumber").equals("no record")) {
					mBtnEdit.performClick();
				} else {
					try {
						setView(result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mIvPic.setImageBitmap((Bitmap) msg.obj);
				break;
			}
		}
	};

	class picDownload implements Runnable {

		@Override
		public void run() {
			try {

				// byte[] data = ImageService.getImage(path);
				Log.e(TAG,picpath);
				URL url = new URL(picpath);
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setConnectTimeout(5000);
				con.setRequestMethod("GET");
				con.connect();

				if (con.getResponseCode() == 200) {
					InputStream is = con.getInputStream();
					Bitmap bitmap = BitmapFactory.decodeStream(is);// (data, 0,
																	// data.length);
					handler.obtainMessage(1, bitmap).sendToTarget();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
