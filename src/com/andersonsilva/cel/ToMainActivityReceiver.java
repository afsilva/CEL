package com.andersonsilva.cel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ToMainActivityReceiver extends BroadcastReceiver {
	public static final String ACTION_STATUS_REFRESH = "com.andersonsilva.cel.ACTION_STATUS_REFRESH";

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startIntent = new Intent(context, MainActivityService.class);
		context.startService(startIntent);

	}

}