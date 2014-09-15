/*
 * EnemyShip.java
 * Model defining an enemy ship.
 * Enemy ships are poolable objects.  Do not call constructor directly, set up a pool and use obtain()
 */

package com.herb.phoenix.models;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.herb.phoenix.controllers.PhoenixController;

public class EnemyShip implements Poolable
{
	//Static class constants
	private static final float DESCEND_SPEED = -2f;
	public static final float SQUARE_SIDE = 0.4f;
	private static final float ROTATE_INTERVAL = 1f;
	private static final float PHASE_INTERVAL = 3f;
	private static final float REFLECT_INTERVAL = 5f;
	private static final float UNSTEALTH_DURATION = 1f;
	
	/*
	 * MovementType:
	 * Square - ship moves clockwise in a set square pattern with side length of SQUARE_SIDE
	 * Line - ship moves horizontally in a line
	 * Bounce - ship moves erratically and bounces off the walls and the world's equator
	 * Loop - ship moves clockwise around the borders of the world and the world's equator
	 */
	public enum MovementType
	{
		SQUARE, LINE, BOUNCE, LOOP
	};
	
	/*
	 * ShotType:
	 * Normal - shot goes straight down
	 * Angled - shot fired at random downward angles
	 * Aimed - shot fired semi-aimed at the player
	 * Spawn - instead of firing a normal shot, enemy spawns a ship
	 * Bomb - fires a bomb straight down
	 */
	public enum ShotType
	{
		NORMAL, ANGLED, AIMED, SPAWN, BOMB
	};
	
	/*
	 * Special: 
	 * Phasing - Alternates between in phase and out of phase.  Shots will pass through out of phase ships.
	 * Reflect - Alternates between reflecting and not reflecting.  Shots that hit reflecting ships will be
	 * reflected on the shooter.
	 * Stealth - Ships are invisible, except for a second after shooting.  They can still be hit, though.
	 * Bomb - On death, ship will drop towards player and explode.
	 * Last Boss - Last boss will go between phasing, reflect, and vulnerable.  It will also continuously spawn
	 * various minions, increasing in dangerousness as the fight goes on.
	 */
	public enum Special
	{
		NONE, PHASING, REFLECT, STEALTH, BOMBER, LAST_BOSS
	};
	
	
	/*
	 * State:
	 * Starting - The ship is currently descending from its initial position.  Might not be visible yet.
	 * Loop - The ship has reached its terminal Y position and is going through its loop movements
	 */
	public enum State
	{
		STARTING, LOOP
	};
	
	//Static class variables
	public static int spawnType = -1;
	
	//Static class definitions of ships.  Correspond to values from phoenixSettings.json.
	public static Array<String> typeToName = new Array<String>();
	public static Array<Rectangle> typeToBounds = new Array<Rectangle>();
	public static Array<Float> typeToShotFrequency = new Array<Float>();
	public static Array<Integer> typeToMaxHP = new Array<Integer>();
	public static Array<Integer> typeToScoreValue = new Array<Integer>();
	public static Array<MovementType> typeToMovementType = new Array<MovementType>();
	public static Array<ShotType> typeToShotType = new Array<ShotType>();
	public static Array<Integer> typeToNumberOfShots = new Array<Integer>();
	public static Array<Array<String>> typeToTextures = new Array<Array<String>>();
	public static Array<Special> typeToSpecial = new Array<Special>();
	public static Array<Integer> spawnables = new Array<Integer>();
	public static Array<Double> typeToMoneyDropChance = new Array<Double>();
	public static Array<Integer> typeToMoneyDropAmount = new Array<Integer>();
	public static Map<String, MovementType> stringToMovementTypeMap = new HashMap<String, MovementType>();
	public static Map<String, ShotType> stringToShotTypeMap = new HashMap<String, ShotType>();
	public static Map<String, Special> stringToSpecialMap = new HashMap<String, Special>();
	
