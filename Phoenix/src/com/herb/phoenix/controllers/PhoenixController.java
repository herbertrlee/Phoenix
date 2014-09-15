/*
 * PhoenixController.java
 * Sets up user input, updates the game world, and manages game flow.
 */

package com.herb.phoenix.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.herb.game.controllers.HerbController;
import com.herb.game.models.HerbWorld;
import com.herb.phoenix.PhoenixGame;
import com.herb.phoenix.models.Bomb;
import com.herb.phoenix.models.Bomb.BombState;
import com.herb.phoenix.models.EnemyShip;
import com.herb.phoenix.models.EnemyShip.Special;
import com.herb.phoenix.models.EnemyShipBuilder;
import com.herb.phoenix.models.EnemyShot;
import com.herb.phoenix.models.EnemyShot.ShotDirection;
import com.herb.phoenix.models.Explosion;
import com.herb.phoenix.models.Fleet;
import com.herb.phoenix.models.Money;
import com.herb.phoenix.models.PhoenixShip;
import com.herb.phoenix.models.PhoenixShot;
import com.herb.phoenix.models.PhoenixWorld;
import com.herb.phoenix.models.Weapon;

public class PhoenixController extends HerbController
{
	//The border between the edge of the screen and the ships.
	public static final float FLEET_BUFFER = 0.5f;
	//Thirty degrees in radians.
	private static final float THIRTY_DEGREES = (float) (Math.PI/6f);
	//Delay between unpausing the game and gameplay resuming
	private static final float PAUSE_DELAY_INTERVAL = 0.5f;
	
	//Phoenix members
	private PhoenixWorld phoenixWorld;
	private PhoenixShip phoenix;
	
	//Flags
	private boolean gameNotEnded = true;
	private boolean paused, delayed;
	private boolean leftHold, rightHold, fireHold;
	private boolean controlsEnabled;
	
	//Timers
	private float pauseDelay = 0f;
	private float autoFireTimer = 0f;
	
	//Bonus calculation variables
	private int shotsFired, hits;
	private float timeElapsed, accuracy;
	private int accuracyBonus, timeBonus;
	
	//UI elements
	private TextButton moveLeftButton, moveRightButton, fireButton1, fireButton2;
	private Slider hpBar;
	private Label hpLabel, moneyLabel, moneyCounter, scoreLabel, scoreCounter;
	
	//Active game elements.
	private Array<PhoenixShot> activeShots;
	private Array<EnemyShip> activeShips;
	private Array<EnemyShot> activeEnemyShots;
	private Array<Money> activeMoney;
	private Array<Explosion> activeExplosions;
	private Array<Bomb> activeBombs;
			
	//Pools for all poolable game elements.
	private final Pool<PhoenixShot> shotPool = new Pool<PhoenixShot>(){

		@Override
		protected PhoenixShot newObject()
		{
			return new PhoenixShot();
		}
	};
	
	private final Pool<EnemyShip> shipPool = new Pool<EnemyShip>(){

		@Override
		protected EnemyShip newObject()
		{
			return new EnemyShip();
		}
		
	};
	
	private final Pool<EnemyShot> enemyShotPool = new Pool<EnemyShot>(){

		@Override
		protected EnemyShot newObject()
		{
			return new EnemyShot();
		}
		
	};
	
	private final Pool<Money> moneyPool = new Pool<Money>(){

		@Override
		protected Money newObject()
		{
			return new Money();
		}
		
	};
	
	private final Pool<Explosion> explosionPool = new Pool<Explosion>(){

		@Override
		protected Explosion newObject()
		{
			return new Explosion();
		}
		
	};
	
	private final Pool<Bomb> bombPool = new Pool<Bomb>(){

		@Override
		protected Bomb newObject()
		{
			return new Bomb();
		}
		
	};
	
	//Constructor
	public PhoenixController(HerbWorld world, Stage stage, Skin skin)
	{
		super(world, stage, skin);
	}

