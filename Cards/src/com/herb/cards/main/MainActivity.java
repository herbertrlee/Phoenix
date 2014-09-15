package com.herb.cards.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.example.games.basegameutils.BaseGameActivity;
import com.herb.cards.utils.DatabaseHandler;
import com.herb.cards.utils.EndpointsHandler;

public class MainActivity extends BaseGameActivity implements MainMenuFragment.Listener, CreateGameFragment.Listener, GameCzarFragment.Listener, GameHistoryFragment.Listener, GameNotCzarFragment.Listener, HelpFragment.Listener, PendingGameInfoFragment.Listener, PendingGameListFragment.Listener, SettingsFragment.Listener
{
	public static String MAIN_MENU_FRAG_TAG = "MAIN_MENU";
	public static String CREATE_GAME_FRAG_TAG = "CREATE_GAME";
	public static String PENDING_GAME_LIST_FRAG_TAG = "PENDING_GAME_LIST";
	public static String PENDING_GAME_INFO_FRAG_TAG = "PENDING_GAME_INFO";
	public static String SETTINGS_FRAG_TAG = "SETTINGS";
	public static String HELP_FRAG_TAG = "HELP";
	
	public static String GAME_CZAR_FRAG_TAG = "GAME_CZAR";
	public static String GAME_NOT_CZAR_FRAG_TAG = "GAME_NOT_CZAR";
	public static String GAME_HISTORY_FRAG_TAG = "GAME_HISTORY";
	
	public static int AUTH_REQUEST_CODE = 1;
	
	private static int GAME_STATUS_PENDING = 0;
	public static int GAME_STATUS_WAITING_SUBMITS = 1;
	private static int GAME_STATUS_WAITING_CZAR = 2;
	
	private static String TAB_HISTORY_TAG = "HISTORY";
	private static String TAB_GAME_TAG = "GAME";
	private static String TAB_INFO_TAG = "INFO";
	
	public static String CARDS_PREFS_KEY = "herb_cards_prefs";
	
	MainMenuFragment mainMenuFragment;
	CreateGameFragment createGameFragment;
	PendingGameListFragment pendingGameListFragment;
	PendingGameInfoFragment pendingGameInfoFragment;
	SettingsFragment settingsFragment;
	HelpFragment helpFragment;
	
	GameCzarFragment gameCzarFragment;
	GameNotCzarFragment gameNotCzarFragment;
	GameHistoryFragment gameHistoryFragment;
	
	EndpointsHandler endpointsHandler;
	DatabaseHandler dbHandler;
	SharedPreferences prefs;
	
	DrawerLayout drawerLayout;
	ListView drawerList;
	String[] drawerItemTitles;
	ActionBarDrawerToggle drawerToggle;
	
	ArrayList<Fragment> drawerFragments;
	ArrayList<Fragment> allFragments;
	Map<Fragment, String> fragmentToNameMap;
	Fragment currentFragment;
	String currentFragTag;
	
	JSONObject currentGame;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		endpointsHandler = new EndpointsHandler(this);
		dbHandler = new DatabaseHandler(this);
		
		fragmentToNameMap = new HashMap<Fragment, String>();
		drawerFragments = new ArrayList<Fragment>();
		allFragments = new ArrayList<Fragment>();
		
		mainMenuFragment = new MainMenuFragment();
		createGameFragment = new CreateGameFragment();
		pendingGameListFragment = new PendingGameListFragment();
		pendingGameInfoFragment = new PendingGameInfoFragment();
		settingsFragment = new SettingsFragment();
		helpFragment = new HelpFragment();
		gameCzarFragment = new GameCzarFragment();
		gameNotCzarFragment = new GameNotCzarFragment();
		gameHistoryFragment = new GameHistoryFragment();
		
		mainMenuFragment.setListener(this);
		createGameFragment.setListener(this);
		pendingGameListFragment.setListener(this);
		pendingGameInfoFragment.setListener(this);
		settingsFragment.setListener(this);
		helpFragment.setListener(this);
		gameCzarFragment.setListener(this);
		gameNotCzarFragment.setListener(this);
		gameHistoryFragment.setListener(this);
		
