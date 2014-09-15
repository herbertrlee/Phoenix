package com.herb.cards.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class HelpFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_main_menu_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
			
		return v;
	}

	@Override
	public void onClick(View v)
	{
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
}
