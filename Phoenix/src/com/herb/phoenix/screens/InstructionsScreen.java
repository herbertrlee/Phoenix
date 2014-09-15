package com.herb.phoenix.screens;

import org.json.JSONArray;
import org.json.JSONException;

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

public class InstructionsScreen extends HerbMenuScreen
{
	private static final String TITLE = "INSTRUCTIONS";
	
	private static Array<String> instructions = new Array<String>();
	
	private TextureRegionDrawable bgDrawable;
	private boolean initialized = false;
	
	Label titleLabel;
	Array<Label> instructionLabels = new Array<Label>();
	TextButton returnToMainMenuButton;
	
	public static void initialize(JSONArray paragraphs) throws JSONException
	{
		for(int i=0;i<paragraphs.length();i++)
		{
			instructions.add(paragraphs.getString(i));
		}
	}
	
	public InstructionsScreen(HerbGame game)
	{
		super(game);
		bgDrawable = new TextureRegionDrawable(atlas.findRegion("blankWhite"));
		titleLabel = new Label(TITLE, skin, "default-font", Color.BLACK);
		
		for(String instruction : instructions)
		{
			Label instructionLabel = new Label(String.format(">   %s", instruction), skin, "default-font-small", Color.BLACK);
			instructionLabels.add(instructionLabel);
		}
		
		returnToMainMenuButton = new TextButton("Main Menu", skin, "default");
	}

	@Override
	public void createMenu()
	{
		stage.clear();
		
		if(!initialized)
		{
			initialized = true;
			
			returnToMainMenuButton.addListener(new ClickListener(){
				@Override 
	            public void clicked(InputEvent event, float x, float y){
					((PhoenixGame)game).returnToMainMenu(false);
	            }
			});
		}
		
		Table rootTable = new Table();
		rootTable.setBackground(bgDrawable);
		rootTable.setFillParent(true);
		rootTable.top();
		
		Table instructionTable = new Table();
		Table buttonTable = new Table();
		
		instructionTable.add(titleLabel).center().top().padBottom(20);
		instructionTable.row();
		
		for(Label instructionLabel : instructionLabels)
		{
			instructionTable.add(instructionLabel).left().pad(10);
			instructionTable.row();
		}
		
		buttonTable.add(returnToMainMenuButton).fill();
		
		rootTable.add(instructionTable).expand().fill();
		rootTable.add(buttonTable).fillY();
		
		stage.addActor(rootTable);
	}

}
