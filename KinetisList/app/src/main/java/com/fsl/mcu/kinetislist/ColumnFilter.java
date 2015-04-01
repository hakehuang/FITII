package com.fsl.mcu.kinetislist;


import com.fsl.mcu.kinetislist.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ColumnFilter implements View.OnLongClickListener {
    private static final String TAG = "ColumnFilter class";
	private Context myContext;
	private String nowFamily;
	
	public ColumnFilter (Context myContext, int id, String family) {
		this.myContext = myContext;
		nowFamily = family;
	}

	@Override
	public boolean onLongClick(View clickedView) {
		final String selectColumn = String.valueOf((String)clickedView.getTag());
		
		try {
			DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_COLUMN_VALUES, null, true, nowFamily, selectColumn,null);
		} catch (InterruptedException e) {
			Toast.makeText(myContext, "Can not get data from the database.", Toast.LENGTH_LONG).show();
			return false;
		//	e.printStackTrace();
		}
				
		final int BASEBOXID = myContext.getResources().getInteger(R.integer.ParamFilterBoxID);

		final int nValues = DeviceList.valueList.size();
		/** This records the changes of each value. Used to compare what are changed when press "OK". */
		final boolean[] valueSelection = new boolean[nValues];
		/** This records the initial setting of each value. Used to compare what are changed when press "OK". */
		final boolean[] dbSelection = new boolean[nValues];

		LayoutInflater factory = LayoutInflater.from(myContext);
		final View dlgView = factory.inflate(R.layout.filter_layout, null);

		OnCheckedChangeListener checkBox_Listener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int checkedId = buttonView.getId()-BASEBOXID;
				valueSelection[checkedId] = isChecked;
			}
		};
		
		final LinearLayout checkGroup = (LinearLayout)dlgView.findViewById(R.id.check_group);
		final CheckBox[] checks = new CheckBox[nValues];
		String strText;
		DBdefine.ColumnValue cValue;
		for (int iBox=0; iBox < nValues; iBox++) {
			CheckBox checkbox = new CheckBox(myContext);
			checkbox.setId(BASEBOXID+iBox);
			cValue = DeviceList.valueList.get(iBox);
			strText = cValue.Text;
			checkbox.setTag(strText);
			if (strText.equals(String.valueOf("-1")))
				strText = "-";
			if (strText.contains(".0"))
				strText = strText.replace(".0", "");
			checkbox.setText(strText);
			checkbox.setOnCheckedChangeListener(checkBox_Listener);
			valueSelection[iBox] = dbSelection[iBox] = cValue.Visible;
			checkbox.setChecked(valueSelection[iBox]);
			checkGroup.addView(checkbox);
			checks[iBox] = checkbox;
		}
		
		Builder dialog= new AlertDialog.Builder(myContext);
		dialog.setTitle("Column: " + selectColumn);
		dialog.setView(dlgView);
		dialog.setNegativeButton("Cancel", null);
		dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean refresh = evaluateChanges();
				if (refresh) {				
					try {
						DeviceList.addTask(DBdefine.MSG.MSG_TASK_CHANGE_COLUMN_VALUES, null, true, selectColumn, null, null);
					} catch (InterruptedException e) {
						Toast.makeText(myContext, "Can not change data from the database.", Toast.LENGTH_LONG).show();
						return;
					}
					ChartActivity.InvalideTable();
				}
			}
			
			private boolean evaluateChanges() {
				boolean refresh = false;
				DBdefine.ColumnValue cv;
				for (int iBox=0; iBox < nValues; iBox++) {
					cv = DeviceList.valueList.get(iBox);
					if (valueSelection[iBox] != dbSelection[iBox]) {
						cv.Visible = valueSelection[iBox];	// The new state
						DeviceList.valueList.set(iBox, cv);	// Change it
						refresh = true;
					}
					else {
						cv.Text = null;
						DeviceList.valueList.set(iBox, cv);	// Dummy remove it
					}
				}
				return refresh;
			}
		});
		
		Button btn_sel = (Button)dlgView.findViewById(R.id.select_all);
		btn_sel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int ibox = 0; ibox < nValues; ibox++) {
					checks[ibox].setChecked(true);
				}
			}
		});
		Button btn_desell = (Button)dlgView.findViewById(R.id.deselect_all);
		btn_desell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int ibox = 0; ibox < nValues; ibox++) {
					checks[ibox].setChecked(false);
				}
			}
		});
		
		dialog.create();
		dialog.show();
		return true;
	}
}
