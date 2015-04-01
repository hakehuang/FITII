package com.fsl.mcu.kinetislist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.xmlpull.v1.XmlPullParserException;

import com.fsl.mcu.kinetislist.R;
import com.fsl.mcu.kinetislist.DBdefine.ColumnValue;
import com.fsl.mcu.kinetislist.DBdefine.HeaderItem;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseManager";
	private static final boolean RESET_DATABASE = true;

	/** The database that the provider uses as its underlying data store */
	private static final String DATABASE_NAME = "Devices.db";

	/** The database version */
	private static final int DATABASE_VERSION = 1;

	/**
	 * A projection map used to select columns from the database
	 */
	private static LinkedHashMap<String, String> sHeadersProjectionMap;
	private static LinkedHashMap<String, String> sDevicesProjectionMap;
	private static LinkedHashMap<String, String> sColumnsProjectionMap;

	static {
		/*
		 * Creates and initializes a projection map that returns all columns
		 */
		// Creates a new projection map instance. The map returns a column name
		// given a string. The two are usually equal.
		sHeadersProjectionMap = new LinkedHashMap<String, String>();

		// Maps the string "_ID" to the column name "_ID"
		sHeadersProjectionMap.put(DBdefine.Header._ID, DBdefine.Header._ID);
		// Maps "Name" to "Name"
		sHeadersProjectionMap.put(DBdefine.Header.COLUMN_NAME_NAME,
				DBdefine.Header.COLUMN_NAME_NAME);
		// Maps "Title" to "Title"
		sHeadersProjectionMap.put(DBdefine.Header.COLUMN_NAME_TITLE,
				DBdefine.Header.COLUMN_NAME_TITLE);
		// Maps "type" to "type"
		sHeadersProjectionMap.put(DBdefine.Header.COLUMN_NAME_TYPE,
				DBdefine.Header.COLUMN_NAME_TYPE);
		// Maps "width" to "width"
		sHeadersProjectionMap.put(DBdefine.Header.COLUMN_NAME_WIDTH,
				DBdefine.Header.COLUMN_NAME_WIDTH);
		// Maps "show" to "show"
		sHeadersProjectionMap.put(DBdefine.Header.COLUMN_NAME_SHOW,
				DBdefine.Header.COLUMN_NAME_SHOW);

		sDevicesProjectionMap = new LinkedHashMap<String, String>();
		// sDevicesProjectionMap.put(DBdefine.Devices._ID,
		// DBdefine.Devices._ID);

		sColumnsProjectionMap = new LinkedHashMap<String, String>();
		sColumnsProjectionMap.put(DBdefine.Column._ID, DBdefine.Column._ID);
		sColumnsProjectionMap.put(DBdefine.Column.COLUMN_NAME_NAME,
				DBdefine.Column.COLUMN_NAME_NAME);
		sColumnsProjectionMap.put(DBdefine.Column.COLUMN_NAME_TYPE,
				DBdefine.Column.COLUMN_NAME_TYPE);
		sColumnsProjectionMap.put(DBdefine.Column.COLUMN_NAME_TEXT,
				DBdefine.Column.COLUMN_NAME_TEXT);
		sColumnsProjectionMap.put(DBdefine.Column.COLUMN_NAME_VALUE,
				DBdefine.Column.COLUMN_NAME_VALUE);
		sColumnsProjectionMap.put(DBdefine.Column.COLUMN_NAME_SHOW,
				DBdefine.Column.COLUMN_NAME_SHOW);
	}

	private static ArrayList<DBdefine.xHeaderItem> xmlHeader = null;
	private static ArrayList<String[]> xmlDevice = null;

	private Context context;

	DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		XmlResourceParser xmlParser = context.getResources().getXml(R.xml.test);
		XmlResourceParser xml = context.getResources().getXml(R.xml.newfile);
		ParseXmlResource PXR = new ParseXmlResource(xmlParser,xml);
		try {
			xmlHeader = new ArrayList<DBdefine.xHeaderItem>();
			xmlDevice = new ArrayList<String[]>();
			PXR.getData(xmlHeader, xmlDevice);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		fillTable_Header(db);
		fillTable_Devices(db);
		fillTable_Column(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Kills all the tables and existing data
		db.execSQL("DROP TABLE IF EXISTS " + DBdefine.Header.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBdefine.Column.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBdefine.Devices.TABLE_NAME);

		// Recreates the database with a new version
		onCreate(db);

		// Ping: Should consider to upgrade the database.
	}

	// Create "header" table. Then to fill it with data from XML file
	private void fillTable_Header(SQLiteDatabase db) {
		Log.d(TAG, "Fill Header Table");
		db.execSQL("CREATE TABLE " + DBdefine.Header.TABLE_NAME + " ("
				+ DBdefine.Header._ID + " INTEGER PRIMARY KEY,"
				+ DBdefine.Header.COLUMN_NAME_NAME + " TEXT,"
				+ DBdefine.Header.COLUMN_NAME_TITLE + " TEXT,"
				+ DBdefine.Header.COLUMN_NAME_TYPE + " TEXT,"
				+ DBdefine.Header.COLUMN_NAME_WIDTH + " INTEGER,"
				+ DBdefine.Header.COLUMN_NAME_SHOW + " BOOLEAN" + ");");
		int iColumn;
		int numColumn = xmlHeader.size();
		for (iColumn = 0; iColumn < numColumn; iColumn++) {
			DBdefine.xHeaderItem xHeader = xmlHeader.get(iColumn);
			ContentValues values = new ContentValues();
			values.put(DBdefine.Header.COLUMN_NAME_NAME, xHeader.Name);
			values.put(DBdefine.Header.COLUMN_NAME_TITLE, xHeader.Title);
			values.put(DBdefine.Header.COLUMN_NAME_TYPE, xHeader.Type);
			values.put(DBdefine.Header.COLUMN_NAME_WIDTH, xHeader.Width);
			values.put(DBdefine.Header.COLUMN_NAME_SHOW, xHeader.Visible);
            if(xHeader.Name == "Marking Line1[//Line2]"){
                Log.d(TAG,"init filter:"+xHeader.Visible);
            }
			db.insert(DBdefine.Header.TABLE_NAME, null, values);
		}
	}

	private void fillTable_Devices(SQLiteDatabase db) {
		Log.d(TAG, "Fill Devices Table");
		String columnList = new String("");
		int iColumn, numColumn = xmlHeader.size();
		for (iColumn = 0; iColumn < numColumn; iColumn++) {
			DBdefine.xHeaderItem xHeader = xmlHeader.get(iColumn);
			if (xHeader.Type.equals("INTEGER"))
				columnList += ", `" + xHeader.Name + "` INTEGER";
			else
				columnList += ", `" + xHeader.Name + "` TEXT";
		}
		columnList +=", `SourceID` TEXT";
		db.execSQL("CREATE TABLE " + DBdefine.Devices.TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY" + columnList + ");");

		ContentValues values = new ContentValues();
		int iDev, numDev = xmlDevice.size();
		for (iDev = 0; iDev < numDev; iDev++) {
			String[] xDev = xmlDevice.get(iDev);
			for (iColumn = 0; iColumn < numColumn; iColumn++) {
				DBdefine.xHeaderItem xHeader = xmlHeader.get(iColumn);

				if (xHeader.Type.equals("INTEGER")) {
					Integer number = 0;
					try {
						String intStr = xDev[iColumn].trim();
						if (intStr.equals("-"))
							number = -1;
						else
							number = Integer.valueOf(intStr.replace(".0", ""));
					} catch (NumberFormatException ne) {
						ne.printStackTrace();
					}
					values.put("`" + xHeader.Name + "`", number);
				} else
					values.put("`" + xHeader.Name + "`", xDev[iColumn]);
			}
			values.put("`SourceID`", xDev[iColumn]);
			//Log.d(TAG,"Put sourceID: "+xDev[iColumn]);
			db.insert(DBdefine.Devices.TABLE_NAME, null, values);
		}
	}

	private void fillTable_Column(SQLiteDatabase db) {
		Log.d(TAG, "Fill Column Table");
		db.execSQL("CREATE TABLE " + DBdefine.Column.TABLE_NAME + " ("
				+ DBdefine.Column._ID + " INTEGER PRIMARY KEY,"
				+ DBdefine.Column.COLUMN_NAME_NAME + " TEXT,"
				+ DBdefine.Column.COLUMN_NAME_TYPE + " TEXT,"
				+ DBdefine.Column.COLUMN_NAME_TEXT + " TEXT,"
				+ DBdefine.Column.COLUMN_NAME_VALUE + " INTEGER,"
				+ DBdefine.Column.COLUMN_NAME_SHOW + " BOOLEAN" + ");");

		int iColumn;
		int numColumn = xmlHeader.size();
		for (iColumn = 2; iColumn < numColumn; iColumn++) { // skip the "Family"
															// and "Part"
			DBdefine.xHeaderItem xHeader = xmlHeader.get(iColumn);
			ContentValues values = new ContentValues();

			// Look for all available values in the column
			String[] projection = { "`" + xHeader.Name + "`" };
			//Log.e(TAG, "Projection:" + projection[0]);
			String orderBy = "`" + xHeader.Name + "`" + " ASC";
			Cursor cursor = db.query(true, DBdefine.Devices.TABLE_NAME,
					projection, null, null, null, null, orderBy, null);
			assert (cursor != null);
			cursor.moveToFirst();
			do {
				values.put(DBdefine.Column.COLUMN_NAME_NAME, xHeader.Name);
				values.put(DBdefine.Column.COLUMN_NAME_TYPE, xHeader.Type);

				if (xHeader.Type.equals(String.valueOf("STRING"))) { // check
																		// the
																		// value
																		// of
																		// DBdefine.Header.COLUMN_NAME_TYPE
					values.put(DBdefine.Column.COLUMN_NAME_TEXT,
							cursor.getString(0));
					values.put(DBdefine.Column.COLUMN_NAME_VALUE, 0);
				} else {
					values.put(DBdefine.Column.COLUMN_NAME_TEXT,
							cursor.getString(0));
					values.put(DBdefine.Column.COLUMN_NAME_VALUE,
							cursor.getInt(0));
				}
				values.put(DBdefine.Column.COLUMN_NAME_SHOW, true); // default
																	// value is
																	// to show
																	// this.
				db.insert(DBdefine.Column.TABLE_NAME, null, values);
			} while (cursor.moveToNext());
		}
	}

	public void initDBM() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (RESET_DATABASE)
			onUpgrade(db, 0, 0);
		else
			try { // To test if the all the table are exist.
				db.rawQuery("SELECT COUNT(*) FROM "
						+ DBdefine.Header.TABLE_NAME, null);
				db.rawQuery("SELECT COUNT(*) FROM "
						+ DBdefine.Column.TABLE_NAME, null);
				db.rawQuery("SELECT COUNT(*) FROM "
						+ DBdefine.Devices.TABLE_NAME, null);
			} catch (Exception exp) {
				onUpgrade(db, 0, 0); // To rebuild all the tables.
			}

		ArrayList<DBdefine.HeaderItem> tableHeader = new ArrayList<DBdefine.HeaderItem>();
		readHeaderList(tableHeader, true);
		buildDevicesProjectionMap(tableHeader);
		db.close();
	}

	public void getListData(String family,
			ArrayList<DBdefine.HeaderItem> hList,
			ArrayList<DBdefine.DeviceRow> dList) {
		if (hList.size() != 0) {
			hList.clear();
			dList.clear();
		}

		readHeaderList(hList, false);
		readDeviceList(family, hList, dList);
	}

	public void readHeaderList(ArrayList<DBdefine.HeaderItem> HeaderList,
			boolean All) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteQueryBuilder qbHeader = new SQLiteQueryBuilder();
		qbHeader.setTables(DBdefine.Header.TABLE_NAME);
		qbHeader.setProjectionMap(sHeadersProjectionMap);
		String[] projection = { DBdefine.Header.COLUMN_NAME_NAME,
				DBdefine.Header.COLUMN_NAME_TITLE,
				DBdefine.Header.COLUMN_NAME_TYPE,
				DBdefine.Header.COLUMN_NAME_WIDTH,
				DBdefine.Header.COLUMN_NAME_SHOW };
		Cursor cursor = null;
		if (All)
			cursor = qbHeader.query(db, projection, null, null, null, null,
					DBdefine.Header.DEFAULT_SORT_ORDER);
		else {
			String selection = DBdefine.Header.COLUMN_NAME_SHOW + " <> ?";
			String[] selectionArgs = { "0" }; // Collect all non visible columns
			cursor = qbHeader.query(db, projection, selection, selectionArgs,
					null, null, DBdefine.Header.DEFAULT_SORT_ORDER);
		}
		assert (cursor != null);

		int iHeader, nHeader = cursor.getCount();
		cursor.moveToFirst();
		for (iHeader = 0; iHeader < nHeader; iHeader++) {
			DBdefine.HeaderItem headerItem = new DBdefine.HeaderItem();
			headerItem.Name = cursor.getString(0);
			headerItem.Title = cursor.getString(1);
			headerItem.Type = cursor.getString(2);
			headerItem.Width = cursor.getInt(3);
			headerItem.Visible = (cursor.getInt(4) != 0);
			HeaderList.add(headerItem);
			cursor.moveToNext();
		}
		db.close();
	}

	private void buildDevicesProjectionMap(
			ArrayList<DBdefine.HeaderItem> HeaderList) {
		sDevicesProjectionMap.clear();
		int iHeader, nHeader = HeaderList.size();
		DBdefine.HeaderItem headerItem;
		for (iHeader = 0; iHeader < nHeader; iHeader++) {
			headerItem = HeaderList.get(iHeader);
			sDevicesProjectionMap.put("`" + headerItem.Name + "`", "`"
					+ headerItem.Name + "`");
		}
		sDevicesProjectionMap.put("`SourceID`", "`SourceID`");
	}

	private void readDeviceList(String sFamily,
			ArrayList<DBdefine.HeaderItem> HeaderList,
			ArrayList<DBdefine.DeviceRow> devList) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteQueryBuilder qbColumn = new SQLiteQueryBuilder();
		qbColumn.setTables(DBdefine.Column.TABLE_NAME);
		qbColumn.setProjectionMap(sColumnsProjectionMap);
		String[] projection = { DBdefine.Column.COLUMN_NAME_NAME,
				DBdefine.Column.COLUMN_NAME_TYPE,
				DBdefine.Column.COLUMN_NAME_TEXT,
				DBdefine.Column.COLUMN_NAME_VALUE };
		String selection = DBdefine.Column.COLUMN_NAME_SHOW + " = ?";
		String[] selectionArgs = { "0" };
		// Look for all invisible values.
		Cursor cursor = null;
		cursor = qbColumn.query(db, projection, selection, selectionArgs, null,
				null, DBdefine.Column.DEFAULT_SORT_ORDER);
		assert (cursor != null);

		// Build up the query condition to exclude those invisible values.
		String filterType;
		StringBuilder condition = new StringBuilder();
		ArrayList<String> conditionArgs = new ArrayList<String>();
		if (sFamily.equals(String.valueOf("*")) == false) {
			condition.append("SubFamily = ?");
			conditionArgs.add(sFamily);
		}
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			do {
				if (condition.length() != 0)
					condition.append(" AND ");
				condition.append("`" + cursor.getString(0) + "` <> ?");
				filterType = cursor.getString(1);
				if (filterType.equals(String.valueOf("INTEGER")))
					conditionArgs.add(cursor.getString(3));
				else
					conditionArgs.add(cursor.getString(2));
			} while (cursor.moveToNext());
		}

		SQLiteQueryBuilder qbDevice = new SQLiteQueryBuilder();
		qbDevice.setTables(DBdefine.Devices.TABLE_NAME);
		qbDevice.setProjectionMap(sDevicesProjectionMap);
		cursor = null;
		if (condition.length() == 0) {
			selection = null;
			selectionArgs = null;
		} else {
			selection = condition.toString();
			selectionArgs = new String[conditionArgs.size()];
			conditionArgs.toArray(selectionArgs);
		}
		int nColumn = HeaderList.size();
		String[] projectionIn = new String[nColumn+1];
		int iCol;
		for (iCol = 0; iCol < nColumn; iCol++)
			projectionIn[iCol] = "`" + HeaderList.get(iCol).Name.trim() + "`";
		projectionIn[iCol] = "`SourceID`";
		// cursor =
		// db.rawQuery("SELECT (MK Part Number, SubFamily, Silicon Revision[s], Marking Line1[//Line2], Estimated MK Availability Date, Recommended K2 Part Number Recommended new L Part Number, MK Superset Part Number, PK Superset Part Number, PK Superset Availability Date, Suggested Evaluation Hardware, 10Ku S/R, CPU, CPU Frequency [MHz], Total Flash Memory [KB], Flash [KB], FlexNVM[KB], EEPROM/FlexRAM [KB], SRAM [KB], Boot ROM [KB], Cache [Bytes], DSP, MPU, Peripheral XBAR, DMA [channels], Pin Count, Package, Dimensions [xyz][mm], Pitch [mm], UART, UART Notes, SPI [no. of modules], SPI [chip selects], I2C, I2S, CAN, FlexIO, USB OTG LS/FS, USB OTG LS/FS/HS, USB Notes, Ethernet w/1588, External Bus Interface [Flexbus], DDR Controller, NAND Flash Controller, Enhanced SDHC, Segment LCD, Graphic LCD, TSI[Capacitive Touch] channels, Total GPIOS, GPIO w interrupt/high drive pins, High Drive GPIOs [mA], 5V IOS, Total 16bit PWM Channels, General Purpose PWM [6ch/2ch], Motor Control General purpose PWM [8ch/6ch/2ch], Quad decoder General purpose PWM [2ch], Additional Timers/PWM channel, Additional Timers/PWM Type, Total 16bit timer channels, Low Power Timer, QuadTimer [4ch], 32bit Periodic Interupt Timer [PIT], RTC [32KHz Osc, Vbat], RTC_CLKOUT/RTC_WAKEUP, Pulse Width Timer, CMT[Carrier Module Transmitter], FTM External CLK, Programmable Delay Block, Watchdog [SW/HW], Power Management Controller [PMC], Multi Clock Generator [MCG], 48MHz IRC, intelligent Low Power Oscillator  [iLPO] [~1KHz], Main OSC, Secondary OSC, No. of ADC modules [16bit/12bit/24bit], ADC0 channels [SE/DP], ADC0 [resolution], ADC1 Channels [SE/DP], ADC1 [resolution], ADC2 Channels [SE/DP], ADC2 [resolution], ADC3 Channels [SE/DP], ADC3 [resolution], ADC4 channels [SE/DP], ADC4 [resolution], Total ADC DP [Channels], Total ADC SE [Channels], PGA, DAC [6bit/12bit], Notes, Analog Comparator, Analog Comparator Inputs, OPAMP, TRIAMP, Vref, Random Number Generator, Memory mapped Crytpo Acceleration unit, Tamper Detect [DryIce], Number of External Tamper Pins, Passive Tamper Pins, Active Tamper Pins, CRC, Debug, Trace, Serial Programming Interface, True opendrain, BME [Bit Manipulation Engine], NMI, Voltage Range [V], Flash Write V, Temp Range [C], Data Sheet Document Number FROM Devices) WHERE (SubFamily = ?) ORDER BY `MK Part Number` ASC",
		// null);
		// cursor = db.query(true, DBdefine.Devices.TABLE_NAME, projection,
		// null, null, null, null, orderBy, null);
		cursor = qbDevice.query(db, projectionIn, selection, selectionArgs,
				null, null, DBdefine.Devices.DEFAULT_SORT_ORDER);

		assert (cursor != null);

		int iDevice, nDevice = cursor.getCount();
		int iParam, nParam = cursor.getColumnCount();
		cursor.moveToFirst();
		for (iDevice = 0; iDevice < nDevice; iDevice++) {
			DBdefine.DeviceRow Device = new DBdefine.DeviceRow(nParam);
			for (iParam = 0; iParam < nParam; iParam++)
				Device.Param[iParam] = cursor.getString(iParam);
			devList.add(Device);
			cursor.moveToNext();
		}
		db.close();
	}
	public void getSources(ArrayList<String> sourceList,ArrayList<Integer> cntList){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		String sql = "SELECT SourceID, COUNT(*) FROM "
				+ DBdefine.Devices.TABLE_NAME + " GROUP BY SourceID ORDER BY COUNT(*) DESC";
		cursor = db.rawQuery(sql, null);
		Log.d(TAG,"num: "+cursor.getCount());
		int sources = cursor.getCount();
		int total = 0;
		cursor.moveToFirst();
		for (int i = 0; i < sources; i++) {
			String str = cursor.getString(0);
			int count = cursor.getInt(1);
			if (str != null) {
				sourceList.add(str);
				cntList.add(count);
			}
			total += count;
			cursor.moveToNext();
		}
		sourceList.add("All parts");
		cntList.add(total);
		db.close();
	}
	public void getFamilies(String SourceID,ArrayList<String> familyList,
			ArrayList<Integer> cntList) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		String sql;
		if(SourceID.equals("All parts"))
			 sql = "SELECT SubFamily, COUNT(*) FROM "
					+ DBdefine.Devices.TABLE_NAME + " GROUP BY SubFamily";
		else
			sql = "SELECT SubFamily, COUNT(*) FROM "
					+ DBdefine.Devices.TABLE_NAME + " WHERE SourceID = '"+SourceID+"' GROUP BY SubFamily";
		
		cursor = db.rawQuery(sql, null);
		assert (cursor != null);

		int iFamily, nFamily = cursor.getCount();
		int total = 0;
		cursor.moveToFirst();
		for (iFamily = 0; iFamily < nFamily; iFamily++) {
			String str = cursor.getString(0);
			int count = cursor.getInt(1);
			if (str != null) {
				familyList.add(str);
				cntList.add(count);
			}
			total += count;
			cursor.moveToNext();
		}
		familyList.add("All families");
		cntList.add(total);
		db.close();
	}

	/*
	 * public ArrayList<DBdefine.ColumnValue> getColumnValues(
	 * ArrayList<DBdefine.HeaderItem> HeaderList, ArrayList<DBdefine.DeviceRow>
	 * devList, String selColumn) { ArrayList<String> valueList = new
	 * ArrayList<String>(); int col_num = 0; // get col_num for(int iCol = 0;
	 * iCol < HeaderList.size();iCol++){
	 * if(HeaderList.get(iCol).Name.equals(selColumn)) col_num = iCol; }
	 * for(DBdefine.DeviceRow Device:devList){
	 * if(!valueList.contains(Device.Param[col_num]))
	 * valueList.add("'"+Device.Param[col_num]+"'"); } Log.d(TAG,
	 * valueList.toString()); SQLiteDatabase db = this.getReadableDatabase();
	 * Cursor cursor = null; StringBuilder strSQL = new StringBuilder();
	 * strSQL.append("SELECT " + DBdefine.Column.COLUMN_NAME_TEXT + ", " +
	 * DBdefine.Column.COLUMN_NAME_VALUE + ", " +
	 * DBdefine.Column.COLUMN_NAME_SHOW); strSQL.append(" FROM " +
	 * DBdefine.Column.TABLE_NAME); strSQL.append(" WHERE " +
	 * DBdefine.Column.COLUMN_NAME_NAME + " = ? "); strSQL.append(" AND " +
	 * DBdefine.Column.COLUMN_NAME_TEXT + " IN ");
	 * strSQL.append(valueList.toString().replace("[", "(").replace("]", ")"));
	 * strSQL.append(" ORDER BY "+DBdefine.Column.COLUMN_NAME_VALUE+" ASC");
	 * String[] selectionArgs = { selColumn }; Log.e(TAG,strSQL.toString());
	 * cursor = db.rawQuery(strSQL.toString(), selectionArgs); assert(cursor !=
	 * null);
	 * 
	 * ArrayList<DBdefine.ColumnValue> cvArray = new
	 * ArrayList<DBdefine.ColumnValue>(); DBdefine.ColumnValue cv;
	 * cursor.moveToFirst(); do { cv = new DBdefine.ColumnValue(); cv.Text =
	 * cursor.getString(0); cv.Value = cursor.getInt(1); cv.Visible =
	 * (cursor.getInt(2) != 0); cvArray.add(cv); } while (cursor.moveToNext());
	 * db.close();
	 * 
	 * return cvArray; }
	 */
	public ArrayList<DBdefine.ColumnValue> getColumnValues(String strFamily,
			String selColumn) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteQueryBuilder qbColumn = new SQLiteQueryBuilder();
		qbColumn.setTables(DBdefine.Column.TABLE_NAME);
		qbColumn.setProjectionMap(sColumnsProjectionMap);
		String[] projection = { DBdefine.Column.COLUMN_NAME_NAME,
				DBdefine.Column.COLUMN_NAME_TYPE,
				DBdefine.Column.COLUMN_NAME_TEXT,
				DBdefine.Column.COLUMN_NAME_VALUE };
		String selection = DBdefine.Column.COLUMN_NAME_SHOW + " = ? AND "
				+ DBdefine.Column.COLUMN_NAME_NAME + " != ? ";
		String[] selectionArgs = { "0", selColumn };
		// Look for all invisible values.
		Cursor cursor = null;
		cursor = qbColumn.query(db, projection, selection, selectionArgs, null,
				null, DBdefine.Column.DEFAULT_SORT_ORDER);
		assert (cursor != null);

		// Build up the query condition to exclude those invisible values.
		String filterType;
		StringBuilder condition = new StringBuilder();
		ArrayList<String> SeleArgs = new ArrayList<String>();
		SeleArgs.add(selColumn);
		StringBuilder strSQL = new StringBuilder();
		strSQL.append("SELECT " + DBdefine.Column.COLUMN_NAME_TEXT + ", "
				+ DBdefine.Column.COLUMN_NAME_VALUE + ", "
				+ DBdefine.Column.COLUMN_NAME_SHOW);
		strSQL.append(" FROM " + DBdefine.Column.TABLE_NAME);
		strSQL.append(" WHERE " + DBdefine.Column.COLUMN_NAME_NAME + " = ? ");
		if (strFamily.equals(String.valueOf("*")) == false) {
			strSQL.append(" AND " + DBdefine.Column.COLUMN_NAME_TEXT + " IN ");
			strSQL.append("(SELECT `" + selColumn + "`");
			strSQL.append(" FROM " + DBdefine.Devices.TABLE_NAME
					+ " WHERE SubFamily = ? ");
			SeleArgs.add(strFamily);
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				do {
					if (strSQL.length() != 0)
						strSQL.append(" AND ");
					strSQL.append("`" + cursor.getString(0) + "` <> ?");
					filterType = cursor.getString(1);
					if (filterType.equals(String.valueOf("INTEGER")))
						SeleArgs.add(cursor.getString(3));
					else
						SeleArgs.add(cursor.getString(2));
				} while (cursor.moveToNext());
			}
			strSQL.append(" ) ORDER BY " + DBdefine.Column.COLUMN_NAME_VALUE
					+ " ASC");
			// String[] selectionArgs = { selColumn, strFamily };
