package com.herb.phoenix.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.herb.game.HerbGame;
import com.herb.game.screens.HerbMenuScreen;
import com.herb.phoenix.PhoenixGame;
import com.herb.phoenix.data.HighScore;

public class HighScoreScreen extends HerbMenuScreen
{
	private static final String TITLE = "HIGH SCORES";
	private static final String NAME_TITLE = "NAME";
	private static final String LEVEL_TITLE = "LEVEL";
	private static final String SCORE_TITLE = "SCORE";
	
	private Array<HighScore> highScores = new Array<HighScore>();
	
	private TextureRegionDrawable bgDrawable;
	Label titleLabel, nameTitleLabel, levelTitleLabel, scoreTitleLabel;
	TextButton mainMenuButton;
	
	boolean initialized = false;
	
	public HighScoreScreen(HerbGame game)
	{
		super(game);
		this.highScores = ((PhoenixGame)game).highScores;
		bgDrawable = new TextureRegionDrawable(atlas.findRegion("blankWhite"));
		titleLabel = new Label(TITLE, skin, "default-font", Color.BLACK);
		nameTitleLabel = new Label(NAME_TITLE, skin, "default-font", Color.BLACK);
		levelTitleLabel = new Label(LEVEL_TITLE, skin, "default-font", Color.BLACK);
		scoreTitleLabel = new Label(SCORE_TITLE, skin, "default-font", Color.BLACK);
		mainMenuButton = new TextButton("Main Menu", skin, "default");
	}

	@Override
	public void createMenu()
	{
		stage.clear();
		
		if(!initialized)
		{
			mainMenuButton.addListener(new ClickListener(){
				@Override 
	            public void clicked(InputEvent event, float x, float y){
					((PhoenixGame)game).returnToMainMenu(false);
	            }
			});
		}
				
		Table rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.setBackground(bgDrawable);
		rootTable.pad(20).top();
		
		Table scoreTable = new Table();
		Table buttonTable = new Table();
		scoreTable.top();
		
		scoreTable.add(titleLabel).top().padBottom(20).colspan(2);
		scoreTable.row();
		scoreTable.add(nameTitleLabel).left().pad(10);
		scoreTable.add(levelTitleLabel).expandX().center();
		scoreTable.add(scoreTitleLabel).right().pad(10);
		scoreTable.row();
		
		int i=0;
		for(HighScore highScore : highScores)
		{
			i++;
			Label nameLabel = new Label(String.format("%d.  %s", i, highScore.name), skin, "default-font-small", Color.BLACK);
			Label levelLabel = new Label(String.format("%d", highScore.level), skin, "default-font-small", Color.BLACK);
			Label scoreLabel = new Label(String.format("%d", highScore.score), skin, "default-font-small", Color.BLACK);
			scoreTable.add(nameLabel).left().pad(10);
			scoreTable.add(levelLabel).center();
			scoreTable.add(scoreLabel).right().pad(10);
			scoreTable.row();
		}
		
		buttonTable.add(mainMenuButton).fill();
		
		rootTable.add(scoreTable).expand().fill();
		rootTable.add(buttonTable).expandY();
		stage.addActor(rootTable);
	}
}
