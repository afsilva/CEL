package com.andersonsilva.cel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivityWeb extends Activity {
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_viewer);

		Intent launchingIntent = getIntent();
		String content = launchingIntent.getData().toString();

		WebView viewer = (WebView) findViewById(R.id.webView1);
		viewer.setWebViewClient(new MyWebViewClient());
		viewer.getSettings().setJavaScriptEnabled(true);
		viewer.getSettings().setDomStorageEnabled(true);
		viewer.loadUrl(content);
	}

	public class MyWebViewClient extends WebViewClient {
		/*
		 * (non-Java doc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit
		 * .WebView, java.lang.String)
		 */

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.endsWith(".mp4")) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(url), "video/*");

				view.getContext().startActivity(intent);
				return true;
			} else {
				return super.shouldOverrideUrlLoading(view, url);
			}
		}
	}
}
