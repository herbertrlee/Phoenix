package com.herb.phoenix.views;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.herb.game.models.HerbWorld;
import com.herb.game.views.HerbWorldRenderer;
import com.herb.phoenix.models.Bomb;
import com.herb.phoenix.models.EnemyShip;
import com.herb.phoenix.models.EnemyShot;
import com.herb.phoenix.models.Explosion;
import com.herb.phoenix.models.Money;
import com.herb.phoenix.models.PhoenixShip;
import com.herb.phoenix.models.PhoenixWorld;
import com.herb.phoenix.models.PhoenixShot;

public class PhoenixWorldRenderer extends HerbWorldRenderer
{
	private PhoenixShip phoenix;
	private PhoenixWorld phoenixWorld;
	
	private TextureRegion backGroundTexture;
	
	private TextureRegion phoenixTexture;
	private TextureRegion moneyTexture;
	private TextureRegion explosionTexture;
	private TextureRegion enemyShotTexture;
	private TextureRegion bombTexture;
	private IntMap<TextureRegion> phoenixShotTextureMap;
	private Map<String, TextureRegion> enemyShipTextureMap;
	
	private Array<PhoenixShot> phoenixActiveShots;
	private Array<EnemyShip> phoenixActiveShips;
	private Array<EnemyShot> activeEnemyShots;
	private Array<Money> activeMoney;
	private Array<Explosion> activeExplosions;
	private Array<Bomb> activeBombs;
	
	//private FPSLogger logger;
	
	
	public PhoenixWorldRenderer(HerbWorld world, SpriteBatch batch,
			TextureAtlas atlas, BitmapFont font)
	{
		super(world, batch, atlas, font);
		
		phoenixWorld = (PhoenixWorld)world;
		phoenix = phoenixWorld.getShip();
		phoenixActiveShots = phoenixWorld.getActiveShots();
		phoenixActiveShips = phoenixWorld.getActiveShips();
		activeEnemyShots = phoenixWorld.getActiveEnemyShots();
		activeMoney = phoenixWorld.getActiveMoney();
		activeExplosions = phoenixWorld.getActiveExplosions();
		activeBombs = phoenixWorld.getActiveBombs();
		//logger = new FPSLogger();
	}

	@Override
	protected void loadTextures()
	{
		enemyShipTextureMap = new HashMap<String, TextureRegion>();
		phoenixShotTextureMap = new IntMap<TextureRegion>();
		
		backGroundTexture = atlas.findRegion("blankWhite");
		phoenixTexture = atlas.findRegion("phoenix");
		moneyTexture = atlas.findRegion("money");
		explosionTexture = atlas.findRegion("explosion");
		bombTexture = atlas.findRegion("bomb");
		
		enemyShotTexture = atlas.findRegion("mookShot");
		
		for(int type : PhoenixShot.types)
		{
			phoenixShotTextureMap.put(type, atlas.findRegion(PhoenixShot.textures.get(type)));
		}
		
		for(Array<String> shipTextures : EnemyShip.typeToTextures)
		{
			for(String texture : shipTextures)
			{
				if(!enemyShipTextureMap.containsKey(texture))
				{
					enemyShipTextureMap.put(texture, atlas.findRegion(texture));
				}
			}
		}
	}

	@Override
	public void drawBackground()
	{
		batch.draw(backGroundTexture, phoenixWorld.bounds.x*ppuX, phoenixWorld.bounds.y*ppuY, phoenixWorld.bounds.width*ppuX, phoenixWorld.bounds.height*ppuX);
	}

	@Override
	public void drawSprites()
	{
		drawPhoenix();
		drawShots();
		drawMoney();
		drawEnemyShips();
		drawExplosions();
		drawBombs();
		
		//logger.log();
	}

