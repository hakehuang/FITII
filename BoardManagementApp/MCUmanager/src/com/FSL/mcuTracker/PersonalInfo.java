package com.FSL.mcuTracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.FSL.mcuTracker.UnitManager.picDownload;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PersonalInfo extends ActionBarActivity {
	private TextView mTvCoreID, mTvName, mTvDept, mTvLocation, mTvPhone;
	private final String TAG = "PersonalInfo";
	private Intent intent;
	private Bundle bundle;
	private String coreID;
	private String addr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    addr= prefs.getString("ip","");
		mTvCoreID = (TextView) findViewById(R.id.tv_pi_displayCoreID);
		mTvName = (TextView) findViewById(R.id.tv_pi_displayName);
		mTvDept = (TextView) findViewById(R.id.tv_pi_displaydc);
		mTvLocation = (TextView) findViewById(R.id.tv_pi_displayloc);
		mTvPhone = (TextView) findViewById(R.id.tv_pi_displayph);
		intent = this.getIntent();
		bundle = intent.getExtras();
		coreID = bundle.getString("ID");
		Task mTask = new Task();
		mTask.execute(coreID);
	}

	private class Task extends AsyncTask<String, Void, JSONObject> {
		private String address = addr+"FSL_WebServer/Users";

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject result = new JSONObject();
			HttpClient hc = new DefaultHttpClient();
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("CoreID", params[0]);
				HttpPost hp = new HttpPost(address);
				hp.setEntity(new StringEntity(jsonObj.toString()));
				HttpResponse response = hc.execute(hp);
				if (response.getStatusLine().getStatusCode() == 200) {
					String mStrResult = EntityUtils.toString(response
							.getEntity());
					result = new JSONObject(mStrResult);

				} else {
					Log.e(TAG, "Connection Failed");
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
		 * Update text view to display the information of other user
		 * 
		 * @param json
		 * @throws JSONException
		 */
		private void setView(JSONObject json) throws JSONException {
			Log.d(TAG, "Set View");
			mTvCoreID.setText(coreID);
			mTvName.setText(json.getString("name"));
			mTvDept.setText(json.getString("DeptCode"));
			mTvLocation.setText(json.getString("Location"));
			mTvPhone.setText(json.getString("Phone"));

		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			try {
				setView(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

}