	//Initializes all of the variables.
	@Override
	public void initializeVariables()
	{
		phoenixWorld = (PhoenixWorld)world;
		phoenix = phoenixWorld.getShip();
		activeShots = phoenixWorld.getActiveShots();
		activeShips = phoenixWorld.getActiveShips();
		activeEnemyShots = phoenixWorld.getActiveEnemyShots();
		activeMoney = phoenixWorld.getActiveMoney();
		activeExplosions = phoenixWorld.getActiveExplosions();
		activeBombs = phoenixWorld.getActiveBombs();
		paused = false;
		shotsFired = 0;
		hits = 0;
		timeElapsed = 0f;
		accuracy = 0f;
		controlsEnabled = true;
		
		clearHolds();
	}

	//Clear the button holds.
	private void clearHolds()
	{
		leftHold = false;
		rightHold = false;
		fireHold = false;
	}

	//Update the game world for rendering.
	@Override
	public void update(float delta)
	{
		//Check if the game is currently paused.
		if(!paused && !delayed )
		{
			//Check if the game is currently alive
			if(phoenixWorld.alive)
			{
				//Add time to the counter
				timeElapsed += delta;
				
				//Check if all of the enemy ships are dead
				if(activeShips.size == 0)
				{
					//Check if the world is on the last fleet of the current level
					if(!phoenixWorld.onLastFleet)
					{
						//Create the next fleet.
						buildNextFleet();
					}
					//Check if the game is over
					else if(gameNotEnded)
					{
						//If the game is over, end the game
						if(phoenixWorld.onLastLevel)
							endGameWin();
						//Else, go to the next level
						else
							endLevel();
					}
				}
				
				//Update each enemy ship
				for(EnemyShip ship : activeShips)
				{
					//Update the position of the enemy ship
					ship.update(delta);
					
					//If the ship's firing clock is up, fire the appropriate shot(s)
					if(ship.firing)
					{
						//Create the appropriate projectiles
						switch(ship.shotType)
						{
							case NORMAL:
								normalFire(ship);
								break;
							case ANGLED:
								angleFire(ship);
								break;
							case AIMED:
								aimFire(ship);
								break;
							case SPAWN:
								spawnFire(ship, ship.nextSpawn);
								break;
							case BOMB:
								bombFire(ship);
								break;
							default:
								break;
						}
						
						//Reset the ship's firing clock and turn off fire flag
						ship.fire();
						
						//For stealth ships, make the ship briefly visible.
						if(ship.special.equals(Special.STEALTH))
						{
							ship.visible = true;
							ship.resetSpecialClock();
						}
					}
				}
				
				//Update each player-controlled shot
				for(PhoenixShot shot : activeShots)
				{
					//Update the position of each shot
					shot.update(delta);
							
					//Kill all shots that go out of the world bounds
					if(shot.position.y > phoenixWorld.bounds.y + phoenixWorld.bounds.height)
					{
						shot.kill();
					}
					
					//Iterate through each enemy ship
					for(EnemyShip ship : activeShips)
					{
						//Check if the ship and shot collide, and whether the ship is phased in
						if(ship.bounds.overlaps(shot.bounds) && ship.inPhase)
						{
							//If it's a hit, kill the shot and increment the shot counter
							shot.kill();
							hits++;
							//If the ship is currently reflecting, fire a shot back at the player.  Otherwise, do damage to the ship equal to the shot's damage.
							if(!ship.reflectOn)
								ship.hit(shot.damage);
							else
								ship.firing = true;
						}
					}
				}
				
				//Update each enemy shot
				for(EnemyShot shot : activeEnemyShots)
				{
					//Update the shot's position
					shot.update(delta);
					
					//Kill any shots that go below the edge of the world
					if(shot.getTopY() < 0)
					{
						shot.kill();
					}
					
					//Check if the shot can hit the player's ship vertically
					if(shot.getBottomY() < PhoenixShip.TOP_Y)
					{
						//Check if the shot overlaps the player's ship
						if(shot.bounds.overlaps(phoenix.bounds))
						{
							//Register the damage to the ship
							phoenix.hit();
							updateHpBar();
							shot.kill();
							
							//Create a small explosion on the ship where the shot hits
							Explosion explosion = explosionPool.obtain();
							explosion.init(shot.bounds.x-shot.bounds.width, shot.bounds.y, shot.bounds.width*2f, shot.bounds.height*2f, true);
							activeExplosions.add(explosion);
							
							//Kills the game if the ship takes lethal damage
							if(phoenix.getCurrentHP() <= 0)
								phoenixWorld.alive = false;
						}
					}
				}
				
				//Updates all money objects on the screen
				for(Money money : activeMoney)
				{
					//Update the money position
					money.update(delta);
					
					//Check if the money overlaps with the player's ship
					if(money.bounds.overlaps(phoenix.bounds))
					{
						//Add the money to the player's cash and score, update UI elements, and kill the money object
						phoenix.addMoney(money.value);
						phoenix.addScore(money.value);
						updateMoney();
						updateScore();
						money.kill();
					}
				}
				
				//Update all bombs
				for(Bomb bomb : activeBombs)
				{
					//Update bomb position/size
					bomb.update(delta);
					
					//Check if the bomb is currently exploding and hits the player's ship
					if(bomb.bombState.equals(BombState.EXPLODING) && bomb.bounds.overlaps(phoenix.bounds))
					{
						//Register the hit on the ship and turn off the bomb's exploding-ness
						bomb.setToExploded();
						phoenix.hit();
					}
						
				}
				
				//Update all explosions
				for(Explosion explosion : activeExplosions)
				{
					//Update the explosion size/alpha
					explosion.update(delta);
				}
				
				//Process user input
				
				//Move left if the player is holding down the left button and the ship is not on the left screen boundary
				if(leftHold && phoenix.getLeftX() > 0 && !rightHold)
				{
					phoenix.moveLeft();
				}
				//Move right if the player is holding down the right button and the ship is not on the right screen boundary
				if(rightHold && phoenix.getRightX() < phoenixWorld.bounds.width && !leftHold)
				{
					phoenix.moveRight();
				}
				//Fire at the autofire rate if the player is holding down the fire button
				if(fireHold)
				{
					autoFireTimer += delta;
					
					if(autoFireTimer > phoenix.autoFireRate)
					{
						fire();
						autoFireTimer = 0;
					}
				}
				
				//Clean up objects
				
				//Free all dead player shots
				PhoenixShot shot;
				int len = activeShots.size;
				for(int i=len;--i >= 0;)
				{
					shot = activeShots.get(i);
					if(!shot.alive)
					{
						activeShots.removeIndex(i);
						shotPool.free(shot);
					}
				}
				
				//Free all dead enemy ships.
				EnemyShip ship;
				len = activeShips.size;
				for(int i=len;--i >= 0;)
				{
					ship = activeShips.get(i);
					if(!ship.alive)
					{
						//Check if the ship contains money
						if(ship.containsMoney)
						{
							//Create a new money object at the site of the dead ship
							Money money = moneyPool.obtain();
							money.init(ship.getCenterX(), ship.getBottomY(), ship.moneyValue);
							activeMoney.add(money);
						}
						
						//Create an explosion at the site of the ship
						Explosion explosion = explosionPool.obtain();
						explosion.init(ship.bounds.x, ship.bounds.y, ship.bounds.width, ship.bounds.height);
						activeExplosions.add(explosion);
						
						//If the ship is a bomber, spawn a bomb at the site of the ship
						if(ship.special.equals(Special.BOMBER))
						{
							Bomb bomb = bombPool.obtain();
							bomb.init(ship.getCenterX(), ship.getBottomY());
							activeBombs.add(bomb);
						}
						
						//Add the ship's score value to the player's score
						phoenix.addScore(ship.scoreValue);
						updateScore();
						
						//Free the ship
						activeShips.removeIndex(i);
						shipPool.free(ship);
					}
				}
				
				//Free dead enemy shots
				EnemyShot eShot;
				len = activeEnemyShots.size;
				for(int i=len;--i >= 0;)
				{
					eShot = activeEnemyShots.get(i);
					if(!eShot.alive)
					{
						activeEnemyShots.removeIndex(i);
						enemyShotPool.free(eShot);
					}
				}
				
				//Free dead money objects
				Money money;
				len = activeMoney.size;
				for(int i=len;--i >= 0;)
				{
					money = activeMoney.get(i);
					if(!money.alive)
					{
						activeMoney.removeIndex(i);
						moneyPool.free(money);
					}
				}
				
				//Free dead explosion objects
				Explosion explosion;
				len = activeExplosions.size;
				for(int i=len;--i >= 0;)
				{
					explosion = activeExplosions.get(i);
					if(!explosion.alive)
					{
						activeExplosions.removeIndex(i);
						explosionPool.free(explosion);
					}
				}
				
				//Free dead bomb objects
				Bomb bomb;
				len = activeBombs.size;
				for(int i=len;--i >= 0;)
				{
					bomb = activeBombs.get(i);
					if(!bomb.alive)
					{
						activeBombs.removeIndex(i);
						bombPool.free(bomb);
					}
				}
			}
			//Bring up the end game screen
			else if(gameNotEnded)
				endGame();
		//Check if the game is currently delayed.
		}
		else if(!paused && delayed)
		{
			//Increment the delay timer
			pauseDelay += delta;
			
			//Check if the delay timer is done
			if(pauseDelay >= PAUSE_DELAY_INTERVAL)
			{
				//restart the gameplay
				delayed = false;
				enableControls();
			}
		}
	}

