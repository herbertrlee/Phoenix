package com.herb.phoenix.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class FlashingLabel extends Label
{
	private static final float FLASH_INTERVAL = 0.2f;
	
	private Color normal, flash;
	private boolean flashing;
	private float flashDuration, maxFlashDuration, flashIntervalDelta;
	private LabelStyle style;
	
	
	public FlashingLabel(CharSequence text, Skin skin, String fontName, Color normal, Color flash)
	{
		super(text, skin, fontName, normal);
		style = super.getStyle();
		this.normal = normal;
		this.flash = flash;
		this.flashing = false;
		flashDuration = 0;
		maxFlashDuration = 0;
	}
	
	public void startFlash(float maxFlashDuration)
	{
		this.maxFlashDuration = maxFlashDuration;
		flashDuration = 0;
		flashing = true;
	}
	
	public void update(float delta)
	{
		if(flashing)
		{
			flashDuration += delta;
			flashIntervalDelta += delta;
			
			if(flashIntervalDelta >= FLASH_INTERVAL)
			{
				flashIntervalDelta = 0;
				if(style.fontColor.equals(normal))
					style.fontColor = flash;
				else
					style.fontColor = normal;
			}
			
			if(flashDuration >= maxFlashDuration)
			{
				flashing = false;
				style.fontColor = normal;
				flashDuration = 0;
				maxFlashDuration = 0;
			}
		}
	}
	
}
