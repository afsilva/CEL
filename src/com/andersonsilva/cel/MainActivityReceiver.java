package com.andersonsilva.cel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MainActivityReceiver extends BroadcastReceiver {
	public static final String ACTION_RESP = "com.anderson.cel.intent.action.MESSAGE_PROCESSED";
	public NotificationManager myNotificationManager;
	public static final int NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TextView result = (TextView) findViewById(R.id.txt_result);
		String text = intent.getStringExtra(MainActivityService.PARAM_OUT_MSG);
		MainActivity.items[0] = text;
		MainActivity.lv.setAdapter(MainActivity.adapter);
		MainActivity.setListViewBackgroundColor(MainActivity.lv,
				MainActivity.items[0]);
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();

		if (!text.endsWith("(OK)")) {
			myNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent = new Intent(Intent.ACTION_VIEW);

			notificationIntent.setData(Uri.parse(MainActivity.go_url));
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			Notification notification = new Notification(
					R.drawable.ic_launcher, text, System.currentTimeMillis());
			notification.setLatestEventInfo(context, text, "See Details",
					contentIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			myNotificationManager.notify(NOTIFICATION_ID, notification);

			Log.i(getClass().getSimpleName(), "Openshift Status Notified");
		}
	}

}