	//Fire a bomb object from each fireport
	private void bombFire(EnemyShip ship)
	{
		for(Float firePort : ship.firePorts)
		{
			Bomb bomb = bombPool.obtain();
			bomb.init(ship.bounds.x + firePort, ship.getBottomY());
			activeBombs.add(bomb);
		}
	}

	//Spawn a ship of the appropriate type from each fireport.
	private void spawnFire(EnemyShip ship, int spawnType)
	{
		for(Float firePort : ship.firePorts)
		{
			EnemyShip spawn = shipPool.obtain();
			spawn.init(ship.bounds.x + firePort, ship.getBottomY(), 1, phoenixWorld.bounds, spawnType);
			activeShips.add(spawn);
		}
	}

	//Fire an randomly-angled shot from each fireport
	private void angleFire(EnemyShip ship)
	{
		for(Float firePort : ship.firePorts)
		{
			EnemyShot shot = enemyShotPool.obtain();
			shot.init(ship.bounds.x + firePort, ship.getBottomY(), EnemyShip.getRandFloat(-45f, 45f));
			activeEnemyShots.add(shot);
		}
	}

	//Fire a shot straight down from each fireport
	private void normalFire(EnemyShip ship)
	{
		for(Float firePort: ship.firePorts)
		{
			EnemyShot shot = enemyShotPool.obtain();
			shot.init(ship.bounds.x + firePort, ship.getBottomY());
			activeEnemyShots.add(shot);
		}
	}
	
