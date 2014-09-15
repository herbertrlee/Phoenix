package com.herb.phoenix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class PhoenixDesktop implements PhoenixConfig {
		
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Phoenix";
		cfg.width = 1920/2;
		cfg.height = 1080/2;
		
		
		new LwjglApplication(new PhoenixGame(new PhoenixDesktop()), cfg);
	}

	@Override
	public void log(String s)
	{
		System.out.println(s);
		
	}

	@Override
	public void quit()
	{
		Gdx.app.exit();
	}
}
