/**
 * UI fragment showing the main menu.
 */

package com.herb.stinky.pinky;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest.Builder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainMenuFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public void onPlayButtonClicked();
		public void onInfoButtonClicked();
		public void onAchievementsButtonClicked();
		public void onLeaderboardButtonClicked();
		public void onSignInButtonClicked();
		public void onSignOutButtonClicked();
		public void onQuitButtonClicked();
	}
	
	private Listener listener;
	private boolean showSignIn = true;
		
	private static final int[] CLICKABLES = {R.id.play_button, R.id.info_button, R.id.achievements_button, R.id.leaderboards_button, R.id.quit_button, R.id.sign_in_button, R.id.sign_out_button};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu_screen, container, false);
        
        for(int i : CLICKABLES)
        {
        	v.findViewById(i).setOnClickListener(this);
        }
                
        AdView adView = (AdView) v.findViewById(R.id.adView);
        Builder adBuilder = new AdRequest.Builder();
        //adBuilder.addTestDevice("472FF7827E743365E9E208B1C229D2B7");
        AdRequest adRequest = adBuilder.build();
        adView.loadAd(adRequest);
        
        return v;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		updateUi();
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
			case R.id.play_button:
				listener.onPlayButtonClicked();
				break;
			case R.id.info_button:
				listener.onInfoButtonClicked();
				break;
			case R.id.achievements_button:
				listener.onAchievementsButtonClicked();
				break;
			case R.id.leaderboards_button:
				listener.onLeaderboardButtonClicked();
				break;
			case R.id.quit_button:
				listener.onQuitButtonClicked();
				break;
			case R.id.sign_in_button:
				listener.onSignInButtonClicked();
				break;
			case R.id.sign_out_button:
				listener.onSignOutButtonClicked();
				break;
			default:
				break;
		}
	}
	
	public void updateUi()
	{
		if(getActivity() == null)
			return;
		
		((LinearLayout) getActivity().findViewById(R.id.sign_in_bar)).setVisibility(showSignIn ? View.VISIBLE : View.GONE);
		((LinearLayout) getActivity().findViewById(R.id.sign_out_bar)).setVisibility(showSignIn ? View.GONE : View.VISIBLE);
	}

	public void setShowSignIn(boolean showSignIn)
	{
		this.showSignIn = showSignIn;
		updateUi();
	}
}
