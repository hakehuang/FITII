package com.FSL.mcuTracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.FSL.mcuTracker.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends ActionBarActivity{
	private TextView mTvDialog;
	private EditText mEtCoreId,mEtName,mEtPassword,mEtLocation,mEtDeptId,mEtPhone;
	private Button mBtnSubmit;
	private final String TAG = "SignUpActivity";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		mTvDialog = (TextView) findViewById(R.id.tv_s_dialog);
		mEtCoreId = (EditText) findViewById(R.id.et_s_username);
		mEtName = (EditText) findViewById(R.id.et_s_name);
		mEtPassword = (EditText) findViewById(R.id.et_s_password);
		mEtLocation = (EditText) findViewById(R.id.et_s_location);
		mEtDeptId = (EditText) findViewById(R.id.et_s_department);
		mEtPhone = (EditText) findViewById(R.id.et_s_phone);
		mBtnSubmit = (Button) findViewById(R.id.btn_s_register);
		mBtnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SignUpTask mTask = new SignUpTask();
				mTask.execute();
			}
		});

	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && requestCode == RESULT_OK )
        	finish();
    }	
	private class SignUpTask extends AsyncTask<String, Void, String> {
		private JSONObject RegisterJson() throws JSONException{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("CoreID",mEtCoreId.getText().toString());
			jsonObj.put("Password", MainActivity.MD5(mEtPassword.getText().toString()));
			jsonObj.put("Name", mEtName.getText().toString());
			jsonObj.put("DeptID", mEtDeptId.getText().toString());
			jsonObj.put("Location",mEtLocation.getText().toString());
			jsonObj.put("Phone", mEtPhone.getText().toString());
			return jsonObj;
			
		}
		@Override
		protected String doInBackground(String... params) {
			String address = "http://10.192.244.114:8080/FSL_WebServer/Users";
			String result = "";
			HttpClient hc = new DefaultHttpClient();
			try {
				JSONObject jsonObj = RegisterJson();
				HttpPost hp = new HttpPost(address);
				hp.setEntity(new StringEntity(jsonObj.toString()));
				Log.d(TAG,"Test Connection");
				HttpResponse response = hc.execute(hp);
				if (response.getStatusLine().getStatusCode() == 200) {
					String mStrResult = EntityUtils.toString(response
							.getEntity());
					Log.d(TAG, mStrResult);
					JSONObject jsonResult = new JSONObject(mStrResult);
					result = jsonResult.getString("Status");

				} else {
					Log.e(TAG, "Connection Failed!");
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {

				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG,"Test SignUp"+result);
			if(result.equals("Succeed")){
				User user  = (User) getApplication();
				user.setId(mEtCoreId.getText().toString());
				Intent in = new Intent(SignUpActivity.this, IndexActivity.class);
				startActivityForResult(in,0);
				SignUpActivity.this.finish();
			}else{
				mTvDialog.setText(result);
			}
			
		}
	}
}
	
