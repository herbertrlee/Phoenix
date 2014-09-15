package com.herb.phoenix.models;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;

public class WeaponType
{
	private int weaponLevel;
	
	private PhoenixShip ship;
	
	private enum Position
	{
		CENTER, LEFT, RIGHT
	}
	
	private static final Map<String, Position> stringToPosition = new HashMap<String, Position>();
	static
	{
		stringToPosition.put("center", Position.CENTER);
		stringToPosition.put("left", Position.LEFT);
		stringToPosition.put("right", Position.RIGHT);
	};
	
	public static final IntArray weaponLevels = new IntArray();
	public static final IntMap<String> nameMap = new IntMap<String>();
	public static final IntMap<String> descriptionMap = new IntMap<String>();
	public static final IntIntMap costMap = new IntIntMap();
	private static final IntMap<IntArray> weaponTypeMap = new IntMap<IntArray>();
	private static final IntMap<FloatArray> shotAngleMap = new IntMap<FloatArray>();
	private static final IntMap<Array<Position>> shotPositionMap = new IntMap<Array<Position>>();
	
	public static void initialize(JSONArray weaponDefs) throws JSONException
	{
		for(int i=0;i<weaponDefs.length();i++)
		{
			JSONObject weaponDef = weaponDefs.getJSONObject(i);
			int weaponLevel = weaponDef.getInt("weaponLevel");
			weaponLevels.add(weaponLevel);
			
			nameMap.put(weaponLevel, weaponDef.getString("name"));
			descriptionMap.put(weaponLevel, weaponDef.getString("description"));
			costMap.put(weaponLevel, weaponDef.getInt("cost"));
			
			IntArray weaponTypes = new IntArray();
			FloatArray shotAngles = new FloatArray();
			Array<Position> shotPositions = new Array<Position>();
			
			JSONArray weapons = weaponDef.getJSONArray("weapons");
			
			for(int j=0;j<weapons.length();j++)
			{
				JSONObject weapon = weapons.getJSONObject(j);
				
				weaponTypes.add(weapon.getInt("weaponType"));
				shotAngles.add((float) weapon.getDouble("angle"));
				shotPositions.add(stringToPosition.get(weapon.getString("position")));
			}
			
			weaponTypeMap.put(weaponLevel, weaponTypes);
			shotAngleMap.put(weaponLevel, shotAngles);
			shotPositionMap.put(weaponLevel, shotPositions);
		}
	}
		
	public WeaponType(int weaponLevel, PhoenixShip ship)
	{
		this.weaponLevel = weaponLevel;
		this.ship = ship;
	}
	
	public void setWeaponLevel(int weaponLevel)
	{
		this.weaponLevel = weaponLevel;
	}
	
	public Array<Weapon> getWeapons()
	{
		Array<Weapon> weapons = new Array<Weapon>();
		
		IntArray weaponArray = weaponTypeMap.get(weaponLevel);
		FloatArray shotAngleArray = shotAngleMap.get(weaponLevel);
		Array<Position> positionArray = shotPositionMap.get(weaponLevel);
		
		for(int i=0;i<weaponArray.size;i++)
		{
			float position = getPosition(positionArray.get(i));
			Weapon weapon = new Weapon(weaponArray.get(i), shotAngleArray.get(i), position);
			
			weapons.add(weapon);
			
		}
		
		return weapons;
	}

	private float getPosition(Position position)
	{
		float x = 0;
		
		switch(position)
		{
			case CENTER:
				x = ship.bounds.width/2f;
				break;
			case LEFT:
				x = 0;
				break;
			case RIGHT:
				x = ship.bounds.width;
				break;
			default:
				break;
		};
				
		return x;
	}
}
