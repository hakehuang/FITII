package com.fsl.mcu.kinetislist;

import java.util.ArrayList;

import com.fsl.mcu.kinetislist.R;
import com.fsl.mcu.kinetislist.DBdefine.DeviceRow;
import com.fsl.mcu.kinetislist.DBdefine.HeaderItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ChartActivity extends Activity {
    private static final String TAG = "ChartActivity class";
    private static int openNum = 0;
    private static int positionX = 0;
    private static int positionY = 0;
    
	// Initialize some constants
	private static int PART_NUMBER_WIDTH;
	private static int TABLE_ITEM_HEIGHT;
	private static int HEADER_ITEM_ID;
	private static int densityDpi = 0;
	private static int screenHeight = 0;
	private static int screenWidth = 0;
	private static int screenOrientation;
	private static final String strMINUS1 = String.valueOf("-1");

    private static Context myContext;
    
    private static String showFamily;
    
	/** This is where contains all the header items */
	private static LinearLayout HeaderBar_LinearLayout = null;
	private static int[] headerWidth = null;		// Contains width of each column

	/** This is where contains all the part numbers in vertical */
    private static LinearLayout partNumber_LinearLayout = null;

	/** This is where contains all the table rows */
    private static TableLayout TableContent_TableLayout = null;
    private static MyScrollView vScrollView;
    
    private static MyHscroll header_scroll = null;
    private static MyHscroll table_hscroll = null;
    
	private static int[] CalculateWidth(ArrayList<HeaderItem> hList) {
		DBdefine.HeaderItem hi;
        int numCol = hList.size();
        int[] hw = new int[numCol];
        for (int iCol = 0; iCol < numCol; iCol++) {
        	hi = hList.get(iCol);
        	hw[iCol] = (hi.Width * densityDpi) / 240;
        }
        return hw;
	}
    
    /** Target we publish for clients to send messages to IncomingHandler. */
    final static Messenger mMessenger = new Messenger(new IncomingHandler());

    
    private static final int TABLE_ID = 9999;
	private static final int PART_LIST = 9998;
    
    /** Handler of incoming messages from service. */
    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
			case DBdefine.MSG.MSG_TASK_LIST_CONTENTS:
            	headerWidth = CalculateWidth(DeviceList.headerList);
        		FillHeaderBar(DeviceList.headerList);
        		FillPartNumber(DeviceList.deviceList);
        		Fill_FirstColumn(DeviceList.deviceList);
            	break;
        	case DBdefine.MSG.MSG_FILL_TABLE_LINE:
        		Fill_Lines(msg.arg1, msg.arg2, DeviceList.headerList.size(), DeviceList.deviceList);
        		break;
        	case DBdefine.MSG.MSG_INIT_SCROLL_POSITION:
				header_scroll.scrollTo(msg.arg1, 0);
				table_hscroll.scrollTo(msg.arg1, 0);
				vScrollView.scrollTo(0, msg.arg2);
				break;
            default:
            	super.handleMessage(msg);
            }
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			openNum = savedInstanceState.getInt("OpenTimes", 0);
			positionX = savedInstanceState.getInt("posX", 0);
			positionY = savedInstanceState.getInt("posY", 0);
			Log.i(TAG, "positionX="+positionX+"; positionY="+positionY);
		}
		if (openNum == 0) {	// This is the first time of open this Activity
			// Let's initialize some constants
	        DisplayMetrics metrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        densityDpi = metrics.densityDpi;
	        screenHeight = metrics.heightPixels;
	        screenWidth = metrics.widthPixels;
	        if (screenHeight < screenWidth) {
	        	screenWidth = screenHeight;
	        	screenHeight = metrics.widthPixels;
	        }

	        TABLE_ITEM_HEIGHT = (getResources().getInteger(R.integer.TableItemHeight) * densityDpi) / 240;
	        PART_NUMBER_WIDTH = (getResources().getInteger(R.integer.PartNumberWidth) * densityDpi) / 240;
	        
			HEADER_ITEM_ID = getResources().getInteger(R.integer.HeaderItemId);
		}
		openNum++;

		setContentView(R.layout.selection_chart);
		myContext = this;
		
        final Intent intent = getIntent();
		showFamily = intent.getExtras().getString("Family");
		try {
			DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_CONTENTS, mMessenger, false, showFamily, null, null);
		} catch (InterruptedException e) {
			Toast.makeText(myContext, "Can not get family data from the database.", Toast.LENGTH_LONG).show();
			return;
		}

		LinearLayout theScreen = (LinearLayout)findViewById(R.id.chart);
