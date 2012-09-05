package com.andersonsilva.cel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MainActivityService extends IntentService {

	private JSONObject jObject;
	JSONArray openIssues = null;
	JSONArray resolvedIssues = null;
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";

	public NotificationManager myNotificationManager;
	public static final int NOTIFICATION_ID = 1;

	private AlarmManager alarmManager;
	private PendingIntent alarmIntent;

	public MainActivityService() {
		super("MainActivityService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		super.onCreate();
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		String ALARM_ACTION = ToMainActivityReceiver.ACTION_STATUS_REFRESH;
		Intent intentToFire = new Intent(ALARM_ACTION);
		alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("MainActivityService", "Service started");

		// int updateFreq = 60; // this needs to be a preference
		// boolean autoUpdateChecked = true;
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		int updateFreq = Integer.parseInt(prefs.getString(
				PreferencesActivity.PREF_UPDATE_FREQ, "60"));

		Log.d("MainActivityService", "updateFreq: " + updateFreq);
		boolean autoUpdateChecked = prefs.getBoolean(
				PreferencesActivity.PREF_AUTO_UPDATE, false);

		if (autoUpdateChecked) {
			int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
			long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq
					* 60 * 1000;
			alarmManager.setInexactRepeating(alarmType, timeToRefresh,
					updateFreq * 60 * 1000, alarmIntent);
		} else
			alarmManager.cancel(alarmIntent);

		String appStatus = null;
		int appStatusInt = 0;
		String json = getJSON();
		String results = "";

		// if something comes back from getJSON()
		if (json != null) {

			try {
				jObject = new JSONObject(json);
				// Getting Array of Contacts
				openIssues = jObject.getJSONArray("open");
				// resolvedIssues = jObject.getJSONArray("resolved");

				if (openIssues.length() == 0) {
					appStatus = "Openshift Status: (OK)";
					appStatusInt = 0;
				} else {
					appStatus = "Openshift Status: (" + openIssues.length()
							+ ")";
					appStatusInt = 1;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Grammar fix
			if (appStatus.endsWith("(1)"))
				appStatus = appStatus + " Issue";
			else if (!appStatus.endsWith("(OK)"))
				appStatus = appStatus + " Issues";

			results = " " + appStatus;
		} else {

			results = " Couldn't load status\n -Check network ";

		}

		Intent notificationIntent = new Intent(context, MainActivityWeb.class);
		notificationIntent.setData(Uri.parse(MainActivity.go_url));

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Resources res = ctx.getResources();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);

		builder.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setContentTitle(results).setContentText("See Details");

		myNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		if (appStatusInt == 1) {

			myNotificationManager.notify(NOTIFICATION_ID,
					builder.getNotification());

			Log.i(getClass().getSimpleName(), "Openshift Status Notified");
		}

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MainActivityReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_MSG, results);
		sendBroadcast(broadcastIntent);
	};

	// json parser
	public String getJSON() {
		try {
			URL url = new URL(MainActivity.status_url);
			HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setRequestProperty("Content-length", "0");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.connect();
			int status = c.getResponseCode();

			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(
						c.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				return sb.toString();
			}

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}