		fragmentToNameMap.put(mainMenuFragment, MAIN_MENU_FRAG_TAG);
		fragmentToNameMap.put(createGameFragment, CREATE_GAME_FRAG_TAG);
		fragmentToNameMap.put(pendingGameListFragment, PENDING_GAME_LIST_FRAG_TAG);
		fragmentToNameMap.put(pendingGameInfoFragment, PENDING_GAME_INFO_FRAG_TAG);
		fragmentToNameMap.put(settingsFragment, SETTINGS_FRAG_TAG);
		fragmentToNameMap.put(helpFragment, HELP_FRAG_TAG);
		fragmentToNameMap.put(gameCzarFragment, GAME_CZAR_FRAG_TAG);
		fragmentToNameMap.put(gameNotCzarFragment, GAME_NOT_CZAR_FRAG_TAG);
		fragmentToNameMap.put(gameHistoryFragment, GAME_HISTORY_FRAG_TAG);
		
		allFragments.add(mainMenuFragment);
		allFragments.add(createGameFragment);
		allFragments.add(pendingGameListFragment);
		allFragments.add(pendingGameInfoFragment);
		allFragments.add(settingsFragment);
		allFragments.add(helpFragment);
		allFragments.add(gameCzarFragment);
		allFragments.add(gameNotCzarFragment);
		allFragments.add(gameHistoryFragment);
		
		drawerFragments.add(mainMenuFragment);
		drawerFragments.add(createGameFragment);
		drawerFragments.add(pendingGameListFragment);
		drawerFragments.add(settingsFragment);
		drawerFragments.add(helpFragment);
		
		currentFragment = mainMenuFragment;
		currentFragTag = MAIN_MENU_FRAG_TAG;
		currentGame = new JSONObject();
		