	//Fire a shot at the player's ship from each fireport
	private void aimFire(EnemyShip ship)
	{
		for(Float firePort : ship.firePorts)
		{
			//Calculate whether ship should fire to the left, right, or straight down
			float dx = ship.bounds.x + firePort - phoenix.getCenterX();
			float y = ship.getBottomY();
			
			float dy = y - phoenix.getTopY();
			
			double angle = -Math.atan(dx/dy);
			
			EnemyShot shot = enemyShotPool.obtain();
			
			if(angle < -THIRTY_DEGREES)
				shot.init(ship.bounds.x + firePort, y, ShotDirection.LEFT);
			else if(angle < THIRTY_DEGREES)
				shot.init(ship.bounds.x + firePort, y, ShotDirection.DOWN);
			else
				shot.init(ship.bounds.x + firePort, y, ShotDirection.RIGHT);
			
			activeEnemyShots.add(shot);
		}
	}
	
	//End the current level
	private void endLevel()
	{
		//Clear the active objects from the screen and set the game not ended flag to false
		clearActiveObjects();
		gameNotEnded = false;
		stage.clear();
		
		//Compute the accuracy and time bonuses
		computeBonuses();
		
		//Create the root table
		Table levelCompleteTable = new Table();
		levelCompleteTable.setFillParent(true);
		
		//Create the level complete elements
		Label levelCompleteLabel = new Label("Level complete!", skin, "default-font", Color.RED);
		Label accuracyLabel = new Label(String.format("Accuracy: %.2f%% Bonus: %d", accuracy, accuracyBonus), skin, "default-font-small", Color.BLUE);
		Label timeLabel = new Label(String.format("Time: %dm %ds Bonus: %d", ((int)timeElapsed)/60, ((int)timeElapsed)%60, timeBonus), skin, "default-font-small", Color.BLUE);
		TextButton levelCompleteButton = new TextButton("Go to Shop", skin, "inverse");
		
		levelCompleteButton.addListener(new ClickListener(){
			@Override 
            public void clicked(InputEvent event, float x, float y){
                ((PhoenixGame)world.getGame()).goToShop();
            }
		});
		
		//Add level complete elements to the table and add the table to the stage
		levelCompleteTable.add(levelCompleteLabel);
		levelCompleteTable.row();
		levelCompleteTable.add(accuracyLabel);
		levelCompleteTable.row();
		levelCompleteTable.add(timeLabel);
		levelCompleteTable.row();
		levelCompleteTable.add(levelCompleteButton);
		stage.addActor(levelCompleteTable);
	}

