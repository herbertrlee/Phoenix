package com.herb.phoenix.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.herb.game.HerbGame;
import com.herb.game.screens.HerbMenuScreen;
import com.herb.phoenix.PhoenixGame;

public class SettingsScreen extends HerbMenuScreen
{
	private static final String TITLE = "SETTINGS";
	
	private String username;
	
	Label titleLabel, usernameLabel;
	TextField usernameTextField;
	TextButton saveButton, cancelButton;
	
	TextureRegionDrawable bg;
	
	private boolean initialized = false;
	
	public SettingsScreen(HerbGame game)
	{
		super(game);
		
		username = ((PhoenixGame) game).getUsername();
				
		titleLabel = new Label(TITLE, skin, "default-font", Color.BLACK);
		bg = new TextureRegionDrawable(atlas.findRegion("blankWhite"));
		saveButton = new TextButton("Save Changes", skin, "default");
		cancelButton = new TextButton("Cancel", skin, "default");
		usernameLabel = new Label("Username: ", skin, "default-font", Color.BLACK);
		usernameTextField = new TextField(username, skin);
	}

	@Override
	public void createMenu()
	{
		stage.clear();
		
		if(!initialized)
		{
			initialized = true;
			
			saveButton.addListener(new ClickListener(){
				@Override 
	            public void clicked(InputEvent event, float x, float y){
					usernameTextField.getOnscreenKeyboard().show(false);
					((PhoenixGame)game).setUsername(usernameTextField.getText());
					((PhoenixGame)game).commit();
					((PhoenixGame)game).returnToMainMenu(false);
	            }
			});
			
			cancelButton.addListener(new ClickListener(){
				@Override 
	            public void clicked(InputEvent event, float x, float y){
					usernameTextField.getOnscreenKeyboard().show(false);
					((PhoenixGame)game).returnToMainMenu(false);
	            }
			});
		}
		
		Table rootTable = new Table();
		rootTable.setBackground(bg);
		rootTable.setFillParent(true);
		rootTable.pad(20f);
		rootTable.top();
		
		Table settingsTable = new Table();
		Table buttonTable = new Table();
		settingsTable.top();
		
		settingsTable.add(titleLabel).top().padBottom(20).colspan(2);
		settingsTable.row();
		settingsTable.add(usernameLabel).left();
		settingsTable.add(usernameTextField).expandX().fill();
		
		buttonTable.add(saveButton).fill().pad(10);
		buttonTable.row();
		buttonTable.add(cancelButton).fill().pad(10);
		
		rootTable.add(settingsTable).expand().fill();
		rootTable.add(buttonTable).expandY();
		
		stage.addActor(rootTable);
	}

}
