/**
 * Background class that contains most of the game logic.
 */

package com.herb.stinky.pinky;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.Games;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.herb.stinky.pinky.entities.Riddle;
import com.herb.stinky.pinky.lib.DatabaseHandler;
import com.herb.stinky.pinky.lib.SyncHandler;

public class MainActivity extends BaseGameActivity implements MainMenuFragment.Listener, LevelFragment.Listener, GamePlayFragment.Listener, InfoFragment.Listener, SettingsFragment.Listener
{
	final int RC_RESOLVE = 5000, RC_UNUSED = 5001;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	final static String MAIN_MENU_FRAG_TAG = "MAIN_MENU";
	final static String LEVEL_FRAG_TAG = "LEVEL";
	final static String GAME_PLAY_FRAG_TAG = "GAME_PLAY";
	final static String INFO_FRAG_TAG = "INFO";
	final static String SETTINGS_FRAG_TAG = "SETTINGS";
	
	private static final String CURRENT_FRAGMENT_KEY = "current_fragment";
	private static final String CURRENT_LEVEL_KEY = "current_level";
	private static final String RIDDLES_KEY = "riddles";
	private static final String MAX_LEVEL_KEY = "max_level";
	private static final String MAX_UNLOCKED_LEVEL_KEY = "max_unlocked_level";
	private static final String STARS_KEY = "stars";
	private static final String UNLOCKED_KEY = "unlocked";
	private static final String CURRENT_RIDDLE_INDEX_KEY = "riddle_index";
	
	public static final String SYNC_AVAILABLE_KEY = "sync_available";
	public static final String SERVER_LEVEL_KEY = "server_level";
	
	public final static String STINKY_PINKY_TAG = "stinky pinky";
	final static String STARS_TAG = "stars";
	
	final int HINT_COST = 10;
	
	final static String SENDER_ID = "768812001979";
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String REGISTRATION_STATUS_KEY = "registration_status";
	private static final String FRESH_INSTALL_KEY = "fresh_install";
	public static final String NOTIFICATIONS_ON_KEY = "notifications_on";
	public static final String NOTIFICATIONS_SOUND_ON_KEY = "notifications_sound_on";
	public static final String NOTIFICATIONS_VIBRATE_ON_KEY = "notifications_vibrate_on";
	
	DatabaseHandler myDbHandler;
	SyncHandler mySyncHandler;
	
	SharedPreferences prefs;
	
	GoogleCloudMessaging gcm;
	String regid;
	Context context;
	
	MainMenuFragment myMainMenuFragment;
	LevelFragment myLevelFragment;
	GamePlayFragment myGamePlayFragment;
	InfoFragment myInfoFragment;
	SettingsFragment mySettingsFragment;
	
	Fragment currentFragment;
	String currentFragTag;
	
	Riddle currentRiddle;
	
	Map<Fragment, String> fragmentToNameMap;
	
	ArrayList<Fragment> myBackStack = new ArrayList<Fragment>();
	
	private int level = 1, maxLevel=0, maxUnlockedLevel = 1, stars=0, currentRiddleIndex = 0;;
	private boolean unlocked = true;
	
	private Riddle[] riddles = new Riddle[10];
	
	AccomplishmentsOutbox outbox = new AccomplishmentsOutbox();
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myDbHandler = new DatabaseHandler(this);
		mySyncHandler = new SyncHandler();
		
		prefs = getSharedPreferences(STINKY_PINKY_TAG, MODE_PRIVATE);
		
		context = getApplicationContext();
		
		if(checkPlayServices())
		{
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId();
			
			if(regid.isEmpty())
			{
				registerDevice();
			}
		}
		
