package com.FSL.mcuTracker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.FSL.mcuTracker.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

/**
 * 
 * @author B53505
 *
 */
public class MainActivity extends ActionBarActivity {
	String CoreID;
	private ArrayList<String> mList = new ArrayList<String>();
	private final String TAG = "MainActivity";
	private TextView mTvDialog;
	private EditText mEtUsername, mEtPassword;
	private Button mBtnLogin, mBtnRegister;
	private LoginTask mTask;
	private PopupWindow mPopup;
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	//private boolean mInitPopup;
	private boolean mShowing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTvDialog = (TextView) findViewById(R.id.tv_dialog);
		mEtUsername = (EditText) findViewById(R.id.et_username);
		mEtUsername.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("TAG","read username");
				if (mList != null && mList.size() > 0 ) {
					//mInitPopup = true;
					initPopup();

					if (mPopup != null) {
						if (!mShowing) {
							mPopup.showAsDropDown(mEtUsername, 0, -5);
							mShowing = true;
						} else {
							mPopup.dismiss();
						}
					}
				}

			}

		});
		mEtPassword = (EditText) findViewById(R.id.et_password);
		// login button
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = (mEtUsername).getText().toString();
				CoreID = username;
				String password = MD5((mEtPassword).getText().toString());
				mTask = new LoginTask();
				mTask.execute(username, password);
			}
		});
		// Register button
		mBtnRegister = (Button) findViewById(R.id.btn_register);
		mBtnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(MainActivity.this, SignUpActivity.class);
				startActivity(in);
			}
		});
		init();

	}

	@SuppressWarnings("unchecked")
	private void init() {
		ObjectInputStream in = null;
		try {
			InputStream is = openFileInput("account");
			in = new ObjectInputStream(is);
			mList = (ArrayList<String>) in.readObject();
			if (mList.size() > 0) {
				String[] userinfo = mList.get(mList.size() - 1).split(":");
				mEtUsername.setText(userinfo[0]);
				mEtPassword.setText(userinfo[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String MD5(String plainText) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && requestCode == RESULT_OK)
			finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG,"write info into account.obj");
		String input = mEtUsername.getText().toString()+":"+mEtPassword.getText().toString();
		mList.remove(input);
		mList.add(input);
		if (mList.size() > 5) {
			mList.remove(0);
		}
		ObjectOutputStream out = null;
		try {
			FileOutputStream os = openFileOutput("account", MODE_PRIVATE);
			out = new ObjectOutputStream(os);
			out.writeObject(mList);
		} catch (Exception e) {

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initPopup() {
		ArrayList<String> username = new ArrayList<String> ();
		for(String s:mList){
			username.add(s.substring(0, s.indexOf(":")));
		}
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, username);
		mListView = new ListView(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String[] userinfo = mList.get(position).split(":");
				mEtUsername.setText(userinfo[0]);
				mEtPassword.setText(userinfo[1]);
				mPopup.dismiss();

			}

		});
		int height = ViewGroup.LayoutParams.WRAP_CONTENT;
		int width = mEtUsername.getWidth();
		System.out.println(width);
		mPopup = new PopupWindow(mListView, width, height, true);
		mPopup.setOutsideTouchable(true);
		mPopup.setBackgroundDrawable(new ColorDrawable(0xffffffff));
		/*
		 * mPopup.setBackgroundDrawable(getResources().getDrawable(
		 * R.drawable.popup_bg));
		 */
		mPopup.setOnDismissListener(new OnDismissListener() {
			public void onDismiss() {
				mShowing = false;
			}
		});
	}

	private class LoginTask extends AsyncTask<String, Void, String> {
		private String address = "http://10.192.244.114:8080/FSL_WebServer/Users";

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpParams hparams = new BasicHttpParams();
			// set connection timeout = 3000 milliseconds
			HttpConnectionParams.setConnectionTimeout(hparams, 3000);
			// The time out for waiting for data
			HttpConnectionParams.setSoTimeout(hparams, 5000);
			HttpClient hc = new DefaultHttpClient(hparams);
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("CoreID", params[0]);
				jsonObj.put("Password", params[1]);

				HttpPost hp = new HttpPost(address);
				hp.setEntity(new StringEntity(jsonObj.toString()));
				HttpResponse response = hc.execute(hp);
				if (response.getStatusLine().getStatusCode() == 200) {
					String mStrResult = EntityUtils.toString(response
							.getEntity());
					Log.d(TAG, mStrResult);
					JSONObject jsonResult = new JSONObject(mStrResult);
					result = jsonResult.getString("SignIn");

				} else {
					Log.e(TAG, "Connection Failed!");
				}

			} catch (UnsupportedEncodingException e) {
				// result = e.getMessage();
				e.printStackTrace();
			} catch (JSONException e) {
				// result = e.getMessage();

				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// result = e.getMessage();

				e.printStackTrace();
			} catch (IOException e) {
				// result = e.getMessage();
				e.printStackTrace();
			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("true")) {
				User user = (User) getApplication();
				user.setId(CoreID);
				Intent in = new Intent(MainActivity.this, IndexActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(in, 0);
				MainActivity.this.finish();
			} else
				mTvDialog.setText("Incorrect CoreID or Password!");

		}
	}
}