	//Initialize the string-to-enum maps.
	static
	{
		stringToMovementTypeMap.put("square", MovementType.SQUARE);
		stringToMovementTypeMap.put("line", MovementType.LINE);
		stringToMovementTypeMap.put("bounce", MovementType.BOUNCE);
		stringToMovementTypeMap.put("loop", MovementType.LOOP);
		
		stringToShotTypeMap.put("normal", ShotType.NORMAL);
		stringToShotTypeMap.put("angled", ShotType.ANGLED);
		stringToShotTypeMap.put("aimed", ShotType.AIMED);
		stringToShotTypeMap.put("spawn", ShotType.SPAWN);
		
		stringToSpecialMap.put("none", Special.NONE);
		stringToSpecialMap.put("phasing", Special.PHASING);
		stringToSpecialMap.put("reflect", Special.REFLECT);
		stringToSpecialMap.put("stealth", Special.STEALTH);
		stringToSpecialMap.put("bomber", Special.BOMBER);
		stringToSpecialMap.put("lastBoss", Special.LAST_BOSS);
	};
		
	//Take in a JSONArray (from phoenixSettings.json) and add the ship definitions to the static ship definition arrays.
	public static void initialize(JSONArray shipDefs) throws JSONException
	{
		for(int i=0;i<shipDefs.length();i++)
		{
			JSONObject shipDef = shipDefs.getJSONObject(i);
			
			typeToName.add(shipDef.getString("name"));
			if(shipDef.getString("name").equals("spawn"))
				spawnType = i;
			typeToBounds.add(new Rectangle(0f, 0f, (float)shipDef.getDouble("width"), (float)shipDef.getDouble("height")));
			typeToShotFrequency.add((float)shipDef.getDouble("shotFrequency"));
			typeToMaxHP.add(shipDef.getInt("maxHP"));
			typeToScoreValue.add(shipDef.getInt("scoreValue"));
			typeToMovementType.add(stringToMovementTypeMap.get(shipDef.getString("movementType")));
			typeToShotType.add(stringToShotTypeMap.get(shipDef.getString("shotType")));
			typeToNumberOfShots.add(shipDef.getInt("numberOfShots"));
			typeToMoneyDropChance.add(shipDef.getDouble("moneyDropChance"));
			typeToMoneyDropAmount.add(shipDef.getInt("moneyDropAmount"));
			
			JSONArray shipTextures = shipDef.getJSONArray("textures");
			Array<String> textureStrings = new Array<String>();
			
			for(int j=0;j<shipTextures.length();j++)
			{
				textureStrings.add(shipTextures.getString(j));
			}
			
			typeToTextures.add(textureStrings);
			typeToSpecial.add(stringToSpecialMap.get(shipDef.getString("special")));
			
			boolean spawnable = shipDef.getBoolean("spawnable");
			if(spawnable)
			{
				spawnables.add(i);
			}
		}
	}
	
	//Return a random spawn type from all of the possible spawn types.
	public static int getRandomSpawnable()
	{
		return spawnables.random();
	}
	
	//Member variables
	public Vector2 position, velocity;
	public Rectangle worldBounds;
	public float totalDescent, sideDistance, currentDescent;
	private float shotClock, shotFrequency;
	public Rectangle bounds = new Rectangle();
	public boolean alive, firing, containsMoney, visible;
	public int type, numberOfShots;
	public State state;
	public int totalHP, currentHP;
	public int scoreValue, moneyValue;
	public float alpha;
	public MovementType movementType;
	public ShotType shotType;
	public Array<Float> firePorts;
	private float rotateClock = 0f;
	private float specialClock = 0f;
	public boolean inPhase, reflectOn;
	public Special special;
	public int nextSpawn;
	private Array<String> shipTextures;
	private int textureIndex;
	public String currentTexture;
	
	//Constructor.  Initializes variables.
	public EnemyShip()
	{
		position = new Vector2();
		velocity = new Vector2();
		alive = false;
		firePorts = new Array<Float>();
	}
	
