package com.example.tutorialandroidsocket.Socket.Classes;

import org.json.JSONObject;

public class FileData extends BaseData {

	public String fileName;
	public String Path;
	public long Size;
	public int Type;
	
	public FileData(String ph) {
		Path = ph;
	}
	
	public FileData(String fn, String ph, long s, int t) {
		fileName = fn;
		Path = ph;
		Size = s;
		Type = t;
	}
	 
	@Override
	public JSONObject toJSON() {
		JSONObject json= new JSONObject();
		try {
			json.put("filePath", Path);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
