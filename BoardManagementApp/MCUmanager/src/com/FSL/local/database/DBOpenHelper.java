package com.FSL.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
	private final static String DATABASE = "LocalHost";
	private final String TABLE_NAME = "app_boards";
	private final static int VERSION = 1;

	// build a local database
	public DBOpenHelper(Context context) {
		super(context, DATABASE, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	// create table with field
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("DATABASE","onCreate");
		String query = "CREATE TABLE " + TABLE_NAME
				+ "(_id INTEGER PRIMARY KEY, "
				+ " Board_Number VARCHAR(255),"
				+ " description VARCHAR(255)  NOT NULL,"
				+ " master_chip_on_board VARCHAR(255),"
				+ " Board_Rev VARCHAR(255)," + " Schematic_Rev VARCHAR(255))";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(query);
		onCreate(db);

	}

	public Cursor select(SQLiteDatabase db) {
		Cursor cursor = null;
		if (db.isOpen()) {
			cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		}
		return cursor;
	}

	public Cursor query(SQLiteDatabase db,String[] args) {
		Cursor cursor = null;
		if (db.isOpen()) {
			cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " WHERE Board_Number LIKE ?", args);
		}
		return cursor;
	}

	/**
	 * Insert to table
	 * 
	 * @param args
	 *            :Board_Number, description, master_chip_on_board, Board_Rev,
	 *            Schematic_Rev
	 * @return
	 */
	public void insert(SQLiteDatabase db,String... args) {
		if (db.isOpen()) {
			ContentValues cv = new ContentValues();
			cv.put("Board_Number", args[0]);
			cv.put("description", args[1]);
			cv.put("master_chip_on_board", args[2]);
			cv.put("Board_Rev", args[3]);
			cv.put("Schematic_Rev", args[4]);
			db.insert(TABLE_NAME, null, cv);
		}
	}
    public void delete(SQLiteDatabase db,int id) {  
        String where ="_id = ?";  
        String[] whereValue = { Integer.toString(id) };  
        db.delete(TABLE_NAME, where, whereValue);  
 }  
	public Boolean isExist(SQLiteDatabase db,String board_number){
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
				+ " WHERE Board_Number LIKE ?", new String[]{board_number});
		if(cursor.getCount()!=0)
			return true;
		return false;
	}
}
