package com.FSL.local.database;

import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;

public class DataBaseManager {
	 
    private AtomicInteger mOpenCounter = new AtomicInteger();  
  
    private static DataBaseManager instance;  
    private static DBOpenHelper mDatabaseHelper;  
    private SQLiteDatabase mDatabase;  
  
    public static synchronized void initializeInstance(DBOpenHelper helper) {  
        if (instance == null) {  
            instance = new DataBaseManager();  
            mDatabaseHelper = helper;  
        }  
    }  
  
    public static synchronized DataBaseManager getInstance() {  
        if (instance == null) {  
            throw new IllegalStateException(DataBaseManager.class.getSimpleName() +  
                    " is not initialized, call initializeInstance(..) method first.");  
        }  
  
        return instance;  
    }  
  
    public synchronized SQLiteDatabase openDatabase() {  
        if(mOpenCounter.incrementAndGet() == 1) {  
            // Opening new database  
            mDatabase = mDatabaseHelper.getWritableDatabase();  
        }  
        return mDatabase;  
    }  
    public DBOpenHelper getHelper(){
    	return mDatabaseHelper;
    }
    
    public synchronized void closeDatabase() {  
        if(mOpenCounter.decrementAndGet() == 0) {  
            // Closing database  
            mDatabase.close();  
  
        }  
    }  
}
