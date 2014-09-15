package com.herb.cards.main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainMenuFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public abstract void openGame(String gameId, int status);
		public abstract void setTitle(String title);
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {};
	
	private String[] gameIds = {};
	private String[] gameNames = {};
	private int[] gameStatuses = {};
	
	private TextView[] gameTextViews = {};
	
	private LinearLayout gameLinearLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_main_menu_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
			
		gameLinearLayout = (LinearLayout) v.findViewById(R.id.current_game_list_linear_layout);
		return v;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		updateUi();
	}
	
	@Override
	public void onClick(View v)
	{
		for(int i=0;i<gameTextViews.length;i++)
		{
			if(v.getTag().toString().equals(gameIds[i]))
				listener.openGame(gameIds[i], gameStatuses[i]);
		}
		
		switch(v.getId())
		{
			default:
				break;
		}
	}

	public void setListener(Listener listener)
	{
		this.listener = listener;
	}
	
	public void setGames(JSONArray gameList)
	{
		int length = gameList.length();
		gameIds = new String[length];
		gameNames = new String[length];
		gameStatuses = new int[length];
		
		for(int i=0;i<length;i++)
		{
			try
			{
				JSONObject game = gameList.getJSONObject(i);
				
				gameIds[i] = game.getString("gameId");
				gameNames[i] = game.getString("gameName");
				gameStatuses[i] = game.getInt("gameStatus");
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updateUi()
	{
		if(getActivity() == null)
			return;
		
		if(listener == null)
			return;
		
		listener.setTitle("My Games");
		
		gameLinearLayout.removeAllViews();
		
		gameTextViews = new TextView[gameIds.length];
		
		for(int i=0;i<gameIds.length;i++)
		{
			TextView gameTextView = new TextView(getActivity());
			gameTextView.setTag(gameIds[i]);
			
			gameTextView.setText(gameNames[i]);
			gameTextView.setClickable(true);
			gameTextView.setOnClickListener(this);
			
			gameLinearLayout.addView(gameTextView);
			gameTextViews[i] = gameTextView;
		}
	}
}
