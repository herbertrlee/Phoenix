/*
 * PhoenixCampaign.java
 * Model for a Phoenix campaign.  Consists of an array of levels and an index for the current level.
 */

package com.herb.phoenix.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.utils.Array;

public class PhoenixCampaign
{
	//Member variables.
	private Array<PhoenixLevel> levels;
	private int levelIndex;
	
	//Default constructor.  Creates an empty array of levels and sets the levelIndex to zero.
	public PhoenixCampaign()
	{
		levels = new Array<PhoenixLevel>();
		levelIndex = 0;
	}
	
	//Constructor using JsonArray to define the levels.
	public PhoenixCampaign(JSONArray jsonArray) throws JSONException
	{
		//Creates an empty PhoenixCampaign.
		this();
		
		//Inserts a new level for each level in the jsonArray.
		for(int i=0;i<jsonArray.length();i++)
		{
			JSONObject json = jsonArray.getJSONObject(i);
			addLevel(new PhoenixLevel(json));
		}
	}
	
	//Returns the next level and increments the levelIndex.
	public PhoenixLevel getNextLevel()
	{
		PhoenixLevel nextLevel = levels.get(levelIndex);
		levelIndex++;
		return nextLevel;
	}

	//Creates a demo campaign with two levels.
	public static PhoenixCampaign getDemoCampaign()
	{
		PhoenixCampaign demoCampaign = new PhoenixCampaign();
		demoCampaign.addLevel(PhoenixLevel.getDemoLevel(1));
		demoCampaign.addLevel(PhoenixLevel.getDemoLevel(2));
		return demoCampaign;
	}
	
	//Add a new level to the levels array
	public void addLevel(PhoenixLevel level)
	{
		levels.add(level);
	}
	
	//Returns true if the campaign is on the last level.
	public boolean onLastLevel()
	{
		return levelIndex==levels.size;
	}
}
