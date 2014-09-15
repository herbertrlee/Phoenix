package com.herb.cards.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpEndpointsHandler
{
	static String url = "";
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    
    static final String AUTHORIZATION = "Authorization";
    static final String BEARER = "Bearer ";
    
	private static String urlBase = "https://herb-cards.appspot.com/_ah/api/cah/v1/";
		
	public HttpEndpointsHandler()
	{
	}
	
	public JSONObject get(String path, String authToken, boolean auth, ArrayList<BasicNameValuePair> values)
	{
		try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            url = urlBase + path;
            
            if(values != null)
           	{
           		for(int i=0;i<values.size();i++)
           		{
           			if(i==0)
           				url += "/?";
           			else
           				url += "&";
           			
           			url += String.format("%s=%s", values.get(i).getName(), values.get(i).getValue());
           		}
           	}
            
           	HttpGet httpGet = new HttpGet(url); 
           	
           	if(auth)
           	{
           		BasicHeader authHeader = new BasicHeader(AUTHORIZATION, BEARER + authToken);
           		httpGet.addHeader(authHeader);
           	}
           	
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
        	Log.e("json", json);
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            return null;
        }
        // return JSON String
        return jObj;
	}
	
	public JSONObject post(String path, String authToken, boolean auth, ArrayList<BasicNameValuePair> values)
	{
		try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            url = urlBase + path;
            
            if(values != null)
           	{
           		for(int i=0;i<values.size();i++)
           		{
           			if(i==0)
           				url += "/?";
           			else
           				url += "&";
           			
           			url += String.format("%s=%s", values.get(i).getName(), values.get(i).getValue());
           		}
           	}
            
            Log.e("url", url);
            
           	HttpPost httpPost = new HttpPost(url); 
           	
           	if(auth)
           	{
           		BasicHeader authHeader = new BasicHeader(AUTHORIZATION, BEARER + authToken);
           		httpPost.addHeader(authHeader);
           	}
           	
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
        	Log.e("json", json);
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            return null;
        }
        // return JSON String
        return jObj;
	}
	
	public JSONObject delete(String path, String authToken, boolean auth, ArrayList<BasicNameValuePair> values)
	{
		try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            url = urlBase + path;
           	
           	if(values != null)
           	{
           		for(int i=0;i<values.size();i++)
           		{
           			if(i==0)
           				url += "/?";
           			else
           				url += "&";
           			
           			url += String.format("%s=%s", values.get(i).getName(), values.get(i).getValue());
           		}
           	}
           	
           	HttpDelete httpDelete = new HttpDelete(url); 
           	
           	if(auth)
           	{
           		BasicHeader authHeader = new BasicHeader(AUTHORIZATION, BEARER + authToken);
           		httpDelete.addHeader(authHeader);
           	}
           	
            HttpResponse httpResponse = httpClient.execute(httpDelete);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
        	Log.e("json", json);
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            return null;
        }
        // return JSON String
        return jObj;
	}
}
