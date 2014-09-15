package com.herb.phoenix.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.herb.game.HerbGame;
import com.herb.game.screens.HerbMenuScreen;
import com.herb.phoenix.PhoenixGame;
import com.herb.phoenix.models.PhoenixShip;
import com.herb.phoenix.models.PhoenixWorld;
import com.herb.phoenix.models.WeaponType;
import com.herb.phoenix.ui.FlashingLabel;

public class ShopScreen extends HerbMenuScreen
{
	private static final int REPAIR_SHIELD_COST = 100;
	private static final int UPGRADE_SHIP_COST = 750;
	private static final String PURCHASED = "PURCHASED";
	private static final String MAX_SHIELD = "MAX SHIELD";
	private static final float INNER_PADDING = 15f;
	private static final float OUTER_PADDING = 10f;
	private static final float FLASH_DURATION = 2f;
	
	private static final String repairDescription = "Sometimes bad guys shoot your ship.  That's a bad thing.  Clicking that button will repair it.  That's a good thing!";
	private static final String upgradeDescription = "Makes your ship fly faster, take more hits, deal more Phoenix Gun and Plasma Torpedo damage, have a quicker auto-fire rate, and be more attractive to sexy lady ships (or guy ships, if that's your ship's thing).";
	
	private PhoenixWorld phoenixWorld;
	private PhoenixShip phoenixShip;
	
	private Slider hpBar;
	private Label hpLabel, moneyLabel, moneyCounter, scoreLabel, scoreCounter;
	
	private final TextButton repairButton = new TextButton("Repair Shield", skin, "default-small");
	private final TextButton upgradeButton = new TextButton("Upgrade Ship", skin, "default-small");
	private final Array<TextButton> weaponButtons = new Array<TextButton>();
	private final Array<TextButton> weaponExplanationButtons = new Array<TextButton>();
	private final TextButton nextLevelButton = new TextButton("Next Level", skin, "default-small");
	
	private final Label allPurchasesFinalLabel = new Label("Welcome to Phoenix Shop!\nAll Sales Are Final!", skin, "default-black"); 
	private final FlashingLabel repairCostLabel = new FlashingLabel(String.format("$%d", REPAIR_SHIELD_COST), skin, "default-font-small", Color.BLACK, Color.RED);
	private final FlashingLabel upgradeCostLabel = new FlashingLabel(String.format("$%d", UPGRADE_SHIP_COST), skin, "default-font-small", Color.BLACK, Color.RED);
	private final Array<FlashingLabel> weaponLabels = new Array<FlashingLabel>();
	
	private final TextButton repairExplanationButton = new TextButton("?", skin, "default-small");
	private final TextButton upgradeExplanationButton = new TextButton("?", skin, "default-small");
	
	private IntArray weaponLevels = new IntArray();
	
	private TextureRegion bg;
	
	private float width = 0;
	
	private boolean initialized;
	