	private void drawBombs()
	{
		for(Bomb bomb : activeBombs)
		{			
			switch(bomb.bombState)
			{
			case EXPLODED:		
				batch.setColor(1f, 1f, 1f, bomb.alpha);
				batch.draw(explosionTexture, (bomb.bounds.x + phoenixWorld.bounds.x)*ppuX, bomb.bounds.y*ppuY, bomb.bounds.width*ppuX, bomb.bounds.height*ppuY);
				batch.setColor(1f, 1f, 1f, 1f);
				break;
			case EXPLODING:
				batch.draw(explosionTexture, (bomb.bounds.x + phoenixWorld.bounds.x)*ppuX, bomb.bounds.y*ppuY, bomb.bounds.width*ppuX, bomb.bounds.height*ppuY);
				break;
			case FALLING:
				batch.draw(bombTexture, (bomb.bounds.x + phoenixWorld.bounds.x)*ppuX, bomb.bounds.y*ppuY, bomb.bounds.width*ppuX, bomb.bounds.height*ppuY);
				break;
			default:
				break;
			
			}
		}
	}

	private void drawExplosions()
	{
		for(Explosion explosion : activeExplosions)
		{
			batch.setColor(1f, 1f, 1f, explosion.alpha);
			batch.draw(explosionTexture, (explosion.bounds.x + phoenixWorld.bounds.x)*ppuX, explosion.bounds.y*ppuY, explosion.bounds.width*ppuX, explosion.bounds.height*ppuY);
		}
		batch.setColor(1f, 1f, 1f, 1f);
	}

	private void drawMoney()
	{
		for(Money money : activeMoney)
		{
			batch.draw(moneyTexture, (money.position.x + phoenixWorld.bounds.x)*ppuX, money.position.y*ppuY, money.bounds.width*ppuX, money.bounds.height*ppuY);
		}
	}

	private void drawEnemyShips()
	{
		for(EnemyShip ship : phoenixActiveShips)
		{
			batch.setColor(1f, 1f, 1f, ship.alpha);
			if(ship.visible)
				batch.draw(enemyShipTextureMap.get(ship.currentTexture), (ship.position.x + phoenixWorld.bounds.x)*ppuX, ship.position.y*ppuY, ship.bounds.width*ppuX, ship.bounds.height*ppuY);
		}
		batch.setColor(1f, 1f, 1f, 1f);
	}

	private void drawShots()
	{
		for(PhoenixShot shot : phoenixActiveShots)
		{
			batch.draw(phoenixShotTextureMap.get(shot.type), (shot.bounds.x+phoenixWorld.bounds.x)*ppuX, shot.bounds.y*ppuY, shot.bounds.width*ppuX, shot.bounds.height*ppuY);
		}
		
		for(EnemyShot shot : activeEnemyShots)
		{
			batch.draw(enemyShotTexture, (shot.bounds.x+phoenixWorld.bounds.x)*ppuX, shot.bounds.y*ppuY, shot.bounds.width*ppuX, shot.bounds.height*ppuY);
		}
	}

	@Override
	public void dispose()
	{
		
	}

	private void drawPhoenix()
	{
		batch.draw(phoenixTexture, (phoenix.bounds.x + phoenixWorld.bounds.x)*ppuX, phoenix.bounds.y*ppuY, phoenix.bounds.width*ppuX, phoenix.bounds.height*ppuY);
	}

	@Override
	public void setSize(int width, int height)
	{

		this.width = width;
		this.height = height;
		
		float ratio = (float)width/(float)height;
		
		cameraWidth = DEFAULT_CAMERA_WIDTH;
		cameraHeight = cameraWidth/ratio;
		
		ppuX = (float)width/cameraWidth;
		ppuY = (float)height/cameraHeight;
		
		phoenixWorld.bounds.x = (cameraWidth - cameraHeight)/2f;
		phoenixWorld.bounds.y = 0;
		phoenixWorld.bounds.width = cameraHeight;
		phoenixWorld.bounds.height = cameraHeight*0.9f;
				
		cam.viewportWidth = cameraWidth;
		cam.viewportHeight = cameraHeight;
		this.cam.position.set(cameraWidth / 2f, cameraHeight / 2f, 0);
		this.cam.update();
	}
}
