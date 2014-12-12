package struct;

import net.sf.json.JSONObject;

public class MCUnit {
	private String ID, MC, Description, BoardRev, SchematicRev, Pic, OwnerID,
			OwnerRegisterDate, LastOwner, BoardNumber, LastUpdate;

	public MCUnit(String ID, String Description, String MC, String BoardRev,
			String SchematicRev, String Pic, String OwnerID,
			String OwnerRegisterDate, String LastOwner, String BoardNumber,
			String LastUpdate) {
		this.ID = ID;
		this.MC = MC;
		this.Description = Description;
		this.BoardRev = BoardRev;
		this.SchematicRev = SchematicRev;
		this.Pic = Pic;
		this.OwnerID = OwnerID;
		this.OwnerRegisterDate = OwnerRegisterDate;
		this.LastOwner = LastOwner;
		this.BoardNumber = BoardNumber;
		this.LastUpdate = LastUpdate;
	}

	public String getID() {
		return ID;
	}

	public String getMC() {
		return MC;
	}

	public String getDescription() {
		return Description;
	}

	public String getBoardRev() {
		return BoardRev;
	}

	public String getSchematicRev() {
		return SchematicRev;
	}

	public String getPic() {
		return Pic;
	}

	public String getOwnerID() {
		return OwnerID;
	}

	public String getOwnerRegisterDate() {
		return OwnerRegisterDate;
	}

	public String getLastOwner() {
		return LastOwner;
	}

	public JSONObject getJSON() {
		JSONObject jo = new JSONObject();
		jo.put("ID", ID);
		jo.put("description", Description);
		jo.put("Master chip on board", MC);
		jo.put("BoardRev", BoardRev);
		jo.put("SchematicRev", SchematicRev);
		jo.put("Pic", Pic);
		jo.put("OwnerID", OwnerID);
		jo.put("OwnerRegisterDate", OwnerRegisterDate);
		jo.put("LastOwner", LastOwner);
		jo.put("BoardNumber", BoardNumber);
		jo.put("LastUpdate",LastUpdate);
		return jo;
	}
}
