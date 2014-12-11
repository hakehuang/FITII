package com.FSL.mcuTracker;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditInfo extends ActionBarActivity {
	private final String TAG = "EditInfo";
	EditText mEtMcob, mEtBr, mEtSr, mEtDes, mEtPic;
	Button mBtnConfirm;
	private Bundle bundle;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);
		mEtMcob = (EditText) findViewById(R.id.et_edit_mcob);
		mEtBr = (EditText) findViewById(R.id.et_edit_br);
		mEtSr = (EditText) findViewById(R.id.et_edit_sr);
		mEtDes = (EditText) findViewById(R.id.et_edit_dcp);
		mEtPic = (EditText) findViewById(R.id.et_edit_pic);
		intent = this.getIntent();
		bundle = intent.getExtras();
		mEtMcob.setText(bundle.getString("Master chip on board"));
		mEtBr.setText(bundle.getString("BoardRev"));
		mEtSr.setText(bundle.getString("SchematicRev"));
		mEtDes.setText(bundle.getString("description"));
		mEtPic.setText(bundle.getString("Pic"));
		mBtnConfirm = (Button) findViewById(R.id.btn_edit_submit);

		mBtnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditTask eTask = new EditTask();
				eTask.execute();
			}

		});
	}
	
	private class EditTask extends AsyncTask<String, Void, String> {
		private String address = "http://10.192.252.162:8080/FSL_WebServer/MCUs";

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient hc = new DefaultHttpClient();
			JSONObject jsonObj;
			try {
				HttpPost hp = new HttpPost(address);
				jsonObj = buildUploadJson();
				hp.setEntity(new StringEntity(jsonObj.toString()));
				HttpResponse response = hc.execute(hp);
				if (response.getStatusLine().getStatusCode() == 200) {
					String mStrResult = EntityUtils.toString(response
							.getEntity());
					Log.d(TAG, mStrResult);
					result = new JSONObject(mStrResult).getString("ID");

				} else {
					Log.e(TAG, "Connection Failed!");
				}
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}

				

			return result;

		}

		private JSONObject buildUploadJson() throws JSONException{
			JSONObject jsonObj = new JSONObject();
			//add new 
			if(bundle.getString("BoardNumber").equals("no record")){
				jsonObj.put("Mode","add");
				jsonObj.put("BoardNumber", bundle.getString("ID"));
			}else{
			//modify info 
				jsonObj.put("ID",bundle.getString("ID"));
				jsonObj.put("Mode", "modify");
			}
			User user = (User) getApplication();
			String CoreID = user.getId();
			jsonObj.put("Editor",CoreID);
			jsonObj.put("Master chip on board", mEtMcob.getText().toString());
			jsonObj.put("BoardRev", mEtBr.getText().toString());
			jsonObj.put("SchematicRev",mEtSr.getText().toString());
			jsonObj.put("description", mEtDes.getText().toString());
			jsonObj.put("Pic", mEtPic.getText().toString());
			return jsonObj;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			Bundle bundle = new Bundle();
			Intent intent = new Intent();
			intent.setClass(EditInfo.this, UnitManager.class);
			bundle.putString("UID",result);
			intent.putExtras(bundle);
			startActivity(intent);
			EditInfo.this.finish();
		}
	}

}
