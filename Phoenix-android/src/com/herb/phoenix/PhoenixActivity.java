package com.herb.phoenix;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class PhoenixActivity extends AndroidApplication implements PhoenixConfig{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = false;
		cfg.useCompass = false;
		cfg.useWakelock = true;
        
        initialize(new PhoenixGame(this), cfg);
    }

    @Override
	public void log(String s)
	{
		Log.e("phoenix", s);
	}

	@Override
	public void quit()
	{
		Gdx.app.exit();
		this.finish();
	}
}