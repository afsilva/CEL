package com.andersonsilva.cel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	ArrayAdapter<String> adapter;
	private JSONObject jObject;
	JSONArray openIssues = null;
	JSONArray resolvedIssues = null;

	// Menu items
	private String[] items = { "", "   ---> tap to refresh", "",
			" openshift.redhat.com", " community", "", " @openshift",
			" #openshift", " @openshift_ops", " +openshift",
	" openshift@facebook" };

	// Menu Links
	private String[] links = { "", "", "", "https://openshift.redhat.com/",
			"http://openshift.redhat.com/community", "",
			"https://mobile.twitter.com/openshift/",
			"https://mobile.twitter.com/search/%23openshift",
			"https://mobile.twitter.com/openshift_ops",
			"https://plus.google.com/108052331678796731786/posts",
	"http://www.facebook.com/openshift" };

	ListView lv = null;

	String go_url = "https://openshift.redhat.com/app/status";
	String status_url = "https://openshift.redhat.com/app/status/status.json";
	// Test URL
	//String status_url = "https://people.redhat.com/~ansilva/status.json";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ListView lv = (ListView) findViewById(android.R.id.list);

		// The next two lines are used to I can make the https call on
		// checkCloudLight on the main application thread. Not the best
		// solution, but it will do for now.

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Here's where we get the current status of our Cloud service
		checkCloudLight();

		adapter = new ArrayAdapter<String>(this, R.layout.activity_main_list,
				items);
		lv.setAdapter(adapter);

		setListViewBackgroundColor(lv, items[0]);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// if the 'refresh' button is clicked
				if (position == 1) {

					checkCloudLight();
					adapter.notifyDataSetChanged();
					setListViewBackgroundColor(lv, items[0]);

					// if 'empty' button is clicked do nothing
				} else if (position == 2 || position == 5) {
					// do nothing on the third link
				} else {
					String content = null;
					// if first button with status of site is clicked
					if (position == 0) {

						content = go_url;
					} else {

						// moving offset to match with the 3 extra items i have
						// on item[] array
						content = links[position];
					}
					Intent showContent = new Intent(getApplicationContext(),
							MainActivityWeb.class);
					showContent.setData(Uri.parse(content));
					startActivity(showContent);
				}

			}
		});

	}

	// set background color depending on result of https call
	public void setListViewBackgroundColor(View v, String s) {
		if (s.endsWith("(OK)")) {
			v.setBackgroundColor(Color.rgb(00, 99, 00));
		} else if (s.endsWith("network ")) {
			v.setBackgroundColor(Color.GRAY);
		} else {
			v.setBackgroundColor(Color.rgb(99, 00, 00));
		}

	}

	//json parser
	public String getJSON() {
		try {
			URL url = new URL(status_url);
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

	// this is the method that checks against Cloud service URL
	// and strips the TITLE tag.
	public void checkCloudLight() {

		String appStatus = null;

		String json = getJSON();
		
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
           
			items[0] = " " + appStatus;
		} else {

			items[0] = " Couldn't load status\n -Check network ";

		}
	}
}