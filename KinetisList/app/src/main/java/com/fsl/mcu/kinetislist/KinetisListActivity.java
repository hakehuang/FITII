package com.fsl.mcu.kinetislist;

import java.util.ArrayList;

import com.fsl.mcu.kinetislist.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class KinetisListActivity extends Activity {
    private static final String TAG = "KinetisListActivity class";

    private static DeviceList devList;
    
    /** Target we publish for clients to send messages to IncomingHandler. */
    final Handler myHandler = new IncomingHandler();
    final Messenger mMessenger = new Messenger(myHandler);
    
    private static ArrayList<String> familyList;
    private static int selectedFamily = 0;
    private static int seletedSource = 0;
    private String[] sources;
    private static ProgressBar loadingBar;
    private static TextView loadingText;
    private static Button browseButton;
    private static ListView SourceList;
    /** Handler of incoming messages from service. */
    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DBdefine.MSG.MSG_TASK_LIST_FAMILIES:
            	Log.i(TAG, "DBdefine.MSG_TASK_LIST_FAMILIES received.");
            	Log.i(TAG, Thread.currentThread().getName() + " is trigging.");
        		familyList = (ArrayList<String>) msg.obj;
        		break;
            default:
            	super.handleMessage(msg);
            }
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kinetis_list);
		
		loadingBar = (ProgressBar)findViewById(R.id.loading_progress);
		loadingText = (TextView)findViewById(R.id.loading_text);
		browseButton = (Button)findViewById(R.id.browsing);
        SourceList = (ListView)findViewById(R.id.SourceList);
	    browseButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v){
/*	    		ArrayList<Integer> countList = new ArrayList<Integer>();
	    		ArrayList<String> sourceList = new ArrayList<String>();
				try {
					DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_SOURCES, mMessenger, true, sourceList,countList,null);
				} catch (InterruptedException e) {
					Toast.makeText(KinetisListActivity.this, "Can not get data from the database.", Toast.LENGTH_LONG);
					return;
				//	e.printStackTrace();
				}
				int nSource = sourceList.size();
				final String[]  sources= new String[nSource];
				sourceList.toArray(sources);
				String[] showList = new String[nSource];
				for (int i=0; i < nSource; i++) {
					showList[i] = sourceList.get(i) + " (" + countList.get(i) + ")";
				}
				AlertDialog.Builder sourceDlg = new AlertDialog.Builder(KinetisListActivity.this);
				sourceDlg.setTitle("Select a parts");
				sourceDlg.setSingleChoiceItems(showList, seletedSource, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						seletedSource = which;
					}
				});
				sourceDlg.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {*/
                seletedSource = SourceList.getCheckedItemPosition();
						familyList = new ArrayList<String>();
						ArrayList<Integer> countList = new ArrayList<Integer>();
						try {
							DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_FAMILIES, mMessenger, true, familyList, countList,sources[seletedSource]);
						} catch (InterruptedException e) {
							Toast.makeText(KinetisListActivity.this, "Can not get data from the database.", Toast.LENGTH_LONG);
							return;
						//	e.printStackTrace();
						}

						int nFamily = familyList.size();
						final String[] families = new String[nFamily];
						familyList.toArray(families);
						String[] showList = new String[nFamily];
						for (int ifm=0; ifm < nFamily; ifm++) {
							showList[ifm] = familyList.get(ifm) + " (" + countList.get(ifm) + ")";
						}
						AlertDialog.Builder familyDlg = new AlertDialog.Builder(KinetisListActivity.this);
						familyDlg.setTitle("Select a family");
						familyDlg.setSingleChoiceItems(showList, selectedFamily, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								selectedFamily = which;
							}
						});
						familyDlg.setPositiveButton("OK", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
					            Intent intent = new Intent(KinetisListActivity.this, ChartActivity.class);
					            Bundle bundle = new Bundle();
					            if (families[selectedFamily].equals("All families"))
					            	bundle.putString("Family", "*");
					            else
					            	bundle.putString("Family", (String) families[selectedFamily]);
					            intent.putExtras(bundle);
					            startActivity(intent);
							}
						});
						familyDlg.create().show();
						
					}
				});
				/*sourceDlg.create().show();

				
	    	}
	    });*/

		// Initialize the database in the background
		new Thread(new Runnable() {
			@Override
			public void run() {
				// It is a lengthy procedure of loading database in the first time.
			    devList = new DeviceList(KinetisListActivity.this);
			    new Thread(devList).start();

			    myHandler.post(new Runnable() {
					@Override
					public void run() {
						loadingBar.setVisibility(View.GONE);
						loadingText.setVisibility(View.GONE);
                        setListView();
					    browseButton.setVisibility(View.VISIBLE);
					}
			    });
			}
		}).start();
	}
    public void setListView(){
        ArrayList<Integer> countList = new ArrayList<Integer>();
        ArrayList<String> sourceList = new ArrayList<String>();
        try {
            DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_SOURCES, mMessenger, true, sourceList,countList,null);
        } catch (InterruptedException e) {
            Toast.makeText(KinetisListActivity.this, "Can not get data from the database.", Toast.LENGTH_LONG);
            return;
            //	e.printStackTrace();
        }
        int nSource = sourceList.size();
        sources= new String[nSource];
        sourceList.toArray(sources);
        String[] showList = new String[nSource];
        for (int i=0; i < nSource; i++) {
            showList[i] = sourceList.get(i) + " (" + countList.get(i) + ")";
        }
        SourceList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice ,showList));
        SourceList.setSelection(1);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kinetis_list, menu);
		return true;
	}

}