	//Initializes the EnemyShip.
	public void init(float posX, float posY, float totalDescent, Rectangle worldBounds, int type)
	{
		//Sets the type and makes the ship alive
		this.type = type;
		this.state = State.STARTING;
		this.alive = true;

		//Initialize variables
		containsMoney = false;
		inPhase = true;
		sideDistance = 0;
		currentDescent = 0f;
		textureIndex = 0;
		visible = true;
		alpha = 1f;
		
		//Get the appropriate size from the map and puts the ship in the right position.
		bounds.set(typeToBounds.get(type));
		position.set(posX - bounds.width/2f, posY);
		bounds.x = position.x;
		bounds.y = position.y;
		
		//Starts the ship moving downwards.  Set totalDescent.
		velocity.set(0, DESCEND_SPEED);
		this.totalDescent = totalDescent;
		
		
		//Gets the shot frequency from the map and initializes the shotClock to a random number.
		shotFrequency = typeToShotFrequency.get(type);
		shotClock = getRandFloat(0f, shotFrequency);
		
		//Initialize worldBounds
		this.worldBounds = worldBounds;
		special = typeToSpecial.get(type);
		
		reflectOn = special.equals(Special.REFLECT);
		
		//Initialize total and current HP to the ship type's max HP
		totalHP = typeToMaxHP.get(type);
		currentHP = totalHP;
		
		//Initialize these variables
		scoreValue = typeToScoreValue.get(type);
		shotType = typeToShotType.get(type);
		movementType = typeToMovementType.get(type);
		numberOfShots = typeToNumberOfShots.get(type);
		shipTextures = typeToTextures.get(type);
		currentTexture = shipTextures.get(0);
		
		//Define the nextSpawn as the current spawntype.  Only matters for ships with Spawn fire type
		nextSpawn = spawnType;
		
		//Create the firePorts
		float firePortDeltaX = bounds.width/((float)(numberOfShots+1));
		float firePortX = firePortDeltaX;
		
		for(int i=0;i<numberOfShots;i++)
		{
			firePorts.add(firePortX);
			firePortX += firePortDeltaX;
		}
		
		//Roll for whether the ship contains money
		float moneyRoll = getRandFloat(0f, 1f);
		if(moneyRoll < typeToMoneyDropChance.get(type))
			containsMoney = true;
		
		//Initialize moneyValue
		moneyValue = typeToMoneyDropAmount.get(type);
		
		//Initialize specialClock
		switch (special)
		{
		case BOMBER:
			break;
		case LAST_BOSS:
			specialClock = 0f;
			break;
		case PHASING:
			specialClock = getRandFloat(0f, PHASE_INTERVAL);
			break;
		case REFLECT:
			specialClock = getRandFloat(0f, REFLECT_INTERVAL);
			break;
		case STEALTH:
			specialClock = UNSTEALTH_DURATION;
			break;
		default:
			break;
		}
	}
	
	//Update the ship
	public void update(float delta)
	{
		//Update the position
		Vector2 v = velocity.cpy();
		position.add(v.scl(delta));
		bounds.x = position.x;
		bounds.y = position.y;
		
		currentDescent -= v.y;
		
		//Update the shotClock
		shotClock += delta;
		
		//If the shotClock is up, set firing to true and reset shotClock
		if(shotClock >= shotFrequency)
		{
			shotClock = getRandFloat(0f, shotFrequency);
			firing = true;
		}
		
		//Update the velocity
		switch(movementType)
		{
			case SQUARE:
				standardUpdate(v);
				break;
			case LINE:
				bossUpdate();
				break;
			case BOUNCE:
				bounceUpdate();
				break;
			case LOOP:
				loopUpdate(delta);
				break;
			default:
				break;	
		}
		
		//Update the special attributes
		switch(special)
		{
			case PHASING:
				phaseUpdate(delta);
				break;
			case REFLECT:
				reflectUpdate(delta);
				break;
			case STEALTH:
				stealthUpdate(delta);
				break;
			default:
				break;
		
		}
	}