	public ShopScreen(HerbGame game)
	{
		super(game);
		
		bg = atlas.findRegion("blankWhite");
		weaponLevels = WeaponType.weaponLevels;
		
		allPurchasesFinalLabel.setAlignment(Align.center);
		
		repairButton.pad(INNER_PADDING);
		upgradeButton.pad(INNER_PADDING);
		repairExplanationButton.pad(INNER_PADDING);
		upgradeExplanationButton.pad(INNER_PADDING);
		nextLevelButton.pad(INNER_PADDING);
		
		repairButton.addListener(new ClickListener(){
			@Override 
            public void clicked(InputEvent event, float x, float y){
				if(!repairButton.isDisabled())
				{
					phoenixShip.spendMoney(REPAIR_SHIELD_COST);
					phoenixShip.repair();
					updateMoney();
					updateHpBar();
					updateButtons();
				}
				else
				{
					repairCostLabel.startFlash(FLASH_DURATION);
				}
            }
		});
		
		upgradeButton.addListener(new ClickListener(){
			@Override 
            public void clicked(InputEvent event, float x, float y){
				if(!upgradeButton.isDisabled())
				{
					phoenixShip.spendMoney(UPGRADE_SHIP_COST);
					phoenixShip.upgrade();
					updateMoney();
					updateButtons();
				}
				else
				{
					upgradeCostLabel.startFlash(FLASH_DURATION);
				}
            }
		});
		
		repairExplanationButton.addListener(new ClickListener(){
			@Override 
            public void clicked(InputEvent event, float x, float y){
				Dialog dialog = new Dialog("Repair Shield", skin, "dialog");
				Label descriptionLabel = new Label(repairDescription, skin, "default-small");
				descriptionLabel.setWrap(true);
				dialog.getContentTable().add(descriptionLabel).width(width/2f);
				TextButton exitButton = new TextButton("Return to Shop", skin, "default-small");
				dialog.button(exitButton);
				dialog.layout();
				dialog.show(stage);
            }
		});
		
		upgradeExplanationButton.addListener(new ClickListener(){
			@Override 
            public void clicked(InputEvent event, float x, float y){
				Dialog dialog = new Dialog("Upgrade Ship", skin, "dialog");
				Label descriptionLabel = new Label(upgradeDescription, skin, "default-small");
				descriptionLabel.setWrap(true);
				dialog.getContentTable().add(descriptionLabel).width(width/2f);
				TextButton exitButton = new TextButton("Return to Shop", skin, "default-small");
				dialog.button(exitButton);
				dialog.layout();
				dialog.show(stage);
            }
		});
		
		if(weaponButtons.size != weaponLevels.size)
		{
			for(int i=0;i<weaponLevels.size;i++)
			{
				final int weaponLevel = weaponLevels.get(i);
				
				final String name = WeaponType.nameMap.get(weaponLevel);
				final String description = WeaponType.descriptionMap.get(weaponLevel);
				final int cost = WeaponType.costMap.get(weaponLevel, 0);
				
				final TextButton weaponButton = new TextButton(name, skin, "default-small");
				final FlashingLabel weaponLabel = new FlashingLabel(String.format("$%d", cost), skin, "default-font-small", Color.BLACK, Color.RED);
				final TextButton weaponExplanationButton = new TextButton("?", skin, "default-small");
				
				weaponButton.pad(INNER_PADDING);
				weaponExplanationButton.pad(INNER_PADDING);
				
				weaponButton.addListener(new ClickListener(){
					@Override 
		            public void clicked(InputEvent event, float x, float y){
						if(!weaponButton.isDisabled())
						{
							phoenixShip.spendMoney(cost);
							phoenixShip.setWeaponType(weaponLevel);
							updateMoney();
							updateButtons();
						}
						else
						{
							weaponLabel.startFlash(FLASH_DURATION);
						}
		            }
				});
				
				weaponExplanationButton.addListener(new ClickListener(){
					@Override 
		            public void clicked(InputEvent event, float x, float y){
						Dialog dialog = new Dialog(name, skin, "dialog");
						dialog.padTop(20);
						Label descriptionLabel = new Label(description, skin, "default-small");
						descriptionLabel.setWrap(true);
						dialog.getContentTable().add(descriptionLabel).width(width/2f);
						TextButton exitButton = new TextButton("Return to Shop", skin, "default-small");
						dialog.button(exitButton);
						dialog.layout();
						dialog.show(stage);
		            }
				});
				weaponButtons.add(weaponButton);
				weaponLabels.add(weaponLabel);
				weaponExplanationButtons.add(weaponExplanationButton);
			}
		}
		
		
	}

