/*
 * Bomb.java
 * Model representing a bomb.  Drops down to the bottom of the world and explodes.
 * Bombs are poolable
 */

package com.herb.phoenix.models;

public class Bomb extends EnemyShot
{
	//Static class constants
	public static final float WIDTH = 0.2f;
	public static final float HEIGHT = 0.2f;
	public static final float SPEED = 3f;
	public static final float TERMINAL_Y = 0.1f;
	public static final float EXPLOSION_DURATION = 0.5f;
	public static final float EXPLOSION_GROWTH_COEFFICIENT = 0.03f; 
	
	//Member variables.
	public BombState bombState;
	public float alpha;
	public float explosionDuration;
	
	//BombState - three possible states.
	//Falling - dropping down the screen.  When it hits bottom, transitions to exploding
	//Exploding - expanding.  Transitions to exploded when it reaches maximum size or it hits the player.
	//Exploded - fading.  Killed when it has faded completely.
	public enum BombState
	{
		FALLING, EXPLODING, EXPLODED
	};
	
	//Constructor.  No change from EnemyShot constructor.
	public Bomb()
	{
		super();
	}
	
	//Initializes the bomb.  Primary differences from super.init are different dimensions and presence of bombState.
	@Override
	public void init(float posX, float posY)
	{
		position.x = posX;
		position.y = posY;
		bounds.x = posX;
		bounds.y = posY;
		bounds.width = WIDTH;
		bounds.height = HEIGHT;
		velocity.set(0, -SPEED);
		
		alive = true;
		bombState = BombState.FALLING;
	}
	
	//Resets the bomb.  Used when bomb is freed back to the pool.
	@Override
	public void reset()
	{
		alive = false;
		bombState = BombState.FALLING;
	}
	
	//Updates the bomb.  Varies depending on bombState
	@Override
	public void update(float delta)
	{
		switch (bombState)
		{
		case EXPLODED:
			explodedUpdate(delta);
			break;
		case EXPLODING:
			explodingUpdate(delta);
			break;
		case FALLING:
			fallUpdate(delta);
			break;
		default:
			break;
		}
	}

	//Called by update if the bomb is exploded. Fades the alpha of the explosion, and kills the bomb if the explosion has run its course.
	private void explodedUpdate(float delta)
	{
		explosionDuration += delta;
		alpha = (EXPLOSION_DURATION - explosionDuration)/EXPLOSION_DURATION;
		
		if(explosionDuration > EXPLOSION_DURATION)
		{
			alive = false;
		}
	}

	//Called by update if the bomb is exploding.  Increases the size of the explosion, and transitions if the explosion has reached its duration.
	private void explodingUpdate(float delta)
	{
		explosionDuration += delta;
		
		bounds.x -= EXPLOSION_GROWTH_COEFFICIENT;
		bounds.y -= EXPLOSION_GROWTH_COEFFICIENT;
		bounds.width += EXPLOSION_GROWTH_COEFFICIENT*2f;
		bounds.height += EXPLOSION_GROWTH_COEFFICIENT*2f;
		
		if(explosionDuration > EXPLOSION_DURATION)
		{
			setToExploded();
		}
	}

	//Called if the bomb is falling.  Transitions if the bomb has reached the bottom of the screen.
	private void fallUpdate(float delta)
	{
		super.update(delta);
		if(position.y <= TERMINAL_Y)
		{
			bombState = BombState.EXPLODING;
			explosionDuration = 0f;
			alpha = 1f;
		}
	}

	//Transitions from exploding to exploded.  Called in PhoenixController.
	public void setToExploded()
	{
		bombState = BombState.EXPLODED;
		explosionDuration = 0f;
	}
}