	//Stealths the ship if the specialClock has expired and the ship is visible.
	private void stealthUpdate(float delta)
	{
		if(state.equals(State.LOOP))
		{
			specialClock += delta;
			
			if((specialClock >= UNSTEALTH_DURATION) && visible)
			{
				visible = false;
				resetSpecialClock();
			}
		}
	}

	//Toggles reflection if the special clock has expired.
	private void reflectUpdate(float delta)
	{
		specialClock += delta;
		
		if(specialClock >= REFLECT_INTERVAL)
		{
			resetSpecialClock();
			reflectOn = !reflectOn;
			advanceTexture();
		}	
	}

	//Toggles phasing if the special clock has expired
	private void phaseUpdate(float delta)
	{
		specialClock += delta;
		
		if(specialClock >= PHASE_INTERVAL)
		{
			resetSpecialClock();
			inPhase = !inPhase;
			advanceTexture();
		}
	}

	//Updates looping ships.
	private void loopUpdate(float delta)
	{
		if(state.equals(State.LOOP))
		{
			rotateClock += delta;
			
			//Advances to the next texture if the rotateClock is expired
			if(rotateClock >= ROTATE_INTERVAL)
			{
				advanceTexture();
				rotateClock = 0f;
			}
			
			//Rotates the velocity if the ship has reached the edge of its loop
			if(velocity.x > 0 && (bounds.x + bounds.width) > worldBounds.width - PhoenixController.FLEET_BUFFER/2f)
				velocity.setAngle(90);
			else if(velocity.y > 0 && ((bounds.y + bounds.height) > (worldBounds.height -PhoenixController.FLEET_BUFFER/2f)))
				velocity.setAngle(180);
			else if(velocity.x < 0 && bounds.x < PhoenixController.FLEET_BUFFER/2f)
				velocity.setAngle(270);
			else if(velocity.y < 0 && bounds.y < worldBounds.height/2f)
				velocity.setAngle(0);
		}
		
		//If the ship has reached its terminal velocity, transition to loop state.
		if(state.equals(State.STARTING) && bounds.y <= worldBounds.height/2f)
			setToLoopLoop();
	}

	//Transition to loop state.
	private void setToLoopLoop()
	{
		state = State.LOOP;
		
		velocity.setAngle(0f);
	}

	//Update bouncing ships
	private void bounceUpdate()
	{
		if(state.equals(State.LOOP))
		{
			//If the ship hits a wall, bounce off at the appropriate angle (with some wobble)
			if((bounds.x + bounds.width > worldBounds.width) && (velocity.x > 0))
			{
				velocity.x *= -1f;
				velocity.rotate(getRandFloat(-5f, 5f));
			}
			else if((bounds.x < 0) && (velocity.x < 0))
			{
				velocity.x *= -1f;
				velocity.rotate(getRandFloat(-5f, 5f));
			}
			else if((bounds.y + bounds.height > worldBounds.height) && (velocity.y > 0))
			{
				velocity.y *= -1f;
				velocity.rotate(getRandFloat(-5f, 5f));
			}
			else if((bounds.y < worldBounds.height/2f) && (velocity.y < 0))
			{
				velocity.y *= -1f;
				velocity.rotate(getRandFloat(-5f, 5f));
			}
		}
		
		//Transition to loop if it reaches its terminal descent
		if(state.equals(State.STARTING) && currentDescent >= totalDescent)
			setToLoopBounce();
	}

	//Transition bouncing to loop from starting 
	private void setToLoopBounce()
	{
		state = State.LOOP;
		velocity.scl(2f);
		
		float angle = 0f;
		
		//Start the ship off at an angle that is not too horizontal or vertical
		while((angle < 10f) || ((angle > 80f) &&  (angle < 100f)) || ((angle > 170f) &&  (angle < 190f)) || ((angle > 260f) &&  (angle < 280f)) || (angle > 350f))
			angle = getRandFloat(0f, 360f);
		
		velocity.setAngle(angle);
	}

