package com.andersonsilva.cel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MainActivityReceiver extends BroadcastReceiver {
	public static final String ACTION_RESP = "com.anderson.cel.intent.action.MESSAGE_PROCESSED";

	@Override
	public void onReceive(Context context, Intent intent) {
		String text = intent.getStringExtra(MainActivityService.PARAM_OUT_MSG);
		MainActivity.items[0] = text;
		Log.d("BroadcastReceiver", "item: " + MainActivity.items[0] + " text: "
				+ text);
		MainActivity.lv.setAdapter(MainActivity.adapter);
		MainActivity.setListViewBackgroundColor(MainActivity.lv,
				MainActivity.items[0]);
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();

	}

}