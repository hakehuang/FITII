package com.fsl.mcu.kinetislist;

import java.util.ArrayList;

import com.fsl.mcu.kinetislist.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ChooseColumns extends Activity {
	private static final String TAG = "ChooseColumns class";
    private ArrayList<DBdefine.HeaderItem> headerParam = new ArrayList<DBdefine.HeaderItem>();
    private DBdefine.HeaderItem hItem;
    
	private boolean[] userSelection = null;

	private boolean filterChange;	// To mark if there is any column filter changed.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.select_column);

		try {
			DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_HEADERS, null, true, headerParam, null, null);
		} catch (InterruptedException e) {
			Toast.makeText(ChooseColumns.this, "Can not get header parameters from the database.", Toast.LENGTH_LONG).show();
			return;
		}

		DBdefine.HeaderItem hItem;
    	final int FIRST_BOXID = getResources().getInteger(R.integer.ParamFilterBoxID);
        final int SUM_BUTTONID = getResources().getInteger(R.integer.ParamFilterSummary);
        final int nColumn = headerParam.size();
        userSelection = new boolean[nColumn];
        
		OnCheckedChangeListener checkBox_Listener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				int checkedId = buttonView.getId() - FIRST_BOXID;
				userSelection[checkedId] = isChecked;
			}
		};

		TableLayout table = (TableLayout)findViewById(R.id.column_table);
        int iCol;
        for (iCol = 2; iCol < nColumn; iCol++) {
			LayoutInflater inflater = LayoutInflater.from(this);
			LinearLayout llv = (LinearLayout)inflater.inflate(R.layout.selector_line, table, true);
			
			CheckBox cb = (CheckBox)findViewById(R.id.line_text);
        	hItem = headerParam.get(iCol);
        	
        	cb.setId(FIRST_BOXID + iCol);
        	cb.setText(hItem.Title);
        	userSelection[iCol] = hItem.Visible;
      		cb.setChecked(hItem.Visible);      		
      		cb.setTag(hItem.Name);
      		cb.setOnCheckedChangeListener(checkBox_Listener);

      		Button btn = (Button)findViewById(R.id.line_btn);
      		btn.setId(SUM_BUTTONID+iCol-2);
        }

        Button btnOk = (Button)findViewById(R.id.choose_col_ok);
        btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean change = applyAllChanges();

				if (change || filterChange) {
					Intent intent = new Intent();
					intent.putExtra("Refresh", true);
					setResult(getResources().getInteger(R.integer.ChooseColumn_CODE), intent);
				}
				finish();
			}
        });

        Button btnCancel = (Button)findViewById(R.id.choose_col_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
        });
        
        Button btnSelectAll = (Button) findViewById(R.id.select_allcol);
        btnSelectAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int iCol;
		        for (iCol = 2; iCol < nColumn; iCol++) {
					CheckBox cb = (CheckBox)findViewById(FIRST_BOXID + iCol);
		        	cb.setChecked(true);
		        	userSelection[iCol] = true;
		        }
			}
         });

        Button btnDeselectAll = (Button) findViewById(R.id.deselect_allcol);
        btnDeselectAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int iCol;
		        for (iCol = 2; iCol < nColumn; iCol++) {
					CheckBox cb = (CheckBox)findViewById(FIRST_BOXID + iCol);
		        	cb.setChecked(false);
		        	userSelection[iCol] = false;
		        }
			}
         });

        // Layout the filter buttons
        filterChange = false;
        ArrayList<DBdefine.ColumnFilter> colFilters = new ArrayList<DBdefine.ColumnFilter>();
		try {
			DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_COLUMN_FILTERS, null, true, colFilters, null, null);
		} catch (InterruptedException e) {
			Toast.makeText(ChooseColumns.this, "Can not get header parameters from the database.", Toast.LENGTH_LONG).show();
			return;
		}
		Log.d(TAG,"MSG Filter:"+colFilters.size());
        DBdefine.ColumnFilter clf;
        int nCol = nColumn - 2;	// Skip "Family" and "Part Number"
        for (iCol = 0; iCol < nCol; iCol++) {
       		clf = colFilters.get(iCol);
       		Button filterBtn = (Button)findViewById(SUM_BUTTONID+iCol);
        	filterBtn.setTag(clf.Column);
      		
        	if (clf.Visible == true)	// Do not show the button if there is no filter on this column
        		continue;
        	filterBtn.setVisibility(View.VISIBLE);

        	filterBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Log.d(TAG,"clear column filters");
						DeviceList.addTask(DBdefine.MSG.MSG_TASK_CLEAR_COLUMN_FILTERS, null, true, v.getTag(), null, null);
					} catch (InterruptedException e) {
						Toast.makeText(ChooseColumns.this, "Can not clear column filters from the database.", Toast.LENGTH_LONG).show();
						return;
					}
					v.setVisibility(View.INVISIBLE);	// Clear the button
					filterChange = true;
				}
    		});
        }        
	}

	protected boolean applyAllChanges() {
		boolean Changes = false;
        int iCol;
        int nColumn = headerParam.size();
        for (iCol = 2; iCol < nColumn; iCol++) {
        	hItem = headerParam.get(iCol);
        	if (hItem.Visible != userSelection[iCol]) {
        		hItem.Visible = userSelection[iCol];
        		headerParam.set(iCol, hItem);
        		Changes = true;
        	}        	
        }
		try {
			if (Changes)
				DeviceList.addTask(DBdefine.MSG.MSG_TASK_CHANGE_COLUMN_VISIBLE, null, true, headerParam, null, null);
		} catch (InterruptedException e) {
			Toast.makeText(ChooseColumns.this, "Can not change the visibility of header columns in the database.", Toast.LENGTH_LONG).show();
			return false;
		}
		return Changes;
	}
}