	//Called when the ship is returned to pool
	@Override
	public void reset()
	{
		bounds.set(0f, 0f, 0f, 0f);
		position.set(0, 0);
		velocity.set(0, 0);
		alive = false;
		firePorts.clear();
	}
	
	//Lower the current HP by a certain amount of damage.  Lower the alpha of the ship by a proportional amount.  If the ship runs out of HP, kill it.
	public void hit(int damage)
	{
		currentHP-=damage;
		alpha = ((float)totalHP - ((float)(totalHP-currentHP))/2f)/(float)totalHP;
		
		if(currentHP <= 0)
			kill();
	}
	
	//Set the alive flag to false.
	public void kill()
	{
		alive = false;
	}
	
	//Transition a square moving ship to loop.
	public void setToLoopStandard()
	{
		state = State.LOOP;
		velocity.scl(0.5f);
		velocity.rotate(-90f);
		sideDistance = SQUARE_SIDE/2f;
	}
	
	//Transition a linear moving ship to loop
	public void setToLoopBoss()
	{
		state = State.LOOP;
		velocity.setAngle(0f);
		velocity.scl(1.5f);
	}
	
	//Return a random float between the two parameters.
	public static float getRandFloat(float max, float min)
	{
		return (float)(Math.random()*(max-min))+min;
	}
	
	//Fire a shot.
	public void fire()
	{
		firing = false;
		
		//Change the next shot type if the ship is the last boss ship
		if(special.equals(Special.LAST_BOSS))
		{
			//Change the next shot to a spawn and get a new random spawntype.
			if(shotType.equals(ShotType.AIMED))
			{
				shotType = ShotType.SPAWN;
				nextSpawn = getRandomSpawnable();
			}
			else if(shotType.equals(ShotType.SPAWN))
			{
				shotType = ShotType.BOMB;
			}
			else if(shotType.equals(ShotType.BOMB))
			{
				shotType = ShotType.AIMED;
			}
		}
	}

	//Return the bottom Y coordinate of the ship.
	public float getBottomY()
	{
		return bounds.y;
	}

	//Return the center X coordinate of the ship.
	public float getCenterX()
	{
		return bounds.x + bounds.width/2f;
	}
	
	//Update a square moving ship.
	private void standardUpdate(Vector2 v)
	{
		//Rotates the velocity if the ship has moved one SQUARE_SIDE
		if(state.equals(State.LOOP))
		{
			sideDistance += v.len();
			
			if(sideDistance >= SQUARE_SIDE)
			{
				velocity.rotate(-90f);
				sideDistance = 0;
			}
		}
		
		//Transition the ship to loop if it's reached its terminal Y position
		if(state.equals(State.STARTING) && currentDescent >= totalDescent)
			setToLoopStandard();
	}
	
	//Update a linear moving ship
	public void bossUpdate()
	{
		//Reverse direction if the ship has hit a side
		if(state.equals(State.LOOP))
		{
			if((velocity.angle()==0) && (bounds.x + bounds.width > worldBounds.width))
				velocity.x *= -1f;
			else if((velocity.angle()==180) && (bounds.x < 0))
				velocity.x *= -1f;
		}
		
		//Transition ship to loop if it's reached its terminal Y
		if(state.equals(State.STARTING) && currentDescent >= totalDescent)
			setToLoopBoss();
	}
		
	//Set the texture to the next texture in shipTextures.  If it's the last texture, loop back around to the first texture.
	public void advanceTexture()
	{
		textureIndex++;
		if(textureIndex >= shipTextures.size)
			textureIndex = 0;
		
		currentTexture = shipTextures.get(textureIndex);
	}

	//Reset the special clock
	public void resetSpecialClock()
	{
		specialClock = 0;
	}
}
