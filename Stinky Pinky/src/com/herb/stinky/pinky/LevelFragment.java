/**
 * UI fragment displaying 10 Stinky Pinkies at a time. 
 */

package com.herb.stinky.pinky;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest.Builder;
import com.herb.stinky.pinky.entities.Riddle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class LevelFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public void onSelectRiddle(int i);
		public void onClickNextLevelButton();
		public void onClickPreviousLevelButton();
	}
		
	private final int[] CLICKABLES = {R.id.previous_level_button, R.id.next_level_button,
		R.id.riddle_box_1, R.id.riddle_box_2, R.id.riddle_box_3,
		R.id.riddle_box_4, R.id.riddle_box_5, R.id.riddle_box_6,
		R.id.riddle_box_7, R.id.riddle_box_8, R.id.riddle_box_9,
		R.id.riddle_box_10};
	
	private final int[] RIDDLE_BOXES = {R.id.riddle_box_1, R.id.riddle_box_2, R.id.riddle_box_3,
		R.id.riddle_box_4, R.id.riddle_box_5, R.id.riddle_box_6,
		R.id.riddle_box_7, R.id.riddle_box_8, R.id.riddle_box_9,
		R.id.riddle_box_10};
	
	private final int[] TEXTVIEWS_1 = 
		{R.id.riddle_textview_1_1, R.id.riddle_textview_2_1, R.id.riddle_textview_3_1,
		R.id.riddle_textview_4_1, R.id.riddle_textview_5_1, R.id.riddle_textview_6_1,
		R.id.riddle_textview_7_1, R.id.riddle_textview_8_1, R.id.riddle_textview_9_1,
		R.id.riddle_textview_10_1};
	
	private final int[] TEXTVIEWS_2 = 
		{R.id.riddle_textview_1_2, R.id.riddle_textview_2_2, R.id.riddle_textview_3_2,
		R.id.riddle_textview_4_2, R.id.riddle_textview_5_2, R.id.riddle_textview_6_2,
		R.id.riddle_textview_7_2, R.id.riddle_textview_8_2, R.id.riddle_textview_9_2,
		R.id.riddle_textview_10_2};
	
	private final String locked = "Locked";

	private Listener listener;
	
	private boolean unlocked = true;
	private int level = 1;
	private int maxLevel = 0;
	private int stars=0;
	
	private Riddle[] riddles = new Riddle[TEXTVIEWS_1.length];
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_level_screen, container, false);
        
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
			case R.id.previous_level_button:
				listener.onClickPreviousLevelButton();
				((ScrollView) getActivity().findViewById(R.id.riddle_scrollview)).fullScroll(ScrollView.FOCUS_UP);
				break;
			case R.id.next_level_button:
				listener.onClickNextLevelButton();
				((ScrollView) getActivity().findViewById(R.id.riddle_scrollview)).fullScroll(ScrollView.FOCUS_UP);
				break;
			case R.id.riddle_box_1:
				listener.onSelectRiddle(0);
				break;
			case R.id.riddle_box_2:
				listener.onSelectRiddle(1);
				break;
			case R.id.riddle_box_3:
				listener.onSelectRiddle(2);
				break;
			case R.id.riddle_box_4:
				listener.onSelectRiddle(3);
				break;
			case R.id.riddle_box_5:
				listener.onSelectRiddle(4);
				break;
			case R.id.riddle_box_6:
				listener.onSelectRiddle(5);
				break;
			case R.id.riddle_box_7:
				listener.onSelectRiddle(6);
				break;
			case R.id.riddle_box_8:
				listener.onSelectRiddle(7);
				break;
			case R.id.riddle_box_9:
				listener.onSelectRiddle(8);
				break;
			case R.id.riddle_box_10:
				listener.onSelectRiddle(9);
				break;
			default:
				break;
		}
	}

	public void updateUi()
	{
		if(getActivity() == null)
			return;
		
		if(listener == null)
			return;
		
		if(riddles == null)
			return;
		
		((Button) getActivity().findViewById(R.id.previous_level_button)).setEnabled(level>1);
		((Button) getActivity().findViewById(R.id.next_level_button)).setEnabled(level<maxLevel);
		
		((TextView) getActivity().findViewById(R.id.star_textView_level)).setText(Integer.toString(stars));
		((TextView) getActivity().findViewById(R.id.level_name_textView)).setText(String.format("Level %d", level));
		
		for(int i=0;i<riddles.length;i++)
		{
			TextView riddleTextView1 = (TextView) getActivity().findViewById(TEXTVIEWS_1[i]);
			TextView riddleTextView2 = (TextView) getActivity().findViewById(TEXTVIEWS_2[i]);
			LinearLayout riddleBox = (LinearLayout) getActivity().findViewById(RIDDLE_BOXES[i]);
			
			if(unlocked)
			{
				riddleTextView1.setText(riddles[i].getClue1());
				riddleTextView2.setText(riddles[i].getClue2());
			}
			else
			{
				riddleTextView1.setText(locked);
				riddleTextView2.setText(locked);
			}
			
			if(riddles[i].isSolved())
			{
				riddleBox.setBackground(getResources().getDrawable(R.color.paleGreen));
			}
			else
			{
				riddleBox.setBackground(getResources().getDrawable(R.color.pink));
			}
		}
	}
	
	public void setUnlocked(boolean unlocked)
	{
		this.unlocked = unlocked;
	}

	public void setRiddles(Riddle[] riddles)
	{
		this.riddles = riddles;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public void setMaxLevel(int maxLevel)
	{
		this.maxLevel = maxLevel;
	}

	public void setStars(int stars)
	{
		this.stars = stars;
	}
	
	
}
