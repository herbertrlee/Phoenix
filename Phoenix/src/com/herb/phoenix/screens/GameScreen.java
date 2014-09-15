package com.herb.phoenix.screens;

import org.json.JSONArray;

import com.herb.game.HerbGame;
import com.herb.game.screens.HerbGameScreen;
import com.herb.phoenix.PhoenixGame;
import com.herb.phoenix.controllers.PhoenixController;
import com.herb.phoenix.models.PhoenixWorld;
import com.herb.phoenix.views.PhoenixWorldRenderer;

public class GameScreen extends HerbGameScreen
{
	private JSONArray campaign;
	
	public GameScreen(HerbGame game)
	{
		super(game);
	}

	public GameScreen(PhoenixGame phoenixGame, PhoenixWorld phoenixWorld)
	{
		super(phoenixGame);
		world = phoenixWorld;
	}

	public GameScreen(PhoenixGame phoenixGame, JSONArray campaign)
	{
		super(phoenixGame);
		this.campaign = campaign;
	}

	@Override
	protected void initializeWorld()
	{
		if(world==null)
		{
			world = new PhoenixWorld(game, campaign);
			((PhoenixGame)game).bindShopScreen((PhoenixWorld)world);
		}
		
		controller = new PhoenixController(world, stage, skin);
		renderer = new PhoenixWorldRenderer(world, batch, atlas, font);
	}

	@Override
	public void pause()
	{
		((PhoenixController)controller).pause();
	}
	
	@Override
	public void resume()
	{
		((PhoenixController)controller).resume();
	}
}
