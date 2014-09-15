/*
 * Handles calls to the web server for content updating.
 */

package com.herb.stinky.pinky.lib;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class SyncHandler
{
	private static final String appUrl = "http://stinky-pinky.appspot.com";
	
	private static final String TAG = "tag";
	
	private static final String REGISTER_DEVICE_TAG = "registerDevice";
	private static final String FETCH_LEVEL_TAG = "fetchLevel";
	private static final String FETCH_ALL_LEVELS_AFTER_TAG = "fetchAllLevelsAfter";
	
	private static final String REG_ID_TAG = "deviceId";
	private static final String LEVEL_TAG = "level";

	
	private JSONParser jsonParser;
	
	public SyncHandler()
	{
		jsonParser = new JSONParser();
	}

	public JSONObject registerDevice(String regid)
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(TAG, REGISTER_DEVICE_TAG));
		params.add(new BasicNameValuePair(REG_ID_TAG, regid));
		JSONObject json = jsonParser.getJSONFromUrl(appUrl, params);
		return json;
	}
	
	public JSONObject fetchLevel(int level)
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(TAG, FETCH_LEVEL_TAG));
		params.add(new BasicNameValuePair(LEVEL_TAG, Integer.toString(level)));
		JSONObject json = jsonParser.getJSONFromUrl(appUrl, params);
		return json;
	}

	public JSONObject fetchAllLevelsAfter(int level)
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(TAG, FETCH_ALL_LEVELS_AFTER_TAG));
		params.add(new BasicNameValuePair(LEVEL_TAG, Integer.toString(level)));
		JSONObject json = jsonParser.getJSONFromUrl(appUrl, params);
		return json;
	}
}