//// Below lines start to fill the screen
        header_scroll = new MyHscroll(this);
		table_hscroll = new MyHscroll(this);

	//	FillHeaderBar();
        LinearLayout header_bar=(LinearLayout)findViewById(R.id.hearder_bar);
        header_bar.addView(header_scroll, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
        
        HeaderBar_LinearLayout = new LinearLayout(this);
        HeaderBar_LinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        header_scroll.addView(HeaderBar_LinearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));

        vScrollView = new MyScrollView(this);
        vScrollView.setMessenger(mMessenger);
        buildChartTable(vScrollView);
        theScreen.addView(vScrollView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        
	//	FillPartNumber();
////	partNumber_LinearLayout = (LinearLayout)findViewById(PART_LIST);
        partNumber_LinearLayout = (LinearLayout)findViewById(R.id.part_list);
		
	//	FillTable(table_hscroll);
////	LinearLayout table = (LinearLayout)findViewById(TABLE_ID);	// The whole table area frame for horizontal scroll
        LinearLayout table = (LinearLayout)findViewById(R.id.table);	// The whole table area frame for horizontal scroll
		table.addView(table_hscroll, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

		TableContent_TableLayout = new TableLayout(this);	// This is the real table for contents
////	TableContent_TableLayout = new MyTableLayout(this);	// This is the real table for contents
////	TableContent_TableLayout.setMessenger(mMessenger);
		table_hscroll.addView(TableContent_TableLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

		// To implement the joint horizontal scroll of the header row and the table!!
		header_scroll.setBuddy(table_hscroll);	
		table_hscroll.setBuddy(header_scroll);
		
		logoControl();
		
		screenOrientation = getResources().getConfiguration().orientation;
}

/*	<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
			android:id="@+id/table_vscroll"
			android:layout_width="match_parent"
			android:layout_height="wrap_content" >
			<LinearLayout
				android:id="@+id/table"
				android:layout_width="match_parent"
				android:layout_height="wrap_content"
				android:orientation="horizontal" >
				<LinearLayout 
					android:layout_width="wrap_content"
					android:layout_height="wrap_content"
				    android:id="@+id/part_list"
				    android:orientation="vertical">
	<!-- 			    put Part Number row here...... -->
				</LinearLayout>
	<!-- 			Table of Parameters is here -->
			</LinearLayout>
	            
		</ScrollView>
*/	private void buildChartTable(MyScrollView vScrollView) {
		LinearLayout ll_table = new LinearLayout(this);
		ll_table.setOrientation(LinearLayout.HORIZONTAL);
		ll_table.setId(R.id.table);
		vScrollView.addView(ll_table, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		
		LinearLayout ll_list = new LinearLayout(this);
		ll_list.setOrientation(LinearLayout.VERTICAL);
		ll_list.setId(R.id.part_list);
		ll_table.addView(ll_list, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final int MyCODE = getResources().getInteger(R.integer.ChooseColumn_CODE);
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode != MyCODE) || (resultCode != MyCODE))
			return;

		boolean refresh = data.getBooleanExtra("Refresh", false);
		if (refresh == false)
			return;

		InvalideTable();
	}

	private void logoControl() {
		ImageView img = (ImageView)findViewById(R.id.hearder_logo);
		img.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
	            Intent intent = new Intent(ChartActivity.this, ChooseColumns.class);
	            startActivityForResult(intent, getResources().getInteger(R.integer.ChooseColumn_CODE));
				return true;
			}			
		});
	}
	
	private static void FillHeaderBar(ArrayList<HeaderItem> hList) {
	//	long startTime = System.currentTimeMillis();

		View.OnLongClickListener columnSelector = new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View clickedView) {
				TextView tv = (TextView)clickedView;
				final String selectColumn = String.valueOf((String)tv.getTag());
				final String nameColumn = String.valueOf(tv.getText());
				
	            Intent intent = new Intent(myContext, ColumnSelector.class);
	            intent.putExtra("Column", selectColumn);
	            intent.putExtra("Family", showFamily);
	            intent.putExtra("Name", nameColumn);
	            myContext.startActivity(intent);
				return true;
			}
		};
		// This is the handler of the filter and it will control the visibility of columns
