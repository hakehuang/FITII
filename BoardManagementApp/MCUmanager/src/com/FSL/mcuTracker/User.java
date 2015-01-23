package com.FSL.mcuTracker;

import com.FSL.local.database.DBOpenHelper;
import com.FSL.local.database.DataBaseManager;

import android.app.Application;

/**
 * Used to deliver user information
 * @author B53505
 *
 */
public class User extends Application{
	public User(){
		DataBaseManager.initializeInstance(new DBOpenHelper(this));  
	}
	private String ID;
	
	public String getId(){
		return ID;
	}
	public void setId(String ID){
		this.ID = ID;
	}
}
