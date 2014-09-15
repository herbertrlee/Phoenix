package com.herb.phoenix.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class Shot implements Poolable
{
	
	public Vector2 position;
	public Vector2 velocity;
	public boolean alive;
	public Rectangle bounds = new Rectangle();
	
	@Override
	public void reset()
	{
		position.set(0,  0);
		velocity.set(0, 0);
		alive = false;
	}
	
	public abstract void init(float posX, float posY);
	
	public void update(float delta)
	{
		position.add(velocity.x*delta, velocity.y*delta);
		bounds.x = position.x;
		bounds.y = position.y;
	}
	
	public void kill()
	{
		alive = false;
	}
}
