package com.FSL.mcuTracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.FSL.local.database.DBOpenHelper;
import com.FSL.local.database.DataBaseManager;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ListActivity extends ActionBarActivity {
	private final String TAG = "ListActivity";
	private ArrayList<String> mList = null;
	private ArrayList<Map<String, Object>> mInfos = new ArrayList<Map<String, Object>>();
	ListView mListView;
	private boolean Online;
	private boolean localList;
	private Button mBtnUpload;
	private String addr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    addr= prefs.getString("ip","http://10.192.244.114:8080/");
		mList = new ArrayList<String>();
		mListView = (ListView) findViewById(R.id.lv_unit);
		Intent intent = this.getIntent();
		Online = intent.getBooleanExtra("Online", true);
		localList = intent.getBooleanExtra("Local", true);
		Bundle bundle = intent.getExtras();
		mBtnUpload = (Button) findViewById(R.id.btn_upload);
		// Online list
		if (Online && !localList) {
			mBtnUpload.setVisibility(View.GONE);
			ListTask mTask = new ListTask();
			if (bundle.size()>2) {
				mTask.execute("search", bundle.getString("UID"));
			} else {
				User user = (User) getApplication();
				String CoreID = user.getId();
				Log.e(TAG,"CoreID" + CoreID);
				mTask.execute(CoreID);
			}
			// click item to get the info of item

			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Bundle bundle = new Bundle();
					Intent intent = new Intent();
					intent.setClass(ListActivity.this, UnitManager.class);
					bundle.putString("UID", mList.get(position));
					intent.putExtras(bundle);
					startActivity(intent);

				}

			});
		} else {
			// Local data list
			setView();
			if (!Online)
				mBtnUpload.setVisibility(View.GONE);
		}
		mBtnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBtnUpload.setFocusable(false);
				mBtnUpload.setFocusableInTouchMode(false);
				UploadTask task = new UploadTask();
				task.execute();
			}
		});

	}


	private void setView() {
		DBOpenHelper helper = DataBaseManager.getInstance().getHelper();
		Cursor cursor = helper.select(DataBaseManager.getInstance()
				.openDatabase());
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.unitlist, cursor, new String[] { "Board_Number",
						"description", "master_chip_on_board" }, new int[] {
						R.id.tv_lv_id, R.id.tv_lv_bn, R.id.tv_lv_mcob }, 0);
		mListView.setAdapter(adapter);
		DataBaseManager.getInstance().closeDatabase();

	}

	private class UploadTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			
			DBOpenHelper helper = DataBaseManager.getInstance().getHelper();
			SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
			Cursor cursor = helper.select(db);
			for (int i = 0; i < mListView.getCount(); i++) {
				cursor.moveToPosition(i);
				try {
					HttpClient hc = new DefaultHttpClient();
					HttpPost hp = new HttpPost(addr+"FSL_WebServer/MCUs");
					hp.setEntity(new StringEntity(buildJson(cursor).toString()));
					HttpResponse response = hc.execute(hp);
					if (response.getStatusLine().getStatusCode() == 200) {
						String mStrResult = EntityUtils.toString(response
								.getEntity());
						Log.d(TAG, mStrResult);
						helper.delete(db, cursor.getInt(0));
					} else {
						Log.e(TAG, "Connection Failed!"+response.getStatusLine().getStatusCode());
						return false;
					}
				} catch (JSONException e) {
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			DataBaseManager.getInstance().closeDatabase();
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				Toast.makeText(ListActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
				mBtnUpload.setVisibility(View.GONE);
			}
		}
		private JSONObject buildJson(Cursor curosr) throws JSONException {
			
			JSONObject jsonObj = new JSONObject();
			User user = (User) getApplication();
			String CoreID = user.getId();
			jsonObj.put("Editor", CoreID);
			jsonObj.put("Mode", "add");
			jsonObj.put("BoardNumber", curosr.getString(1));
			jsonObj.put("Master chip on board", curosr.getString(3));
			jsonObj.put("BoardRev", curosr.getString(4));
			jsonObj.put("SchematicRev", curosr.getString(5));
			jsonObj.put("description", curosr.getString(2));
			jsonObj.put("Pic","");
			Log.d(TAG,"JSON: "+jsonObj.toString());
			return jsonObj;
		}

	}

	private class ListTask extends AsyncTask<String, Void, JSONArray> {
		private String address = addr+"FSL_WebServer/MCUs";

		@Override
		protected JSONArray doInBackground(String... params) {
			JSONArray result = new JSONArray();
			HttpClient hc = new DefaultHttpClient();
			JSONObject jsonObj = new JSONObject();
			try {
				if (params[0].equals("search")) {
					jsonObj.put("Search", params[1]);
				} else {
					jsonObj.put("OwnerID", params[0]);
				}
				HttpPost hp = new HttpPost(address);
				hp.setEntity(new StringEntity(jsonObj.toString()));
				HttpResponse response = hc.execute(hp);
				if (response.getStatusLine().getStatusCode() == 200) {
					String mStrResult = EntityUtils.toString(response
							.getEntity());
					Log.d(TAG, mStrResult);
					result = new JSONArray(mStrResult);

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
		 * Parse the json Array
		 * 
		 * @param json
		 * @throws JSONException
		 */
		private void parseArray(JSONArray json) throws JSONException {
			Log.d(TAG, "parse array");
			if (json != null) {
				// iterate json array
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObj = json.optJSONObject(i);
					if (jsonObj == null)
						continue;
					String id = jsonObj.optString("ID");
					Map<String, Object> map = parser2Map(jsonObj);
					mInfos.add(map);
					mList.add(id);
				}
			}
		}

		public Map<String, Object> parser2Map(JSONObject jsonObj)
				throws JSONException {
			Map<String, Object> map = new HashMap<String, Object>();
			Iterator<String> keys = jsonObj.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = jsonObj.get(key).toString();
				map.put(key, value);

			}
			return map;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			super.onPostExecute(result);
			try {
				parseArray(result);
				SimpleAdapter adapter = new SimpleAdapter(ListActivity.this,
						mInfos, R.layout.unitlist, new String[] { "ID",
								"BoardNumber", "Master chip on board",
								"OwnerID" }, new int[] { R.id.tv_lv_id,
								R.id.tv_lv_bn, R.id.tv_lv_mcob, R.id.tv_lv_lo });
				mListView.setAdapter(adapter);
				/*
				 * mListView.setAdapter(new ArrayAdapter(ListActivity.this,
				 * android.R.layout.simple_expandable_list_item_1, mList));
				 */
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// }else
			// mTvDialog.setText("Incorrect CoreID or Password!");

		}
	}
}
