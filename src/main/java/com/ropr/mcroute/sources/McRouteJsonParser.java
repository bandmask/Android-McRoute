package com.ropr.mcroute.sources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ropr.mcroute.models.McRoute;

public class McRouteJsonParser {
	private static Gson gsonParser;
	
	public static McRoute fromJson(String json) {
		try {
			if (gsonParser == null)
				gsonParser = new GsonBuilder().create();
			return gsonParser.fromJson(json, McRoute.class);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String toJson(McRoute route) {
		try {
			if (gsonParser == null)
				gsonParser = new GsonBuilder().create();
			return gsonParser.toJson(route);
		} catch(Exception ex) {
			return null;
		}
	}
}