	//Remove active objects at the end of a game
	private void clearActiveObjects()
	{
		PhoenixShot shot;
		int len = activeShots.size;
		for(int i=len;--i >= 0;)
		{
			shot = activeShots.get(i);
			activeShots.removeIndex(i);
			shotPool.free(shot);
		}
		
		//Any money objects still alive should be added to the player's stash
		Money money;
		len = activeMoney.size;
		for(int i=len;--i >= 0;)
		{
			money = activeMoney.get(i);
			phoenix.addMoney(money.value);
			phoenix.addScore(money.value);
			activeMoney.removeIndex(i);
			moneyPool.free(money);
		}
		
		EnemyShot enemyShot;
		len = activeEnemyShots.size;
		for(int i=len;--i >= 0;)
		{
			enemyShot = activeEnemyShots.get(i);
			activeEnemyShots.removeIndex(i);
			enemyShotPool.free(enemyShot);
		}
		
		Bomb bomb;
		len = activeBombs.size;
		for(int i=len;--i >= 0;)
		{
			bomb = activeBombs.get(i);
			activeBombs.removeIndex(i);
			bombPool.free(bomb);
		}
		
		Explosion explosion;
		len = activeExplosions.size;
		for(int i=len;--i >= 0;)
		{
			explosion = activeExplosions.get(i);
			activeExplosions.removeIndex(i);
			explosionPool.free(explosion);
		}
	}

	//End the game if the player dies.
	private void endGame()
	{
		clearActiveObjects();
		gameNotEnded = false;
		stage.clear();

		computeBonuses();
		
		Table gameOverTable = new Table();
		gameOverTable.setFillParent(true);
		
		
		Label gameOverLabel = new Label("Game over", skin, "default");
		gameOverLabel.setColor(Color.RED);
		TextButton gameOverButton = new TextButton("Return to main menu", skin, "default");
		
		gameOverButton.addListener(new ClickListener(){
			@Override 
            public void clicked(InputEvent event, float x, float y){
				((PhoenixGame)world.getGame()).saveHighScore(phoenixWorld.level, phoenix.getScore());
                ((PhoenixGame)world.getGame()).returnToMainMenu(true);
            }
		});
		
		gameOverTable.add(gameOverLabel);
		gameOverTable.row();
		gameOverTable.add(gameOverButton);
		stage.addActor(gameOverTable);
	}
	
	//End the game if the player beats all the levels.
	private void endGameWin()
	{
		clearActiveObjects();
		gameNotEnded = false;
		stage.clear();

		computeBonuses();
		
		Table gameOverTable = new Table();
		gameOverTable.setFillParent(true);
		
		Label gameOverLabel = new Label("You beat the game!\nCongratulations!", skin, "default-font", Color.RED);
		Label accuracyLabel = new Label(String.format("Accuracy: %.2f%%", 100.0*(double)hits/(double)shotsFired), skin, "default-font-small", Color.BLUE);
		Label timeLabel = new Label(String.format("Time: %.1f s", timeElapsed), skin, "default-font-small", Color.BLUE);
		
		TextButton gameOverButton = new TextButton("Return to main menu", skin, "default");
		
		gameOverButton.addListener(new ClickListener(){
			@Override 
            public void clicked(InputEvent event, float x, float y){
				((PhoenixGame)world.getGame()).saveHighScore(phoenixWorld.level, phoenix.getScore());
                ((PhoenixGame)world.getGame()).returnToMainMenu(true);
            }
		});
		gameOverTable.add(gameOverLabel);
		gameOverTable.row();
		gameOverTable.add(accuracyLabel);
		gameOverTable.row();
		gameOverTable.add(timeLabel);
		gameOverTable.row();
		gameOverTable.add(gameOverButton);
		stage.addActor(gameOverTable);
	}

