package com.andersonsilva.cel;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

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

	String status_url = "https://openshift.redhat.com/app/status";

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

						content = status_url;
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

	// this is the method that checks against Cloud service URL
	// and strips the TITLE tag.
	public void checkCloudLight() {

		String html = "", tmp = "";

		try {
			URL url = new URL(status_url);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			DataInputStream dis;
			dis = new DataInputStream(con.getInputStream());
			while ((tmp = dis.readUTF()) != null) {
				html += " " + tmp;
			}
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		html = html.replaceAll("\\s+", " ");
		Pattern p = Pattern.compile("<title>(.*?)</title>");
		Matcher m = p.matcher(html);
		String appStatus = null;
		while (m.find() == true) {
			appStatus = m.group(1);
		}
		// return t;
		if (appStatus == null) {
			items[0] = " Couldn't load status\n -Check network ";
		} else {
			
			if (appStatus.endsWith("(1)")) 
				appStatus = appStatus + " Issue";
			else if (!appStatus.endsWith("(OK)"))
				appStatus = appStatus + " Issues";
			
			items[0] = " " + appStatus;
		}
	}

}