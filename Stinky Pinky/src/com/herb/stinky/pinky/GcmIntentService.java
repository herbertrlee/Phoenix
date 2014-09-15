package com.herb.stinky.pinky;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.herb.stinky.pinky.lib.DatabaseHandler;
import com.herb.stinky.pinky.lib.SyncHandler;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService
{
    public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
	static final String GCM_INTENT_SERVICE_NAME = "GcmIntentService";
	static final String TAG = "GCM";
	static final String LEVEL = "level";
	static final String SYNC = "sync";
	
	public GcmIntentService()
	{
		super(MainActivity.SENDER_ID);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		
		String regId = intent.getExtras().getString("registration_id");
		if(regId != null && !regId.equals("")) 
		{
			storeRegId(regId);
		}
		
		String messageType = gcm.getMessageType(intent);
		
		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(TAG, GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(TAG, GoogleCloudMessaging.MESSAGE_TYPE_DELETED);
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                if(extras.getString("type").equals(SYNC))
                {
                	sync(Integer.parseInt(extras.getString(LEVEL)));
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void storeRegId(String regId)
	{
		new UploadRegistrationProcess().execute(regId);
	}

	private void sync(int level)
	{
		new SyncProcess().execute(level);
	}

	private class UploadRegistrationProcess extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params)
		{
			SyncHandler syncHandler = new SyncHandler();
			syncHandler.registerDevice(params[0]);
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(String regId)
		{
			SharedPreferences prefs = getSharedPreferences(MainActivity.STINKY_PINKY_TAG, Activity.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(MainActivity.PROPERTY_REG_ID, regId);
			editor.putInt(MainActivity.PROPERTY_APP_VERSION, MainActivity.getAppVersion(getApplicationContext()));
			editor.commit();
		}
	}
	
	private class SyncProcess extends AsyncTask<Integer, Void, Void>
	{

		@Override
		protected Void doInBackground(Integer... params)
		{
			int level = params[0];
			
			DatabaseHandler myDbHandler = new DatabaseHandler(getApplicationContext());
			SyncHandler mySyncHandler = new SyncHandler();
			
			int maxLevel = myDbHandler.getMaxLevel();
			
			if(maxLevel < level)
			{
				Log.i("Stinky Pinky", "Updates available.");
				JSONObject json = mySyncHandler.fetchAllLevelsAfter(maxLevel);
				try
				{
					JSONArray levels = json.getJSONArray("riddles");
					myDbHandler.insertRiddles(levels);
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				Log.i("Stinky Pinky", "All records up to date.");
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v)
		{
			createNotification();
		}
		
	}

	public void createNotification()
	{
		SharedPreferences prefs = getSharedPreferences(MainActivity.STINKY_PINKY_TAG, Activity.MODE_PRIVATE);
		
		if(prefs.getBoolean(MainActivity.NOTIFICATIONS_ON_KEY, true))
		{
			mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Intent intent = new Intent(this, MainActivity.class);
	        
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
	        int defaults = 0;
	        

	        if(prefs.getBoolean(MainActivity.NOTIFICATIONS_SOUND_ON_KEY, true))
	        {
	        	defaults |= Notification.DEFAULT_SOUND;
	        }
	        if(prefs.getBoolean(MainActivity.NOTIFICATIONS_VIBRATE_ON_KEY, true))
	        {
	        	defaults |= Notification.DEFAULT_VIBRATE;
	        }
	        
	        String msg = getResources().getString(R.string.new_content_available);
	        
	        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.stinky_pinky_launcher); 
	        		
			builder = new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.stinky_pinky_small_icon)
	        .setLargeIcon(logo)
	        .setContentTitle(getResources().getString(R.string.app_name))
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(msg))
	        .setContentText(msg)
	        .setAutoCancel(true)
	        .setDefaults(defaults);
			
	        builder.setContentIntent(contentIntent);
	        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
		}
		else
		{
			Log.e("notifications", "off");
		}
	}
}
