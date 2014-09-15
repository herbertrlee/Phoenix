package com.herb.cards.main;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class GameHistoryFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public abstract void pageForward();
		public abstract void pageBackward();
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {R.id.forward_button, R.id.back_button};
	
	private String gameName = "", gameId = "", czarName = "", winnerName = "";
	private int roundNumber = 0, pick = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_game_history_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
			
		return v;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.forward_button:
				listener.pageForward();
				break;
			case R.id.back_button:
				listener.pageBackward();
				break;
			default:
				break;
		}
	}

	public void setListener(Listener listener)
	{
		this.listener = listener;
	}
	
	public void setRoundInfo(JSONObject roundInfoJSON)
	{
		
	}
	
	public void updateUi()
	{
		
	}
}
