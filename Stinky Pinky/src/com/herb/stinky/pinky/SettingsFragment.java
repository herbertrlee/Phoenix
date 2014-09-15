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
import android.widget.Switch;

public class SettingsFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public void onNotificationToggle();
		public void onNotificationSoundToggle();
		public void onNotificationVibrateToggle();
	}
	
	Listener listener;
	
	final static int[] CLICKABLES = {R.id.notifications_switch, R.id.notifications_sound_switch, R.id.notifications_vibrate_switch};
	
	private boolean notificationsOn = true, notificationsSoundOn = true, notificationsVibrateOn = true;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_screen, container, false);
        
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
	
	public void setListener(Listener l)
	{
		listener = l;
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.notifications_switch:
				notificationsOn = ((Switch) v).isChecked();
				listener.onNotificationToggle();
				break;
			case R.id.notifications_sound_switch:
				notificationsSoundOn = ((Switch) v).isChecked();
				listener.onNotificationSoundToggle();
				break;
			case R.id.notifications_vibrate_switch:
				notificationsVibrateOn = ((Switch) v).isChecked();
				listener.onNotificationVibrateToggle();
				break;
			default:
				break;
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		updateUi();
	}
	
	public void updateUi()
	{
		if(getActivity() == null)
			return;
		
		if(listener == null)
			return;
		
		((Switch) getActivity().findViewById(R.id.notifications_switch)).setChecked(notificationsOn);
		((Switch) getActivity().findViewById(R.id.notifications_sound_switch)).setChecked(notificationsSoundOn);
		((Switch) getActivity().findViewById(R.id.notifications_vibrate_switch)).setChecked(notificationsVibrateOn);
	}
	
	public void setNotificationsOn(boolean notificationsOn)
	{
		this.notificationsOn = notificationsOn;
	}
	
	public void setNotificationsSoundOn(boolean notificationsSoundOn)
	{
		this.notificationsSoundOn = notificationsSoundOn;
	}
	
	public void setNotificationsVibrateOn(boolean notificationsVibrateOn)
	{
		this.notificationsVibrateOn = notificationsVibrateOn;
	}
}