	@Override
	public void createMenu()
	{
		stage.clear();
		updateButtons();
				
		if(!initialized)
		{
			initialized = true;
			
			nextLevelButton.addListener(new ClickListener(){
				@Override 
	            public void clicked(InputEvent event, float x, float y){
					phoenixWorld.goToNextLevel();
	                ((PhoenixGame)game).goToNextLevel(phoenixWorld);
	            }
			});
		}
		
		Table rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.top();
		rootTable.setBackground(new TextureRegionDrawable(bg));
		
		
		Table menuTable = new Table();
							
		Table hpTable = new Table();
		hpTable.center();
		
		Table moneyScoreTable = new Table();
		
		hpLabel = new Label("HP:", skin, "default-black");
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
		
		menuTable.add(allPurchasesFinalLabel).colspan(3);
		menuTable.row();
		menuTable.add(repairButton).fill().padTop(OUTER_PADDING);
		menuTable.add(repairCostLabel).expandX();
		menuTable.add(repairExplanationButton);
		menuTable.row();
		menuTable.add(upgradeButton).fill().padTop(OUTER_PADDING);
		menuTable.add(upgradeCostLabel).expandX();
		menuTable.add(upgradeExplanationButton);
		menuTable.row();
		
		for(int i=0;i<weaponButtons.size;i++)
		{
			menuTable.add(weaponButtons.get(i)).fill().padTop(OUTER_PADDING);
			menuTable.add(weaponLabels.get(i)).expandX();
			menuTable.add(weaponExplanationButtons.get(i));
			menuTable.row();
		}
		
		menuTable.add(nextLevelButton).colspan(3).fill().padTop(OUTER_PADDING);
		
		rootTable.add(hpTable).expandX().fill();
		rootTable.row();
		rootTable.add(menuTable).expand().fill().pad(30, 100, 30, 100);
		
		stage.addActor(rootTable);
	}

	public void setWorld(PhoenixWorld phoenixWorld)
	{
		this.phoenixWorld = phoenixWorld;
		this.phoenixShip = phoenixWorld.getShip();
	}
	
	private void updateScore()
	{
		scoreCounter.setText(String.format("%d", phoenixShip.getScore()));
	}

	private void updateMoney()
	{
		moneyCounter.setText(String.format("$%d", phoenixShip.getMoney()));
	}

	private void updateHpBar()
	{
		float currentHpPercent = (float)phoenixShip.getCurrentHP()*100f/(float)phoenixShip.getTotalHP();
		hpBar.setValue(currentHpPercent);
	}
	
	private void updateButtons()
	{
		repairButton.setDisabled((phoenixShip.getMoney() < REPAIR_SHIELD_COST) || (phoenixShip.getCurrentHP() == phoenixShip.getTotalHP()));
		
		if(phoenixShip.getCurrentHP() == phoenixShip.getTotalHP())
			repairCostLabel.setText(MAX_SHIELD);
		else
			repairCostLabel.setText(String.format("$%d", REPAIR_SHIELD_COST));
		
		upgradeButton.setDisabled((phoenixShip.getMoney() < UPGRADE_SHIP_COST) || (phoenixShip.upgraded));
		
		if(phoenixShip.upgraded)
			upgradeCostLabel.setText(PURCHASED);
		else
			upgradeCostLabel.setText(String.format("$%d", UPGRADE_SHIP_COST));
		
		for(int i=0;i<weaponLevels.size;i++)
		{
			int weaponLevel = weaponLevels.get(i);
			
			weaponButtons.get(i).setDisabled((phoenixShip.getMoney() < WeaponType.costMap.get(weaponLevel, 0)) || (phoenixShip.getWeaponLevel() >= weaponLevel));
			
			if(phoenixShip.getWeaponLevel() >= weaponLevel)
				weaponLabels.get(i).setText(PURCHASED);
			else
				weaponLabels.get(i).setText(String.format("$%d", WeaponType.costMap.get(weaponLevel, 0)));
		}
	}
	
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		
		this.width = width;
	}
	
	@Override
	public void update(float delta)
	{
		repairCostLabel.update(delta);
		upgradeCostLabel.update(delta);
		for(FlashingLabel weaponLabel : weaponLabels)
		{
			weaponLabel.update(delta);
		}
	}
}
