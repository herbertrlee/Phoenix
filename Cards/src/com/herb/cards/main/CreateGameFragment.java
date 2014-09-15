package com.herb.cards.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateGameFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public abstract void createGame(String gameName, int[] cardSets, int maxPlayers);
		public abstract void setTitle(String title);
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {R.id.create_game_button};
	
	private int[] cardSetIds = {};
	private String[] cardSetNames = {};
	private TextView[] cardSetTextViews = {};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_create_game_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
			
		return v;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.create_game_button:
				String gameName = ((EditText) getActivity().findViewById(R.id.game_name_editText)).getText().toString();
				int maxPlayers = Integer.parseInt(((Spinner) getActivity().findViewById(R.id.spinner_num_players)).getSelectedItem().toString());
				
				ArrayList<Integer> selectedCardSets = new ArrayList<Integer>();
				for(int i=0;i<cardSetIds.length;i++)
				{
					if(cardSetTextViews[i].isSelected())
					{
						selectedCardSets.add(cardSetIds[i]);
					}
				}
				
				int[] cardSets = new int[selectedCardSets.size()];
				for(int i=0;i<selectedCardSets.size();i++)
					cardSets[i] = selectedCardSets.get(i);
				
				listener.createGame(gameName, cardSets, maxPlayers);
			default:
				break;
		}
	}

	public void setListener(Listener listener)
	{
		this.listener = listener;
	}
	
	public void setCardSets(JSONArray cardSets)
	{
		cardSetIds = new int[cardSets.length()];
		cardSetNames = new String[cardSets.length()];
		
		for(int i=0;i<cardSets.length();i++)
		{
			try
			{
				JSONObject cardSet = cardSets.getJSONObject(i);
			
				cardSetIds[i] = cardSet.getInt("idNum");
				cardSetNames[i] = cardSet.getString("name");
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
		
		listener.setTitle("Create Game");
		
		LinearLayout cardSetLayout = (LinearLayout) getActivity().findViewById(R.id.card_set_layout);
		cardSetLayout.removeAllViews();

		cardSetTextViews = new TextView[cardSetIds.length];
		
		for(int i=0;i<cardSetIds.length;i++)
		{
			TextView cardSetTextView = new TextView(getActivity());
			
			cardSetTextView.setText(cardSetNames[i]);
			cardSetTextView.setClickable(true);
			cardSetTextView.setSelected(false);
			
			cardSetTextView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					TextView tv = (TextView) v;
					tv.setSelected(!tv.isSelected());
					
					if(tv.isSelected())
					{
						tv.setBackgroundColor(Color.BLACK);
						tv.setTextColor(Color.WHITE);
					}
					else
					{
						tv.setBackgroundResource(R.color.background_holo_light);
						tv.setTextColor(Color.BLACK);
					}
				}
			});
			
			cardSetTextViews[i] = cardSetTextView;
			
			cardSetLayout.addView(cardSetTextView);
		}
	}
}
