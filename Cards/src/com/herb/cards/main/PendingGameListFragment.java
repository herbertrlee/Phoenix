package com.herb.cards.main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PendingGameListFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public abstract void openPendingGame(String gameId);
		
		public abstract void setTitle(String title);
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {};
	
	private static String[] gameIds = {};
	private static String[] gameNames = {};
	
	private static TextView[] gameTextViews = {};
	
	private LinearLayout gameLinearLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_pending_game_list_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
			
		gameLinearLayout = (LinearLayout) v.findViewById(R.id.pending_game_linear_layout);
		return v;
	}

	@Override
	public void onClick(View v)
	{
		for(String gameId : gameIds)
		{
			if(v.getTag().toString().equals(gameId))
				listener.openPendingGame(gameId);
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

	public void setPendingGameList(JSONArray gameList)
	{
		int length = gameList.length();
		
		gameIds = new String[length];
		gameNames = new String[length];
		
		for(int i=0;i<length;i++)
		{
			try
			{
				JSONObject game = gameList.getJSONObject(i);
				gameIds[i] = game.getString("id");
				gameNames[i] = game.getString("gameName");
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
		
		listener.setTitle("Open Games");
		
		gameLinearLayout.removeAllViews();
		
		gameTextViews = new TextView[gameIds.length];
		
		for(int i=0;i<gameTextViews.length;i++)
		{
			TextView gameTextView = new TextView(getActivity());
			
			gameTextView.setTag(gameIds[i]);
			gameTextView.setText(gameNames[i]);
			gameTextView.setClickable(true);
			gameTextView.setOnClickListener(this);
			
			gameLinearLayout.addView(gameTextView);
		}
	}
}
