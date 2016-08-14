package com.example.tutorialandroidsocket.Socket.Classes;

import org.json.JSONObject;

public class BaseSocketObject {

	public String request;
	public BaseData data;
	
	
	public String toJSON() {
		JSONObject json= new JSONObject();
		try {
			json.put("request", request);
			if (data != null)
				json.put("data", data.toJSON());
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] toByte() {
		return toJSON().getBytes();
	}

}
