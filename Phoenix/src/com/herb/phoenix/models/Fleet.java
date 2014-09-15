/*
 * Fleet.java
 * Model defining a Fleet.  Fleets are rows of ships with offsets determining their starting X position and abovePadding determining the vertical spacing between rows.
 * Fleets are defined in phoenixSettings.json.
 */
package com.herb.phoenix.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.utils.Array;

public class Fleet
{
	//Member variables
	public Array<Array<EnemyShipBuilder>> shipDefRows;
	public Array<Float> offsets;
	public Array<Float> abovePadding;
	public float totalDescent;
	
	//Default constructor.  Creates an empty fleet.
	public Fleet()
	{
		shipDefRows = new Array<Array<EnemyShipBuilder>>();
		offsets = new Array<Float>();
		abovePadding = new Array<Float>();
		totalDescent = 0;
	}
	
	//Json constructor.  Creates a fleet according to a JsonObject.
	public Fleet(JSONObject jsonObject) throws JSONException
	{
		//Create an empty fleet.
		this();
		
		//Get fleet info from jsonobject.
		JSONArray jsonOffsets = jsonObject.getJSONArray("offsets");
		JSONArray jsonAbovePaddings = jsonObject.getJSONArray("abovePaddings");
		JSONArray jsonRows = jsonObject.getJSONArray("rows");
		
		//Define fleet rows
		for(int i=0;i<jsonRows.length();i++)
		{
			offsets.add((float)jsonOffsets.getDouble(i));
			float padding = (float)jsonAbovePaddings.getDouble(i);
			abovePadding.add(padding);
			totalDescent += padding;
			
			//Get a row of ships from the json and create an array of ship types
			JSONArray ships = jsonRows.getJSONArray(i);
			Array<EnemyShipBuilder> row = new Array<EnemyShipBuilder>();
			
			//Add ships to the row
			for(int j=0;j<ships.length();j++)
			{
				row.add(new EnemyShipBuilder(EnemyShip.typeToName.indexOf(ships.getString(j), false)));
			}
			
			//Add new row to the ship definitions
			shipDefRows.add(row);
		}
	}
	
	
}