		if(savedInstanceState == null)
		{
			fragmentToNameMap = new HashMap<Fragment, String>();
			
			myMainMenuFragment = new MainMenuFragment();
			myLevelFragment = new LevelFragment();
			myGamePlayFragment = new GamePlayFragment();
			myInfoFragment = new InfoFragment();
			mySettingsFragment = new SettingsFragment();
			
			myMainMenuFragment.setListener(this);
			myLevelFragment.setListener(this);
			myGamePlayFragment.setListener(this);
			myInfoFragment.setListener(this);
			mySettingsFragment.setListener(this);
				
			if(isFreshInstall())
			{
				SyncProcess sync = new SyncProcess();
				sync.setContext(this);
				sync.execute();
			}
			
			fragmentToNameMap.put(myMainMenuFragment, MAIN_MENU_FRAG_TAG);
			fragmentToNameMap.put(myLevelFragment, LEVEL_FRAG_TAG);
			fragmentToNameMap.put(myGamePlayFragment, GAME_PLAY_FRAG_TAG);
			fragmentToNameMap.put(myInfoFragment, INFO_FRAG_TAG);
			fragmentToNameMap.put(mySettingsFragment, SETTINGS_FRAG_TAG);
			
			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container, mySettingsFragment, SETTINGS_FRAG_TAG)
			.hide(mySettingsFragment)
			.add(R.id.fragment_container, myInfoFragment, INFO_FRAG_TAG)
			.hide(myInfoFragment)
			.add(R.id.fragment_container, myGamePlayFragment, GAME_PLAY_FRAG_TAG)
			.hide(myGamePlayFragment)
			.add(R.id.fragment_container, myLevelFragment, LEVEL_FRAG_TAG)
			.hide(myLevelFragment)
			.add(R.id.fragment_container, myMainMenuFragment, MAIN_MENU_FRAG_TAG)
			.commit();
			
			currentFragment = myMainMenuFragment;
			currentFragTag = MAIN_MENU_FRAG_TAG;
			
			fetchRiddles();
			checkLockedStatus();
			setMaxLevel();
			getStars();
			
			currentRiddle = riddles[0];
			