	//Creates the next fleet.
	private void buildNextFleet()
	{
		//Updates the current fleet
		phoenixWorld.updateFleet();
		
		//Gets the current fleet
		Fleet fleet = phoenixWorld.getCurrentFleet();
		
		//Gets the fleet definition variables
		Array<Array<EnemyShipBuilder>> shipDefRows = fleet.shipDefRows;
		Array<Float> offsets = fleet.offsets;
		float y = phoenixWorld.bounds.height;
		float totalDescent = fleet.totalDescent;
		
		//Create each ship in its proper initial position by rows
		for(int i=0;i<shipDefRows.size;i++)
		{
			//Create a single row of ships
			Array<EnemyShipBuilder> row = shipDefRows.get(i);
			
			//Determine the initial X coordinate for this row and the X-spacing between each ship in this row
			float x = FLEET_BUFFER + offsets.get(i) + EnemyShip.SQUARE_SIDE;
			float width = phoenixWorld.bounds.width - 2f*EnemyShip.SQUARE_SIDE;
			float deltaX = width/(row.size);
			
			//Create each ship in this row
			for(int j=0;j<row.size;j++)
			{
				EnemyShip ship = shipPool.obtain();
				ship.init(x, y, totalDescent, phoenixWorld.bounds, row.get(j).type);
				activeShips.add(ship);
				x += deltaX;
			}
			
			//Get the Y-position for the next row
			y += fleet.abovePadding.get(i);
		}
	}

	//Fire a player shot
	private void fire()
	{
		//Increment the shot counter
		shotsFired++;
		
		//Fire the appropriate shot from each weapon
		for(Weapon weapon : phoenix.getWeapons())
		{
			PhoenixShot shot = shotPool.obtain();
			shot.init(phoenix.getLeftX() + weapon.xOffset, phoenix.getTopY(), weapon.weaponLevel, weapon.angle);
			//If the ship is upgraded and the shot is upgradeable, upgrade the damage
			if(phoenix.upgraded && shot.upgradeable)
				shot.damageMultiply(PhoenixShip.UPGRADE_FIRE_MULTIPLIER);
			activeShots.add(shot);
		}
	}