/*			selectionArgs = new String[SeleArgs.size()];
			SeleArgs.toArray(selectionArgs);
			Log.d(TAG, strSQL.toString());
			Log.d(TAG, selectionArgs.toString());
			cursor = db.rawQuery(strSQL.toString(), selectionArgs);*/
		} else if (cursor.getCount() != 0) {
			strSQL.append(" AND " + DBdefine.Column.COLUMN_NAME_TEXT + " IN ");
			strSQL.append("(SELECT `" + selColumn + "`");
			strSQL.append(" FROM " + DBdefine.Devices.TABLE_NAME + " WHERE ");
			Boolean flag = true;
			cursor.moveToFirst();
			do {
				if (strSQL.length() != 0 && !flag)
					strSQL.append(" AND ");
				strSQL.append("`" + cursor.getString(0) + "` <> ?");
				filterType = cursor.getString(1);
				if (filterType.equals(String.valueOf("INTEGER")))
					SeleArgs.add(cursor.getString(3));
				else
					SeleArgs.add(cursor.getString(2));
				flag = false;
			} while (cursor.moveToNext());
			strSQL.append(" ) ORDER BY " + DBdefine.Column.COLUMN_NAME_VALUE
					+ " ASC");
			// String[] selectionArgs = { selColumn, strFamily };
			Log.e(TAG, strSQL.toString());
		}
		selectionArgs = new String[SeleArgs.size()];
		SeleArgs.toArray(selectionArgs);
		cursor = db.rawQuery(strSQL.toString(), selectionArgs);
		assert (cursor != null);

		ArrayList<DBdefine.ColumnValue> cvArray = new ArrayList<DBdefine.ColumnValue>();
		DBdefine.ColumnValue cv;
		cursor.moveToFirst();
		do {
			cv = new DBdefine.ColumnValue();
			cv.Text = cursor.getString(0);
			cv.Value = cursor.getInt(1);
			cv.Visible = (cursor.getInt(2) != 0);
			cvArray.add(cv);
		} while (cursor.moveToNext());
		db.close();

		return cvArray;
	}

	public void setColumnValues(ArrayList<ColumnValue> cvArray, String selColumn) {
		setColumnValues(cvArray, selColumn, false);
		setColumnValues(cvArray, selColumn, true);
	}

	private void setColumnValues(ArrayList<ColumnValue> cvArray,
			String selColumn, boolean showState) {
		SQLiteDatabase db = this.getReadableDatabase();
		String strType = "SELECT DISTINCT " + DBdefine.Column.COLUMN_NAME_TYPE
				+ " FROM " + DBdefine.Column.TABLE_NAME + " WHERE "
				+ DBdefine.Column.COLUMN_NAME_NAME + " = ?";

		String[] args = { selColumn };
		Cursor cursor = db.rawQuery(strType, args);
		assert (cursor != null);
		cursor.moveToFirst();
		boolean columnIsValue = (cursor.getString(0).equals(String
				.valueOf("INTEGER")));

		StringBuilder strWhere = new StringBuilder();
		StringBuilder strParam = new StringBuilder();
		ColumnValue cv;
		int icv, ncv = cvArray.size();
		ArrayList<String> strArgs = new ArrayList<String>();
		String[] whereArgs = null;

		// Update visibility values for "false" first.
		strWhere.append(DBdefine.Column.COLUMN_NAME_NAME + "=? AND (");
		strArgs.add(selColumn);
		for (icv = 0; icv < ncv; icv++) {
			cv = cvArray.get(icv);
			if (cv.Text == null)
				continue;
			if (cv.Visible == showState) {
				if (strParam.length() != 0)
					strParam.append(" OR ");
				if (columnIsValue) {
					strParam.append(DBdefine.Column.COLUMN_NAME_VALUE + "=?");
					strArgs.add(String.valueOf(cv.Value));
				} else {
					strParam.append(DBdefine.Column.COLUMN_NAME_TEXT + "=?");
					strArgs.add(cv.Text);
				}
			}
		}
		if (strParam.length() != 0) {
			strWhere.append(strParam.toString() + ")");

			ContentValues updateValues = new ContentValues();
			updateValues.put(DBdefine.Column.COLUMN_NAME_SHOW, showState);
			whereArgs = new String[strArgs.size()];
			strArgs.toArray(whereArgs);
			db.update(DBdefine.Column.TABLE_NAME, updateValues,
					strWhere.toString(), whereArgs);
		}
		db.close();
	}

	/** Find out what are the Columns have non-visible values */
	public void getColumnFilters(ArrayList<DBdefine.ColumnFilter> filterList) {
		Log.d(TAG, "Get Data");
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		String strSQL = "SELECT DISTINCT " + DBdefine.Column.COLUMN_NAME_NAME
				+ ", " + DBdefine.Column.COLUMN_NAME_SHOW + " FROM "
				+ DBdefine.Column.TABLE_NAME + " ORDER BY _id";
		Log.d(TAG, "SQL: " + strSQL);
		cursor = db.rawQuery(strSQL, null);
		assert (cursor != null);

		DBdefine.ColumnFilter clf;
		DBdefine.ColumnFilter clf0 = new DBdefine.ColumnFilter();
		clf0.Column = "";
		clf0.Visible = true;
		int iclf = 0;
		cursor.moveToFirst();
		do {
			clf = new DBdefine.ColumnFilter();
			clf.Column = cursor.getString(0);
			clf.Visible = (cursor.getInt(1) != 0);
			if (clf.Column.equals(clf0.Column)) {
				if (clf0.Visible == true)
					filterList.set(iclf - 1, clf);
				continue;
			}
			filterList.add(clf);
			clf0.Column = clf.Column;
			clf0.Visible = clf.Visible;
			iclf++;
		} while (cursor.moveToNext());
		db.close();
		Log.d(TAG, "Clf:" + filterList.size());
	}

	public void clearColumnFilters(String theColumn) {
		Log.d(TAG, "clear filters in database");
		/* Example: UPDATA Column set Show=1 WHERE Column="RAM"; */
		SQLiteDatabase db = this.getReadableDatabase();
		String strWhere = DBdefine.Column.COLUMN_NAME_NAME + "=?";
		String[] whereArgs = { theColumn };
		ContentValues updateValues = new ContentValues();
		updateValues.put(DBdefine.Column.COLUMN_NAME_SHOW, true);
		db.update(DBdefine.Column.TABLE_NAME, updateValues, strWhere, whereArgs);
		db.close();
	}

	public void updateColumnVisibility(ArrayList<HeaderItem> hvList) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues updateValues = new ContentValues();
		updateValues.put(DBdefine.Header.COLUMN_NAME_SHOW, true);
		// To set all the column to be visible first
		db.update(DBdefine.Header.TABLE_NAME, updateValues, null, null);

		StringBuilder whereStr = new StringBuilder();
		ArrayList<String> whereArgs = new ArrayList<String>();
		int iItem, nItem = hvList.size();
		HeaderItem hItem;
		for (iItem = 0; iItem < nItem; iItem++) {
			hItem = hvList.get(iItem);
			if (hItem.Visible == false) {
				if (whereStr.length() != 0)
					whereStr.append(" OR ");
				whereStr.append(DBdefine.Header.COLUMN_NAME_NAME + "=?");
				whereArgs.add(hItem.Name);
			}
		}
		if (whereStr.length() != 0) {
			updateValues.clear();
			updateValues.put(DBdefine.Header.COLUMN_NAME_SHOW, false);
			String[] args = new String[whereArgs.size()];
			whereArgs.toArray(args);
			db.update(DBdefine.Header.TABLE_NAME, updateValues,
					whereStr.toString(), args);
		}
		db.close();
	}
}