			mySettingsFragment.setNotificationsOn(getNotificationsOn());
			mySettingsFragment.setNotificationsSoundOn(getNotificationsSoundOn());
			mySettingsFragment.setNotificationsVibrateOn(getNotificationsVibrateOn());
		}
		else
		{
			fragmentToNameMap = new HashMap<Fragment, String>();
			
			FragmentManager fm = getSupportFragmentManager();
			myMainMenuFragment = (MainMenuFragment) fm.findFragmentByTag(MAIN_MENU_FRAG_TAG);
			myInfoFragment = (InfoFragment) fm.findFragmentByTag(INFO_FRAG_TAG);
			myLevelFragment = (LevelFragment) fm.findFragmentByTag(LEVEL_FRAG_TAG);
			myGamePlayFragment = (GamePlayFragment) fm.findFragmentByTag(GAME_PLAY_FRAG_TAG);
			mySettingsFragment = (SettingsFragment) fm.findFragmentByTag(SETTINGS_FRAG_TAG);
			
			myMainMenuFragment.setListener(this);
			myLevelFragment.setListener(this);
			myGamePlayFragment.setListener(this);
			myInfoFragment.setListener(this);
			mySettingsFragment.setListener(this);
			
			if(isFreshInstall())
			{
				SyncProcess sync = new SyncProcess();
				sync.setContext(this);
				sync.execute();
			}
			
			fragmentToNameMap.put(myMainMenuFragment, MAIN_MENU_FRAG_TAG);
			fragmentToNameMap.put(myLevelFragment, LEVEL_FRAG_TAG);
			fragmentToNameMap.put(myGamePlayFragment, GAME_PLAY_FRAG_TAG);
			fragmentToNameMap.put(myInfoFragment, INFO_FRAG_TAG);
			
			currentFragTag = savedInstanceState.getString(CURRENT_FRAGMENT_KEY, MAIN_MENU_FRAG_TAG);
			level = savedInstanceState.getInt(CURRENT_LEVEL_KEY, 1);
			maxLevel = savedInstanceState.getInt(MAX_LEVEL_KEY, 0);
			maxUnlockedLevel = savedInstanceState.getInt(MAX_UNLOCKED_LEVEL_KEY, 1);
			stars = savedInstanceState.getInt(STARS_KEY, 0);
			unlocked = savedInstanceState.getBoolean(UNLOCKED_KEY, false);
			currentRiddleIndex = savedInstanceState.getInt(CURRENT_RIDDLE_INDEX_KEY, 0);
			
			try
			{
				String[] riddleStrings = savedInstanceState.getStringArray(RIDDLES_KEY);
				
				for(int i=0;i<riddleStrings.length;i++)
				{
						riddles[i] = Riddle.fromJSON(new JSONObject(riddleStrings[i]));
				}
				
				currentRiddle = riddles[currentRiddleIndex];
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			
			myLevelFragment.setLevel(level);
			myLevelFragment.setMaxLevel(maxLevel);
			myLevelFragment.setStars(stars);
			myLevelFragment.setRiddles(riddles);
			myLevelFragment.setUnlocked(unlocked);
			
			myGamePlayFragment.setRiddle(currentRiddle);
			myGamePlayFragment.setStars(stars);
			myGamePlayFragment.setRiddleIndex(currentRiddleIndex);
			
			if(currentFragTag.equals(MAIN_MENU_FRAG_TAG))
			{
				currentFragment = myMainMenuFragment;
				fm.beginTransaction()
				.show(myMainMenuFragment)
				.hide(myInfoFragment)
				.hide(myLevelFragment)
				.hide(myGamePlayFragment)
				.hide(mySettingsFragment)
				.commit();
				getActionBar().setDisplayHomeAsUpEnabled(false);
			}
			else if(currentFragTag.equals(INFO_FRAG_TAG))
			{
				currentFragment = myInfoFragment;
				fm.beginTransaction()
				.show(myInfoFragment)
				.hide(myMainMenuFragment)
				.hide(myLevelFragment)
				.hide(myGamePlayFragment)
				.hide(mySettingsFragment)
				.commit();
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			else if(currentFragTag.equals(LEVEL_FRAG_TAG))
			{
				currentFragment = myLevelFragment;
				fm.beginTransaction()
				.show(myLevelFragment)
				.hide(myInfoFragment)
				.hide(myMainMenuFragment)
				.hide(myGamePlayFragment)
				.hide(mySettingsFragment)
				.commit();
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			else if(currentFragTag.equals(GAME_PLAY_FRAG_TAG))
			{
				currentFragment = myGamePlayFragment;
				fm.beginTransaction()
				.show(myGamePlayFragment)
				.hide(myInfoFragment)
				.hide(myLevelFragment)
				.hide(myMainMenuFragment)
				.hide(mySettingsFragment)
				.commit();
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			else if(currentFragTag.equals(SETTINGS_FRAG_TAG))
			{
				currentFragment = mySettingsFragment;
				fm.beginTransaction()
				.show(mySettingsFragment)
				.hide(myInfoFragment)
				.hide(myLevelFragment)
				.hide(myMainMenuFragment)
				.hide(myGamePlayFragment)
				.commit();
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		myBackStack.add(currentFragment);
		
		mySettingsFragment.setNotificationsOn(getNotificationsOn());
		mySettingsFragment.setNotificationsSoundOn(getNotificationsSoundOn());
		mySettingsFragment.setNotificationsVibrateOn(getNotificationsVibrateOn());
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		savedInstanceState.putString(CURRENT_FRAGMENT_KEY, currentFragTag);
		savedInstanceState.putInt(CURRENT_LEVEL_KEY, level);
		savedInstanceState.putInt(MAX_LEVEL_KEY, maxLevel);
		savedInstanceState.putInt(MAX_UNLOCKED_LEVEL_KEY, maxUnlockedLevel);
		savedInstanceState.putInt(STARS_KEY, stars);
		savedInstanceState.putBoolean(UNLOCKED_KEY, unlocked);
		savedInstanceState.putInt(CURRENT_RIDDLE_INDEX_KEY, currentRiddleIndex);
		
		try
		{
			String[] riddleStrings = new String[riddles.length];
			
			for(int i=0;i<riddles.length;i++)
			{
				riddleStrings[i] = riddles[i].toJSON().toString();
			}
			
			savedInstanceState.putStringArray(RIDDLES_KEY, riddleStrings);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private boolean isFreshInstall()
	{
		return prefs.getBoolean(FRESH_INSTALL_KEY, true);
	}

	private void registerDevice()
	{
		new RegistrationProcess().execute();
	}
	
	private class RegistrationProcess extends AsyncTask<Void, Void, Void>
	{
		
		@Override
		protected Void doInBackground(Void... params)
		{
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                regid = gcm.register(SENDER_ID);
                
                mySyncHandler.registerDevice(regid);

            } catch (IOException ex) {
            	ex.printStackTrace();
            }
            return null;
		}
		
		@Override
		protected void onPostExecute(Void params)
		{
			int appVersion = getAppVersion(context);
			Editor editor = prefs.edit();
			editor.putString(PROPERTY_REG_ID, regid);
			editor.putInt(REGISTRATION_STATUS_KEY, 0);
			editor.putInt(PROPERTY_APP_VERSION, appVersion);
			editor.commit();
		}
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId() 
	{
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(STINKY_PINKY_TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(STINKY_PINKY_TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(STINKY_PINKY_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    
	private void getStars()
	{
		stars = prefs.getInt(STARS_TAG, 0);
		myLevelFragment.setStars(stars);
		myLevelFragment.updateUi();
		myGamePlayFragment.setStars(stars);
	}

	private void checkMaxUnlockedLevel()
	{
		int newMaxUnlockedLevel = maxUnlockedLevel;
		try
		{
			newMaxUnlockedLevel = new CheckMaxUnlockedLevel().execute().get();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		
		if(newMaxUnlockedLevel > maxUnlockedLevel)
		{
			maxUnlockedLevel = newMaxUnlockedLevel;
			newLevelUnlocked();
		}
	}

	private void newLevelUnlocked()
	{
		showAlert(getResources().getString(R.string.unlock));
		outbox.levelRevelAchievement = true;
		pushAccomplishments();
	}

	private void checkLockedStatus()
	{
		try
		{
			unlocked = new CheckLockStatus().execute(level).get();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		
		myLevelFragment.setUnlocked(unlocked);
		myLevelFragment.updateUi();
	}

	private void setMaxLevel()
	{
		try
		{
			maxLevel = new GetMaxLevelProcess().execute().get();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		
		myLevelFragment.setMaxLevel(maxLevel);
		myLevelFragment.updateUi();
	}

	@Override
	public void onBackPressed()
	{
		if(myBackStack.size() == 1)
			finish();
		else
			popBackStack();
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
        myBackStack.add(currentFragment);
        t.commit();
        
    	getActionBar().setDisplayHomeAsUpEnabled(!currentFragTag.equals(MAIN_MENU_FRAG_TAG));
    }
    
    private void popBackStack() 
    {
    	myBackStack.remove(myBackStack.size()-1);
    	
    	Fragment newFrag = myBackStack.get(myBackStack.size() -1);
    			
        if(newFrag.isVisible())
        	return;
        
        FragmentTransaction t =  getSupportFragmentManager().beginTransaction();
        
        newFrag.getView().bringToFront();
        currentFragment.getView().bringToFront();
        
        t.hide(currentFragment).show(newFrag);
        
        currentFragment = newFrag;
        currentFragTag = fragmentToNameMap.get(newFrag);
        t.commit();
        
    	getActionBar().setDisplayHomeAsUpEnabled(!currentFragTag.equals(MAIN_MENU_FRAG_TAG));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    		case android.R.id.home:
    			if(currentFragment == myLevelFragment || currentFragment == myInfoFragment || currentFragment == mySettingsFragment)
    				switchToFragment(myMainMenuFragment);
    			else if(currentFragment == myGamePlayFragment)
    				switchToFragment(myLevelFragment);
    			break;
    		case R.id.action_settings:
    			switchToFragment(mySettingsFragment);
    			break;
			default:
				break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onSignInFailed()
	{
		myMainMenuFragment.setShowSignIn(true);
	}

	@Override
	public void onSignInSucceeded()
	{
		myMainMenuFragment.setShowSignIn(false);
	}

	@Override
	public void onPlayButtonClicked()
	{
		switchToFragment(myLevelFragment);
	}

	@Override
	public void onInfoButtonClicked()
	{
		switchToFragment(myInfoFragment);
	}

	@Override
	public void onAchievementsButtonClicked()
	{
		if (isSignedIn()) 
        {
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),
                    RC_UNUSED);
        } 
        else 
        {
            showAlert(getString(R.string.achievements_not_available));
        }
	}

	@Override
	public void onLeaderboardButtonClicked()
	{
		if (isSignedIn()) 
        {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),
                    RC_UNUSED);
        } 
        else 
        {
            showAlert(getString(R.string.leaderboards_not_available));
        }
	}

	@Override
	public void onQuitButtonClicked()
	{
		this.finish();
	}

	@Override
	public void onSelectRiddle(int i)
	{
		if(unlocked)
		{
			currentRiddleIndex = i;
			currentRiddle = riddles[i];
			myGamePlayFragment.setRiddle(currentRiddle);
			myGamePlayFragment.setRiddleIndex(currentRiddleIndex);
			myGamePlayFragment.updateUi();
			switchToFragment(myGamePlayFragment);
		}
		else
		{
			showAlert(getResources().getString(R.string.locked));
		}
	}

	@Override
	public void onSignInButtonClicked()
	{
		beginUserInitiatedSignIn();
	}

	@Override
	public void onSignOutButtonClicked()
	{
		signOut();
		myMainMenuFragment.setShowSignIn(true);
	}

	@Override
	public void onCheckButtonClicked(String answer1, String answer2)
	{
		if(answer1.length() != 0 && answer2.length() != 0)
		{
			boolean correct = currentRiddle.validate(answer1, answer2);
			
			if(correct)
			{
				new SaveRiddleProcess().execute(currentRiddle);
				checkMaxUnlockedLevel();
				addStars(currentRiddle.getStarValue());
				myGamePlayFragment.setClearKeyboard(true);
				
				outbox.count++;
				outbox.starCount += currentRiddle.getStarValue();
				outbox.firstBurstAchievement = true;
			}
			else
			{
				currentRiddle.decStarValue();
				new SaveRiddleProcess().execute(currentRiddle);
				Toast.makeText(this, "Sorry!  That was incorrect", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "You must fill in both answers", Toast.LENGTH_SHORT).show();
		}
		
		pushAccomplishments();
		myLevelFragment.updateUi();
	}

	private void addStars(int starValue)
	{
		stars += starValue;
		
		myLevelFragment.setStars(stars);
		myLevelFragment.updateUi();
		myGamePlayFragment.setStars(stars);
		myGamePlayFragment.updateUi();
		
		Editor editor = prefs.edit();
		editor.putInt(STARS_TAG, stars);
		editor.apply();
	}
	
	private void decStars(int starValue)
	{
		stars -= starValue;
		
		myLevelFragment.setStars(stars);
		myLevelFragment.updateUi();
		myGamePlayFragment.setStars(stars);
		myGamePlayFragment.updateUi();
		
		
		Editor editor = prefs.edit();
		editor.putInt(STARS_TAG, stars);
		editor.apply();
	}

	@Override
	public void onLengthHintButtonClicked()
	{
		if(stars >= HINT_COST)
		{
			currentRiddle.useHint(Riddle.WORD_LENGTH_HINT);
			new SaveRiddleProcess().execute(currentRiddle);
			myGamePlayFragment.useHint();
			decStars(HINT_COST);
		}
		else
		{
			Toast.makeText(this, getResources().getString(R.string.needStars), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onFirstHintButtonClicked()
	{
		if(stars >= HINT_COST)
		{
			currentRiddle.useHint(Riddle.FIRST_LETTER_HINT);
			new SaveRiddleProcess().execute(currentRiddle);
			myGamePlayFragment.useHint();
			decStars(HINT_COST);
		}
		else
		{
			Toast.makeText(this, getResources().getString(R.string.needStars), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLastHintButtonClicked()
	{
		if(stars >= HINT_COST)
		{
			currentRiddle.useHint(Riddle.LAST_LETTER_HINT);
			new SaveRiddleProcess().execute(currentRiddle);
			myGamePlayFragment.useHint();
			decStars(HINT_COST);
		}
		else
		{
			Toast.makeText(this, getResources().getString(R.string.needStars), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClickNextLevelButton()
	{
		level++;
		checkLockedStatus();
		fetchRiddles();
		myLevelFragment.setLevel(level);
		myLevelFragment.updateUi();
	}
	
	@Override
	public void onNextRiddleButtonClicked()
	{
		if(currentRiddleIndex < 9)
		{
			currentRiddleIndex++;
			currentRiddle = riddles[currentRiddleIndex];
			myGamePlayFragment.setRiddle(currentRiddle);
			myGamePlayFragment.setRiddleIndex(currentRiddleIndex);
			myGamePlayFragment.updateUi();
		}
		else
		{
			onClickNextLevelButton();
			switchToFragment(myLevelFragment);
		}
	}

	public Riddle[] getRiddles()
	{
		return riddles;
	}

	private void fetchRiddles()
	{
		try
		{
			riddles = new FetchRiddleProcess().execute(level).get();
		}
		catch(ExecutionException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		myLevelFragment.setRiddles(riddles);
		myLevelFragment.updateUi();
	}
	
	private class FetchRiddleProcess extends AsyncTask<Integer, Void, Riddle[]>
	{

		@Override
		protected Riddle[] doInBackground(Integer... params)
		{
			return myDbHandler.fetchRiddleList(params[0]);
		}
	}
	
	private class SaveRiddleProcess extends AsyncTask<Riddle, Void, Void>
	{
		@Override
		protected Void doInBackground(Riddle... params)
		{
			myDbHandler.saveRiddle(params[0]);
			return null;
		}
	}
	
	private class GetMaxLevelProcess extends AsyncTask<Void, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Void... params)
		{
			return myDbHandler.getMaxLevel();
		}
	}
	
	private class CheckLockStatus extends AsyncTask<Integer, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(Integer... params)
		{
			return myDbHandler.isUnlocked(params[0]);
		}
	}
	
	private class CheckMaxUnlockedLevel extends AsyncTask<Void, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Void... params)
		{
			return myDbHandler.getMaxUnlockedLevel();
		}
	}

	@Override
	public void onClickPreviousLevelButton()
	{
		level--;
		checkLockedStatus();
		fetchRiddles();
		myLevelFragment.setLevel(level);
		myLevelFragment.updateUi();
	}
	
	private class SyncProcess extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog alert;
		Context syncContext;
		
		protected void setContext(Context context)
		{
			this.syncContext = context;
		}
		
		@Override
		protected void onPreExecute()
		{
			Log.i("FRESH INSTALL", "updating files");
			alert = new ProgressDialog(this.syncContext);
			alert.setMessage("Updating game files");
			alert.show();
		}
		
		@Override
		protected Void doInBackground(Void... params)
		{
			int level = myDbHandler.getMaxLevel();
			
			JSONObject json = mySyncHandler.fetchAllLevelsAfter(level);
			
			try
			{
				JSONArray riddlesJSONs = json.getJSONArray("riddles");
				
				myDbHandler.insertRiddles(riddlesJSONs);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v)
		{
			Editor editor = prefs.edit();
			editor.putBoolean(FRESH_INSTALL_KEY, false);
			editor.commit();
			setMaxLevel();
			alert.dismiss();
		}
	}
	
	class AccomplishmentsOutbox
	{
		boolean firstBurstAchievement = false;
		boolean levelRevelAchievement = false;
		boolean thriftyFiftyAchievement = false;
		boolean farStarAchievement = false;
		boolean awesomePossumAchievement = false;
		
		int count=0;
		int starCount=0;
		
		boolean isEmpty()
		{
			return !firstBurstAchievement && !levelRevelAchievement && !thriftyFiftyAchievement
					&& !farStarAchievement && !awesomePossumAchievement && count == 0 && starCount == 0; 
		}
	}
	
	private void pushAccomplishments()
	{
		if(!isSignedIn())
		{
			return;
		}
		
		if(outbox.firstBurstAchievement)
		{
			Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_first_burst));
			outbox.firstBurstAchievement = false;
		}
		if(outbox.levelRevelAchievement)
		{
			Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_level_revel));
			outbox.levelRevelAchievement = false;
		}
		
		if(outbox.count > 0)
		{
			Games.Achievements.increment(getApiClient(), getString(R.string.achievement_thrifty_fifty), outbox.count);
			Games.Achievements.increment(getApiClient(), getString(R.string.achievement_awesome_possum), outbox.count);
			Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_most_stinky_pinkies_answered), outbox.count);
			outbox.count = 0;
		}
		
		if(outbox.starCount > 0)
		{
			Games.Achievements.increment(getApiClient(), getString(R.string.achievement_far_star), outbox.starCount);
			outbox.starCount = 0;
		}
	}

	@Override
	public void onNotificationToggle()
	{
		boolean newNotificationsOnValue = !getNotificationsOn();
		
		Editor editor = prefs.edit();
		editor.putBoolean(NOTIFICATIONS_ON_KEY, newNotificationsOnValue);
		editor.apply();
	}
	
	private boolean getNotificationsOn()
	{
		return prefs.getBoolean(NOTIFICATIONS_ON_KEY, true);
	}

	@Override
	public void onNotificationSoundToggle()
	{
		boolean newNotificationsSoundOnValue = !getNotificationsSoundOn();
		
		Editor editor = prefs.edit();
		editor.putBoolean(NOTIFICATIONS_SOUND_ON_KEY, newNotificationsSoundOnValue);
		editor.apply();
	}

	private boolean getNotificationsSoundOn()
	{
		return prefs.getBoolean(NOTIFICATIONS_SOUND_ON_KEY, true);
	}

	@Override
	public void onNotificationVibrateToggle()
	{
		boolean newNotificationsVibrateOnValue = !getNotificationsVibrateOn();
		
		Editor editor = prefs.edit();
		editor.putBoolean(NOTIFICATIONS_VIBRATE_ON_KEY, newNotificationsVibrateOnValue);
		editor.apply();
	}

	private boolean getNotificationsVibrateOn()
	{
		return prefs.getBoolean(NOTIFICATIONS_VIBRATE_ON_KEY, true);
	}
}