	//Create the control and status UI elements
	@Override
	public void initializeControls()
	{
		//Create the root table
		Table rootTable = new Table();
		
		//Determine the width and height for the buttons
		float width = (stage.getWidth() - stage.getHeight())/2f;
		float height = stage.getHeight()*0.45f;
		
		//Create the 4 control buttons and set their listeners
		moveLeftButton = new TextButton("L", skin, "blueButton");
		moveRightButton = new TextButton("R", skin, "blueButton");
		fireButton1 = new TextButton("FIRE!", skin, "redButton");
		fireButton2 = new TextButton("FIRE!", skin, "redButton");
		
		moveLeftButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 
			{
				if(controlsEnabled)
					leftHold = true;
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				leftHold = false;
			}
		});
		
		moveRightButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 
			{
				if(controlsEnabled)
					rightHold = true;
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				rightHold = false;
			}
		});
		
		ClickListener fireListener = new ClickListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 
			{
				if(controlsEnabled)
				{
					fireHold = true;
					autoFireTimer = phoenix.autoFireRate;
				}
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				fireHold = false;
			}
		};
		
		fireButton1.addListener(fireListener);
		fireButton2.addListener(fireListener);
			
		//Create the top status bar
		Table hpTable = new Table();
		hpTable.top().center();
		
		Table moneyScoreTable = new Table();
		
		hpLabel = new Label("HP:", skin, "default");
		hpLabel.setAlignment(Align.right);
		
		hpBar = new Slider(0f, 100f, 1f, false, skin, "health-bar-horizontal");
		hpBar.setTouchable(Touchable.disabled);
		hpBar.setColor(Color.GREEN);
		updateHpBar();
				
		moneyLabel = new Label("Money:", skin, "default-font-small", Color.GREEN);
		moneyLabel.setAlignment(Align.right);
		
		moneyCounter = new Label("", skin, "default-font-small", Color.GREEN);
		moneyCounter.setAlignment(Align.left);
		updateMoney();
		
		scoreLabel = new Label("Score:", skin, "default-font-small", Color.CYAN);
		scoreLabel.setAlignment(Align.right);
		
		scoreCounter = new Label("", skin, "default-font-small", Color.CYAN);
		scoreCounter.setAlignment(Align.left);
		updateScore();
		
		hpTable.add(hpLabel).padLeft(10).padRight(10);
		hpTable.add(hpBar).expand().fill();
		
		moneyScoreTable.add(moneyLabel).padLeft(5).padRight(5);
		moneyScoreTable.add(scoreLabel).padLeft(5).padRight(5);
		moneyScoreTable.row();
		moneyScoreTable.add(moneyCounter).padLeft(5).padRight(5);
		moneyScoreTable.add(scoreCounter).padLeft(5).padRight(5);
		
		hpTable.add(moneyScoreTable).padLeft(20).padRight(10).center();
		
		//Put the UI buttons into a table
		Table buttonTable = new Table();
		Table leftButtonTable = new Table();
		Table rightButtonTable = new Table();
		
		leftButtonTable.add(fireButton1).fill().width(width).height(height);
		leftButtonTable.row();
		leftButtonTable.add(moveLeftButton).expand().fill();
		
		rightButtonTable.add(fireButton2).fill().width(width).height(height);
		rightButtonTable.row();
		rightButtonTable.add(moveRightButton).expand().fill();
		rightButtonTable.right();
		
		buttonTable.add(leftButtonTable).expandY().left().fill().padTop(5);
		buttonTable.add().expand();
		buttonTable.add(rightButtonTable).expandY().right().fill().padTop(5);
		
		//Add the status bar and the button table to the root table
		rootTable.add(hpTable).expandX().fill();
		rootTable.row();
		rootTable.add(buttonTable).expand().fill();
		rootTable.setFillParent(true);
		rootTable.top();
		
		stage.addActor(rootTable);
	}

	//Update the score counter
	private void updateScore()
	{
		scoreCounter.setText(String.format("%d", phoenix.getScore()));
	}

	//Update the money counter
	private void updateMoney()
	{
		moneyCounter.setText(String.format("$%d", phoenix.getMoney()));
	}

	//Update the health bar
	private void updateHpBar()
	{
		float currentHpPercent = (float)phoenix.getCurrentHP()*100f/(float)phoenix.getTotalHP();
		hpBar.setValue(currentHpPercent);
				
		//Change the color of the health bar if the health drops too low
		if(currentHpPercent <= 50)
		{
			hpBar.setColor(Color.YELLOW);
		}
		if(currentHpPercent <= 25)
		{
			hpBar.setColor(Color.RED);
		}
	}
	
	//Enable the controls
	private void enableControls()
	{
		controlsEnabled = true;
	}
	
	//Disable the controls
	private void disableControls()
	{
		controlsEnabled = false;
	}
	
	//Pause the game.  Called by the GameScreen.
	public void pause()
	{
		paused = true;
		disableControls();
	}
	
	//Starts the delay timer and turns off the pause flag
	public void resume()
	{
		paused = false;
		delayed = true;
		pauseDelay = 0f;
	}
	
	//Compute the accuracy and time bonuses and add them to the player's score and money
	private void computeBonuses()
	{
		//Accuracy bonus = total number of hits/total number of shots fired*current level.
		accuracy = 100f*(float)hits/(float)shotsFired;
		accuracyBonus = (int) (phoenixWorld.level * accuracy);
		phoenix.addMoney(accuracyBonus);
		phoenix.addScore(accuracyBonus);
		
		//Each level is given a maximum time of N minutes, where N is the level number
		//Time bonus = maximum time - time it took to complete the level
		timeBonus = 0;
		int maxTime = phoenixWorld.level * 60;
		
		if(timeElapsed < maxTime)
			timeBonus = (int) (maxTime - timeElapsed);
		
		phoenix.addMoney(timeBonus);
		phoenix.addScore(timeBonus);
	}
}
