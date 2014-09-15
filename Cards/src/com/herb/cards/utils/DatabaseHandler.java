package com.herb.cards.utils;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHandler extends SQLiteAssetHelper
{
	private static final String DB_NAME = "cards.db";
	private static final int DB_VERSION = 1;
	
	private static final String BLACK_CARDS_TABLE = "black_cards";
	private static final String WHITE_CARDS_TABLE = "white_cards";
	
	private static final String ID_COL = "_id";
	private static final String TEXT_COL = "text";
	private static final String PICK_COL = "pick";
	
	
	
	public DatabaseHandler(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	public JSONObject getBlackCard(int id)
	{
		JSONObject card = null;
				
		try
		{
			GetBlackCardProcess getBlackCardProcess = new GetBlackCardProcess();
			getBlackCardProcess.setId(id);
			card = getBlackCardProcess.execute().get();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return card;
	}
	
	public JSONArray getWhiteCards(int[] ids)
	{
		JSONArray cards = null;
		
		try
		{
			GetWhiteCardsProcess getWhiteCardsProcess = new GetWhiteCardsProcess();
			getWhiteCardsProcess.setIds(ids);
			cards = getWhiteCardsProcess.execute().get();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cards;
	}
	
	private class GetBlackCardProcess extends AsyncTask<Integer, Void, JSONObject>
	{
		int id;
		
		void setId(int id)
		{
			this.id = id;
		}
		
		@Override
		protected JSONObject doInBackground(Integer... params)
		{
			Log.e("id", Integer.toString(id));
			
			JSONObject json = new JSONObject();

			SQLiteDatabase db = getReadableDatabase();
			String sql = String.format("SELECT %s, %s FROM %s WHERE %s=%d", TEXT_COL, PICK_COL, BLACK_CARDS_TABLE, ID_COL, id);
			
			try
			{
				Cursor c = db.rawQuery(sql, null);
				if(c.moveToFirst())
				{
					json.put("id", id);
					json.put("text", c.getString(c.getColumnIndex(TEXT_COL)));
					json.put("pick", c.getInt(c.getColumnIndex(PICK_COL)));
				}
				c.close();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			db.close();
			
			return json;
		}
	}
	
	private class GetWhiteCardsProcess extends AsyncTask<Void, Void, JSONArray>
	{
		int[] ids;
		
		void setIds(int[] ids)
		{
			this.ids = ids;
		}
		
		@Override
		protected JSONArray doInBackground(Void... params)
		{
			JSONArray json = new JSONArray();
			SQLiteDatabase db = getReadableDatabase();
			
			String whereClause = "(";
			
			for(int i=0;i<ids.length;i++)
			{
				if(i>0)
				{
					whereClause += ",";
				}
				whereClause += Integer.toString(ids[i]);
			}
			whereClause += ")";
			
			String sql = String.format("SELECT %s, %s FROM %s WHERE %s IN %s", ID_COL, TEXT_COL, WHITE_CARDS_TABLE, ID_COL, whereClause);
			
			Cursor c = db.rawQuery(sql, null);
			
			while(c.moveToNext())
			{
				JSONObject card = new JSONObject();
				try
				{
					card.put("id", c.getInt(c.getColumnIndex(ID_COL)));
					card.put("text", c.getString(c.getColumnIndex(TEXT_COL)));
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				json.put(card);
			}
			
			c.close();
			db.close();
			return json;
		}
		
	}
}
