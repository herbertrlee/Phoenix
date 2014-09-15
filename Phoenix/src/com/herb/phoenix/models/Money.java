/*
 * Money.java
 * Model for money object.  Extends Shot
 * Money drops down towards the bottom of the screen.
 * This is a poolable object.  Do not call constructor, create a pool and use obtain().
 */
package com.herb.phoenix.models;

import com.badlogic.gdx.math.Vector2;

public class Money extends Shot
{
	//Static class constants.
	private static final float WIDTH = 0.25f;
	private static final float HEIGHT = 0.4f;
	private static final float SPEED = 1f;
	private static final int DEFAULT_VALUE = 100;
	
	//Member variables
	public int value;
	
	//Constructor.  Called by pool.
	public Money()
	{
		position = new Vector2();
		velocity = new Vector2();
		alive = false;
		value = 0;
	}
	
	//Initialize a default money.
	@Override
	public void init(float posX, float posY)
	{
		this.init(posX, posY, DEFAULT_VALUE);
	}
	
	//Initialize a money with a defined value.
	public void init(float posX, float posY, int val)
	{
		position.x = posX;
		position.y = posY;
		bounds.x = posX;
		bounds.y = posY;
		bounds.width = WIDTH;
		bounds.height = HEIGHT;
		velocity.set(0, -SPEED);
		value = val;
		alive = true;
	}

}
