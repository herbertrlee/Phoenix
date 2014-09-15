/*
 * PhoenixGame.java
 * Sets up the Phoenix game by reading in the configuration files and high score data.
 */

package com.herb.phoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.herb.game.HerbGame;
import com.herb.phoenix.data.HighScore;
import com.herb.phoenix.models.EnemyShip;
import com.herb.phoenix.models.PhoenixShot;
import com.herb.phoenix.models.PhoenixWorld;
import com.herb.phoenix.models.WeaponType;
import com.herb.phoenix.screens.GameScreen;
import com.herb.phoenix.screens.HighScoreScreen;
import com.herb.phoenix.screens.InstructionsScreen;
import com.herb.phoenix.screens.MainMenuScreen;
import com.herb.phoenix.screens.SettingsScreen;
import com.herb.phoenix.screens.ShopScreen;

public class PhoenixGame extends HerbGame
{
	//Locations of the various internal read-only data files.  
	private static final String CAMPAIGN_FILE = "data/phoenixCampaign.json";
	//private static final String CAMPAIGN_FILE = "data/test.json";
	private static final String SETTINGS_FILE = "data/phoenixSettings.json";
	
	//Locations of the saved data files.
	private static final String SAVE_FILE = "phoenixSave.json";
	
	//Default username.  Can be changed in settings.
	private static final String DEFAULT_USERNAME = "Phoenix";
	
	//The various screens in Phoenix.
	GameScreen gameScreen;
	MainMenuScreen mainMenuScreen;
	ShopScreen shopScreen;
	HighScoreScreen highScoreScreen;
	InstructionsScreen instructionsScreen;
	SettingsScreen settingsScreen;
	
	//The interface with which the game prints out data and makes network connections.
	PhoenixConfig config;
	
	//The campaign loaded from the local data.
	JSONArray campaign;
	
	//User's name.  Used for high score saving.
	private String username;
	
	//Array of the top 10 current high scores.
	public Array<HighScore> highScores = new Array<HighScore>();
	
	//Filehandle for the high score data
	private FileHandle saveGameFile;

	//Constructor.  Takes a PhoenixConfig interface as an argument.
	public PhoenixGame(PhoenixConfig config)
	{
		super();
		this.config = config;
		setUsername(DEFAULT_USERNAME);
	}

	//Sets the locations of the various UI files.
	@Override
	protected void initializeLocations()
	{
		fontFntLocation = "fonts/sansserif.fnt";
		fontPngLocation = "fonts/sansserif.png";
		skinLocation = "data/uiskin.json";
		textureAtlasLocation = "images/phoenix.pack";
		skinFonts = new Array<String>();
		skinFonts.add("default-font");
		skinFonts.add("default-font-large");
		skinFonts.add("default-font-small");
	}

	//Reads in the data from the configuration files, initializes static class variables, and creates the screens.
	@Override
	protected void initializeScreens()
	{
		//Load campaign data
		FileHandle handle = Gdx.files.internal(CAMPAIGN_FILE);
		try
		{
			JSONObject json = new JSONObject(handle.readString());
			campaign = json.getJSONArray("levels");
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		//Load setting data
		FileHandle handle2 = Gdx.files.internal(SETTINGS_FILE);
		try
		{
			JSONObject json = new JSONObject(handle2.readString());
			
			EnemyShip.initialize(json.getJSONArray("enemies"));
			PhoenixShot.initialize(json.getJSONArray("phoenixShots"));
			WeaponType.initialize(json.getJSONArray("phoenixWeapons"));
			InstructionsScreen.initialize(json.getJSONArray("instructions"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		//Load high score data
		saveGameFile = Gdx.files.local(SAVE_FILE);
		if(saveGameFile.exists())
		{
			try
			{
				JSONObject json = new JSONObject(saveGameFile.readString());
				
				username = json.getString("username");
				
				JSONArray scores = json.getJSONArray("scores");
				
				for(int i=0;i<scores.length();i++)
				{
					JSONObject scoreObject = scores.getJSONObject(i);
					
					String name = scoreObject.getString("name");
					int level = scoreObject.getInt("level");
					int score = scoreObject.getInt("score");
					
					HighScore highScore = new HighScore(name, level, score);
					
					highScores.add(highScore);
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		
		//Create screens
		mainMenuScreen = new MainMenuScreen(this);
		gameScreen = new GameScreen(this);
		shopScreen = new ShopScreen(this);
		highScoreScreen = new HighScoreScreen(this);
		settingsScreen = new SettingsScreen(this);
		instructionsScreen = new InstructionsScreen(this);
				
		//Open the main menu
		setScreen(mainMenuScreen);
	}
	
	//Start a new game
	public void startNewGame()
	{
		gameScreen = new GameScreen(this, campaign);
		setScreen(gameScreen);
	}
	
	//Output a string.
	@Override
	public void log(String s)
	{
		config.log(s);
	}

	//Returns to main menu.  Disposes of any existing games.
	public void returnToMainMenu(boolean disposeGame)
	{
		if(disposeGame)
			gameScreen.dispose();
		
		setScreen(mainMenuScreen);
	}
	
	
	//Goes to high score list.
	public void goToHighScores()
	{
		setScreen(highScoreScreen);
	}
	
	//Goes to instructions.
	public void goToInstructions()
	{
		setScreen(instructionsScreen);
	}

	//Goes to shop.
	public void goToShop()
	{
		gameScreen.dispose();
		setScreen(shopScreen);
	}
	
	//Binds the shop to a particular game world.
	public void bindShopScreen(PhoenixWorld phoenixWorld)
	{
		shopScreen.setWorld(phoenixWorld);
	}

	//Goes to the next level of a game world.
	public void goToNextLevel(PhoenixWorld phoenixWorld)
	{
		gameScreen = new GameScreen(this, phoenixWorld);
		setScreen(gameScreen);
	}

	//Exits.
	public void quitGame()
	{
		config.quit();
	}

	//Goes to settings.
	public void goToSettings()
	{
		setScreen(settingsScreen);
	}

	//Returns the username.
	public String getUsername()
	{
		return username;
	}

	//Sets the username.
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	//Saves high score data to disk.
	public void commit()
	{
		JSONObject json = new JSONObject();
		
		try
		{
			json.put("username", username);
			
			JSONArray highScoreJsonArray = new JSONArray();
			
			for(HighScore highScore : highScores)
			{
				JSONObject highScoreJsonObject = new JSONObject();
				
				highScoreJsonObject.put("name", highScore.name);
				highScoreJsonObject.put("level", highScore.level);
				highScoreJsonObject.put("score", highScore.score);
				highScoreJsonArray.put(highScoreJsonObject);
			}
			
			json.put("scores", highScoreJsonArray);
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		saveGameFile.writeString(json.toString(), false);
	}
	
	//Inserts a score into its proper position in the high score list.  If there are more than ten scores, remove the lowest one.  Save the list to disk.
	public void saveHighScore(int level, int score)
	{
		int len = highScores.size;
		
		int i = 0;
		
		HighScore newHighScore = new HighScore(username, level, score);
		
		while(i<len && newHighScore.compareTo(highScores.get(i)) < 0)
		{
			i++;
		}
		
		highScores.insert(i, newHighScore);
		
		if(highScores.size > 10)
		{
			highScores.pop();
		}
		
		commit();
	}
}