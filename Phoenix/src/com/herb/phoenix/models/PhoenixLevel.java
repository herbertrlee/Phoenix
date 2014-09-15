/*
 * PhoenixLevel.java
 * Model defining a single level in Phoenix.  Consists of an array of fleets, the level index, and the fleet index.
 */
package com.herb.phoenix.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.utils.Array;

public class PhoenixLevel
{
	//Member variables
	private int level;
	private Array<Fleet> fleets;
	private int fleetIndex;
	
	//Constructor. Creates an empty level with the given level index
	public PhoenixLevel(int level)
	{
		this.level = level;
		fleetIndex = 0;
		fleets = new Array<Fleet>();
	}
	
	//Constructor.  Creates a level from the given jsonObject.
	public PhoenixLevel(JSONObject jsonObject) throws JSONException
	{
		//Creates an empty level with the given level index.
		this(jsonObject.getInt("level"));
		
		//Reads in fleet data from the jsonObject
		JSONArray jsonArray = jsonObject.getJSONArray("fleets");
		
		//Adds each fleet to the fleets array
		for(int i=0;i<jsonArray.length();i++)
		{
			fleets.add(new Fleet(jsonArray.getJSONObject(i)));
		}
	}

	//Creates a demo level with the given level index.
	public static PhoenixLevel getDemoLevel(int level)
	{
		PhoenixLevel demoLevel = new PhoenixLevel(level);
		//demoLevel.fleets.add(Fleet.createDemoFleet4());
		//demoLevel.fleets.add(Fleet.createDemoFleet1());
		//demoLevel.fleets.add(Fleet.createDemoFleet2());
		//demoLevel.fleets.add(Fleet.createDemoFleet3());
		return demoLevel;
	}

	//level getter
	public int getLevel()
	{
		return level;
	}

	//level setter
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	//Returns the next fleet in fleets, and increments the fleetIndex.
	public Fleet getNextFleet()
	{
		Fleet nextFleet = fleets.get(fleetIndex);
		fleetIndex++;
		return nextFleet;
	}
	
	//Returns true if the level is displaying the final fleet.
	public boolean isOnLastFleet()
	{
		return fleetIndex==fleets.size;
	}
}
