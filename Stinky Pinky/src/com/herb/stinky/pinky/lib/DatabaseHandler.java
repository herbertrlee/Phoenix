/*
 * Handles calls to the SQLite database 
 */

package com.herb.stinky.pinky.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.herb.stinky.pinky.entities.Riddle;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class DatabaseHandler extends SQLiteAssetHelper
{
	private static final String DB_NAME = "stinkyPinky.db";
	private static final int DB_VERSION = 1;
	
	private static final int RIDDLES_PER_LEVEL = 10;
	private static final int STARS_PER_LEVEL_UNLOCK = 7;
	
	private static final String RIDDLE_TABLE_NAME = "riddles";
	
	private static final String ID_COL = "_id";
	private static final String LEVEL_COL = "level";
	private static final String CLUE1_COL = "clue1";
	private static final String CLUE2_COL = "clue2";
	private static final String ANSWER1_COL = "answer1";
	private static final String ANSWER2_COL = "answer2";
	private static final String STAR_VALUE_COL = "starValue";
	private static final String HINT_1_COL = "hint1used";
	private static final String HINT_2_COL = "hint2used";
	private static final String HINT_3_COL = "hint3used";
	private static final String SOLVED_COL = "solved";
	
	private static final String TRUE_VALUE = "TRUE";
	private static final String FALSE_VALUE = "FALSE";
	
	public DatabaseHandler(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	public Riddle[] fetchRiddleList(int level)
	{
		Riddle[] riddles = new Riddle[RIDDLES_PER_LEVEL];
		
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = String.format("SELECT * FROM %s WHERE %s=%d LIMIT %d", RIDDLE_TABLE_NAME, LEVEL_COL, level, RIDDLES_PER_LEVEL);
		
		Cursor c = db.rawQuery(sql, null);
		
		for(int i=0;i<RIDDLES_PER_LEVEL;i++)
		{
			c.moveToNext();
			Riddle riddle = new Riddle();
			
			boolean solved = c.getString(c.getColumnIndex(SOLVED_COL)).equalsIgnoreCase(TRUE_VALUE);
			boolean hint1used = c.getString(c.getColumnIndex(HINT_1_COL)).equalsIgnoreCase(TRUE_VALUE);
			boolean hint2used = c.getString(c.getColumnIndex(HINT_2_COL)).equalsIgnoreCase(TRUE_VALUE);
			boolean hint3used = c.getString(c.getColumnIndex(HINT_3_COL)).equalsIgnoreCase(TRUE_VALUE);
			
			riddle.setId(c.getInt(c.getColumnIndex(ID_COL)));
			riddle.setClue1(c.getString(c.getColumnIndex(CLUE1_COL)));
			riddle.setClue2(c.getString(c.getColumnIndex(CLUE2_COL)));
			riddle.setAnswer1(c.getString(c.getColumnIndex(ANSWER1_COL)));
			riddle.setAnswer2(c.getString(c.getColumnIndex(ANSWER2_COL)));
			riddle.setStarValue(c.getInt(c.getColumnIndex(STAR_VALUE_COL)));
			
			riddle.setSolved(solved);
			riddle.setWordLengthHint(hint1used);
			riddle.setFirstLetterHint(hint2used);
			riddle.setLastLetterHint(hint3used);
			
			riddles[i] = riddle;
		}
		
		c.close();
		db.close();
		return riddles;
	}
	
	public void saveRiddle(Riddle riddle)
	{
		SQLiteDatabase db = getWritableDatabase();
		String filter = String.format("%s=%d", ID_COL, riddle.getId());
				
		String hint1used = riddle.isHintUsed(Riddle.WORD_LENGTH_HINT) ? TRUE_VALUE : FALSE_VALUE;
		String hint2used = riddle.isHintUsed(Riddle.FIRST_LETTER_HINT) ? TRUE_VALUE : FALSE_VALUE;
		String hint3used = riddle.isHintUsed(Riddle.LAST_LETTER_HINT) ? TRUE_VALUE : FALSE_VALUE;
		String solved = riddle.isSolved() ? TRUE_VALUE : FALSE_VALUE;
		
		ContentValues contents = new ContentValues();
		contents.put(STAR_VALUE_COL, riddle.getStarValue());
		contents.put(HINT_1_COL, hint1used);
		contents.put(HINT_2_COL, hint2used);
		contents.put(HINT_3_COL, hint3used);
		contents.put(SOLVED_COL, solved);
		
		db.update(RIDDLE_TABLE_NAME, contents, filter, null);
		db.close();
	}

	public int getMaxLevel()
	{
		int maxLevel = 0;
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = String.format("SELECT max(%s) FROM %s", LEVEL_COL, RIDDLE_TABLE_NAME);
		
		Cursor c = db.rawQuery(sql, null);
		
		if(c.moveToFirst())
			maxLevel = c.getInt(0);
		
		db.close();
		return maxLevel;
	}

	public boolean isUnlocked(int level)
	{
		int requiredStars = (level-1)*STARS_PER_LEVEL_UNLOCK;
		boolean unlocked = true;
		
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = String.format("SELECT count(%s) FROM %s WHERE %s='%s'", STAR_VALUE_COL, RIDDLE_TABLE_NAME, SOLVED_COL, TRUE_VALUE);
		
		Cursor c = db.rawQuery(sql, null);
		
		if(c.moveToFirst())
			unlocked = c.getInt(0) >= requiredStars;
		
		c.close();
		db.close();
		return unlocked;
	}

	public Integer getMaxUnlockedLevel()
	{
		int maxUnlockedLevel = 0;
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = String.format("SELECT count(%s) FROM %s WHERE %s='%s'", STAR_VALUE_COL, RIDDLE_TABLE_NAME, SOLVED_COL, TRUE_VALUE);
		
		Cursor c = db.rawQuery(sql, null);
		
		if(c.moveToFirst())
		{
			int stars = c.getInt(0);
			maxUnlockedLevel = stars/STARS_PER_LEVEL_UNLOCK +1;
		}
		
		c.close();
		db.close();
		return maxUnlockedLevel;
	}
	
	public void insertRiddles(JSONArray riddlesJSONs) throws JSONException
	{
		SQLiteDatabase db = getWritableDatabase();
		
		for(int i=0;i<riddlesJSONs.length();i++)
		{
			JSONObject riddleJSON = riddlesJSONs.getJSONObject(i);
			
			ContentValues contents = new ContentValues();
			contents.put(ID_COL, riddleJSON.getInt(ID_COL));
			contents.put(LEVEL_COL, riddleJSON.getInt(LEVEL_COL));
			contents.put(CLUE1_COL, riddleJSON.getString(CLUE1_COL));
			contents.put(CLUE2_COL, riddleJSON.getString(CLUE2_COL));
			contents.put(ANSWER1_COL, riddleJSON.getString(ANSWER1_COL));
			contents.put(ANSWER2_COL, riddleJSON.getString(ANSWER2_COL));
			contents.put(STAR_VALUE_COL, Riddle.MAX_STAR_VALUE);
			contents.put(HINT_1_COL, FALSE_VALUE);
			contents.put(HINT_2_COL, FALSE_VALUE);
			contents.put(HINT_3_COL, FALSE_VALUE);
			contents.put(SOLVED_COL, FALSE_VALUE);
			
			db.insert(RIDDLE_TABLE_NAME, null, contents);
		}
		
		db.close();
	}
}
