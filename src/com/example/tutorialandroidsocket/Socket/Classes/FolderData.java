package com.example.tutorialandroidsocket.Socket.Classes;

import org.json.JSONObject;

public class FolderData extends BaseData {

	public String folder;

	public FolderData(String f) {
		folder = f;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json= new JSONObject();
		try {
			json.put("folder", folder);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
