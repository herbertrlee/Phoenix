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

public class PendingGameInfoFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public abstract void startGame(String gameId);
		public abstract void cancelGame(String gameId);
		public abstract void joinGame(String gameId);
		public abstract void leaveGame(String gameId);
		
		public abstract void setTitle(String title);
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {R.id.start_game_button, R.id.cancel_game_button, R.id.join_game_button, R.id.leave_game_button};
	private static final String GAME_FULL = "Game is full!";
	
	private String gameId = "";
	private String gameName = "";
	private int maxPlayers = 0;
	private String[] playerNames = {};
	private boolean userIsHost = false;
	
	private TextView gameNameTextView, playersNeededTextView;
	private LinearLayout nonHostLinearLayout, hostLinearLayout;
	private LinearLayout playerNameLinearLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_pending_game_info_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
		
		playerNameLinearLayout = (LinearLayout) v.findViewById(R.id.player_name_layout);
		nonHostLinearLayout = (LinearLayout) v.findViewById(R.id.non_host_button_layout);
		hostLinearLayout = (LinearLayout) v.findViewById(R.id.host_button_layout);
		
		gameNameTextView = (TextView) v.findViewById(R.id.game_name_textview);
		playersNeededTextView = (TextView) v.findViewById(R.id.players_needed_textview);
		
		return v;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.start_game_button:
				if(userIsHost)
					listener.startGame(gameId);
				break;
			case R.id.cancel_game_button:
				if(userIsHost)
					listener.cancelGame(gameId);
				break;
			case R.id.join_game_button:
				if(!userIsHost)
					listener.joinGame(gameId);
				break;
			case R.id.leave_game_button:
				if(!userIsHost)
					listener.leaveGame(gameId);
			default:
				break;
		}
	}

	public void setListener(Listener listener)
	{
		this.listener = listener;
	}

	public void setPendingGameInfo(JSONObject pendingGameInfoJSON)
	{
		try
		{
			gameId = pendingGameInfoJSON.getString("gameId");
			gameName = pendingGameInfoJSON.getString("gameName");
			userIsHost = pendingGameInfoJSON.getBoolean("userIsHost");
			maxPlayers = pendingGameInfoJSON.getInt("maxPlayers");
			JSONArray playerJSONArray = pendingGameInfoJSON.getJSONArray("players");
			
			playerNames = new String[playerJSONArray.length()];
			
			for(int i=0;i<playerNames.length;i++)
			{
				playerNames[i] = playerJSONArray.getString(i);
			}
			
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateUi()
	{
		if(getActivity() == null)
			return;
		
		if(listener == null)
			return;
		
		listener.setTitle("Waiting Room");
		
		gameNameTextView.setText(gameName);
		
		int playersNeeded = maxPlayers - playerNames.length;
		
		String playersNeededText = "";
		
		switch(playersNeeded)
		{
			case 0:
				playersNeededText = GAME_FULL;
				break;
			case 1:
				playersNeededText = "Waiting for 1 more player";
				break;
			default:
				playersNeededText = String.format("Waiting for %d more players", playersNeeded);
				break;
		}
		
		playersNeededTextView.setText(playersNeededText);
		
		nonHostLinearLayout.setVisibility(userIsHost ? View.GONE : View.VISIBLE);
		hostLinearLayout.setVisibility(userIsHost ? View.VISIBLE : View.GONE);
		
		playerNameLinearLayout.removeAllViews();
		
		for(String playerName : playerNames)
		{
			TextView playerNameTextView = new TextView(getActivity());
			
			playerNameTextView.setText(playerName);
			
			playerNameLinearLayout.addView(playerNameTextView);
		}
	}
}
