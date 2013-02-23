package com.aftercider.webcustomview;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
	private WebView myWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// myWebViewのインスタンスを取得
		myWebView = (WebView) findViewById(R.id.webview);

		// カスタムWebViewを設定する
		myWebView.setWebViewClient(new CustomWebView());

		// Googleを表示する
		myWebView.loadUrl("http://www.mizukinana.jp/blog/index.html");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Google");
		menu.add(0, 1, 1, "IT TRICK");
		menu.add(0, 2, 2, "ERROR");
		return true;
	}

	// オプションメニュー選択された場合、選択項目に合わせて
	// WebViewの表示先URLを変更する。
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		int itemId = item.getItemId();
		switch (itemId) {
		case 0:
			myWebView.loadUrl("http://www.mizukinana.jp/blog/index.html");
			break;
		case 1:
			HTMLTask preTask = new HTMLTask();
			preTask.execute();
			break;
		case 2:
			String summary = "<html><body><iframe src='http://www.mizukinana.jp/blog/index.html' height=1200 width=1200>この部分は iframe 対応のブラウザで見てください。</iframe></body></html>";
			myWebView.loadData(summary, "text/html", null);
			break;
		}
		return true;
	}

	// AndroidのBackキーを押された時のイベントを受け取り、
	// 前回表示したページに戻る。
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 前処理
	public class HTMLTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient c = new DefaultHttpClient();
			HttpGet g = new HttpGet("http://mizukinana.jp/blog/index.html");
			String ret = "";

			HttpResponse response = null;
			try {
				response = c.execute(g);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
				try {
					ret = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
			}
			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			result = convert(result);
		}
		protected String convert(String original){
			String ret = "";
			String start = "<div id=\"menu\">";
			String end = "<div id=\"container\">";
			ret += original.substring(0, original.indexOf(start));
			ret += original.substring(original.indexOf(end)+end.length() , original.length());
			myWebView.loadData(ret, "text/html; charset=utf-8", "utf-8");
			return ret;
		}
	}

	/**
	 * WebViewClientクラスを継承したカスタムWebView（内部クラス）
	 * 
	 */
	private class CustomWebView extends WebViewClient {

		// ページの読み込み開始
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Toast.makeText(view.getContext(), "onPageStarted",
					Toast.LENGTH_LONG).show();
		}

		// ページの読み込み完了
		@Override
		public void onPageFinished(WebView view, String url) {
			Toast.makeText(view.getContext(), "onPageFinished",
					Toast.LENGTH_LONG).show();
		}

		// ページの読み込み失敗
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			Toast.makeText(view.getContext(), "onReceivedError",
					Toast.LENGTH_LONG).show();
		}
	}
}