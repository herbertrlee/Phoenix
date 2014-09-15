package com.herb.stinky.pinky.entities;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/*
 * Entity containing information for a Stinky Pinky riddle.
 * Stinky Pinky fields-
 * Id : id of the riddle in the database
 * Clue 1 : First clue of the riddle
 * Clue 2 : Second clue of the riddle
 * Answer 1: First answer of the riddle
 * Answer 2: Second answer of the riddle
 * Star Value : How many stars this riddle is currently worth.  Starts at 5, and goes down one every time an attempt is made or a hint is used.  Minimum value is 1.
 * Hints used : Which hints have been used.
 * Solved : true if this riddle has already been solved.
 * 
 * Hints: There are three types of hints.  Each hint can only be used once per riddle.
 * 
 * Length of word - Displays the number of letters in each answer
 * First letter of each answer - Displays the first letter in each answer
 * Last letter of each answer - Displays the last letter in each answer
 */

public class Riddle
{
	public static final int WORD_LENGTH_HINT = 0;
	public static final int FIRST_LETTER_HINT = 1;
	public static final int LAST_LETTER_HINT = 2;
	public static final int MAX_STAR_VALUE = 5;
	
	public static final String ID_KEY = "ID";
	public static final String CLUE1_KEY = "CLUE1";
	public static final String CLUE2_KEY = "CLUE2";
	public static final String ANSWER1_KEY = "ANSWER1";
	public static final String ANSWER2_KEY = "ANSWER2";
	public static final String STAR_VALUE_KEY = "STAR_VALUE";
	public static final String HINT1_KEY = "HINT1";
	public static final String HINT2_KEY = "HINT2";
	public static final String HINT3_KEY = "HINT3";
	public static final String SOLVED_KEY = "SOLVED";
	
	private final String BLANK_WORD = "________";
	
	private int id = 0;
	private String clue1 = "", clue2 = "", answer1 = "", answer2 = "";
	private int starValue = 5;
	private boolean[] hintsUsed = {false, false, false};
	private boolean solved = false;
	
	public Riddle()
	{
		
	}

	public String getClue1()
	{
		return clue1;
	}

	public void setClue1(String clue1)
	{
		this.clue1 = clue1;
	}

	public String getClue2()
	{
		return clue2;
	}

	public void setClue2(String clue2)
	{
		this.clue2 = clue2;
	}

	public String getAnswer1()
	{
		return answer1;
	}

	public void setAnswer1(String answer1)
	{
		this.answer1 = answer1;
	}

	public String getAnswer2()
	{
		return answer2;
	}

	public void setAnswer2(String answer2)
	{
		this.answer2 = answer2;
	}

	public void decStarValue()
	{
		if(starValue > 1)
			starValue--;
	}
	
	public void setStarValue(int starValue)
	{
		this.starValue = starValue;
	}
	
	public int getStarValue()
	{
		return starValue;
	}
	
	public void setWordLengthHint(boolean hint)
	{
		hintsUsed[WORD_LENGTH_HINT] = hint;
	}
	
	public void setFirstLetterHint(boolean hint)
	{
		hintsUsed[FIRST_LETTER_HINT] = hint;
	}
	
	public void setLastLetterHint(boolean hint)
	{
		hintsUsed[LAST_LETTER_HINT] = hint;
	}
	
	public boolean isHintUsed(int hint)
	{
		return hintsUsed[hint];
	}
	
	public void useHint(int hint)
	{
		if(!hintsUsed[hint])
		{
			hintsUsed[hint] = true;
		}
	}

	public boolean isSolved()
	{
		return solved;
	}

	public void setSolved(boolean solved)
	{
		this.solved = solved;
	}
	
	public boolean validate(String a1, String a2)
	{
		if(!solved)
		{
			if(answer1.equalsIgnoreCase(a1) && answer2.equalsIgnoreCase(a2))
			{
				solved = true;
			}
		}
		
		return solved;
	}
	
