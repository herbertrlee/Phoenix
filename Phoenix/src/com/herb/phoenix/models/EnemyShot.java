/*
 * EnemyShot.java
 * Model for an enemy shot.
 * This is a poolable object.  Do not call constructor directly, create a Pool and use obtain().
 */

package com.herb.phoenix.models;

import com.badlogic.gdx.math.Vector2;

public class EnemyShot extends Shot
{
	//Static class constants.
	public static final float WIDTH = 0.1f;
	public static final float HEIGHT = 0.1f;
	public static final float SPEED = 4f;
	
	public ShotDirection direction;
	
	/*
	 * ShotDirection:
	 * Down - fires straight down
	 * Left - fires 45 degrees left of down
	 * Right - fires 45 degrees right of down
	 */
	public enum ShotDirection
	{
		DOWN, LEFT, RIGHT
	}
	
	//Constructor.  Called by Pool.
	public EnemyShot()
	{
		position = new Vector2();
		velocity = new Vector2();
		alive = false;
	}
	
	//Initiates the shot at (posX, posY) in the given direction.
	public void init(float posX, float posY, ShotDirection direction)
	{
		position.x = posX - WIDTH/2F;
		position.y = posY;
		bounds.x = posX;
		bounds.y = posY;
		bounds.width = WIDTH;
		bounds.height = HEIGHT;
		velocity.set(0, -SPEED);
		
		alive = true;
		
		this.direction = direction;
		
		switch(direction)
		{
			case LEFT:
				velocity.rotate(-45f);
				break;
			case RIGHT:
				velocity.rotate(45f);
				break;
			default:
				break;
		}
	}
	
	//Initializes the shot straight down, then rotates the velocity by angle degrees.
	public void init(float posX, float posY, float angle)
	{
		init(posX, posY);
		velocity.rotate(angle);
	}
	
	//Initializes the shot straight down.
	@Override
	public void init(float posX, float posY)
	{
		init(posX, posY, ShotDirection.DOWN);
	}

	//Returns the top coordinate of the shot.
	public float getTopY()
	{
		return bounds.y + bounds.height;
	}

	//Returns the bottom coordinate of the shot.
	public float getBottomY()
	{
		return position.y;
	}

}