		drawerItemTitles = getResources().getStringArray(R.array.drawer_item_array);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemTitles));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
		
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
		
        drawerLayout.setDrawerListener(drawerToggle);
        
        ActionBar actionBar = getActionBar();
        
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        
        Tab tab = actionBar.newTab().setText(R.string.history).setTag(TAB_HISTORY_TAG).setTabListener(new TabListener(this));
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.game).setTag(TAB_GAME_TAG).setTabListener(new TabListener(this));
        actionBar.addTab(tab);
        tab.select();
        tab = actionBar.newTab().setText(R.string.info).setTag(TAB_INFO_TAG).setTabListener(new TabListener(this));
        actionBar.addTab(tab);
        
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		
		for(int i=0;i<allFragments.size();i++)
		{
			t.add(R.id.fragment_container, allFragments.get(i), fragmentToNameMap.get(allFragments.get(i)));
			t.hide(allFragments.get(i));
		}
		
		t.show(currentFragment).commit();
		
		if(savedInstanceState == null)
		{
			getGames();
		}
	}
	
	@Override
	public void onSignInFailed()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded()
	{
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == AUTH_REQUEST_CODE)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				endpointsHandler.insertUserInfo();
			}
		}
	}
	
	/* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position)
    {
    	switch(position)
    	{
    		case 0:
    			endpointsHandler.fetchCurrentGames();
    			break;
	    	case 1:
	    		endpointsHandler.fetchCardSets();
	    		break;
	    	case 2:
	    		endpointsHandler.fetchPendingGames();
	    		break;
    		default:
    			break;
    	}
    	
    	switchToFragment(drawerFragments.get(position));
    	drawerLayout.closeDrawer(drawerList);
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) 
        {
            return true;
        }
        
        if(item.getItemId() == android.R.id.home)
        {
        	switchToFragment(mainMenuFragment);
        	mainMenuFragment.updateUi();
        }
        
        return super.onOptionsItemSelected(item);
    }
    
 // Switch UI to the given fragment
    private void switchToFragment(Fragment newFrag) 
    {
        if(newFrag.isVisible())
        	return;
        
        FragmentTransaction t =  getSupportFragmentManager().beginTransaction();
        
        newFrag.getView().bringToFront();
        currentFragment.getView().bringToFront();
        
        t.hide(currentFragment).show(newFrag);
        
        currentFragment = newFrag;
        currentFragTag = fragmentToNameMap.get(newFrag);
        t.commit();
        
        Log.e("switch to", currentFragTag);
        
        boolean onMainMenu = currentFragTag.equals(MAIN_MENU_FRAG_TAG);
        
        if(onMainMenu)
        {
        	drawerToggle.setDrawerIndicatorEnabled(true);
        	drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else
        {
        	drawerToggle.setDrawerIndicatorEnabled(false);
        	drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        
        boolean showGameTabs = currentFragTag.equals(GAME_NOT_CZAR_FRAG_TAG) || currentFragTag.equals(GAME_HISTORY_FRAG_TAG) || currentFragTag.equals(GAME_CZAR_FRAG_TAG) || currentFragTag.equals(HELP_FRAG_TAG);
        
        int navMode = showGameTabs ? ActionBar.NAVIGATION_MODE_TABS : ActionBar.NAVIGATION_MODE_STANDARD;
        
        if(getActionBar().getNavigationMode() != navMode)
        	getActionBar().setNavigationMode(navMode);
    }

	@Override
	public void createGame(String gameName, int[] cardSets, int maxPlayers)
	{
		endpointsHandler.createGame(gameName, cardSets, maxPlayers);
	}
	
	public void setCardSets(JSONArray cardSetsJSON)
	{
		createGameFragment.setCardSets(cardSetsJSON);
		createGameFragment.updateUi();
	}
	
	public void getGames()
	{
		endpointsHandler.fetchCurrentGames();
	}
	
	public void returnToMainMenu()
	{
		switchToFragment(mainMenuFragment);
		getGames();
	}
	
	public void setGameList(JSONArray gameList)
	{
		mainMenuFragment.setGames(gameList);
		mainMenuFragment.updateUi();
	}

	@Override
	public void openGame(String gameId, int status)
	{
		if(status == GAME_STATUS_PENDING)
		{
			endpointsHandler.fetchPendingGameInfo(gameId);
			switchToFragment(pendingGameInfoFragment);
		}
		else
		{
			endpointsHandler.fetchActiveGameInfo(gameId);
		}
		
	}
	
	public void setPendingGameInfo(JSONObject pendingGameInfoJSON)
	{
		pendingGameInfoFragment.setPendingGameInfo(pendingGameInfoJSON);
		pendingGameInfoFragment.updateUi();
	}

	@Override
	public void openPendingGame(String gameId)
	{
		openGame(gameId, GAME_STATUS_PENDING);
	}

	public void setPendingGameList(JSONArray gameList)
	{
		pendingGameListFragment.setPendingGameList(gameList);
		pendingGameListFragment.updateUi();
	}

	@Override
	public void startGame(String gameId)
	{
		endpointsHandler.startGame(gameId);
	}

	@Override
	public void cancelGame(String gameId)
	{
		endpointsHandler.cancelGame(gameId);
	}

	@Override
	public void joinGame(String gameId)
	{
		endpointsHandler.joinGame(gameId);
	}

	@Override
	public void leaveGame(String gameId)
	{
		endpointsHandler.leaveGame(gameId);
	}
	
	public void goToCurrentGame()
	{
		try
		{
			boolean isCzar = currentGame.getBoolean("isCzar");
			
			if(isCzar)
				switchToFragment(gameCzarFragment);
			else
				switchToFragment(gameNotCzarFragment);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void goToCurrentHistory()
	{
		switchToFragment(gameHistoryFragment);
	}
	
	public void goToCurrentInfo()
	{
	}
	
	public void goToActiveGame(JSONObject json)
	{
		currentGame = json;
		
		boolean isCzar = false;
		
		try
		{
			isCzar = json.getBoolean("isCzar");
			
			String gameId = currentGame.getString("gameId");
			int numPlayers = currentGame.getInt("subsNeeded") + currentGame.getJSONArray("submissionIds").length();
			
			endpointsHandler.fetchPastRounds(gameId, null);
			endpointsHandler.fetchPastSubmissions(gameId, numPlayers, null);
		} catch (JSONException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(isCzar)
		{
			switchToFragment(gameCzarFragment);

			try
			{
				gameCzarFragment.setGameInfo(json);
			
				JSONObject blackCardJSON = getBlackCard(json.getInt("blackCardId"));
				
				gameCzarFragment.setBlackCard(blackCardJSON);
				if(json.getInt("subsNeeded") == 0)
				{
					endpointsHandler.fetchCurrentSubmissions(json.getString("gameId"));
				}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gameCzarFragment.updateUi();
		}
		else
		{
			switchToFragment(gameNotCzarFragment);
			gameNotCzarFragment.setGameInfo(json);
			
			try
			{
				JSONArray jsonIds = json.getJSONArray("hand");
				int[] ids = new int[jsonIds.length()];
				
				for(int i=0;i<jsonIds.length();i++)
				{
					ids[i] = jsonIds.getInt(i);
				}
				
				JSONArray whiteCardsJSON = getWhiteCards(ids);
				JSONObject blackCardJSON = getBlackCard(json.getInt("blackCardId"));
				
				gameNotCzarFragment.setBlackCard(blackCardJSON);
				gameNotCzarFragment.setWhiteCard(whiteCardsJSON);
				
				if(json.getBoolean("userSubmitted"))
				{
					JSONArray submissionJSON = json.getJSONArray("submissionIds");
					int[] submissionIds = new int[submissionJSON.length()];
					
					for(int i=0;i<submissionIds.length;i++)
					{
						submissionIds[i] = submissionJSON.getInt(i);
					}
					
					JSONArray submissionTextsJSON = getWhiteCards(submissionIds);
					gameNotCzarFragment.setSubmissions(submissionTextsJSON);
				}
				
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
			

			gameNotCzarFragment.updateUi();
		}
	}

	@Override
	public void setTitle(String title)
	{
		getActionBar().setTitle(title);
	}
	
	public JSONObject getBlackCard(int id)
	{
		return dbHandler.getBlackCard(id);
	}
	
	public JSONArray getWhiteCards(int[] ids)
	{
		return dbHandler.getWhiteCards(ids);
	}

	@Override
	public void submitWhite(String gameId, int[] ids)
	{
		endpointsHandler.submitWhite(gameId, ids);
	}
	
	public static class TabListener implements ActionBar.TabListener
	{
		private MainActivity main;
		
		public TabListener(MainActivity main)
		{
			this.main = main;
		}
		
		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft)
		{
			String tag = tab.getTag().toString();
			
			if(tag.equals(TAB_GAME_TAG))
			{
				main.goToCurrentGame();
			}
			else if(tag.equals(TAB_HISTORY_TAG))
			{
				main.goToCurrentHistory();
			}
			else if(tag.equals(TAB_INFO_TAG))
			{
				main.goToCurrentInfo();
			}
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft)
		{
		}

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft)
		{
		}
		
	}

	public void setCzarSubmissions(JSONArray jsonArray)
	{
		JSONArray newJsonArray = new JSONArray();
		
		try
		{
			for(int i=0;i<jsonArray.length();i++)
			{
				JSONObject json = jsonArray.getJSONObject(i);
				JSONArray whiteCardIds = json.getJSONArray("whiteCardIds");
				
				int[] whiteCardArray = new int[whiteCardIds.length()];
				
				for(int j=0;j<whiteCardArray.length;j++)
				{
					whiteCardArray[j] = whiteCardIds.getInt(j);
				}
				
				JSONArray whiteCards = getWhiteCards(whiteCardArray);
				JSONArray whiteCardTexts = new JSONArray();
				
				for(int j=0;j<whiteCards.length();j++)
					whiteCardTexts.put(whiteCards.getJSONObject(j).getString("text"));
				
				json.put("whiteCardTexts", whiteCardTexts);
				newJsonArray.put(json);
			}
			
			gameCzarFragment.setSubmissions(newJsonArray);
			gameCzarFragment.updateUi();
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void pickWinner(String gameId, String submissionId)
	{
		endpointsHandler.pickWinner(gameId, submissionId);
	}

	@Override
	public void pageForward()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pageBackward()
	{
		// TODO Auto-generated method stub
		
	}
	
	public void setPastSubmissions(JSONArray submissionsJSON)
	{
		
	}
	
	public void setPastRounds(JSONArray pastRoundsJSON)
	{
		
	}
}