	public static Riddle getDemoRiddle()
	{
		Riddle r = new Riddle();
		
		r.setClue1("smelly");
		r.setClue2("finger");
		r.setAnswer1("stinky");
		r.setAnswer2("pinky");

		return r;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getHint1()
	{
		String s = "";
		
		if(hintsUsed[WORD_LENGTH_HINT] && !hintsUsed[FIRST_LETTER_HINT] && !hintsUsed[LAST_LETTER_HINT])
		{
			for(int i=0;i<answer1.length();i++)
			{
				s += "_ ";
			}
		}
		else if(hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && !hintsUsed[LAST_LETTER_HINT])
		{
			for(int i=0;i<answer1.length();i++)
			{
				if(i==0)
					s += answer1.charAt(0) + " ";
				else
					s+="_ ";
			}
		}
		else if(hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && hintsUsed[LAST_LETTER_HINT])
		{
			for(int i=0;i<answer1.length();i++)
			{
				if(i==0)
					s += answer1.charAt(0) + " ";
				else if(i==answer1.length()-1)
					s += answer1.charAt(answer1.length()-1);
				else
					s+="_ ";
			}
		}
		else if(!hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && !hintsUsed[LAST_LETTER_HINT])
		{
			s += answer1.charAt(0) + BLANK_WORD;
		}
		else if(!hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && hintsUsed[LAST_LETTER_HINT])
		{
			s += answer1.charAt(0) + BLANK_WORD + answer1.charAt(answer1.length()-1);
		}
		else if(!hintsUsed[WORD_LENGTH_HINT] && !hintsUsed[FIRST_LETTER_HINT] && hintsUsed[LAST_LETTER_HINT])
		{
			s += BLANK_WORD + answer1.charAt(answer1.length()-1);
		}
		
		return s;
	}
	
	public String getHint2()
	{
		String s = "";
		
		if(hintsUsed[WORD_LENGTH_HINT] && !hintsUsed[FIRST_LETTER_HINT] && !hintsUsed[LAST_LETTER_HINT])
		{
			for(int i=0;i<answer2.length();i++)
			{
				s+="_ ";
			}
		}
		else if(hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && !hintsUsed[LAST_LETTER_HINT])
		{
			for(int i=0;i<answer2.length();i++)
			{
				if(i==0)
					s += answer2.charAt(0) + " ";
				else
					s+="_ ";
			}
		}
		else if(hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && hintsUsed[LAST_LETTER_HINT])
		{
			for(int i=0;i<answer2.length();i++)
			{
				if(i==0)
					s += answer2.charAt(0) + " ";
				else if(i==answer2.length()-1)
					s += answer2.charAt(answer2.length()-1);
				else
					s+="_ ";
			}
		}
		else if(!hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && !hintsUsed[LAST_LETTER_HINT])
		{
			s += answer2.charAt(0) + BLANK_WORD;
		}
		else if(!hintsUsed[WORD_LENGTH_HINT] && hintsUsed[FIRST_LETTER_HINT] && hintsUsed[LAST_LETTER_HINT])
		{
			s += answer2.charAt(0) + BLANK_WORD + answer2.charAt(answer2.length()-1);
		}
		else if(!hintsUsed[WORD_LENGTH_HINT] && !hintsUsed[FIRST_LETTER_HINT] && hintsUsed[LAST_LETTER_HINT])
		{
			s += BLANK_WORD + answer2.charAt(answer2.length()-1);
		}
		
		return s;
	}

	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		
		json.put(ID_KEY, id);
		json.put(CLUE1_KEY, clue1);
		json.put(CLUE2_KEY, clue2);
		json.put(ANSWER1_KEY, answer1);
		json.put(ANSWER2_KEY, answer2);
		json.put(STAR_VALUE_KEY, starValue);
		json.put(HINT1_KEY, hintsUsed[0]);
		json.put(HINT2_KEY, hintsUsed[1]);
		json.put(HINT3_KEY, hintsUsed[2]);
		json.put(SOLVED_KEY, solved);
		
		return json;
	}

	public static Riddle fromJSON(JSONObject json) throws JSONException
	{
		Riddle riddle = new Riddle();
		
		riddle.setId(json.getInt(ID_KEY));
		riddle.setClue1(json.getString(CLUE1_KEY));
		riddle.setClue2(json.getString(CLUE2_KEY));
		riddle.setAnswer1(json.getString(ANSWER1_KEY));
		riddle.setAnswer2(json.getString(ANSWER2_KEY));
		riddle.setStarValue(json.getInt(STAR_VALUE_KEY));
		riddle.setWordLengthHint(json.getBoolean(HINT1_KEY));
		riddle.setFirstLetterHint(json.getBoolean(HINT2_KEY));
		riddle.setLastLetterHint(json.getBoolean(HINT3_KEY));
		riddle.setSolved(json.getBoolean(SOLVED_KEY));
		
		return riddle;
	}
}
