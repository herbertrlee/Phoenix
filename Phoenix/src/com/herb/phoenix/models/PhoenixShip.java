package com.herb.phoenix.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class PhoenixShip
{
	private static final float DEFAULT_AUTO_FIRE_RATE = 0.5f;
	private static final float ENHANCED_AUTO_FIRE_RATE = 0.2f;
	
	public static final int UPGRADE_FIRE_MULTIPLIER = 2;
	
	private static final int STARTING_HP = 20;
	private static final int UPGRADE_HP_BONUS = 10;
	
	private static final float SIZE = 0.4f;
	private static final float Y_POS = 0.1f;
	private static final float X_POS = 3f;
	
	private static final int DEFAULT_STARTING_WEAPONLEVEL = 1;
	public static final float TOP_Y = SIZE + Y_POS;
	
	private static final float SPEED = 0.05f; 
	private static final float UPGRADED_SPEED = 0.075f;
	
	private int money;
	private int totalHP, currentHP;
	private int score;
	public Rectangle bounds = new Rectangle();
	public boolean alive, upgraded;
	
	private Array<Weapon> weapons;
	private WeaponType weaponType;
	private int weaponLevel;
	public float autoFireRate;
	private float speed;
	
	public PhoenixShip()
	{
		money = 0;
		totalHP = STARTING_HP;
		currentHP = STARTING_HP;
		
		center();
		bounds.y = Y_POS;
		bounds.height = SIZE;
		bounds.width = SIZE;
		score = 0;
		alive = true;
		speed = SPEED;
		
		weaponLevel = DEFAULT_STARTING_WEAPONLEVEL;
		weaponType = new WeaponType(weaponLevel, this);
		weapons = weaponType.getWeapons();
				
		autoFireRate = DEFAULT_AUTO_FIRE_RATE;
		upgraded = false;
	}

	public int getMoney()
	{
		return money;
	}

	public void addMoney(int money)
	{
		this.money += money;
	}

	public int getTotalHP()
	{
		return totalHP;
	}

	public void setTotalHP(int totalHP)
	{
		this.totalHP = totalHP;
	}

	public int getCurrentHP()
	{
		return currentHP;
	}

	public void setCurrentHP(int currentHP)
	{
		this.currentHP = currentHP;
	}
	
	public void moveLeft()
	{
		bounds.x -= speed;
	}

	public void moveRight()
	{
		bounds.x += speed;
	}
	
	public float getLeftX(){return bounds.x;}
	public float getRightX(){return bounds.x + bounds.width;}
	
	public float getCenterX(){return bounds.x + bounds.width/2f;}
	public float getTopY(){return bounds.y + bounds.height;}
	
	public float getMidLeftX(){return bounds.x + bounds.width/4f;}
	
	public void hit()
	{
		currentHP--;
		
		if(currentHP <= 0)
		{
			alive = false;
		}
	}

	public int getScore()
	{
		return score;
	}

	public void addScore(int score)
	{
		this.score += score;
	}
	
	public void center()
	{
		bounds.x = X_POS - SIZE/2f;
	}

	public void spendMoney(int cost)
	{
		money -= cost;
	}

	public void repair()
	{
		if(currentHP < totalHP)
			currentHP++;
	}
	
	public void upgradeArmor()
	{
		currentHP += UPGRADE_HP_BONUS;
		totalHP += UPGRADE_HP_BONUS;
	}

	public Array<Weapon> getWeapons()
	{
		return weapons;
	}

	public void setWeaponType(int weaponLevel)
	{
		if(weaponLevel > 0 && weaponLevel < 6)
		{
			this.weaponLevel = weaponLevel;
			weaponType.setWeaponLevel(weaponLevel);
			weapons = weaponType.getWeapons();
		}
	}
	
	public int getWeaponLevel()
	{
		return weaponLevel;
	}
	
	public void upgrade()
	{
		if(!upgraded)
		{
			upgraded = true;
			autoFireRate = ENHANCED_AUTO_FIRE_RATE;
			speed = UPGRADED_SPEED;
			upgradeArmor();
		}
	}
}
