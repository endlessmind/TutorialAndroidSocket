package com.example.tutorialandroidsocket;

import java.util.ArrayList;

public class Utils {

	/**
	 * Takes a subfolder array and converts it to a single string.
	 * @param data
	 * @return
	 */
	public static String createFolderPathFromArray(ArrayList<String> data) {
		String formatted = "";
		for (String s : data) {
			formatted += "\\" + s;
		}
		
		return formatted;
	}
	
	/**
	 * Format double to 2 decimals
	 * @param dub Double value to reduce to 2 decimals
	 * @return Double with 2 decimal as String
	 */
	private static String formateDouble(double dub) {
		String value = dub + "";
		if (value.length() > value.indexOf('.') +2){
			value = value.substring(0, value.indexOf('.') +2);
		}
		
		return value;
	}
	
	/**
	 * Format bytes to nearest unit prefix
	 * @param size File size in bytes
	 * @return Formatted string
	 */
	public static String FormatFileSize(double size) {
		long KB = 1024;
		long MB = KB*KB;
		long GB = MB * KB;
		if (size < KB) {
			if (size > -1) {
			return (int)size + "b";
			} else {
				return "0b";
			}
		}
		long pre_value = (long) (size / KB);

		if (pre_value < 1000) {
			//Less than 1000KB
			return formateDouble(size/KB) + "Kb";
		} else if (pre_value < 1000000) {
			//Less than 1000MB
			return formateDouble(size/MB) + "Mb";
		} else if (pre_value > 1000000 && pre_value < 100000000) {
			//Less than 1000GB
			return formateDouble(size/MB/KB) + "Gb";
		} else if (pre_value < 10000000000L) {
			//Less than 1000TB
			return formateDouble(size/GB/MB/KB) + "Tb";
		} else {
			return size + "b";
		}

	}
	
}
