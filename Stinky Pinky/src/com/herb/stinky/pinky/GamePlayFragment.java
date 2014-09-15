package com.herb.stinky.pinky;

/**
 * UI fragment displaying the Stinky Pinky clues and with two text boxes for the player to put in their guess. 
 */

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest.Builder;
import com.herb.stinky.pinky.entities.Riddle;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GamePlayFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public void onCheckButtonClicked(String answer1, String answer2);
		public void onLengthHintButtonClicked();
		public void onFirstHintButtonClicked();
		public void onLastHintButtonClicked();
		public void onNextRiddleButtonClicked();
	}
	
	private final int[] CLICKABLES = {R.id.hint1_button, R.id.hint2_button, R.id.hint3_button, R.id.check_button, R.id.next_button};
	
	private final int[] STARS = {R.id.star_1_img, R.id.star_2_img, R.id.star_3_img, R.id.star_4_img, R.id.star_5_img};
	
	private Listener listener;
	private Riddle riddle;
	
	private boolean clearKeyboard = false;
	private int stars=0, riddleIndex=0;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_play_screen, container, false);
        
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
			case R.id.check_button:
				String answer1 = ((EditText)getActivity().findViewById(R.id.answer_1_editText)).getText().toString();
				String answer2 = ((EditText)getActivity().findViewById(R.id.answer_2_editText)).getText().toString();
				listener.onCheckButtonClicked(answer1, answer2);
				updateUi();
				break;
			case R.id.hint1_button:
				listener.onLengthHintButtonClicked();
				updateUi();
				break;
			case R.id.hint2_button:
				listener.onFirstHintButtonClicked();
				updateUi();
				break;
			case R.id.hint3_button:
				listener.onLastHintButtonClicked();
				updateUi();
				break;
			case R.id.next_button:
				listener.onNextRiddleButtonClicked();
				updateUi();
				break;
			default:
				break;
		}
	}

	public void setRiddle(Riddle riddle)
	{
		this.riddle = riddle;
	}

	public void updateUi()
	{
		if(getActivity() == null)
			return;
		
		if(listener == null)
			return;
		
		if(riddle == null)
			return;
		
		TextView clue1TextView = (TextView) getActivity().findViewById(R.id.clue_1_textView);
		TextView clue2TextView = (TextView) getActivity().findViewById(R.id.clue_2_textView);
		TextView answer1TextView = (TextView) getActivity().findViewById(R.id.answer_1_textView);
		TextView answer2TextView = (TextView) getActivity().findViewById(R.id.answer_2_textView);
        
		TextView hint1TextView = (TextView) getActivity().findViewById(R.id.hint_1_textView);
		TextView hint2TextView = (TextView) getActivity().findViewById(R.id.hint_2_textView);
        
		TextView riddleNumberTextView = (TextView) getActivity().findViewById(R.id.riddle_number_textView);
		TextView starTextView = (TextView) getActivity().findViewById(R.id.star_textView);
        
        EditText answer1EditText = (EditText) getActivity().findViewById(R.id.answer_1_editText);
        EditText answer2EditText = (EditText) getActivity().findViewById(R.id.answer_2_editText);
        
        LinearLayout textViewBox = (LinearLayout) getActivity().findViewById(R.id.textViewBox);
        LinearLayout editTextBox = (LinearLayout) getActivity().findViewById(R.id.editTextBox);
        
        Button checkButton = (Button) getActivity().findViewById(R.id.check_button);
        
        TextView hintTextView = (TextView) getActivity().findViewById(R.id.hint_textview);
        Button wordLengthHintButton = (Button) getActivity().findViewById(R.id.hint1_button);
        Button firstLetterHintButton = (Button) getActivity().findViewById(R.id.hint2_button);
        Button lastLetterHintButton = (Button) getActivity().findViewById(R.id.hint3_button);
        
        Button nextRiddleButton = (Button) getActivity().findViewById(R.id.next_button);
        
        ImageButton[] starButtons = new ImageButton[STARS.length];
        
        for(int i=0;i<STARS.length;i++)
        {
        	starButtons[i] = (ImageButton) getActivity().findViewById(STARS[i]);
        }
        
		starTextView.setText(Integer.toString(stars));
		riddleNumberTextView.setText(String.format("%d/10", riddleIndex+1));
		clue1TextView.setText(riddle.getClue1());
		clue2TextView.setText(riddle.getClue2());
		
		if(riddle.isSolved())
		{
			editTextBox.setVisibility(View.GONE);
			textViewBox.setVisibility(View.VISIBLE);
			
			checkButton.setClickable(false);
			checkButton.setText(getResources().getString(R.string.correct));
			checkButton.setTextColor(Color.WHITE);
			checkButton.setBackground(getResources().getDrawable(R.color.blue));
			
			hintTextView.setVisibility(View.GONE);
			wordLengthHintButton.setVisibility(View.GONE);
			firstLetterHintButton.setVisibility(View.GONE);
			lastLetterHintButton.setVisibility(View.GONE);
			hint1TextView.setVisibility(View.GONE);
			hint2TextView.setVisibility(View.GONE);
			
			answer1TextView.setText(riddle.getAnswer1());
			answer2TextView.setText(riddle.getAnswer2());
			answer1EditText.setText("");
			answer2EditText.setText("");
			
			nextRiddleButton.setVisibility(View.VISIBLE);
			
			for(int i=0;i<STARS.length;i++)
	        {
				if(i<riddle.getStarValue())
				{
					starButtons[i].setEnabled(true);
					starButtons[i].setSelected(true);
				}
				else
				{
					starButtons[i].setEnabled(false);
				}
	        }
		}
		else
		{
			editTextBox.setVisibility(View.VISIBLE);
			textViewBox.setVisibility(View.GONE);
			
			checkButton.setClickable(true);
			checkButton.setText(getResources().getString(R.string.check));
			checkButton.setTextColor(Color.BLACK);
			checkButton.setBackground(getResources().getDrawable(R.color.lightGray));
			
			hintTextView.setVisibility(View.VISIBLE);
			wordLengthHintButton.setVisibility(View.VISIBLE);
			firstLetterHintButton.setVisibility(View.VISIBLE);
			lastLetterHintButton.setVisibility(View.VISIBLE);
			
			wordLengthHintButton.setEnabled(!riddle.isHintUsed(Riddle.WORD_LENGTH_HINT));
			firstLetterHintButton.setEnabled(!riddle.isHintUsed(Riddle.FIRST_LETTER_HINT));
			lastLetterHintButton.setEnabled(!riddle.isHintUsed(Riddle.LAST_LETTER_HINT));
			
			answer1TextView.setText("");
			answer2TextView.setText("");
			answer1EditText.setText("");
			answer2EditText.setText("");
			
			nextRiddleButton.setVisibility(View.GONE);
			
			if(riddle.isHintUsed(Riddle.WORD_LENGTH_HINT) || riddle.isHintUsed(Riddle.FIRST_LETTER_HINT) || riddle.isHintUsed(Riddle.LAST_LETTER_HINT))
			{
				hint1TextView.setVisibility(View.VISIBLE);
				hint2TextView.setVisibility(View.VISIBLE);
			}
			else
			{
				hint1TextView.setVisibility(View.GONE);
				hint2TextView.setVisibility(View.GONE);
			}
			
			for(int i=0;i<STARS.length;i++)
	        {
				if(i<riddle.getStarValue())
				{
					starButtons[i].setEnabled(true);
					starButtons[i].setSelected(false);
				}
				else
				{
					starButtons[i].setEnabled(false);
				}
	        }
		}
		
		if(clearKeyboard)
		{
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(answer1EditText.getWindowToken(), 0);
			clearKeyboard=false;
		}
	}
	
	public void useHint()
	{
		((TextView) getActivity().findViewById(R.id.hint_1_textView)).setText(riddle.getHint1());
		((TextView) getActivity().findViewById(R.id.hint_2_textView)).setText(riddle.getHint2());
	}

	public void setClearKeyboard(boolean clearKeyboard)
	{
		this.clearKeyboard = clearKeyboard;
	}
	
	public void setStars(int stars)
	{
		this.stars = stars;
	}
	
	public void setRiddleIndex(int index)
	{
		this.riddleIndex = index;
	}
}
