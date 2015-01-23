package struct;

import net.sf.json.JSONObject;

public class Person {
private String CoreID,name,DeptCode,Location,Phone;

	public Person(String CoreID, String name, String DeptCode,String Location,String Phone) {
		this.CoreID = CoreID;
		this.name = name;
		this.DeptCode = DeptCode;
		this.Location = Location;
		this.Phone = Phone;
	}
	public String getCoreID(){
		return CoreID;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeptCode() {
		return DeptCode;
	}

	public String getLocation(){
		return Location;
	}
	public JSONObject getJSON(){
		JSONObject jo = new JSONObject();
		jo.put("name",name);
		jo.put("DeptCode", DeptCode);
		jo.put("Location", Location);
		jo.put("Phone", Phone);
		return jo;
	}
	public String getPhone() {
		return Phone;
	}

}
