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

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListActivity extends ActionBarActivity {
	private final String TAG = "ListActivity";
	private ArrayList<String> mList = null;
	private ArrayList<Map<String, Object>> mInfos = new ArrayList<Map<String, Object>>();
	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		mList = new ArrayList<String>();
		mListView = (ListView) findViewById(R.id.lv_unit);
		ListTask mTask = new ListTask();

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			mTask.execute("search", bundle.getString("UID"));
		}else{
			User user = (User) getApplication();
			String CoreID = user.getId();
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
	}

	private class ListTask extends AsyncTask<String, Void, JSONArray> {
		private String address = "http://10.192.244.114:8080/FSL_WebServer/MCUs";

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
