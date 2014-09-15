package com.herb.cards.main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameNotCzarFragment extends Fragment implements OnClickListener
{
	public interface Listener
	{
		public abstract void setTitle(String title);
		public abstract void submitWhite(String gameId, int[] ids);
	}
	
	private Listener listener;
	
	private static int[] CLICKABLES = {R.id.submit_white_button, R.id.reset_selections_button};
	
	private static final String SUBMITTED = "You have already submitted.";
	private static final String ALREADY_SUBMITTED = "You can't submit the same card twice.";
	private static final int MARGIN = 10;
	
	private String czarName = "", gameName = "", gameId = "";
	private boolean userSubmitted = false;
	private int pick = 0;
	private int currentRound = 0;
	private int[] whiteCardIds = {};
	private String[] whiteCardTexts = {};
	private String blackCardText = "";
	
	private int topCard = -1, bottomCard = -1;
	private FrameLayout whiteCardLayout;
	private LinearLayout dropAreaLayout;
	private TextView czarNameTextView, blackCardTextView;
	private TextView[] whiteCardTextViews = {};
	private TextView[] submissionTextViews = {};
	private int[] submissionIds = {};
	private String[] submissionTexts = {};
	
	GestureDetector gesture;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_game_not_czar_screen, container, false);
		
		for(int i : CLICKABLES)
			v.findViewById(i).setOnClickListener(this);
		
		whiteCardLayout = (FrameLayout) v.findViewById(R.id.white_card_container);
		czarNameTextView = (TextView) v.findViewById(R.id.czar_name_text_view);
		blackCardTextView = (TextView) v.findViewById(R.id.black_card_not_czar_text_view);
		dropAreaLayout = (LinearLayout) v.findViewById(R.id.drop_area_layout);
		
		gesture = new GestureDetector(getActivity(), new NotCzarGestureListener());
				
		return v;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.submit_white_button:
				if(!userSubmitted)
				{
					boolean valid = (submissionIds.length == pick && pick > 0);
					for(int i=0;i<submissionIds.length;i++)
					{
						if(submissionIds[i] < 0)
							valid = false;
					}
					
					if(valid)
						listener.submitWhite(gameId, submissionIds);
					else
						Toast.makeText(getActivity(), String.format("Need to pick %d cards", pick), Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getActivity(), "You already submitted", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.reset_selections_button:
				for(int i=0;i<submissionTextViews.length;i++)
				{
					if(submissionIds[i] >= 0)
					{
						String text = submissionTextViews[i].getText().toString();
						int id = submissionIds[i];
						addWhiteCardToTop(id, text);
						
						submissionIds[i] = -1;
						submissionTextViews[i].setText(Integer.toString(i+1));
					}
				}
				break;
			default:
				break;
		}
	}

	public void scrollCardsForward()
	{
		float marginDelta = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MARGIN, getResources().getDisplayMetrics());
		
		for(int i=0;i<whiteCardTextViews.length;i++)
		{
			if(i==topCard)
			{
				ObjectAnimator animation = ObjectAnimator.ofFloat(whiteCardTextViews[i], "translationX", whiteCardTextViews[i].getTranslationX(), 0f );
				animation.start();
			}
			else
			{
				ObjectAnimator animation = ObjectAnimator.ofFloat(whiteCardTextViews[i], "translationX", whiteCardTextViews[i].getTranslationX(), whiteCardTextViews[i].getTranslationX() + marginDelta);
				animation.start();
			}
		}
		topCard--;
		bottomCard--;
		
		if(topCard == -1)
			topCard = whiteCardTextViews.length - 1;
		
		if(bottomCard == -1)
			bottomCard = whiteCardTextViews.length - 1;
		
		for(int i=0;i<whiteCardTextViews.length;i++)
		{
			int index = bottomCard + i;
			if(index >= whiteCardTextViews.length)
			{
				index -= whiteCardTextViews.length;
			}
			
			whiteCardTextViews[index].bringToFront();
		}
	}
	
	public void scrollCardsBackward()
	{
		float marginDelta = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MARGIN, getResources().getDisplayMetrics());
		
		for(int i=0;i<whiteCardTextViews.length;i++)
		{
			if(i==bottomCard)
			{
				ObjectAnimator animation = ObjectAnimator.ofFloat(whiteCardTextViews[i], "translationX", 0f, (float) (whiteCardTextViews.length-1) * marginDelta);
				animation.start();
			}
			else
			{
				ObjectAnimator animation = ObjectAnimator.ofFloat(whiteCardTextViews[i], "translationX", whiteCardTextViews[i].getTranslationX(), whiteCardTextViews[i].getTranslationX() - marginDelta);
				animation.start();
			}
		}
		
		topCard++;
		bottomCard++;
		
		if(topCard == whiteCardTextViews.length)
			topCard = 0;
		
		if(bottomCard == whiteCardTextViews.length)
			bottomCard = 0;
		
		whiteCardTextViews[topCard].bringToFront();
	}

	public void setListener(Listener listener)
	{
		this.listener = listener;
	}
	
	public void setGameInfo(JSONObject gameInfoJSON)
	{
		try
		{
			czarName = gameInfoJSON.getString("czarName");
			gameName = gameInfoJSON.getString("gameName");
			userSubmitted = gameInfoJSON.getBoolean("userSubmitted");
			gameId = gameInfoJSON.getString("gameId");
			currentRound = gameInfoJSON.getInt("currentRound");
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
		
		listener.setTitle(gameName);
		czarNameTextView.setText(String.format("Round %d - %s is picking", currentRound, czarName));
		
		blackCardTextView.setText(blackCardText);
		dropAreaLayout.removeAllViews();
		
		if(userSubmitted)
		{
			int weight = 6/pick;
			submissionTextViews = new TextView[pick];
			
			for(int i=0;i<pick;i++)
			{
				TextView submissionTextView = new TextView(getActivity());
				submissionTextView.setText(submissionTexts[i]);
				submissionTextView.setBackground(getResources().getDrawable(R.drawable.white_card));
				submissionTextView.setTextAppearance(getActivity(), R.style.WhiteCard);
				submissionTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, weight));
				submissionTextView.setSelected(false);
				submissionTextView.setId(i);
				
				dropAreaLayout.addView(submissionTextView);
				
				submissionTextViews[i] = submissionTextView;
			}
			
			
		}
		else
		{
			int weight = 6/pick;
			submissionTextViews = new TextView[pick];
			submissionIds = new int[pick];
			
			for(int i=0;i<pick;i++)
			{
				TextView dropAreaTextView = new TextView(getActivity());
				dropAreaTextView.setText(Integer.toString(i + 1));
				dropAreaTextView.setBackground(getResources().getDrawable(R.drawable.drop_area));
				dropAreaTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				dropAreaTextView.setTextAppearance(getActivity(), (pick==1 ? R.style.LargeDropArea : R.style.SmallDropArea));
				dropAreaTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, weight));
				dropAreaTextView.setSelected(false);
				dropAreaTextView.setId(i);
				
				dropAreaTextView.setOnDragListener(new DragAreaListener());
				dropAreaLayout.addView(dropAreaTextView);
				
				submissionTextViews[i] = dropAreaTextView;
				submissionIds[i] = -1;
			}
		}
		
		whiteCardLayout.removeAllViews();
		whiteCardTextViews = new TextView[whiteCardTexts.length];
		
		for(int i=0;i<whiteCardTextViews.length;i++)
		{
			addWhiteCardToStack(i);
		}
		
		topCard = whiteCardTextViews.length - 1;
		bottomCard = 0;
		
		Button submitButton = (Button) getActivity().findViewById(R.id.submit_white_button);
		submitButton.setEnabled(!userSubmitted);
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

	public void setWhiteCard(JSONArray whiteCardsJSON)
	{
		int length = whiteCardsJSON.length();
		whiteCardIds = new int[length];
		whiteCardTexts = new String[length];
		
		for(int i=0;i<length;i++)
		{
			try
			{
				JSONObject whiteCard = whiteCardsJSON.getJSONObject(i);
				whiteCardIds[i] = whiteCard.getInt("id");
				whiteCardTexts[i] = whiteCard.getString("text");
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setSubmissions(JSONArray submissionJSON)
	{
		int length = submissionJSON.length();
		submissionIds = new int[length];
		submissionTexts = new String[length];
		
		for(int i=0;i<length;i++)
		{
			try
			{
				JSONObject whiteCard = submissionJSON.getJSONObject(i);
				submissionIds[i] = whiteCard.getInt("id");
				submissionTexts[i] = whiteCard.getString("text");
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void addWhiteCardToStack(int i)
	{
		int pixelWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 150, getResources().getDisplayMetrics());
		int pixelHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 200, getResources().getDisplayMetrics());
		float marginDelta = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MARGIN, getResources().getDisplayMetrics());
		
		TextView whiteCardTextView = new TextView(getActivity());
		
		whiteCardTextView.setText(whiteCardTexts[i]);
		whiteCardTextView.setId(whiteCardIds[i]);
		whiteCardTextView.setTextAppearance(getActivity(), R.style.WhiteCard);
		whiteCardTextView.setBackground(getResources().getDrawable(R.drawable.white_card));
		whiteCardTextView.setLayoutParams(new FrameLayout.LayoutParams(pixelWidth, pixelHeight));
		whiteCardTextView.setTranslationX(((float) i)*marginDelta );
		
		whiteCardTextView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return gesture.onTouchEvent(event);
			}
		});
		
		whiteCardTextViews[i] = whiteCardTextView;
		whiteCardLayout.addView(whiteCardTextView);
	}
	
	private void removeTopCard()
	{
		int newLength = whiteCardIds.length - 1;
		
		int[] newWhiteCardIds = new int[newLength];
		String[] newWhiteCardTexts = new String[newLength];
		boolean pastTopCard = false;
		
		for(int i=0;i<whiteCardIds.length;i++)
		{
			if(i != topCard && !pastTopCard)
			{
				newWhiteCardIds[i] = whiteCardIds[i];
				newWhiteCardTexts[i] = whiteCardTexts[i];
			}
			else if(i != topCard && pastTopCard)
			{
				newWhiteCardIds[i-1] = whiteCardIds[i];
				newWhiteCardTexts[i-1] = whiteCardTexts[i];
			}
			else
			{
				pastTopCard = true;
			}
		}
		
		whiteCardLayout.removeAllViews();
		whiteCardTextViews = new TextView[newLength];
		whiteCardIds = newWhiteCardIds;
		whiteCardTexts = newWhiteCardTexts;
		
		for(int i=0;i<whiteCardTextViews.length;i++)
		{
			addWhiteCardToStack(i);
		}
		
		topCard = whiteCardTextViews.length - 1;
		bottomCard = 0;
	}
	
	private void addWhiteCardToTop(int id, String text)
	{
		int newLength = whiteCardIds.length + 1;
		
		int[] newWhiteCardIds = new int[newLength];
		String[] newWhiteCardTexts = new String[newLength];
		
		for(int i=0;i<whiteCardIds.length;i++)
		{
			newWhiteCardIds[i] = whiteCardIds[i];
			newWhiteCardTexts[i] = whiteCardTexts[i];
		}
		
		newWhiteCardIds[whiteCardIds.length] = id;
		newWhiteCardTexts[whiteCardTexts.length] = text;
		
		whiteCardLayout.removeAllViews();
		whiteCardTextViews = new TextView[newLength];
		whiteCardIds = newWhiteCardIds;
		whiteCardTexts = newWhiteCardTexts;
		
		for(int i=0;i<whiteCardTextViews.length;i++)
		{
			addWhiteCardToStack(i);
		}
		
		topCard = whiteCardTextViews.length - 1;
		bottomCard = 0;
	}
	
	class NotCzarGestureListener extends GestureDetector.SimpleOnGestureListener
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
				scrollCardsForward();
			else
				scrollCardsBackward();
			
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e)
		{
			ClipData data = ClipData.newPlainText("", "");
			
			View v = whiteCardTextViews[topCard];
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
			v.startDrag(data, shadowBuilder, v, 0);
		}
	}
		
	class DragAreaListener implements OnDragListener
	{
		@Override
		public boolean onDrag(View v, DragEvent event)
		{
			int action = event.getAction();
			
			switch(action)
			{
				case DragEvent.ACTION_DRAG_STARTED:
					break;
				case DragEvent.ACTION_DRAG_ENTERED:
					break;
				case DragEvent.ACTION_DRAG_EXITED:
					break;
				case DragEvent.ACTION_DROP:
					TextView tv = (TextView) v;
					String text = whiteCardTexts[topCard];
					int id = whiteCardIds[topCard];
					
					boolean inList = false;
					for(int i=0;i<submissionIds.length;i++)
					{
						if(id == submissionIds[i])
							inList = true;
					}
					
					if(inList)
					{
						Toast.makeText(getActivity(), ALREADY_SUBMITTED, Toast.LENGTH_SHORT).show();
					}
					else
					{
						if(submissionIds[tv.getId()] == -1)
						{
							tv.setText(text);
							submissionIds[tv.getId()] = id;
							removeTopCard();
						}
						else
						{
							String oldText = tv.getText().toString();
							int oldId = tv.getId();
							
							tv.setText(text);
							submissionIds[tv.getId()] = id;
							removeTopCard();
							addWhiteCardToTop(oldId, oldText);
						}
					}
					break;
				case DragEvent.ACTION_DRAG_ENDED:
					break;
				default:
					break;
			}
			
			return true;
		}
		
	}
}