//    	ColumnFilter colFilter = new ColumnFilter(myContext, HEADER_ITEM_ID, showFamily);
    	int numColumns = hList.size();
    	DBdefine.HeaderItem hItem;
    	
		ViewGroup.LayoutParams layoutParams = HeaderBar_LinearLayout.getLayoutParams();
		for (int iCol = 2; iCol < numColumns; iCol++) {	// Skip "Family" & "Part"
			hItem = hList.get(iCol);
			TextView headerItem = new TextView(myContext);
			headerItem.setId(HEADER_ITEM_ID+iCol);
			headerItem.setText(hItem.Title);
			headerItem.setTag(hItem.Name);	// This TAG is used for filter to extract the name of the column
			headerItem.setLayoutParams(layoutParams);
			headerItem.setGravity(android.view.Gravity.CENTER);	//_HORIZONTAL|android.view.Gravity.CENTER_VERTICAL);
			headerItem.setWidth(headerWidth[iCol]);
			headerItem.setBackgroundColor(0xFFFF7534);
			headerItem.setTextColor(0xFFFFFFFF);
			headerItem.setPadding(2,1,2,1);
			HeaderBar_LinearLayout.addView(headerItem);
			
			headerItem.setClickable(true);
			headerItem.setOnLongClickListener(columnSelector);		
		}
	}
	
	private static void FillPartNumber(ArrayList<DeviceRow> dList) {
	//	long startTime = System.currentTimeMillis();
		String PartNumberText;
		TextView partText;
		int TextColor;
		int numDevices = dList.size();
		boolean evenOdd = true;
        String SourceName = "";
        boolean flag = true;
		for (int iDev = 0; iDev < numDevices; iDev++){
			PartNumberText = dList.get(iDev).Param[0];
			int index = dList.get(iDev).Param.length;
			String source = dList.get(iDev).Param[index-1];
			//Log.d(TAG,source);
			if (!source.equals(SourceName)&&SourceName!="")	{
                //different source
                flag = !flag;
                SourceName = source;
            }
            if(flag)
                TextColor =  0xFFFF6C00;
            else TextColor = 0xFFFF8E3D;

			partText = new TextView(myContext);
			partText.setText(PartNumberText);
			partText.setGravity(android.view.Gravity.CENTER);	//_HORIZONTAL|android.view.Gravity.CENTER_VERTICAL);
			partText.setWidth(PART_NUMBER_WIDTH);	//R.integer.PartNumberWidth);
			partText.setHeight(TABLE_ITEM_HEIGHT);	//R.integer.TableItemHeight);
			partText.setTextColor(TextColor);
			partText.setPadding(4,1,4,1);
			if (evenOdd)
				partText.setBackgroundColor(0xFFE5E5E5);
			else
				partText.setBackgroundColor(0xFFFFFFFF);
			evenOdd = !evenOdd;
			partNumber_LinearLayout.addView(partText);
		}
	}
	
	public static void Fill_FirstColumn(ArrayList<DeviceRow> dList) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		
		int numDevices = dList.size();
		TableRow tableRow;
		TextView tableItem;
		int iDev = 0;
		for (iDev = 0; iDev < numDevices; iDev++) {
			tableRow = new TableRow(myContext);	// This is one line in the table
			tableRow.setLayoutParams(params);
			tableRow.setGravity(android.view.Gravity.CENTER);
			
			tableItem = new TextView(myContext);
			tableItem.setWidth(2);
			tableItem.setHeight(TABLE_ITEM_HEIGHT);
			tableItem.setBackgroundColor(0xFFC0C0C0);
			tableRow.addView(tableItem);
			TableContent_TableLayout.addView(tableRow);
		}

		if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
			Fill_Lines(0, screenWidth-TABLE_ITEM_HEIGHT, DeviceList.headerList.size(), DeviceList.deviceList);
		else
			Fill_Lines(0, screenHeight-TABLE_ITEM_HEIGHT, DeviceList.headerList.size(), DeviceList.deviceList);
	}
	
	public static void Fill_Lines(int visibleTop, int visibleBottom, int numColumns, ArrayList<DeviceRow> dList) {
		int numDevices = dList.size();

		DeviceRow myDev;
		boolean evenOdd = true;
		int lineColor;
		int lineTop = 0;
		int iDev = 0;
		TableRow tableRow;
		TextView tableItem;
		String showText;
		for (iDev = 0; iDev < numDevices; iDev++) {
			if (((lineTop + TABLE_ITEM_HEIGHT) > visibleTop) && (lineTop < visibleBottom)) {
				tableRow = (TableRow)TableContent_TableLayout.getChildAt(iDev);	// This is one line in the table
				if (tableRow == null)
					continue;
				if (tableRow.getChildCount() <= 1) {
					myDev = dList.get(iDev);
					if (evenOdd)	lineColor = 0xFFE5E5E5;
					else			lineColor = 0xFFFFFFFF;
		
					for (int iCol = 2; iCol < numColumns; iCol++) {	// Skip field of "Family" & "Part " & "Source"
						// Fill each column with texts
						tableItem = new TextView(myContext);
						showText = myDev.Param[iCol];
						if (showText.equals(strMINUS1))
							showText = "-";
						if(showText.contains(".0"))
							showText = showText.replace(".0", "");
						tableItem.setText(showText);
						tableItem.setWidth(headerWidth[iCol]);
						tableItem.setPadding(3,1,2,1);
						tableItem.setHeight(TABLE_ITEM_HEIGHT);	//R.integer.TableItemHeight);
						tableItem.setGravity(android.view.Gravity.CENTER);
						tableItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.vgrey_bar, 0);
						tableItem.setBackgroundColor(lineColor);
						tableRow.addView(tableItem);
					}
				}
			}
			lineTop += TABLE_ITEM_HEIGHT;
			evenOdd = !evenOdd;
		}
		
		if (positionX != 0 || positionY != 0) {
			try {
				mMessenger.send(Message.obtain(null, DBdefine.MSG.MSG_INIT_SCROLL_POSITION, positionX, positionY, null));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			positionX = 0;
			positionY = 0;
		}
	}

	/** To delete all the lines and to be ready to refill them again */
	public static void InvalideTable() {		
		if (HeaderBar_LinearLayout != null)
			HeaderBar_LinearLayout.removeAllViews();
		
		if (partNumber_LinearLayout != null)
			partNumber_LinearLayout.removeAllViews();
		
		if (TableContent_TableLayout != null)
			TableContent_TableLayout.removeAllViews();
		
		try {	// To re-load the database contents
			DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_CONTENTS, mMessenger, false, showFamily, null, null);
		} catch (InterruptedException e) {
			Toast.makeText(myContext, "Can not get data from the database.", Toast.LENGTH_LONG);
			return;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		int posX = header_scroll.getScrollX();
		int posY = vScrollView.getScrollY();
		outState.putInt("posX", posX);
		outState.putInt("posY", posY);
		outState.putInt("OpenTimes", openNum);
		Log.i(TAG, "getScrollX="+header_scroll.getScrollX() + "; getScrollY="+header_scroll.getScrollY());
		super.onSaveInstanceState(outState);
	}

}
