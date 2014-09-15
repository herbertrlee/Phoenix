package com.herb.cards.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.herb.cards.main.MainActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class EndpointsHandler
{
	String authToken;
	MainActivity main;
	HttpEndpointsHandler httpEndpointsHandler;
	
	public EndpointsHandler(MainActivity main)
	{
		this.main = main;
		httpEndpointsHandler = new HttpEndpointsHandler();
	}
	
	public void fetchPendingGames()
	{
		new FetchPendingGamesProcess().execute();
	}
	
	public void fetchPendingGameInfo(String id)
	{
		FetchPendingGameInfoProcess fetchPendingGameInfoProcess = new FetchPendingGameInfoProcess();
		fetchPendingGameInfoProcess.setId(id);
		fetchPendingGameInfoProcess.execute();
	}
	
	public void insertUserInfo()
	{
		new InsertUserInfoProcess().execute();
	}
	
	public void setUserInfo(String id, String alias, String regId)
	{
		SetUserInfoProcess setUserInfoProcess = new SetUserInfoProcess();
		setUserInfoProcess.setId(id);
		setUserInfoProcess.setAlias(alias);
		setUserInfoProcess.setRegId(regId);
		setUserInfoProcess.execute();	
	}
	
	public void getUserInfo(String id)
	{
		GetUserInfoProcess getUserInfoProcess = new GetUserInfoProcess();
		getUserInfoProcess.setId(id);
		getUserInfoProcess.execute();
	}
	
	public void createGame(String gameName, int[] cardSets, int maxPlayers)
	{
		CreateGameProcess createGameProcess = new CreateGameProcess();
		createGameProcess.setGameName(gameName);
		createGameProcess.setCardSets(cardSets);
		createGameProcess.setMaxPlayers(3);
		createGameProcess.execute();
	}
	
	public void cancelGame(String id)
	{
		CancelGameProcess cancelGameProcess = new CancelGameProcess();
		cancelGameProcess.setId(id);
		cancelGameProcess.execute();
	}
	
	public void joinGame(String gameId)
	{
		JoinGameProcess joinGameProcess = new JoinGameProcess();
		joinGameProcess.setGameId(gameId);
		joinGameProcess.execute();
	}
	
	public void leaveGame(String gameId)
	{
		LeaveGameProcess leaveGameProcess = new LeaveGameProcess();
		leaveGameProcess.setGameId(gameId);
		leaveGameProcess.execute();
	}
	
	public void startGame(String id)
	{
		StartGameProcess startGameProcess = new StartGameProcess();
		startGameProcess.setId(id);
		startGameProcess.execute();
	}
	
	public void fetchActiveGameInfo(String gameId)
	{
		FetchActiveGameInfoProcess fetchActiveGameInfoProcess = new FetchActiveGameInfoProcess();
		fetchActiveGameInfoProcess.setGameId(gameId);
		fetchActiveGameInfoProcess.execute();
	}
	
	public void pickWinner(String gameId, String submissionId)
	{
		PickWinnerProcess pickWinnerProcess = new PickWinnerProcess();
		pickWinnerProcess.setSubmissionId(submissionId);
		pickWinnerProcess.setGameId(gameId);
		pickWinnerProcess.execute();
	}
	
	public void submitWhite(String gameId, int[] whiteCardIds)
	{
		SubmitWhiteProcess submitWhiteProcess = new SubmitWhiteProcess();
		submitWhiteProcess.setGameId(gameId);
		submitWhiteProcess.setWhiteCardIds(whiteCardIds);
		submitWhiteProcess.execute();
	}
	
	public void fetchCurrentGames()
	{
		new FetchCurrentGamesProcess().execute();
	}
	
	public void fetchPlayers(String gameId)
	{
		FetchPlayersProcess fetchPlayersProcess = new FetchPlayersProcess();
		fetchPlayersProcess.setGameId(gameId);
		fetchPlayersProcess.execute();
	}
	
	public void fetchCardSets()
	{
		new FetchCardSetsProcess().execute();
	}
	
	public void fetchCurrentSubmissions(String gameId)
	{
		FetchCurrentSubmissionsProcess fetchCurrentSubmissionsProcess = new FetchCurrentSubmissionsProcess();
		fetchCurrentSubmissionsProcess.setGameId(gameId);
		fetchCurrentSubmissionsProcess.execute();
	}
	
	public void fetchPastRounds(String gameId, String pageToken)
	{
		FetchPastRoundsProcess fetchPastRoundsProcess = new FetchPastRoundsProcess();
		fetchPastRoundsProcess.setGameId(gameId);
		fetchPastRoundsProcess.setPageToken(pageToken);
		fetchPastRoundsProcess.execute();
	}
	
	public void fetchPastSubmissions(String gameId, int numPlayers, String pageToken)
	{
		FetchPastSubmissionsProcess fetchPastSubmissionsProcess = new FetchPastSubmissionsProcess();
		fetchPastSubmissionsProcess.setNumPlayers(numPlayers);
		fetchPastSubmissionsProcess.setGameId(gameId);
		fetchPastSubmissionsProcess.setPageToken(pageToken);
		fetchPastSubmissionsProcess.execute();
	}
	
	private class InsertUserInfoProcess extends AsyncTask<Void, Void, JSONObject>
	{

		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "insertUserInfo";
				
				json = httpEndpointsHandler.post(path, authToken, true, null);
				
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			Log.e("json", json.toString());
		}
	}
	
	private class FetchPendingGamesProcess extends AsyncTask<Void, Void, JSONObject>
	{
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Loading games");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			String path = "fetchPendingGames";
			
			JSONObject json = httpEndpointsHandler.get(path, "", false, null);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				main.setPendingGameList(json.getJSONArray("items"));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pdia.dismiss();
		}
	}
	
	private class CreateGameProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String gameName = "";
		int[] cardSets = {};
		int maxPlayers = 0;
		
		void setGameName(String gameName)
		{
			try
			{
				this.gameName = URLEncoder.encode(gameName, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		void setCardSets(int[] cardSets)
		{
			this.cardSets = cardSets;
		}
		
		void setMaxPlayers(int maxPlayers)
		{
			this.maxPlayers = maxPlayers;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Creating your game");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "createGame";

				ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
				values.add(new BasicNameValuePair("gameName", gameName));
				values.add(new BasicNameValuePair("cardSetsJson", new JSONArray(cardSets).toString()));
				values.add(new BasicNameValuePair("maxPlayers", Integer.toString(maxPlayers)));
				
				json = httpEndpointsHandler.post(path, authToken, true, values);
				
				
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			main.returnToMainMenu();
			pdia.dismiss();
		}
	}
	
	private class CancelGameProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String id = "";
		
		void setId(String id)
		{
			this.id = id;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Cancelling game");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "cancelGame/" + id;
				
				json = httpEndpointsHandler.delete(path, authToken, true, null);
				
				
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			main.returnToMainMenu();
			pdia.dismiss();
		}
	}
	
	private class StartGameProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String id = "";
		
		void setId(String id)
		{
			this.id = id;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Starting game");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "startGame/" + id;
				
				json = httpEndpointsHandler.post(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			main.openGame(id, MainActivity.GAME_STATUS_WAITING_SUBMITS);
			pdia.dismiss();
		}
	}
	
	private class FetchPendingGameInfoProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String id;
		
		void setId(String id)
		{
			this.id = id;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Loading game info");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = new JSONObject();
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				String path = "fetchPendingGameInfo/" + id;
				
				json = httpEndpointsHandler.get(path, authToken, true, null);
				
				Log.e("json", json.toString());
			}catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				json.put("gameId", id);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			main.setPendingGameInfo(json);
			pdia.dismiss();
		}
	}
	
	private class SetUserInfoProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String id="", alias="", regId="";
		
		void setId(String id)
		{
			this.id = id;
		}
		
		void setAlias(String alias)
		{
			this.alias = alias;
		}
		
		void setRegId(String regId)
		{
			this.regId = regId;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Saving");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "setUserInfo/" + id;
				
				ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
				values.add(new BasicNameValuePair("alias", alias));
				values.add(new BasicNameValuePair("regId", regId));
				
				json = httpEndpointsHandler.post(path, authToken, true, values);
				
				
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			pdia.dismiss();
		}
	}
	
	private class GetUserInfoProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String id = "";
		
		void setId(String id)
		{
			this.id = id;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Getting info");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "getUserInfo/" + id;
				
				json = httpEndpointsHandler.get(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			pdia.dismiss();
		}
	}
	
	private class JoinGameProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String gameId = "";
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Joining game");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "joinGame/" + gameId;
				
				json = httpEndpointsHandler.post(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			main.openPendingGame(gameId);
			pdia.dismiss();
		}
	}
	
	private class LeaveGameProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String gameId = "";
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Leaving game");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "leaveGame/" + gameId;
				
				json = httpEndpointsHandler.post(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			main.returnToMainMenu();
			pdia.dismiss();
		}
	}
	
	private class FetchActiveGameInfoProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String gameId = "";
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Loading game");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "fetchActiveGameInfo/" + gameId;
				
				json = httpEndpointsHandler.get(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				JSONObject gameJSON = json.getJSONArray("items").getJSONObject(0);
				gameJSON.put("gameId", gameId);
				main.goToActiveGame(gameJSON);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pdia.dismiss();
		}

		
	}
	
	private class PickWinnerProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String submissionId = "", gameId = "";
		
		void setSubmissionId(String id)
		{
			this.submissionId = id;
		}
		
		void setGameId(String id)
		{
			this.gameId = id;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Submitting");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "pickWinner/" + submissionId;
				
				json = httpEndpointsHandler.post(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			pdia.dismiss();
			fetchActiveGameInfo(gameId);
		}
	}
	
	private class SubmitWhiteProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String gameId = "";
		int[] whiteCardIds = {};
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		
		void setWhiteCardIds(int[] whiteCardIds)
		{
			this.whiteCardIds = whiteCardIds;
		}
		
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Submitting");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "submitWhite/" + gameId;
				
				ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
				values.add(new BasicNameValuePair("whiteCardIdsJson", new JSONArray(whiteCardIds).toString()));
				
				json = httpEndpointsHandler.post(path, authToken, true, values);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			pdia.dismiss();
			fetchActiveGameInfo(gameId);
		}
	}
	
	private class FetchCurrentGamesProcess extends AsyncTask<Void, Void, JSONObject>
	{
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Loading games");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "fetchCurrentGames";
				
				json = httpEndpointsHandler.get(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				JSONArray gameList = json.getJSONArray("items");
				main.setGameList(gameList);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pdia.dismiss();
		}
	}
	
	private class FetchPlayersProcess extends AsyncTask<Void, Void, JSONObject>
	{
		String gameId = "";
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				String path = "fetchPlayers/" + gameId;
				
				json = httpEndpointsHandler.get(path, authToken, true, null);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
	}
	
	private class FetchCardSetsProcess extends AsyncTask<Void, Void, JSONObject>
	{
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute()
		{
			pdia = new ProgressDialog(main);
			pdia.setMessage("Getting card sets");
			pdia.show();
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			String path = "fetchCardSets";
			
			ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
			values.add(new BasicNameValuePair("order", "idNum"));
			
			JSONObject json = httpEndpointsHandler.get(path, "", false, values);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				JSONArray cardSets = json.getJSONArray("items");
				main.setCardSets(cardSets);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pdia.dismiss();
		}
	}

	private class FetchCurrentSubmissionsProcess extends AsyncTask<String, Void, JSONObject>
	{
		String gameId;
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		@Override
		protected JSONObject doInBackground(String... params)
		{
			String path = "fetchCurrentSubmissions/" + gameId;
			
			JSONObject json = null;
			try
			{
				authToken = GoogleAuthUtil.getToken(main, "herbertrlee@gmail.com", "oauth2:email");
				
				ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
				values.add(new BasicNameValuePair("status", Integer.toString(1)));
				
				json = httpEndpointsHandler.get(path, authToken, true, values);
			} catch (UserRecoverableAuthException e)
			{
				main.startActivityForResult(e.getIntent(), MainActivity.AUTH_REQUEST_CODE);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				main.setCzarSubmissions(json.getJSONArray("items"));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class FetchPastRoundsProcess extends AsyncTask<Void, Void, JSONObject>
	{
		static final String ORDER = "-roundNumber";
		static final String LIMIT = "10";
		
		String gameId;
		String pageToken = null;
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		
		void setPageToken(String pageToken)
		{
			this.pageToken = pageToken;
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			String path = "fetchPastRounds/" + gameId;
			
			ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
			values.add(new BasicNameValuePair("limit", LIMIT));
			values.add(new BasicNameValuePair("order", ORDER));
			
			if(pageToken != null)
				values.add(new BasicNameValuePair("pageToken", pageToken));
			
			JSONObject json = httpEndpointsHandler.get(path, "", false, values);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				main.setPastRounds(json.getJSONArray("items"));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class FetchPastSubmissionsProcess extends AsyncTask<Void, Void, JSONObject>
	{
		static final String STATUS_COMPLETE = "2";
		static final String ORDER = "-roundNumber";
		static final int ROUNDS = 10;
		
		String gameId;
		String pageToken = null;
		int numPlayers = 0;
		
		void setGameId(String gameId)
		{
			this.gameId = gameId;
		}
		
		void setPageToken(String pageToken)
		{
			this.pageToken = pageToken;
		}
		
		void setNumPlayers(int numPlayers)
		{
			this.numPlayers = numPlayers;
		}
		
		@Override
		protected JSONObject doInBackground(Void... params)
		{
			String path = "fetchPastSubmissions/" + gameId;
			
			String limit = Integer.toString(numPlayers * ROUNDS);
			
			ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
			values.add(new BasicNameValuePair("limit", limit));
			values.add(new BasicNameValuePair("order", ORDER));
			values.add(new BasicNameValuePair("status", STATUS_COMPLETE));
			
			if(pageToken != null)
				values.add(new BasicNameValuePair("pageToken", pageToken));
			
			JSONObject json = httpEndpointsHandler.get(path, "", false, null);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			try
			{
				main.setPastSubmissions(json.getJSONArray("items"));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
