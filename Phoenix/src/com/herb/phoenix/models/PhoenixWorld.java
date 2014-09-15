package com.herb.phoenix.models;

import org.json.JSONArray;
import org.json.JSONException;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.herb.game.HerbGame;
import com.herb.game.models.HerbWorld;

public class PhoenixWorld extends HerbWorld
{
	private PhoenixShip ship;
	private Fleet currentFleet;
	
	private PhoenixCampaign campaign;
	private PhoenixLevel currentLevel;
	
	public int level;
	public boolean alive, onLastFleet, onLastLevel;
	
	public Rectangle bounds = new Rectangle();
	
	private Array<PhoenixShot> activeShots = new Array<PhoenixShot>();
	private Array<EnemyShip> activeShips = new Array<EnemyShip>();
	private Array<EnemyShot> activeEnemyShots = new Array<EnemyShot>();
	private Array<Money> activeMoney = new Array<Money>();
	private Array<Explosion> activeExplosions = new Array<Explosion>();
	private Array<Bomb> activeBombs = new Array<Bomb>();
	
	public PhoenixWorld(HerbGame game)
	{
		super(game);
		
		createDemoWorld();
		
		ship = new PhoenixShip();
		currentLevel = campaign.getNextLevel();
		level = currentLevel.getLevel();
		alive = true;
		onLastFleet = false;
		onLastLevel = campaign.onLastLevel();
	}
	
	public PhoenixWorld(HerbGame game, JSONArray campaignJSON)
	{
		super(game);
		
		try
		{
			campaign = new PhoenixCampaign(campaignJSON);
		} catch (JSONException e)
		{
			game.log(e.toString());
		}
		
		ship = new PhoenixShip();
		currentLevel = campaign.getNextLevel();
		level = currentLevel.getLevel();
		alive = true;
		onLastFleet = false;
		onLastLevel = campaign.onLastLevel();
	}

	private void createDemoWorld()
	{
		campaign = PhoenixCampaign.getDemoCampaign();
	}

	public PhoenixShip getShip()
	{
		return ship;
	}

	public Array<PhoenixShot> getActiveShots()
	{
		return activeShots;
	}

	public void setActiveShots(Array<PhoenixShot> activeShots)
	{
		this.activeShots = activeShots;
	}

	public Fleet getCurrentFleet()
	{
		return currentFleet;
	}

	public void setCurrentFleet(Fleet currentFleet)
	{
		this.currentFleet = currentFleet;
	}

	public Array<EnemyShip> getActiveShips()
	{
		return activeShips;
	}
	
	public void updateFleet()
	{
		currentFleet = currentLevel.getNextFleet();
		onLastFleet = currentLevel.isOnLastFleet();
	}

	public Array<EnemyShot> getActiveEnemyShots()
	{
		return activeEnemyShots;
	}

	public void setActiveEnemyShots(Array<EnemyShot> activeEnemyShots)
	{
		this.activeEnemyShots = activeEnemyShots;
	}

	public Array<Money> getActiveMoney()
	{
		return activeMoney;
	}

	public void setActiveMoney(Array<Money> activeMoney)
	{
		this.activeMoney = activeMoney;
	}
	
	public void goToNextLevel()
	{
		ship.center();
		currentLevel = campaign.getNextLevel();
		level = currentLevel.getLevel();
		alive = true;
		onLastFleet = false;
		onLastLevel = campaign.onLastLevel();
	}

	public Array<Explosion> getActiveExplosions()
	{
		return activeExplosions;
	}

	public Array<Bomb> getActiveBombs()
	{
		return activeBombs;
	}
}
