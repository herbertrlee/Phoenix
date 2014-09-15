package com.herb.phoenix.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;


public class PhoenixShot extends Shot
{	
	public static final Array<Integer> types = new Array<Integer>();
	public static final IntFloatMap widths = new IntFloatMap();
	public static final IntFloatMap heights = new IntFloatMap();
	public static final IntFloatMap speeds = new IntFloatMap();
	public static final IntIntMap damages = new IntIntMap();
	public static final IntMap<String> textures = new IntMap<String>();
	public static final IntMap<Boolean> upgradeables = new IntMap<Boolean>();
	
	public int type;
	public int damage;
	public boolean upgradeable;
	
	public static void initialize(JSONArray shotDefs) throws JSONException
	{
		for(int i=0;i<shotDefs.length();i++)
		{
			JSONObject shotDef = shotDefs.getJSONObject(i);
			int type = shotDef.getInt("type");
			types.add(type);
			
			widths.put(type, (float) shotDef.getDouble("width"));
			heights.put(type, (float) shotDef.getDouble("height"));
			speeds.put(type, (float) shotDef.getDouble("speed"));
			damages.put(type, shotDef.getInt("damage"));
			textures.put(type, shotDef.getString("texture"));
			upgradeables.put(type, shotDef.getBoolean("upgradeable"));
		}
	}
	
	public PhoenixShot()
	{
		this.position = new Vector2();
		this.velocity = new Vector2();
		this.alive = false;
		this.upgradeable = false;
	}

	public void init(float posX, float posY)
	{
		init(posX, posY, 5, 0f);
	}
	
	public void init(float posX, float posY, int type, float angle)
	{
		this.type = type;
		
		float width = widths.get(type, 0.1f);
		float height = heights.get(type, 0.1f);
		
		bounds.set(posX - width/2F, posY, width, height);
		position.set(posX - width/2F, posY);
		velocity.set(0, speeds.get(type, 4f));
		velocity.setAngle(90f + angle);
		this.damage = damages.get(type, 2);
		alive = true;
		upgradeable = upgradeables.get(type);
	}
	
	public void damageMultiply(int multiplier)
	{
		damage*=multiplier;
	}
	
}