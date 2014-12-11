package com.FSL.mcuTracker;

import android.app.Application;

/**
 * Used to deliver user information
 * @author B53505
 *
 */
public class User extends Application{
	private String ID;
	
	public String getId(){
		return ID;
	}
	public void setId(String ID){
		this.ID = ID;
	}
}
