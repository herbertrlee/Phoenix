package com.herb.phoenix.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.tablelayout.Cell;
import com.herb.game.HerbGame;
import com.herb.game.screens.HerbMenuScreen;
import com.herb.phoenix.PhoenixGame;


public class MainMenuScreen extends HerbMenuScreen
{
	private TextureRegion bg, cover;
	private TextureRegionDrawable bgDrawable;
	private boolean initialized;
	
	Label phoenixLabel = new Label("Phoenix", skin, "default-font-large", Color.BLACK);
	Image coverImage;
	Label nameLabel = new Label("Herbert Lee 2014.", skin, "default-font-small", Color.BLACK);
			
	TextButton startGameButton = new TextButton("Start New Game", skin, "default");
	TextButton instructionsButton = new TextButton("Instructions", skin, "default");
	TextButton highScoreButton = new TextButton("High Scores", skin, "default");
	TextButton settingsButton = new TextButton("Settings", skin, "default");
	TextButton quitGameButton = new TextButton("Quit Game", skin, "default");
	
	Cell phoenixLabelCell,  nameLabelCell;
	Cell startGameButtonCell, instructionsButtonCell, highScoreButtonCell, settingsButtonCell, quitGameButtonCell;
	
	private float width, height;
	
	public MainMenuScreen(HerbGame game)
	{
		super(game);
		bg = atlas.findRegion("blankWhite");
		cover = atlas.findRegion("cover");
		coverImage = new Image(cover);
		width = stage.getWidth();
		height = stage.getHeight();
		bgDrawable = new TextureRegionDrawable(bg);
	}

	@Override
	public void createMenu()
	{
		if(!initialized)
		{
			startGameButton.addListener(new ClickListener(){
				@Override 
	            public void clicked(InputEvent event, float x, float y){
	                ((PhoenixGame)game).startNewGame();
	            }
			});
			
			instructionsButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					((PhoenixGame)game).goToInstructions();
				}
			});
			
			highScoreButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					((PhoenixGame)game).goToHighScores();
				}
			});
			
			settingsButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					((PhoenixGame)game).goToSettings();
				}
			});
			
			quitGameButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					((PhoenixGame)game).quitGame();
				}
			});
			
			initialized = true;
		}
		
		stage.clear();
		
		Table rootTable = new Table();
		Table nameTable = new Table();
		Table menuTable = new Table();
			
		phoenixLabelCell = nameTable.add(phoenixLabel);
		nameTable.row();
		nameTable.add(coverImage).expandY().fill();
		nameTable.row();
		nameLabelCell = nameTable.add(nameLabel);
		
		startGameButtonCell = menuTable.add(startGameButton).fill();
		menuTable.row();
		instructionsButtonCell = menuTable.add(instructionsButton).fill();
		menuTable.row();
		highScoreButtonCell = menuTable.add(highScoreButton).fill();
		menuTable.row();
		settingsButtonCell = menuTable.add(settingsButton).fill();
		menuTable.row();
		quitGameButtonCell = menuTable.add(quitGameButton).fill();
				
		rootTable.add(nameTable).expand().fill();
		rootTable.add(menuTable).fillY();

		rootTable.setFillParent(true);
		rootTable.setBackground(bgDrawable);
		stage.addActor(rootTable);
	}
	
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		
		this.width = width;
		this.height = height;
		
		startGameButtonCell.pad(this.height*0.05f, this.width*0.01f, this.height*0.05f, this.width*0.01f);
		instructionsButtonCell.pad(this.height*0.05f, this.width*0.01f, this.height*0.05f, this.width*0.01f);
		highScoreButtonCell.pad(this.height*0.05f, this.width*0.01f, this.height*0.05f, this.width*0.01f);
		settingsButtonCell.pad(this.height*0.05f, this.width*0.01f, this.height*0.05f, this.width*0.01f);
		quitGameButtonCell.pad(this.height*0.05f, this.width*0.01f, this.height*0.05f, this.width*0.01f);
		
		phoenixLabelCell.pad(this.height*0.05f, this.width*0.01f, this.height*0.05f, this.width*0.01f);
		nameLabelCell.pad(this.height*0.05f, this.width*0.01f, this.height*0.05f, this.width*0.01f);
	}

}
