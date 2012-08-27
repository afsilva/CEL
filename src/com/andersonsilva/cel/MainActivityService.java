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

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MainActivityService extends IntentService {

	private JSONObject jObject;
	JSONArray openIssues = null;
	JSONArray resolvedIssues = null;
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";

	public MainActivityService() {
		super("MainActivityService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("TAG", "Service started");

		String appStatus = null;
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
				} else {
					appStatus = "Openshift Status: (" + openIssues.length()
							+ ")";
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