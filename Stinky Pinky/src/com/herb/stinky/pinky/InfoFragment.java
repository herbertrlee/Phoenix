package com.herb.stinky.pinky;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest.Builder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class InfoFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
	}
	
	private Listener listener;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_screen, container, false);
        
        AdView adView = (AdView) v.findViewById(R.id.adView);
        Builder adBuilder = new AdRequest.Builder();
        //adBuilder.addTestDevice("472FF7827E743365E9E208B1C229D2B7");
        AdRequest adRequest = adBuilder.build();
        adView.loadAd(adRequest);
        
        return v;
	}
	
	public void setListener(Listener listener)
	{
		this.listener = listener;
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

}
