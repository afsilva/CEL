package com.andersonsilva.cel;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	public static ArrayAdapter<String> adapter;

	// Menu items
	public static String[] items = { "", "", " openshift.redhat.com",
			" community", "", " @openshift", " #openshift", " @openshift_ops",
			" +openshift", " openshift@facebook" };

	// Menu Links
	public static String[] links = { "", "", "https://openshift.redhat.com/",
			"http://openshift.redhat.com/community", "",
			"https://mobile.twitter.com/openshift/",
			"https://mobile.twitter.com/search/%23openshift",
			"https://mobile.twitter.com/openshift_ops",
			"https://plus.google.com/108052331678796731786/posts",
			"http://www.facebook.com/openshift" };

	public static ListView lv = null;

	public static String go_url = "https://openshift.redhat.com/app/status";
	public static String status_url = "https://openshift.redhat.com/app/status/status.json";
	private MainActivityReceiver receiver;

	// Test URL
	// public static String status_url =
	// "https://people.redhat.com/~ansilva/status.json";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService(new Intent(this, MainActivityService.class));

		adapter = new ArrayAdapter<String>(this, R.layout.activity_main_list,
				items);
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 1 || position == 4) {
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

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(MainActivityReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new MainActivityReceiver();
		registerReceiver(receiver, filter);

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	// set background color depending on result of https call
	public static void setListViewBackgroundColor(View v, String s) {
		if (s.endsWith("(OK)")) {
			v.setBackgroundColor(Color.rgb(00, 99, 00));
		} else if (s.endsWith("network ")) {
			v.setBackgroundColor(Color.GRAY);
		} else {
			v.setBackgroundColor(Color.rgb(99, 00, 00));
		}
	}

}