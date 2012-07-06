package com.andersonsilva.cel;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@TargetApi(9)
public class MainActivity extends ListActivity {

	ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Menu items
		final String[] items = { " @openshift", " #openshift",
				" @openshift_ops", " +openshift", " community",
				" openshift@facebook" };

		// Here's where we get the current status of our Cloud service
		String appStatus = checkCloudLight();

		if (appStatus == null) {
			appStatus = " Couldn't load status\n -Check network ";
		}

		List<String> itemsList = new ArrayList<String>(Arrays.asList(items));
		itemsList.add(0, " " + appStatus);
		String[] finalItems = itemsList.toArray(new String[itemsList.size()]);

		this.setListAdapter(new ArrayAdapter<String>(this,
				R.layout.activity_main, finalItems));

		final String[] links = getResources()
				.getStringArray(R.array.menu_links);

		ListView lv = getListView();
		if (appStatus.endsWith("(OK)")) {
			lv.setBackgroundColor(Color.rgb(00, 99, 00));
		} else if (appStatus.endsWith("network ")) {
			lv.setBackgroundColor(Color.GRAY);
		} else {
			lv.setBackgroundColor(Color.rgb(99, 00, 00));
		}

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String content = links[position];
				Intent showContent = new Intent(getApplicationContext(),
						MainActivityWeb.class);
				showContent.setData(Uri.parse(content));
				startActivity(showContent);

			}
		});

	}

	public String checkCloudLight() {

		String html = "", tmp = "";

		try {
			URL url = new URL("https://openshift.redhat.com/app/status");
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
		String t = null;
		while (m.find() == true) {
			t = m.group(1);
		}
		return t;

	}

}