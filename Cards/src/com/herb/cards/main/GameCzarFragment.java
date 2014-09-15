package com.herb.cards.main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameCzarFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public abstract void setTitle(String title);
		public abstract void pickWinner(String gameId, String submissionId);
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {R.id.pick_winner_button};
	private static final String ALL_SUBS_IN = "Everyone has submitted!  You can pick now.";
	private static final String STILL_WAITING = "Still waiting for submissions";
	
	private String gameName = "";
	private String gameId = "";
	private boolean allSubsIn = false;
	private int currentRound = 0;
	private int pick = 0;
	private String blackCardText = "";
	private int[][] whiteCardIds = {};
	private String[][] whiteCardTexts = {};
	private String[] submissionIds = {};
	
	private TextView gameNameTextView, allSubsInTextView, blackCardTextView;
	private LinearLayout[] submissionLayouts = {};
	private FrameLayout submissionLayout;
	private int topSubmission = -1, bottomSubmission = -1;
	
	GestureDetector gesture;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_game_czar_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
			
		gameNameTextView = (TextView) v.findViewById(R.id.user_is_picking_text_view);
		allSubsInTextView = (TextView) v.findViewById(R.id.all_subs_in_text_view);
		blackCardTextView = (TextView) v.findViewById(R.id.black_card_czar_text_view);
		
		submissionLayout = (FrameLayout) v.findViewById(R.id.submission_container);
		
		gesture = new GestureDetector(getActivity(), new CzarGestureListener());
		return v;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.pick_winner_button:
				if(topSubmission != -1)
					listener.pickWinner(gameId, submissionIds[topSubmission]);
				break;
			default:
				break;
		}
	}

	public void setListener(Listener listener)
	{
		this.listener = listener;
	}
	
	public void setGameInfo(JSONObject gameInfoJSON) throws JSONException
	{
		gameName = gameInfoJSON.getString("gameName");
		gameId = gameInfoJSON.getString("gameId");
		currentRound = gameInfoJSON.getInt("currentRound");
		allSubsIn = (gameInfoJSON.getInt("subsNeeded") == 0);
	}
	
	public void setSubmissions(JSONArray submissionsJSON) throws JSONException
	{
		int submissionsCount = submissionsJSON.length();
		
		submissionIds = new String[submissionsCount];
		whiteCardIds = new int[submissionsCount][pick];
		whiteCardTexts = new String[submissionsCount][pick];
		
		for(int i=0;i<submissionsCount;i++)
		{
			JSONObject submission = submissionsJSON.getJSONObject(i);
			submissionIds[i] = submission.getString("id");
			JSONArray whiteIds = submission.getJSONArray("whiteCardIds");
			JSONArray whiteTexts = submission.getJSONArray("whiteCardTexts");
			
			for(int j=0;j<pick;j++)
			{
				whiteCardIds[i][j] = whiteIds.getInt(j);
				whiteCardTexts[i][j] = whiteTexts.getString(j);
			}
		}
		
		bottomSubmission = 0;
		topSubmission = submissionsCount - 1;
	}
	
	public void updateUi()
	{
		if(getActivity() == null)
			return;
		
		if(listener == null)
			return;
		
		listener.setTitle(gameName);
		
		gameNameTextView.setText(String.format("Round %d - YOU are picking", currentRound));
		allSubsInTextView.setText(allSubsIn ? ALL_SUBS_IN : STILL_WAITING);
		
		blackCardTextView.setText(blackCardText);
		
		submissionLayout.removeAllViews();
		submissionLayouts = new LinearLayout[submissionIds.length];
		
		int pixelWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 150, getResources().getDisplayMetrics());
		int pixelHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 200, getResources().getDisplayMetrics());
		int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
		
		if(pick == 3)
		{
			pixelWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 100, getResources().getDisplayMetrics());
		}
		
		for(int i=0;i<submissionIds.length;i++)
		{
			LinearLayout submissionPage = new LinearLayout(getActivity());
			submissionPage.setOrientation(LinearLayout.HORIZONTAL);
			submissionPage.setGravity(Gravity.CENTER);
			FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, pixelHeight);
			flp.setMargins(0, dp10, 0, 0);
			submissionPage.setLayoutParams(flp);
			
			for(int j=0;j<pick;j++)
			{
				TextView whiteCardTextView = new TextView(getActivity());
				whiteCardTextView.setText(whiteCardTexts[i][j]);
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(pixelWidth, pixelHeight);
				if(j!=pick)
					llp.setMargins(0, 0, dp10, 0);
				whiteCardTextView.setLayoutParams(llp);
				whiteCardTextView.setBackground(getResources().getDrawable(R.drawable.white_card));
				whiteCardTextView.setTextAppearance(getActivity(), R.style.WhiteCard);
				
				whiteCardTextView.setOnTouchListener(new View.OnTouchListener()
				{
					
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						return gesture.onTouchEvent(event);
					}
				});
				
				submissionPage.addView(whiteCardTextView);
			}
			
			submissionLayouts[i] = submissionPage;
			submissionLayout.addView(submissionPage);
		}
		
		Button pickButton = (Button) getActivity().findViewById(R.id.pick_winner_button);
		pickButton.setEnabled(allSubsIn);
	}

	public void setBlackCard(JSONObject blackCardJSON)
	{
		try
		{
			pick = blackCardJSON.getInt("pick");
			blackCardText = blackCardJSON.getString("text");
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class CzarGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onDown(MotionEvent e)
		{
			return true;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			if(e1.getX() > e2.getX())
				scrollSubmissionsForward();
			else
				scrollSubmissionsBackward();
			
			return true;
		}
	}

	public void scrollSubmissionsBackward()
	{
		LinearLayout topLayout = submissionLayouts[topSubmission];
		
		float pixelWidth = topLayout.getWidth();
		
		topSubmission++;
		bottomSubmission++;
		
		if(topSubmission == submissionLayouts.length)
			topSubmission = 0;
		if(bottomSubmission == submissionLayouts.length)
			bottomSubmission = 0;

		submissionLayouts[topSubmission].bringToFront();
		
		TranslateAnimation anim = new TranslateAnimation(pixelWidth, 0f, 0f, 0f);
		anim.setDuration(500);
		topLayout.startAnimation(anim);
	}

	public void scrollSubmissionsForward()
	{
		LinearLayout topLayout = submissionLayouts[topSubmission];

		float pixelWidth = topLayout.getWidth();
		
		topSubmission--;
		bottomSubmission--;
		
		if(topSubmission == -1)
			topSubmission = submissionLayouts.length - 1;
		if(bottomSubmission == -1)
			bottomSubmission = submissionLayouts.length - 1;
		
		submissionLayouts[topSubmission].bringToFront();
		
		TranslateAnimation anim = new TranslateAnimation(-pixelWidth, 0f, 0f, 0f);
		anim.setDuration(500);
		topLayout.startAnimation(anim);
	}
}
