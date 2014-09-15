/*
 * Explosion.java
 * Model for an explosion.  Create a large explosion whenever an enemy ship dies, and a small explosion when an enemy hits the player's ship.
 * Explosions fade from the screen over their duration.
 * This is a poolable object.  Do not call constructor directly, create a pool and use obtain().
 */

package com.herb.phoenix.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Explosion implements Poolable
{
	//Static class constants
	private static final float DURATION = 0.5f;
	private static final float SMALL_DURATION = 0.2f;
	
	//Member variables
	public Rectangle bounds = new Rectangle();
	public boolean alive;
	public float alpha, duration;
	
	//Constructor.  Called by pool.
	public Explosion()
	{
		alive = false;
		duration = 0f;
		alpha = 0f;
	}
	
	//Initialize, with a small option.  Small explosions have shorter durations.
	public void init(float x, float y, float width, float height, boolean small)
	{
		//Initialize position and bounds.
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = width;
		
		if(small)
			duration = SMALL_DURATION;
		else
			duration = DURATION;
		
		//Initialize alpha.
		alpha = 1f;
		alive = true;
	}
	
	//Initialize a large explosion.
	public void init(float x, float y, float width, float height)
	{
		init(x, y, width, height, false);
	}
	
	//Reset the explosion.  Called when explosion is freed back to the pool.
	@Override
	public void reset()
	{
		alive = false;
		duration = 0f;
		alpha = 0f;
	}
	
	//Updates the explosion.
	public void update(float delta)
	{
		//Decrements the duration and the alpha.
		duration -= delta;
		alpha = duration/DURATION;
		
		//If the duration is over, kill the explosion.
		if(duration <= 0)
			alive = false;
	}
	
}
